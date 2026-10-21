@echo off
setlocal
<<<<<<< HEAD
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-backend.ps1"
exit /b %ERRORLEVEL%
=======
cd /d "%~dp0backend"
where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven is not installed or not on PATH. Install Maven 3.9+ and Java 17+, then retry.
  exit /b 1
)
mvn spring-boot:run
>>>>>>> 39aa8c2ca1fa8976c6cac2d492cd608848470eec
