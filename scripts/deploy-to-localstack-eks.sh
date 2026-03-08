#!/usr/bin/env bash
set -euo pipefail

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

is_remote_endpoint() {
  ! printf '%s\n' "$1" | grep -Eq '^https?://(localhost|127\.0\.0\.1)(:[0-9]+)?(/|$)'
}

is_localhost_style_host() {
  printf '%s\n' "$1" | grep -Eq '(^localhost(:[0-9]+)?$)|(^[[:alnum:].-]+\.localhost\.localstack\.cloud(:[0-9]+)?$)'
}

render_template() {
  local input_file="$1"
  local output_file="$2"

  sed \
    -e "s|__APP_NAME__|${APP_NAME}|g" \
    -e "s|__APP_BASE_PATH__|${APP_BASE_PATH}|g" \
    -e "s|__IMAGE__|${IMAGE_URI}|g" \
    -e "s|__NAMESPACE__|${K8S_NAMESPACE}|g" \
    "$input_file" > "$output_file"
}

require_command aws
require_command docker
require_command kubectl
require_command sed

: "${AWS_ACCESS_KEY_ID:?AWS_ACCESS_KEY_ID is required}"
: "${AWS_SECRET_ACCESS_KEY:?AWS_SECRET_ACCESS_KEY is required}"

APP_NAME="${APP_NAME:-hello-spring}"
APP_BASE_PATH="${APP_BASE_PATH:-/HelloApp}"
AWS_REGION="${AWS_REGION:-${AWS_DEFAULT_REGION:-us-east-1}}"
ECR_REPOSITORY="${ECR_REPOSITORY:-hello-spring}"
EKS_CLUSTER_NAME="${EKS_CLUSTER_NAME:-localstack-eks-cluster}"
EKS_NODEGROUP_NAME="${EKS_NODEGROUP_NAME:-localstack-eks-node-group}"
K8S_NAMESPACE="${K8S_NAMESPACE:-hello-spring}"
LS_ENDPOINT_URL="${LS_ENDPOINT_URL:-https://localstack.nauthappstest.tech}"
PUBLIC_APP_URL="${PUBLIC_APP_URL:-https://nauthappstest.tech${APP_BASE_PATH}/api/hello}"
SHORT_SHA="$(git rev-parse --short HEAD 2>/dev/null || echo local)"
IMAGE_TAG="${IMAGE_TAG:-${BUILD_NUMBER:-manual}-${SHORT_SHA}}"
WORK_DIR="${WORKSPACE:-$(pwd)}"
KUBECONFIG_DIR="${WORK_DIR}/.kube"
KUBECONFIG_FILE="${KUBECONFIG:-${KUBECONFIG_DIR}/config}"
TMP_DIR="$(mktemp -d)"

trap 'rm -rf "${TMP_DIR}"' EXIT

export AWS_DEFAULT_REGION="${AWS_REGION}"
export KUBECONFIG="${KUBECONFIG_FILE}"

mkdir -p "${KUBECONFIG_DIR}"

echo "Checking LocalStack credentials..."
aws --endpoint-url "${LS_ENDPOINT_URL}" sts get-caller-identity --region "${AWS_REGION}" >/dev/null

echo "Ensuring ECR repository exists..."
if ! aws --endpoint-url "${LS_ENDPOINT_URL}" ecr describe-repositories \
  --repository-names "${ECR_REPOSITORY}" \
  --region "${AWS_REGION}" >/dev/null 2>&1; then
  aws --endpoint-url "${LS_ENDPOINT_URL}" ecr create-repository \
    --repository-name "${ECR_REPOSITORY}" \
    --region "${AWS_REGION}" >/dev/null
fi

ECR_REPOSITORY_URI="$(
  aws --endpoint-url "${LS_ENDPOINT_URL}" ecr describe-repositories \
    --repository-names "${ECR_REPOSITORY}" \
    --query 'repositories[0].repositoryUri' \
    --output text \
    --region "${AWS_REGION}"
)"
ECR_REGISTRY_HOST="${ECR_REPOSITORY_URI%%/*}"

