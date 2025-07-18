@echo off
echo ========================================
echo AECI MMU Companion - Server Setup
echo ========================================
echo.

REM Set ADB path
set "ADB_PATH=C:\Users\VanGuduza\AppData\Local\Android\Sdk\platform-tools"
set "PATH=%ADB_PATH%;%PATH%"

REM Check if PowerShell script exists
if not exist "automated_server_setup.ps1" (
    echo Error: automated_server_setup.ps1 not found!
    pause
    exit /b 1
)

echo Starting automated server setup...
echo.
echo Options:
echo 1. Basic setup (local network only)
echo 2. Setup with public IP
echo 3. Setup with domain name
echo.

set /p choice="Choose option (1-3): "

if "%choice%"=="1" (
    powershell -ExecutionPolicy Bypass -File "automated_server_setup.ps1"
) else if "%choice%"=="2" (
    set /p phoneip="Enter your phone's public IP: "
    powershell -ExecutionPolicy Bypass -File "automated_server_setup.ps1" -PhoneIP "%phoneip%"
) else if "%choice%"=="3" (
    set /p domain="Enter your domain name: "
    powershell -ExecutionPolicy Bypass -File "automated_server_setup.ps1" -Domain "%domain%"
) else (
    echo Invalid choice!
    pause
    exit /b 1
)

echo.
echo Setup completed! Check your phone's Termux app for progress.
pause 