@echo off
:: Enhanced db-maven script with interactive input and CSV import for Windows
:: Usage: db-maven.bat [operation] [options]
::
:: Operations:
::   start    - Start the database container
::   stop     - Stop the database container
::   sync     - Sync the database schema (legacy method)
::   migrate  - Run Flyway migrations
::   info     - Show Flyway migration status
::   restart  - Restart the database container and apply migrations
::   setup    - Interactive setup to create .env file
::   import   - Import CSV file(s) into database
::
:: Options:
::   --db-name=NAME     - Database name
::   --db-user=USER     - Database username
::   --db-pass=PASS     - Database password
::   --db-root=ROOT     - Database root password
::   --env=ENV          - Environment (dev/local)
::   --csv=FILE         - CSV file to import
::   --table=NAME       - Table to import CSV into
::   --delimiter=CHAR   - CSV delimiter (default: ,)
::   --skip-header      - Skip first row in CSV

setlocal enabledelayedexpansion

:: Default operation
set OPERATION=migrate
if not "%~1"=="" set OPERATION=%~1

:: Default properties
set MAVEN_PROFILES=
set MAVEN_PROPS=

:: Variables to store database credentials
set DB_NAME=
set DB_USER=
set DB_PASS=
set DB_ROOT=
set ENV_TYPE=

:: CSV import settings
set CSV_FILE=
set TARGET_TABLE=
set CSV_DELIMITER=,
set SKIP_HEADER=false

:: Check if operation is setup
if "%OPERATION%"=="setup" (
  call :interactive_setup
  exit /b 0
)

:: Check if operation is import
if "%OPERATION%"=="import" (
  goto :parse_args
)

:: Parse additional arguments
:parse_args
shift
if "%~1"=="" goto :load_env

set ARG=%~1

if "!ARG:~0,10!"=="--db-name=" (
  set DB_NAME=!ARG:~10!
  set MAVEN_PROPS=!MAVEN_PROPS! -Ddb.name=!DB_NAME!
  goto :parse_args
)

if "!ARG:~0,10!"=="--db-user=" (
  set DB_USER=!ARG:~10!
  set MAVEN_PROPS=!MAVEN_PROPS! -Ddb.username=!DB_USER!
  goto :parse_args
)

if "!ARG:~0,10!"=="--db-pass=" (
  set DB_PASS=!ARG:~10!
  set MAVEN_PROPS=!MAVEN_PROPS! -Ddb.password=!DB_PASS!
  goto :parse_args
)

if "!ARG:~0,10!"=="--db-root=" (
  set DB_ROOT=!ARG:~10!
  set MAVEN_PROPS=!MAVEN_PROPS! -Ddb.root.password=!DB_ROOT!
  goto :parse_args
)

if "!ARG:~0,6!"=="--env=" (
  set ENV_TYPE=!ARG:~6!
  set MAVEN_PROFILES=!MAVEN_PROFILES!,!ENV_TYPE!
  goto :parse_args
)

if "!ARG:~0,6!"=="--csv=" (
  set CSV_FILE=!ARG:~6!
  goto :parse_args
)

if "!ARG:~0,8!"=="--table=" (
  set TARGET_TABLE=!ARG:~8!
  goto :parse_args
)

if "!ARG:~0,12!"=="--delimiter=" (
  set CSV_DELIMITER=!ARG:~12!
  goto :parse_args
)

if "!ARG!"=="--skip-header" (
  set SKIP_HEADER=true
  goto :parse_args
)

:: Unknown argument, just pass to Maven
set MAVEN_PROPS=!MAVEN_PROPS! !ARG!
goto :parse_args

