@echo off
setlocal
cd /d "%~dp0backend"
where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven is not installed or not on PATH. Install Maven 3.9+ and Java 17+, then retry.
  exit /b 1
)
mvn spring-boot:run
