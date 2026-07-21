$ErrorActionPreference = 'Stop'

$mavenVersion = "3.9.6"
$mavenZip = "apache-maven-$mavenVersion-bin.zip"
$mavenUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$mavenVersion/$mavenZip"
$mavenDir = "apache-maven-$mavenVersion"

if (-Not (Test-Path "$mavenDir\bin\mvn.cmd")) {
    Write-Host "Downloading Maven..." -ForegroundColor Cyan
    Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZip
    
    Write-Host "Extracting Maven..." -ForegroundColor Cyan
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    [System.IO.Compression.ZipFile]::ExtractToDirectory((Resolve-Path $mavenZip).Path, (Resolve-Path .).Path)
    
    Remove-Item $mavenZip
}

$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
$mvnCmd = ".\$mavenDir\bin\mvn.cmd"

Write-Host "Starting Spring Boot Application..." -ForegroundColor Green
& $mvnCmd spring-boot:run