:load_env
:: Source .env file if it exists for regular operations
if exist .env (
  echo Loading environment from .env file
  for /f "tokens=1,2 delims==" %%a in (.env) do (
    if not "%%a"=="" (
      if "%%a"=="DB_ROOT_PASSWORD" (
        if "!DB_ROOT!"=="" set DB_ROOT=%%b
        set DB_ROOT_PASSWORD=%%b
        set MAVEN_PROPS=!MAVEN_PROPS! -Ddb.root.password=%%b
      )
      if "%%a"=="DB_NAME" (
        if "!DB_NAME!"=="" set DB_NAME=%%b
        set DB_NAME=%%b
        set MAVEN_PROPS=!MAVEN_PROPS! -Ddb.name=%%b
      )
      if "%%a"=="DB_USERNAME" (
        if "!DB_USER!"=="" set DB_USER=%%b
        set DB_USERNAME=%%b
        set MAVEN_PROPS=!MAVEN_PROPS! -Ddb.username=%%b
      )
      if "%%a"=="DB_PASSWORD" (
        if "!DB_PASS!"=="" set DB_PASS=%%b
        set DB_PASSWORD=%%b
        set MAVEN_PROPS=!MAVEN_PROPS! -Ddb.password=%%b
      )
      if "%%a"=="ENV_TYPE" (
        if "!ENV_TYPE!"=="" set ENV_TYPE=%%b
        set MAVEN_PROFILES=!MAVEN_PROFILES!,!ENV_TYPE!
      )
    )
  )

  :: Make environment variables available to processes like Docker
  setx DB_ROOT_PASSWORD !DB_ROOT_PASSWORD! >nul 2>&1
  setx DB_NAME !DB_NAME! >nul 2>&1
  setx DB_USERNAME !DB_USERNAME! >nul 2>&1
  setx DB_PASSWORD !DB_PASSWORD! >nul 2>&1
)

:: Check if we need to handle import operation
if "%OPERATION%"=="import" (
  call :import_csv
  exit /b 0
)

:: Set operation-specific profiles
if "%OPERATION%"=="start" (
  set MAVEN_PROFILES=%MAVEN_PROFILES%,db-start
)
if "%OPERATION%"=="stop" (
  set MAVEN_PROFILES=%MAVEN_PROFILES%,db-stop
)
if "%OPERATION%"=="sync" (
  set MAVEN_PROFILES=%MAVEN_PROFILES%,db-sync
)
if "%OPERATION%"=="migrate" (
  set MAVEN_PROFILES=%MAVEN_PROFILES%,db-migrate
)
if "%OPERATION%"=="info" (
  set MAVEN_PROFILES=%MAVEN_PROFILES%,db-info
)
if "%OPERATION%"=="restart" (
  set MAVEN_PROFILES=%MAVEN_PROFILES%,db-stop,db-start,db-migrate
)

:: Remove initial comma if present
if "!MAVEN_PROFILES:~0,1!"=="," set MAVEN_PROFILES=!MAVEN_PROFILES:~1!

:: Execute Maven command
echo Running: mvn process-resources -P%MAVEN_PROFILES% %MAVEN_PROPS%
if exist mvnw.cmd (
  mvnw.cmd process-resources -P%MAVEN_PROFILES% %MAVEN_PROPS%
) else (
  mvn process-resources -P%MAVEN_PROFILES% %MAVEN_PROPS%
)

exit /b 0

