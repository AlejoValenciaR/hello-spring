# Hello Spring

Minimal Spring Boot project with a single Atlas Grove view that renders `Hello, World!`.

## Run

```bash
./mvnw spring-boot:run
```

Open `http://localhost:8080/`.

## Test

```bash
./mvnw test
```

## Whisper Website Routes

The root path `/` stays on the existing Spring welcome page.

The standalone Whisper website lives under `/Whisper/`, and the Kubernetes ingress paths for branded aliases are listed in `k8s/ingress-paths.txt`.

That file is the app-side route list. The public VM nginx gateway needs the same branded paths forwarded to the k3d ingress listener so URLs like `/Whisper` and `/WhisperApp` can reach this app without taking over `/`.
