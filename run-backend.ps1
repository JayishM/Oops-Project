$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$backend = Join-Path $root 'backend'

# Load simple KEY=VALUE entries from the root .env file. Existing shell variables win.
$envFile = Join-Path $root '.env'
if (Test-Path $envFile) {
    foreach ($rawLine in Get-Content -LiteralPath $envFile) {
        $line = $rawLine.Trim()
        if (-not $line -or $line.StartsWith('#')) { continue }
        $equals = $line.IndexOf('=')
        if ($equals -le 0) { continue }

        $name = $line.Substring(0, $equals).Trim()
        $value = $line.Substring($equals + 1).Trim()
        if ($value.Length -ge 2 -and (($value.StartsWith('"') -and $value.EndsWith('"')) -or ($value.StartsWith("'") -and $value.EndsWith("'")))) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        if ($name -match '^[A-Za-z_][A-Za-z0-9_]*$' -and [string]::IsNullOrEmpty([Environment]::GetEnvironmentVariable($name, 'Process'))) {
            [Environment]::SetEnvironmentVariable($name, $value, 'Process')
        }
    }
}

# Find Java on PATH, then common JDK / Android Studio locations.
$javaCommand = Get-Command java.exe -ErrorAction SilentlyContinue
if (-not $javaCommand) {
    $javaCandidates = @()
    if ($env:JAVA_HOME) { $javaCandidates += (Join-Path $env:JAVA_HOME 'bin\java.exe') }
    $javaCandidates += (Join-Path $env:ProgramFiles 'Android\Android Studio\jbr\bin\java.exe')
    $javaCandidates += (Join-Path $env:ProgramFiles 'Eclipse Adoptium\jdk-17\bin\java.exe')
    foreach ($candidate in $javaCandidates) {
        if ($candidate -and (Test-Path $candidate)) {
            $javaHome = Split-Path (Split-Path $candidate -Parent) -Parent
            $env:JAVA_HOME = $javaHome
            $env:Path = "$(Split-Path $candidate -Parent);$env:Path"
            $javaCommand = Get-Command java.exe -ErrorAction SilentlyContinue
            if ($javaCommand) { break }
        }
    }
}
if (-not $javaCommand) {
    Write-Host 'Java was not found.' -ForegroundColor Red
    Write-Host 'Install a JDK (Java 17 or newer), set JAVA_HOME, then run this script again.'
    exit 1
}

# Find Maven on PATH, then common user-installed Maven folders.
$mavenCommand = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if (-not $mavenCommand) { $mavenCommand = Get-Command mvn -ErrorAction SilentlyContinue }
$mavenPath = if ($mavenCommand) { $mavenCommand.Source } else { $null }
if (-not $mavenPath) {
    $mavenFolders = @()
    if ($env:LOCALAPPDATA) { $mavenFolders += (Join-Path $env:LOCALAPPDATA 'Programs\Apache') }
    $mavenFolders += (Join-Path $env:ProgramFiles 'Apache\Maven')
    foreach ($folder in $mavenFolders) {
        if (-not (Test-Path $folder)) { continue }
        $candidateFiles = Get-ChildItem -Path $folder -Filter 'mvn.cmd' -File -Recurse -ErrorAction SilentlyContinue
        if ($candidateFiles) {
            $mavenPath = ($candidateFiles | Sort-Object FullName -Descending | Select-Object -First 1).FullName
            break
        }
    }
}
if (-not $mavenPath) {
    Write-Host 'Maven was not found.' -ForegroundColor Red
    Write-Host 'Install Apache Maven 3.9+ and reopen the terminal, or add Maven bin to PATH.'
    Write-Host 'If Maven is already installed under %LOCALAPPDATA%\Programs\Apache, check that it contains a bin\mvn.cmd file.'
    exit 1
}

if ([string]::IsNullOrWhiteSpace($env:MONGODB_URI)) {
    $env:MONGODB_URI = 'mongodb://localhost:27017/nightmarket'
    Write-Host 'MONGODB_URI is not set; using local MongoDB at mongodb://localhost:27017/nightmarket.' -ForegroundColor Yellow
    Write-Host 'To use MongoDB Atlas, copy .env.example to .env and set MONGODB_URI there.'
} else {
    Write-Host 'A MongoDB connection URI is configured (value hidden).' -ForegroundColor Green
}
if ([string]::IsNullOrWhiteSpace($env:PORT)) { $env:PORT = '8080' }
if ([string]::IsNullOrWhiteSpace($env:SEED_DATA_ENABLED)) { $env:SEED_DATA_ENABLED = 'true' }

Write-Host 'Starting NightMarket Spring Boot backend...' -ForegroundColor Cyan
Write-Host "Backend directory: $backend"
Write-Host 'Health endpoint: http://localhost:8080/api/health'
Push-Location $backend
try {
    & $mavenPath spring-boot:run
    $exitCode = $LASTEXITCODE
} finally {
    Pop-Location
}
if ($null -eq $exitCode) { $exitCode = 1 }
exit $exitCode
