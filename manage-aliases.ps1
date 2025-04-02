# PowerShell script to manage database command aliases
# Usage: .\manage-aliases.ps1 [-Action <install|remove>]
# Example: .\manage-aliases.ps1 -Action install
# Example: .\manage-aliases.ps1 -Action remove

param (
    [Parameter()]
    [ValidateSet('install', 'remove')]
    [string]$Action = 'install'
)

# Text colors
$Green = @{ ForegroundColor = 'Green' }
$Yellow = @{ ForegroundColor = 'Yellow' }
$Red = @{ ForegroundColor = 'Red' }
$Cyan = @{ ForegroundColor = 'Cyan' }

# Get the project directory
$ProjectDir = $PSScriptRoot

# Function to install aliases
function Install-Aliases {
    Write-Host "Setting up database command aliases..." @Cyan

    # Check if PowerShell profile exists, create it if not
    if (!(Test-Path -Path $PROFILE)) {
        Write-Host "PowerShell profile does not exist. Creating new profile..." @Yellow
        New-Item -Path $PROFILE -ItemType File -Force | Out-Null
    }

    # Define the aliases content
    $AliasesContent = @"

# Database management aliases for the project
function dbstart { & "$ProjectDir\db-maven.bat" start `$args }
function dbstop { & "$ProjectDir\db-maven.bat" stop `$args }
function dbsync { & "$ProjectDir\db-maven.bat" sync `$args }
function dbrestart { & "$ProjectDir\db-maven.bat" restart `$args }
function dbmigrate { & "$ProjectDir\db-maven.bat" migrate `$args }
function dbinfo { & "$ProjectDir\db-maven.bat" info `$args }
function dbsetup { & "$ProjectDir\db-maven.bat" setup `$args }
function dbimport { & "$ProjectDir\db-maven.bat" import `$args }

# Function to connect to MariaDB via Docker container
function Connect-MariaDB {
    param(
        [string]`$User = "root",
        [switch]`$Password
    )

    `$PasswordArg = if (`$Password) { "-p" } else { "" }
    docker exec -it java_project_db mariadb -u `$User `$PasswordArg
}

# Create user-friendly aliases
Set-Alias -Name mariadb-connect -Value Connect-MariaDB

# Usage information function
function Show-DbCommands {
    Write-Host "Database management commands available:" -ForegroundColor Cyan
    Write-Host "  dbstart   - Start the database container" -ForegroundColor Cyan
    Write-Host "  dbstop    - Stop the database container" -ForegroundColor Cyan
    Write-Host "  dbsync    - Sync database schema" -ForegroundColor Cyan
    Write-Host "  dbrestart - Restart database and sync schema" -ForegroundColor Cyan
    Write-Host "  dbmigrate - Run database migrations" -ForegroundColor Cyan
    Write-Host "  dbinfo    - Show migration status" -ForegroundColor Cyan
    Write-Host "  dbsetup   - Interactive database setup" -ForegroundColor Cyan
    Write-Host "  dbimport  - Import CSV file into database" -ForegroundColor Cyan
    Write-Host "  mariadb-connect - Connect to MariaDB container" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Example: dbimport --csv=data.csv --table=customers" -ForegroundColor Cyan
}

# Create an alias for the help command
Set-Alias -Name dbhelp -Value Show-DbCommands
"@

    # Check if aliases already exist
    $ProfileContent = Get-Content -Path $PROFILE -Raw -ErrorAction SilentlyContinue
    if ($ProfileContent -match "Database management aliases for the project") {
        Write-Host "Aliases are already defined in your PowerShell profile" @Yellow
        Write-Host "Updating existing aliases..." @Yellow

        # Create a temporary file
        $TempFile = New-TemporaryFile

        # Remove existing aliases section and write to temp file
        $ProfileContent -replace "(?s)`n# Database management aliases for the project.*?Set-Alias -Name dbhelp -Value Show-DbCommands", "" |
                Set-Content -Path $TempFile

        # Add the updated aliases to the end
        Add-Content -Path $TempFile -Value $AliasesContent

        # Replace the original file
        Copy-Item -Path $TempFile -Destination $PROFILE -Force
        Remove-Item -Path $TempFile
    }
    else {
        # Append aliases to profile
        Add-Content -Path $PROFILE -Value $AliasesContent
        Write-Host "Aliases added to your PowerShell profile" @Green
    }

    # Make sure the script is executable
    $ScriptPath = Join-Path -Path $ProjectDir -ChildPath "db-maven.bat"
    if (Test-Path $ScriptPath) {
        # No need to set executable permissions in Windows, but verify the file exists
        Write-Host "Verified db-maven.bat exists" @Green
    }
    else {
        Write-Host "Warning: db-maven.bat not found in $ProjectDir" @Yellow
    }

    Write-Host "To use the aliases in the current session, run: . `$PROFILE" @Green
    Write-Host "Or start a new PowerShell window." @Green
    Write-Host "You can view available commands by typing 'dbhelp'" @Cyan

    # Ask if user wants to apply changes to current session
    $Response = Read-Host "Do you want to apply these changes to your current session? (y/n)"
    if ($Response -eq "y" -or $Response -eq "Y") {
        . $PROFILE
        Write-Host "Aliases are now available in the current session!" @Green
        Write-Host "Type 'dbhelp' to see available commands" @Cyan
    }
}

# Function to remove aliases
function Remove-Aliases {
    Write-Host "Removing database command aliases..." @Cyan

    # Check if PowerShell profile exists
    if (Test-Path -Path $PROFILE) {
        $ProfileContent = Get-Content -Path $PROFILE -Raw
        if ($ProfileContent -match "Database management aliases for the project") {
            # Create a cleaned version of the profile without the aliases section
            $CleanedProfile = $ProfileContent -replace "(?s)`n# Database management aliases for the project.*?Set-Alias -Name dbhelp -Value Show-DbCommands", ""

            # Write the cleaned content back to the profile
            Set-Content -Path $PROFILE -Value $CleanedProfile

            Write-Host "Database aliases removed from PowerShell profile successfully!" @Green
        } else {
            Write-Host "No database aliases found in your PowerShell profile." @Yellow
        }
    } else {
        Write-Host "PowerShell profile not found." @Red
    }

    # Reset current session
    Write-Host "Cleaning database aliases from current session..." @Cyan

    # Remove aliases
    $DbAliases = @('dbstart', 'dbstop', 'dbsync', 'dbrestart', 'dbmigrate', 'dbinfo', 'dbsetup', 'dbimport', 'dbhelp', 'mariadb-connect')
    foreach ($alias in $DbAliases) {
        if (Test-Path "Alias:\$alias" -ErrorAction SilentlyContinue) {
            Remove-Item "Alias:\$alias" -Force -ErrorAction SilentlyContinue
            Write-Host "Removed alias: $alias" @Green
        }
    }

    # Remove functions
    $DbFunctions = @(
        'dbstart', 'dbstop', 'dbsync', 'dbrestart', 'dbmigrate', 'dbinfo', 'dbsetup', 'dbimport',
        'Show-DbCommands', 'Connect-MariaDB'
    )
    foreach ($function in $DbFunctions) {
        if (Get-Command $function -ErrorAction SilentlyContinue) {
            Remove-Item "function:$function" -Force -ErrorAction SilentlyContinue
            Write-Host "Removed function: $function" @Green
        }
    }

    Write-Host "Aliases have been removed. You may need to restart PowerShell for all changes to take effect." @Green
}

# Main script execution
switch ($Action) {
    'install' {
        Install-Aliases
    }
    'remove' {
        Remove-Aliases
    }
}