:: Interactive setup function
:interactive_setup
  echo === Interactive Database Setup ===

  :: Check if .env exists and offer to load defaults
  if exist .env (
    echo Existing .env file found. Load values as defaults? (y/n)
    set /p load_defaults="> "

    if /i "!load_defaults!"=="y" (
      for /f "tokens=1,2 delims==" %%a in (.env) do (
        if "%%a"=="DB_ROOT_PASSWORD" set DB_ROOT=%%b
        if "%%a"=="DB_NAME" set DB_NAME=%%b
        if "%%a"=="DB_USERNAME" set DB_USER=%%b
        if "%%a"=="DB_PASSWORD" set DB_PASS=%%b
        if "%%a"=="ENV_TYPE" set ENV_TYPE=%%b
      )
    )
  )

  :: Database name
  if "!DB_NAME!"=="" set DEFAULT_DB_NAME=your_db_name
  if not "!DB_NAME!"=="" set DEFAULT_DB_NAME=!DB_NAME!
  echo Enter database name: [!DEFAULT_DB_NAME!]
  set /p input_db_name="> "
  if "!input_db_name!"=="" (
    set DB_NAME=!DEFAULT_DB_NAME!
  ) else (
    set DB_NAME=!input_db_name!
  )

  :: Database username
  if "!DB_USER!"=="" set DEFAULT_DB_USER=dbuser
  if not "!DB_USER!"=="" set DEFAULT_DB_USER=!DB_USER!
  echo Enter database username: [!DEFAULT_DB_USER!]
  set /p input_db_user="> "
  if "!input_db_user!"=="" (
    set DB_USER=!DEFAULT_DB_USER!
  ) else (
    set DB_USER=!input_db_user!
  )

  :: Database password
  if "!DB_PASS!"=="" set DEFAULT_DB_PASS=dbuserpassword
  if not "!DB_PASS!"=="" set DEFAULT_DB_PASS=!DB_PASS!
  echo Enter database password: [!DEFAULT_DB_PASS!]
  set /p input_db_pass="> "
  if "!input_db_pass!"=="" (
    set DB_PASS=!DEFAULT_DB_PASS!
  ) else (
    set DB_PASS=!input_db_pass!
  )

  :: Root password
  if "!DB_ROOT!"=="" set DEFAULT_DB_ROOT=rootpassword
  if not "!DB_ROOT!"=="" set DEFAULT_DB_ROOT=!DB_ROOT!
  echo Enter database root password: [!DEFAULT_DB_ROOT!]
  set /p input_db_root="> "
  if "!input_db_root!"=="" (
    set DB_ROOT=!DEFAULT_DB_ROOT!
  ) else (
    set DB_ROOT=!input_db_root!
  )

  :: Environment
  if "!ENV_TYPE!"=="" set DEFAULT_ENV_TYPE=dev
  if not "!ENV_TYPE!"=="" set DEFAULT_ENV_TYPE=!ENV_TYPE!
  echo Enter environment (dev/local): [!DEFAULT_ENV_TYPE!]
  set /p input_env="> "
  if "!input_env!"=="" (
    set ENV_TYPE=!DEFAULT_ENV_TYPE!
  ) else (
    set ENV_TYPE=!input_env!
  )

  :: Confirm values
  echo.
  echo Please confirm the following settings:
  echo Database name: !DB_NAME!
  echo Database username: !DB_USER!
  echo Database password: !DB_PASS!
  echo Root password: !DB_ROOT!
  echo Environment: !ENV_TYPE!
  echo.

  echo Save these settings to .env file? (y/n)
  set /p save_settings="> "

  if /i "!save_settings!"=="y" (
    call :create_env_file
    echo.
    echo Settings saved! You can now run other commands.
    echo Example: db-maven.bat start
  ) else (
    echo.
    echo Settings not saved. Exiting setup.
  )

  goto :eof

:: Function to create .env file
:create_env_file
  :: Check if .env is in .gitignore
  if not exist .gitignore (
    echo Creating .gitignore file
    echo .env> .gitignore
  ) else (
    findstr /C:".env" .gitignore >nul
    if errorlevel 1 (
      echo Warning: .env is not in .gitignore. Adding it now.
      echo .env>> .gitignore
    )
  )

  :: Create .env file with passed parameters
  echo Creating .env file with your settings
  (
    echo # Database configuration created interactively
    echo # Created on %DATE% %TIME%
    echo DB_ROOT_PASSWORD=!DB_ROOT!
    echo DB_NAME=!DB_NAME!
    echo DB_USERNAME=!DB_USER!
    echo DB_PASSWORD=!DB_PASS!
    echo ENV_TYPE=!ENV_TYPE!
  ) > .env

  echo .env file created successfully
  goto :eof

