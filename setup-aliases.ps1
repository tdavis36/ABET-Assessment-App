# Setup-DbAliases.ps1
# Script to set up database command aliases for PowerShell

# Get the project directory
$ProjectDir = $PSScriptRoot

# Text formatting
$Green = "`e[32m"
$Yellow = "`e[33m"
$Cyan = "`e[36m"
$Reset = "`e[0m"

Write-Host "${Green}Setting up database command aliases for PowerShell...${Reset}"

# Define the function content to add to profile
$FunctionContent = @"
# Database management functions for the project
function dbstart { & "$ProjectDir\db-maven.bat" start `$args }
function dbstop { & "$ProjectDir\db-maven.bat" stop `$args }
function dbsync { & "$ProjectDir\db-maven.bat" sync `$args }
function dbrestart { & "$ProjectDir\db-maven.bat" restart `$args }
function dbmigrate { & "$ProjectDir\db-maven.bat" migrate `$args }
function dbinfo { & "$ProjectDir\db-maven.bat" info `$args }
function dbsetup { & "$ProjectDir\db-maven.bat" setup `$args }
function dbimport { & "$ProjectDir\db-maven.bat" import `$args }

# Usage information
function Show-DbCommands {
    Write-Host "${Cyan}Database management commands available:${Reset}"
    Write-Host "${Cyan}  dbstart   - Start the database container${Reset}"
    Write-Host "${Cyan}  dbstop    - Stop the database container${Reset}"
    Write-Host "${Cyan}  dbsync    - Sync database schema${Reset}"
    Write-Host "${Cyan}  dbrestart - Restart database and sync schema${Reset}"
    Write-Host "${Cyan}  dbmigrate - Run database migrations${Reset}"
    Write-Host "${Cyan}  dbinfo    - Show migration status${Reset}"
    Write-Host "${Cyan}  dbsetup   - Interactive database setup${Reset}"
    Write-Host "${Cyan}  dbimport  - Import CSV file into database${Reset}"
    Write-Host ""
    Write-Host "${Cyan}Example: dbimport --csv=data.csv --table=customers${Reset}"
}

# Display commands on startup
Show-DbCommands
"@

# Check if user has a PowerShell profile
if (!(Test-Path -Path $PROFILE)) {
    Write-Host "${Yellow}PowerShell profile does not exist. Creating it...${Reset}"
    New-Item -Path $PROFILE -ItemType File -Force | Out-Null
}

# Check if functions already exist
$ProfileContent = Get-Content -Path $PROFILE -Raw -ErrorAction SilentlyContinue
if ($ProfileContent -match "Database management functions for the project") {
    Write-Host "${Yellow}Database functions are already defined in your PowerShell profile.${Reset}"
    Write-Host "${Yellow}Updating existing functions...${Reset}"

    # Create new profile content without the existing functions
    $NewContent = $ProfileContent -replace "(?s)# Database management functions for the project.*?Show-DbCommands\r?\n", ""

    # Add the updated functions to the end
    $NewContent += "`n$FunctionContent"

    # Save the updated profile
    $NewContent | Set-Content -Path $PROFILE -Force
} else {
    # Append functions to profile
    Add-Content -Path $PROFILE -Value "`n$FunctionContent"
    Write-Host "${Green}Functions added to PowerShell profile at: $PROFILE${Reset}"
}

# Create a temporary module to load the functions for the current session
$TempModule = New-Item -Path "$env:TEMP\DbFunctions.psm1" -Force
Set-Content -Path $TempModule -Value $FunctionContent

# Import the module to make functions available in current session
Import-Module $TempModule -Force -DisableNameChecking

Write-Host "${Green}Database aliases have been set up and are available now.${Reset}"
Write-Host "${Green}The aliases will be loaded automatically the next time you start PowerShell.${Reset}"

# Make the script executable
Set-ItemProperty -Path "$ProjectDir\db-maven.bat" -Name IsReadOnly -Value $false