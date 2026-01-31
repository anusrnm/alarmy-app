@echo off
setlocal enabledelayedexpansion

:: Reminders App Build Script for Windows
:: Usage: build.bat [command]
:: Commands: debug, release, clean, install, test

set COMMAND=%1

if "%COMMAND%"=="" set COMMAND=debug

echo.
echo ========================================
echo   Reminders App Build Script
echo ========================================
echo.

if "%COMMAND%"=="debug" goto :build_debug
if "%COMMAND%"=="release" goto :build_release
if "%COMMAND%"=="clean" goto :clean
if "%COMMAND%"=="install" goto :install
if "%COMMAND%"=="test" goto :test
if "%COMMAND%"=="help" goto :help

echo Unknown command: %COMMAND%
goto :help

:build_debug
echo Building DEBUG APK...
echo.
call gradlew.bat assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo BUILD FAILED!
    exit /b 1
)
echo.
echo ========================================
echo   BUILD SUCCESSFUL!
echo   APK: app\build\outputs\apk\debug\app-debug.apk
echo ========================================
goto :end

:build_release
echo Building RELEASE APK...
echo.
call gradlew.bat assembleRelease
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo BUILD FAILED!
    exit /b 1
)
echo.
echo ========================================
echo   BUILD SUCCESSFUL!
echo   APK: app\build\outputs\apk\release\app-release-unsigned.apk
echo ========================================
goto :end

:clean
echo Cleaning build files...
echo.
call gradlew.bat clean
echo.
echo Clean complete!
goto :end

:install
echo Building and installing DEBUG APK on connected device...
echo.
call gradlew.bat installDebug
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo INSTALL FAILED! Make sure a device is connected.
    exit /b 1
)
echo.
echo ========================================
echo   INSTALLED SUCCESSFULLY!
echo ========================================
goto :end

:test
echo Running tests...
echo.
call gradlew.bat test
echo.
echo Tests complete!
goto :end

:help
echo.
echo Usage: build.bat [command]
echo.
echo Commands:
echo   debug    - Build debug APK (default)
echo   release  - Build release APK
echo   clean    - Clean build files
echo   install  - Build and install on connected device
echo   test     - Run unit tests
echo   help     - Show this help message
echo.
goto :end

:end
endlocal