:: Function to import CSV file
:import_csv
  :: Check if required parameters are provided
  if "!CSV_FILE!"=="" (
    echo Error: No CSV file specified. Use --csv=FILE
    exit /b 1
  )

  if "!TARGET_TABLE!"=="" (
    echo Error: No target table specified. Use --table=NAME
    exit /b 1
  )

  if not exist "!CSV_FILE!" (
    echo Error: CSV file not found: !CSV_FILE!
    exit /b 1
  )

  :: Create a temporary SQL file
  set TEMP_SQL=%TEMP%\import_%RANDOM%.sql

  echo Preparing to import CSV data...

  :: Copy the file to the Docker container
  echo Copying CSV file to Docker container...
  for %%F in ("!CSV_FILE!") do set CSV_FILENAME=%%~nxF
  docker cp "!CSV_FILE!" "java_project_db:/tmp/!CSV_FILENAME!"

  :: Create header line for temporary table
  for /f "usebackq tokens=*" %%a in ("!CSV_FILE!") do (
    set HEADER_LINE=%%a
    goto :header_found
  )
  :header_found

  :: Clean the header line and prepare column definitions
  set COLUMNS=
  for %%i in (!HEADER_LINE!) do (
    if "!COLUMNS!"=="" (
      set COLUMNS=%%i VARCHAR(255)
    ) else (
      set COLUMNS=!COLUMNS!, %%i VARCHAR(255)
    )
  )

  :: Generate the SQL import script
  echo USE !DB_NAME!;> "!TEMP_SQL!"
  echo.>> "!TEMP_SQL!"
  echo -- Create temporary table to handle imports>> "!TEMP_SQL!"
  echo DROP TABLE IF EXISTS temp_import;>> "!TEMP_SQL!"
  echo CREATE TABLE temp_import (>> "!TEMP_SQL!"
  echo   !COLUMNS!>> "!TEMP_SQL!"
  echo );>> "!TEMP_SQL!"
  echo.>> "!TEMP_SQL!"
  echo -- Import the CSV file>> "!TEMP_SQL!"
  echo LOAD DATA INFILE '/tmp/!CSV_FILENAME!'>> "!TEMP_SQL!"
  echo INTO TABLE temp_import>> "!TEMP_SQL!"
  echo FIELDS TERMINATED BY '!CSV_DELIMITER!'>> "!TEMP_SQL!"
  echo ENCLOSED BY '"'>> "!TEMP_SQL!"
  echo LINES TERMINATED BY '\n'>> "!TEMP_SQL!"
  if "!SKIP_HEADER!"=="true" (
    echo IGNORE 1 ROWS>> "!TEMP_SQL!"
  )
  echo ;>> "!TEMP_SQL!"
  echo.>> "!TEMP_SQL!"
  echo -- Display the imported data for verification>> "!TEMP_SQL!"
  echo SELECT * FROM temp_import LIMIT 10;>> "!TEMP_SQL!"
  echo.>> "!TEMP_SQL!"
  echo -- Ask for confirmation before copying to real table>> "!TEMP_SQL!"
  echo -- You will need to manually run:>> "!TEMP_SQL!"
  echo -- INSERT INTO !TARGET_TABLE! SELECT * FROM temp_import;>> "!TEMP_SQL!"
  echo -- DROP TABLE temp_import;>> "!TEMP_SQL!"

  echo Executing import...
  docker exec -i java_project_db mysql -u "!DB_USER!" -p"!DB_PASS!" < "!TEMP_SQL!"

  if !ERRORLEVEL! equ 0 (
    echo CSV data imported to temporary table.
    echo To complete the import, connect to the database and run:
    echo INSERT INTO !TARGET_TABLE! SELECT * FROM temp_import;
    echo DROP TABLE temp_import;
  ) else (
    echo Import failed.
  )

  :: Clean up
  del "!TEMP_SQL!"
  goto :eof

endlocal