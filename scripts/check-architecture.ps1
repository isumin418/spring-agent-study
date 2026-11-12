$ErrorActionPreference = 'Stop'
$event = [Console]::In.ReadToEnd() | ConvertFrom-Json

if ($event.stop_hook_active) {
    '{}'
    exit 0
}

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location -LiteralPath $projectRoot

# Keep Gradle output separate from the Hook JSON response on stdout.
if ($env:OS -eq 'Windows_NT') {
    $testOutput = cmd.exe /d /c '.\gradlew.bat test --tests *ArchitectureTest --console=plain 2>&1'
} else {
    $testOutput = sh -c 'sh ./gradlew test --tests "*ArchitectureTest" --console=plain 2>&1'
}
$testExitCode = $LASTEXITCODE

if ($testExitCode -ne 0) {
    [Console]::Error.WriteLine(($testOutput -join [Environment]::NewLine))
    [Console]::Error.WriteLine('ArchUnit check failed. Check the Gradle output for the test report before finishing.')
    exit 2
}

'{}'