if is_remote_endpoint "${LS_ENDPOINT_URL}" && is_localhost_style_host "${ECR_REGISTRY_HOST}"; then
  echo "LocalStack returned a localhost-style ECR registry host: ${ECR_REPOSITORY_URI}" >&2
  echo "For remote Jenkins, set LOCALSTACK_HOST and preferably ECR_ENDPOINT_STRATEGY=off in the Azure VM LocalStack service." >&2
  exit 1
fi

ECR_LOGIN_PASSWORD="$(
  aws --endpoint-url "${LS_ENDPOINT_URL}" ecr get-login-password --region "${AWS_REGION}"
)"

IMAGE_URI="${ECR_REPOSITORY_URI}:${IMAGE_TAG}"

echo "Building Docker image ${IMAGE_URI}..."
docker build -t "${APP_NAME}:${IMAGE_TAG}" .
printf '%s' "${ECR_LOGIN_PASSWORD}" | docker login --username AWS --password-stdin "${ECR_REGISTRY_HOST}"
docker tag "${APP_NAME}:${IMAGE_TAG}" "${IMAGE_URI}"
docker push "${IMAGE_URI}"

echo "Checking EKS cluster access..."
aws --endpoint-url "${LS_ENDPOINT_URL}" eks describe-cluster \
  --name "${EKS_CLUSTER_NAME}" \
  --region "${AWS_REGION}" >/dev/null

if aws --endpoint-url "${LS_ENDPOINT_URL}" eks describe-nodegroup \
  --cluster-name "${EKS_CLUSTER_NAME}" \
  --nodegroup-name "${EKS_NODEGROUP_NAME}" \
  --region "${AWS_REGION}" >/dev/null 2>&1; then
  echo "EKS node group ${EKS_NODEGROUP_NAME} is present."
else
  echo "Warning: EKS node group ${EKS_NODEGROUP_NAME} was not found. Continuing because LocalStack node group behavior can vary."
fi

echo "Updating kubeconfig..."
aws --endpoint-url "${LS_ENDPOINT_URL}" eks update-kubeconfig \
  --name "${EKS_CLUSTER_NAME}" \
  --region "${AWS_REGION}" \
  --alias "${EKS_CLUSTER_NAME}" >/dev/null

CLUSTER_SERVER="$(kubectl config view --minify -o jsonpath='{.clusters[0].cluster.server}')"

if is_remote_endpoint "${LS_ENDPOINT_URL}" && printf '%s\n' "${CLUSTER_SERVER}" | grep -Eq '^https://(localhost|127\.0\.0\.1|[[:alnum:].-]+\.localhost\.localstack\.cloud)(:[0-9]+)?(/|$)'; then
  echo "Kubeconfig points to a localhost-style Kubernetes endpoint: ${CLUSTER_SERVER}" >&2
  echo "Set LOCALSTACK_HOST in the Azure VM LocalStack service so remote clients receive a reachable cluster endpoint." >&2
  exit 1
fi

kubectl cluster-info >/dev/null

echo "Applying Kubernetes manifests..."
render_template "k8s/namespace.template.yaml" "${TMP_DIR}/namespace.yaml"
render_template "k8s/deployment.template.yaml" "${TMP_DIR}/deployment.yaml"
render_template "k8s/service.template.yaml" "${TMP_DIR}/service.yaml"
render_template "k8s/ingress.template.yaml" "${TMP_DIR}/ingress.yaml"

kubectl apply -f "${TMP_DIR}/namespace.yaml"
kubectl -n "${K8S_NAMESPACE}" create secret docker-registry localstack-ecr \
  --docker-server="${ECR_REGISTRY_HOST}" \
  --docker-username="AWS" \
  --docker-password="${ECR_LOGIN_PASSWORD}" \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl apply -f "${TMP_DIR}/deployment.yaml"
kubectl apply -f "${TMP_DIR}/service.yaml"
kubectl apply -f "${TMP_DIR}/ingress.yaml"

echo "Waiting for rollout..."
kubectl -n "${K8S_NAMESPACE}" rollout status deployment "${APP_NAME}" --timeout=180s

echo "Deployment completed."
echo "Namespace: ${K8S_NAMESPACE}"
echo "Image: ${IMAGE_URI}"
echo "Expected public URL after Azure VM ingress wiring: ${PUBLIC_APP_URL}"
