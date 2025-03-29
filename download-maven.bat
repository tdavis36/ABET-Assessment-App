@echo off
setlocal enabledelayedexpansion

:: --- Configuration ---
:: Maven version to download
set "MAVEN_VERSION=3.9.9"
:: Base Maven ZIP filename
set "MAVEN_ZIP=apache-maven-%MAVEN_VERSION%-bin.zip"
:: Download URL for Maven
set "DOWNLOAD_URL=https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/%MAVEN_ZIP%"

echo.
echo ================================
echo  Apache Maven %MAVEN_VERSION%
echo ================================
echo.

:: Prompt the user for a directory in which to download the file
set "DEFAULT_DIR=%USERPROFILE%"
echo Enter the directory where you want to store the Maven zip file.
echo Press [Enter] to use "%USERPROFILE%".
set /p "DOWNLOAD_DIR=Download directory: "
if "%DOWNLOAD_DIR%"=="" set "DOWNLOAD_DIR=%USERPROFILE%"

:: Create the directory if necessary
if not exist "%DOWNLOAD_DIR%" (
    mkdir "%DOWNLOAD_DIR%"
)

:: Compute the full path to where we'll save the Maven zip
set "DOWNLOAD_PATH=%DOWNLOAD_DIR%\%MAVEN_ZIP%"

echo.
echo Downloading Maven to:
echo   %DOWNLOAD_PATH%
echo.

:: Download the Maven binary zip
curl "%DOWNLOAD_URL%" --output "%DOWNLOAD_PATH%"

echo.
echo Extracting Maven...
tar -xf "%DOWNLOAD_PATH%" -C "%USERPROFILE%"

echo.
echo Download completed. Extracted files are now in:
echo   %DOWNLOAD_DIR%\apache-maven-%MAVEN_VERSION%
del %DOWNLOAD_DIR%\%MAVEN_ZIP%
echo.

pause
endlocal
