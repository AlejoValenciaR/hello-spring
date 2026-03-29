param(
    [string]$BrowserPath
)

$ErrorActionPreference = "Stop"

$siteRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$manualHtml = Join-Path $siteRoot "manual.html"
$manualPdf = Join-Path $siteRoot "Whisper-Live-Subtitles-Manual.pdf"

if (-not $BrowserPath) {
    $candidates = @(
        "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
        "C:\Program Files\Microsoft\Edge\Application\msedge.exe",
        "C:\Program Files\Google\Chrome\Application\chrome.exe",
        "C:\Program Files (x86)\Google\Chrome\Application\chrome.exe"
    )

    foreach ($candidate in $candidates) {
        if (Test-Path $candidate) {
            $BrowserPath = $candidate
            break
        }
    }
}

if (-not $BrowserPath -or -not (Test-Path $BrowserPath)) {
    throw "Edge or Chrome was not found. Pass -BrowserPath explicitly."
}

$manualUri = [System.Uri]::new($manualHtml).AbsoluteUri

& $BrowserPath `
    --headless=new `
    --disable-gpu `
    --print-to-pdf="$manualPdf" `
    --print-to-pdf-no-header `
    $manualUri

Write-Host "PDF generated at $manualPdf"
