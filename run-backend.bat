@echo off
setlocal
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-backend.ps1"
if errorlevel 1 (
  cd /d "%~dp0backend"
  if exist mvnw.cmd (
    call mvnw.cmd spring-boot:run
  ) else (
    mvn spring-boot:run
  )
)
exit /b %ERRORLEVEL%
