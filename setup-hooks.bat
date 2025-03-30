@echo off
:: Script to set up Git hooks for the project on Windows

echo Setting up Git hooks...

:: Get project root
for /f "tokens=*" %%a in ('git rev-parse --show-toplevel') do set PROJECT_ROOT=%%a
cd %PROJECT_ROOT%

:: Create hooks directory if it doesn't exist
if not exist .git\hooks mkdir .git\hooks

:: Create post-commit hook
echo Creating post-commit hook...
(
echo #!/bin/bash
echo # Git post-commit hook to run database migrations after commit
echo.
echo # Get project root directory
echo PROJECT_ROOT=$(git rev-parse --show-toplevel^)
echo cd "$PROJECT_ROOT"
echo.
echo # Check if any migration files were changed in this commit
echo MIGRATION_FILES_CHANGED=$(git diff-tree --no-commit-id --name-only -r HEAD ^| grep -E 'src/main/resources/db/migration/.*\.sql$'^)
echo.
echo if [ -n "$MIGRATION_FILES_CHANGED" ]; then
echo   echo "Migration files changed in this commit. Running migrations..."
echo.
echo   # Determine OS and run appropriate script
echo   if [[ "$OSTYPE" == "msys" ^|^| "$OSTYPE" == "win32" ]]; then
echo     # Windows environment
echo     ./db-maven.bat migrate
echo   else
echo     # Unix-like environment
echo     ./db-maven.sh migrate
echo   fi
echo.
echo   # Check if migrations were successful
echo   if [ $? -eq 0 ]; then
echo     echo "✅ Migrations executed successfully"
echo   else
echo     echo "❌ Migration failed. Please fix issues and run migrations manually."
echo   fi
echo else
echo   echo "No migration files changed. Skipping migrations."
echo fi
) > .git\hooks\post-commit

echo Git hooks set up successfully!
echo Now database migrations will run automatically after commits that change migration files.