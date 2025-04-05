#!/usr/bin/env python3
import os
import sys
import platform
import subprocess
import shutil
import tempfile
import time
import re
from pathlib import Path

# First check if required dependencies are installed
try:
    import click
except ImportError:
    print("The 'click' package is required. Installing it now...")
    try:
        subprocess.check_call([sys.executable, "-m", "pip", "install", "click"])
        import click
    except Exception as e:
        print(f"Error installing 'click': {e}")
        print("Please install it manually with: pip install click")
        sys.exit(1)

# Detect the project root directory
def find_project_root():
    """Find the project root directory by looking for pom.xml"""
    current = Path.cwd().resolve()
    while current != current.parent:
        if (current / "pom.xml").exists():
            return current
        current = current.parent
    return Path.cwd().resolve()

PROJECT_ROOT = find_project_root()

# Check if docker-compose exists
def docker_compose_command():
    """Returns the appropriate docker compose command based on what's installed"""
    # Check for Docker Compose V2 (docker compose)
    try:
        result = subprocess.run(
            ["docker", "compose", "version"],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )
        if result.returncode == 0:
            return ["docker", "compose"]
    except:
        pass

    # Check for docker-compose standalone
    compose_cmd = shutil.which("docker-compose")
    if compose_cmd:
        return [compose_cmd]

    # Default to docker compose and hope it works
    return ["docker", "compose"]

def detect_python_command():
    """
    Detect which Python command is available on Windows.
    For Windows, tries 'py' first, then falls back to 'python' or 'python3'.
    Returns the first working command.
    """
    import subprocess
    import shutil

    # For Windows, prioritize py launcher first
    if platform.system().lower() == 'windows':
        commands = ['py', 'python', 'python3']
    else:
        commands = ['python3', 'python', 'py']

    for cmd in commands:
        if shutil.which(cmd):
            try:
                # Verify command works by getting version
                result = subprocess.run([cmd, '--version'],
                                        capture_output=True,
                                        text=True,
                                        check=False)
                if result.returncode == 0:
                    print(f"Detected Python command: {cmd} ({result.stdout.strip()})")
                    return cmd
            except Exception:
                continue

    # Default fallback based on platform
    default_cmd = 'py' if platform.system().lower() == 'windows' else 'python3'
    print(f"Warning: No Python commands detected. Defaulting to '{default_cmd}'")
    return default_cmd


def get_os_specific_settings():
    """Get OS-specific settings for the current platform."""
    import platform
    import shutil
    import subprocess

    # Base settings
    settings = {
        'platform': platform.system().lower(),
    }

    # Detect Docker command
    docker_cmd = 'docker'
    if shutil.which(docker_cmd):
        settings['docker_cmd'] = docker_cmd
    else:
        print("Warning: Docker command not found in PATH. Some features may not work.")
        settings['docker_cmd'] = docker_cmd  # Set it anyway as a fallback

    # Set Docker Compose command based on platform
    if settings['platform'] == 'windows':
        # Windows typically uses 'docker-compose' (with hyphen)
        compose_cmd = 'docker-compose'
    else:
        # Modern Docker has 'docker compose' (without hyphen) as a subcommand
        compose_cmd = 'docker compose'

    # Store both versions of the compose command
    settings['compose_command'] = compose_cmd  # Keep for backward compatibility
    settings['docker_compose_cmd'] = compose_cmd  # Add the new key

    if settings['platform'] == 'windows':
        # Auto-detect Python command on Windows, default to 'py'
        settings['python_command'] = detect_python_command()

        # If somehow detect_python_command didn't return 'py' and no working command was found,
        # we'll explicitly set it to 'py' as requested
        if not shutil.which(settings['python_command']):
            settings['python_command'] = 'py'

        # Detect shell type on Windows (PowerShell or CMD)
        settings['shell_type'] = 'cmd'  # Default to cmd
        try:
            # Check if PowerShell is available
            ps_result = subprocess.run(['powershell', '-Command', 'echo $PSVersionTable.PSVersion.Major'],
                                       capture_output=True, text=True, check=False)
            if ps_result.returncode == 0 and ps_result.stdout.strip():
                settings['shell_type'] = 'powershell'
        except Exception:
            # If checking fails, stick with cmd
            pass

        # Other Windows-specific settings
        # Add any other Windows-specific settings here
    elif settings['platform'] == 'darwin':  # macOS
        settings['python_command'] = 'python3'
        # Add any other macOS-specific settings here
        settings['shell_type'] = 'bash'
    else:  # Linux and others
        settings['python_command'] = 'python3'
        # Add any other Linux-specific settings here
        settings['shell_type'] = 'bash'

    print(f"Using settings for {settings['platform']}: Python command = {settings['python_command']}, "
          f"Shell = {settings['shell_type']}, Docker command = {settings['docker_cmd']}, "
          f"Docker Compose command = {settings['docker_compose_cmd']}")
    return settings


OS_SETTINGS = get_os_specific_settings()

# Helper function to run shell commands
def run_command(cmd, shell=False, cwd=None, env=None, capture_output=True):
    """Run a command and return the output"""
    try:
        if env is None:
            env = os.environ.copy()

        if capture_output:
            if shell:
                result = subprocess.run(cmd, shell=True, check=True, text=True,
                                        stdout=subprocess.PIPE, stderr=subprocess.PIPE, cwd=cwd, env=env)
            else:
                result = subprocess.run(cmd, shell=False, check=True, text=True,
                                        stdout=subprocess.PIPE, stderr=subprocess.PIPE, cwd=cwd, env=env)
            return result.stdout
        else:
            if shell:
                result = subprocess.run(cmd, shell=True, check=True, cwd=cwd, env=env)
            else:
                result = subprocess.run(cmd, shell=False, check=True, cwd=cwd, env=env)
            return True
    except subprocess.CalledProcessError as e:
        click.echo(f"Command failed: {e}")
        if hasattr(e, 'stderr'):
            click.echo(f"Error output: {e.stderr}")
        return None

# Helper function to load environment variables from .env file
def load_env_file(env_file=None):
    """Load environment variables from .env file"""
    env_vars = {}

    if env_file is None:
        env_file = PROJECT_ROOT / ".env"

    if env_file.exists():
        with open(env_file, "r") as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith("#"):
                    try:
                        key, value = line.split("=", 1)
                        env_vars[key.strip()] = value.strip()
                    except ValueError:
                        # Skip malformed lines
                        pass

    return env_vars


def is_docker_running():
    """Check if Docker daemon is running and accessible."""
    import subprocess
    import shutil

    # First check if Docker is installed
    docker_cmd = OS_SETTINGS.get("docker_cmd", "docker")
    if not shutil.which(docker_cmd):
        print("Error: Docker is not installed or not in PATH.")
        return False

    try:
        # Use a simple Docker command to check if the daemon is running
        # 'docker info' is lightweight and will fail if the daemon isn't accessible
        result = subprocess.run(
            [docker_cmd, "info"],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            check=False,  # Don't raise exception as we're handling it
            timeout=10  # Add timeout to prevent hanging
        )

        # Check the return code
        if result.returncode != 0:
            # Print more helpful error based on stderr content
            if "Cannot connect to the Docker daemon" in result.stderr:
                print("Error: Docker daemon is not running.")
                print("Please start Docker Desktop and try again.")
            elif "permission denied" in result.stderr.lower():
                print("Error: Permission denied when connecting to Docker.")
                print("Make sure your user has permissions to access Docker or try running with elevated privileges.")
            else:
                print(f"Error connecting to Docker: {result.stderr.strip()}")
            return False

        return True

    except subprocess.TimeoutExpired:
        print("Error: Docker command timed out. Docker daemon may be hanging or unresponsive.")
        return False
    except Exception as e:
        print(f"Error checking Docker status: {str(e)}")
        return False


# Function to get database connection parameters
def get_db_connection_params():
    """Get database connection parameters from .env file or defaults"""
    env_vars = load_env_file()

    return {
        "db_name": env_vars.get("DB_NAME", "abetapp"),
        "db_username": env_vars.get("DB_USERNAME", "user"),
        "db_password": env_vars.get("DB_PASSWORD", ""),
        "db_root_password": env_vars.get("DB_ROOT_PASSWORD", "rootpassword"),
        "db_host": env_vars.get("DB_HOST", "localhost"),
        "db_port": env_vars.get("DB_PORT", "3306")
    }

# Database Docker operations
def docker_compose_operation(operation):
    """Run Docker Compose operations with error handling and status reporting."""
    import subprocess
    import os
    import tempfile
    import time
    import shutil

    # Check if Docker is installed and running
    if not is_docker_running():
        print("Docker is not available. Cannot perform Docker Compose operations.")
        return False

    # Get the Docker Compose command
    compose_cmd = OS_SETTINGS["docker_compose_cmd"]

    # Split the command into a list if it's a string with spaces (like 'docker compose')
    if isinstance(compose_cmd, str) and ' ' in compose_cmd:
        compose_cmd = compose_cmd.split()
    elif isinstance(compose_cmd, str):
        compose_cmd = [compose_cmd]  # Convert single string to a list

    # Generate a temporary docker-compose file with our configuration
    with tempfile.NamedTemporaryFile(delete=False, mode='w', suffix='.yml') as temp:
        temp_file = temp.name
        # Write the docker-compose configuration
        temp.write("""
version: '3'
services:
  db:
    container_name: java_project_db
    image: postgres:14
    environment:
      POSTGRES_DB: java_project
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - dbdata:/var/lib/postgresql/data
    restart: unless-stopped

volumes:
  dbdata:
""")

    try:
        # Perform the requested operation
        if operation == "start":
            print("Starting database container...")
            # Use proper list concatenation for commands
            cmd = compose_cmd + ["-f", temp_file, "up", "-d"]
            result = subprocess.run(cmd, check=True, text=True, capture_output=True)
            print("Database container started.")
            return True

        elif operation == "stop":
            print("Stopping database container...")
            cmd = compose_cmd + ["-f", temp_file, "down"]
            result = subprocess.run(cmd, check=True, text=True, capture_output=True)
            print("Database container stopped.")
            return True

        elif operation == "status":
            # Check if the container is running
            container_name = "java_project_db"
            try:
                result = subprocess.run(
                    [OS_SETTINGS["docker_cmd"], "ps", "-a", "--filter", f"name={container_name}", "--format",
                     "{{.Names}}"],
                    check=True, capture_output=True, text=True
                )
                if container_name in result.stdout:
                    status_result = subprocess.run(
                        [OS_SETTINGS["docker_cmd"], "inspect", "-f", "{{.State.Running}}", container_name],
                        check=True, capture_output=True, text=True
                    )
                    is_running = status_result.stdout.strip() == "true"
                    return is_running
                return False
            except subprocess.CalledProcessError as e:
                print(f"Command failed: {e}")
                if e.stderr:
                    print(f"Error output: {e.stderr}")
                return False
    except subprocess.CalledProcessError as e:
        print(f"Error executing docker-compose: {e}")
        if e.stderr:
            print(f"Error details: {e.stderr}")
        return False
    finally:
        # Clean up the temporary file
        try:
            os.unlink(temp_file)
        except Exception:
            pass

    return False


# Database migration operations
def run_flyway_operation(operation, db_params=None):
    """Run a Flyway database operation using Maven"""
    if db_params is None:
        db_params = get_db_connection_params()

    # Check if mvn is available
    if not shutil.which(OS_SETTINGS["mvn_cmd"]):
        click.echo("Maven is not available. Please install Maven.")
        return False

    # Build the Maven command
    mvn_cmd = [
        OS_SETTINGS["mvn_cmd"],
        f"flyway:{operation}",
        f"-Dflyway.url=jdbc:mariadb://{db_params['db_host']}:{db_params['db_port']}/{db_params['db_name']}",
        f"-Dflyway.user={db_params['db_username']}",
        f"-Dflyway.password={db_params['db_password']}",
        "-Dflyway.locations=classpath:db/migration",
        "-Dflyway.mixed=true"
    ]

    if operation == "migrate":
        mvn_cmd.append("-Dflyway.baselineOnMigrate=true")

    # Run the Maven command
    click.echo(f"Running Flyway {operation}...")
    result = run_command(mvn_cmd, cwd=PROJECT_ROOT, capture_output=False)

    if result:
        click.echo(f"Flyway {operation} completed successfully.")
        return True
    else:
        click.echo(f"Flyway {operation} failed.")
        return False

# Create the main command group
@click.group()
@click.version_option(version="1.0.0")
def cli():
    """ABET Assessment App development CLI.

    This tool provides commands for managing the development environment
    for the ABET Assessment Application.
    """
    pass

# Database command group
@cli.group()
def db():
    """Database management commands."""
    pass

@db.command()
def start():
    """Start the database container using Docker."""
    click.echo("Starting database container...")

    # Check if docker is available
    if not shutil.which(OS_SETTINGS["docker_cmd"]):
        click.echo("Docker is not installed or not in PATH. Please install Docker first.")
        return

    # Start the database container
    docker_compose_operation("start")

@db.command()
@click.option("--user", "-u", default=None, help="Database username")
@click.option("--password", "-p", default=None, help="Database password")
@click.option("--database", "-d", default=None, help="Database name")
def connect(user, password, database):
    """Connect to the MariaDB database container using flags."""
    click.echo("Connecting to MariaDB database...")

    # Check if docker is available, start container if necessary, etc.
    if not shutil.which(OS_SETTINGS["docker_cmd"]):
        click.echo("Docker is not installed or not in PATH.")
        return

    # Check if the container is running
    check_cmd = ["docker", "ps", "--filter", "name=java_project_db", "--format", "{{.Names}}"]
    result = run_command(check_cmd, capture_output=True)
    if not result or "java_project_db" not in result:
        click.echo("Database container is not running. Starting it now...")
        if not docker_compose_operation("start"):
            click.echo("Failed to start database container. Please run 'db start' first.")
            return

    # Load database connection parameters and override with flags if provided
    db_params = get_db_connection_params()
    if user is not None:
        db_params['db_username'] = user
    if password is not None:
        db_params['db_password'] = password
    if database is not None:
        db_params['db_name'] = database

    # Build command to connect to MariaDB
    connect_cmd = [
        "docker", "exec", "-it", "java_project_db",
        "mariadb",
        f"-u{db_params['db_username']}"
    ]
    if db_params['db_password']:
        connect_cmd.append(f"-p{db_params['db_password']}")
    connect_cmd.append(db_params['db_name'])

    click.echo(f"Connecting to database {db_params['db_name']} as {db_params['db_username']}...")

    try:
        subprocess.run(connect_cmd)
    except KeyboardInterrupt:
        click.echo("\nDisconnected from database.")
    except Exception as e:
        click.echo(f"Error connecting to database: {e}")

@db.command()
def stop():
    """Stop the database container."""
    click.echo("Stopping database container...")

    # Check if docker is available
    if not shutil.which(OS_SETTINGS["docker_cmd"]):
        click.echo("Docker is not installed or not in PATH.")
        return

    # Stop the database container
    docker_compose_operation("stop")

@db.command()
def migrate():
    """Run database migrations using Flyway."""
    click.echo("Running database migrations...")
    run_flyway_operation("migrate")

@db.command()
def info():
    """Show database migration information using Flyway."""
    click.echo("Database migration information:")
    run_flyway_operation("info")

@db.command()
def clean():
    """Clean the database (delete all data) using Flyway."""
    if click.confirm("This will delete all data in the database. Continue?"):
        click.echo("Cleaning database...")
        run_flyway_operation("clean")

@db.command()
def restart():
    """Restart the database and run migrations."""
    click.echo("Restarting database and running migrations...")

    if docker_compose_operation("restart"):
        # Wait a moment for the database to be ready
        time.sleep(5)
        run_flyway_operation("migrate")

# Environment command group
@cli.group()
def env():
    """Environment setup commands."""
    pass

@env.command()
def setup():
    """Set up the environment file (.env)."""
    click.echo("Setting up environment file...")

    # Get user input with defaults
    db_name = click.prompt("Database name", default="abetapp")
    db_user = click.prompt("Database username", default="user")
    db_password = click.prompt("Database password", default="", hide_input=True)
    db_root_password = click.prompt("Database root password", default="rootpassword", hide_input=True)
    db_host = click.prompt("Database host", default="localhost")
    db_port = click.prompt("Database port", default="3306")

    # Create .env file
    env_file = PROJECT_ROOT / ".env"

    with open(env_file, "w") as f:
        f.write(f"# Database Configuration\n")
        f.write(f"DB_NAME={db_name}\n")
        f.write(f"DB_USERNAME={db_user}\n")
        f.write(f"DB_PASSWORD={db_password}\n")
        f.write(f"DB_ROOT_PASSWORD={db_root_password}\n")
        f.write(f"DB_HOST={db_host}\n")
        f.write(f"DB_PORT={db_port}\n")

    click.echo(f"Environment file created at: {env_file}")

    if click.confirm("Do you want to start the database with these settings now?"):
        start()
        # Wait a moment for the database to be ready
        time.sleep(5)
        if click.confirm("Do you want to run database migrations now?"):
            migrate()

# Hooks command group
@cli.group()
def hooks():
    """Git hooks setup commands."""
    pass

@hooks.command()
def setup():
    """Set up Git hooks for the project."""
    click.echo("Setting up Git hooks...")

    # Create hooks directory if it doesn't exist
    hooks_dir = PROJECT_ROOT / ".git" / "hooks"
    os.makedirs(hooks_dir, exist_ok=True)

    # Create post-commit hook with Python code
    post_commit_path = hooks_dir / "post-commit"

    with open(post_commit_path, "w", newline="\n") as f:
        f.write("""#!/usr/bin/env python3
import subprocess
import sys
import os
from pathlib import Path

# Find the project root
def get_project_root():
    cmd = ["git", "rev-parse", "--show-toplevel"]
    result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    if result.returncode == 0:
        return result.stdout.strip()
    return os.getcwd()

# Get list of changed files
def get_changed_files():
    cmd = ["git", "diff-tree", "--no-commit-id", "--name-only", "-r", "HEAD"]
    result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    if result.returncode == 0:
        return result.stdout.strip().splitlines()
    return []

# Main function
def main():
    project_root = get_project_root()
    os.chdir(project_root)
    
    # Check if any migration files were changed
    changed_files = get_changed_files()
    migration_files = [f for f in changed_files if f.startswith("src/main/resources/db/migration/") and f.endswith(".sql")]
    
    if migration_files:
        print("Migration files changed in this commit. Running migrations...")
        
        # Run the setup.py script to handle migrations
        setup_py = Path(project_root) / "setup.py"
        if setup_py.exists():
            result = subprocess.run([sys.executable, str(setup_py), "db", "migrate"], 
                                    stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            if result.returncode == 0:
                print("✅ Migrations executed successfully")
            else:
                print("❌ Migration failed. Please fix issues and run migrations manually.")
                print(result.stderr)
        else:
            print("❌ setup.py not found. Please run migrations manually.")
    else:
        print("No migration files changed. Skipping migrations.")

if __name__ == "__main__":
    main()
""")

    # Make the hook executable
    os.chmod(post_commit_path, 0o755)
    click.echo("Git hooks set up successfully!")

# Shell integration command group
@cli.group()
def shell():
    """Shell integration commands."""
    pass

@shell.command()
def install():
    """Install shell aliases and functions."""
    # For PowerShell, check multiple possible profile locations
    if OS_SETTINGS["shell_type"] == "powershell":
        ps_profile1 = Path.home() / "Documents" / "PowerShell" / "Microsoft.PowerShell_profile.ps1"
        ps_profile2 = Path.home() / "Documents" / "WindowsPowerShell" / "Microsoft.PowerShell_profile.ps1"
        if ps_profile1.exists():
            shell_config = ps_profile1
        elif ps_profile2.exists():
            shell_config = ps_profile2
        else:
            # Create the PowerShell directory if it doesn't exist, and use ps_profile1 as default
            (Path.home() / "Documents" / "PowerShell").mkdir(parents=True, exist_ok=True)
            shell_config = Path.home() / "Documents" / "PowerShell" / "Microsoft.PowerShell_profile.ps1"
    else:
        shell_config = OS_SETTINGS["shell_config_file"]

    click.echo(f"Installing shell integration for {shell_config}...")

    if OS_SETTINGS["shell_type"] == "powershell":
        aliases = [
            "",
            "# ABET Assessment App aliases",
            "# Added by setup.py script",
            f"function Invoke-AbetDbStart {{ py '{PROJECT_ROOT / 'setup.py'}' db start }}",
            f"function Invoke-AbetDbStop {{ py '{PROJECT_ROOT / 'setup.py'}' db stop }}",
            f"function Invoke-AbetDbMigrate {{ py '{PROJECT_ROOT / 'setup.py'}' db migrate }}",
            f"function Invoke-AbetDbInfo {{ py '{PROJECT_ROOT / 'setup.py'}' db info }}",
            f"function Invoke-AbetDbClean {{ py '{PROJECT_ROOT / 'setup.py'}' db clean }}",
            f"function Invoke-AbetDbRestart {{ py '{PROJECT_ROOT / 'setup.py'}' db restart }}",
            f"function Invoke-AbetEnvSetup {{ py '{PROJECT_ROOT / 'setup.py'}' env setup }}",
            "",
            "# Function to connect to MariaDB using Python flags",
            "function Connect-AbetDb {",
            "    param (",
            "        [Parameter(ValueFromRemainingArguments=$true)]",
            "        [string[]]$Args",
            "    )",
            f"    py '{PROJECT_ROOT / 'setup.py'}' db connect @Args",
            "}",
            "",
            "# Create aliases",
            "Set-Alias -Name dbstart -Value Invoke-AbetDbStart",
            "Set-Alias -Name dbstop -Value Invoke-AbetDbStop",
            "Set-Alias -Name dbmigrate -Value Invoke-AbetDbMigrate",
            "Set-Alias -Name dbinfo -Value Invoke-AbetDbInfo",
            "Set-Alias -Name dbclean -Value Invoke-AbetDbClean",
            "Set-Alias -Name dbrestart -Value Invoke-AbetDbRestart",
            "Set-Alias -Name dbsetup -Value Invoke-AbetEnvSetup",
            "Set-Alias -Name dbconnect -Value Connect-AbetDb",
            "",
            "function Show-AbetDbHelp {",
            "    Write-Host 'Database management commands:' -ForegroundColor Cyan",
            "    Write-Host '  dbstart   - Start the database container' -ForegroundColor Cyan",
            "    Write-Host '  dbstop    - Stop the database container' -ForegroundColor Cyan",
            "    Write-Host '  dbmigrate - Run database migrations' -ForegroundColor Cyan",
            "    Write-Host '  dbinfo    - Show migration status' -ForegroundColor Cyan",
            "    Write-Host '  dbclean   - Clean the database' -ForegroundColor Cyan",
            "    Write-Host '  dbrestart - Restart database and run migrations' -ForegroundColor Cyan",
            "    Write-Host '  dbsetup   - Set up environment variables' -ForegroundColor Cyan",
            "    Write-Host '  dbconnect - Connect to MariaDB (usage: dbconnect --user <user> --password <password> --database <database>)' -ForegroundColor Cyan",
            "    Write-Host '  dbhelp    - Show this help message' -ForegroundColor Cyan",
            "}",
            "",
            "Set-Alias -Name dbhelp -Value Show-AbetDbHelp",
            "# End ABET Assessment App aliases"
        ]
    else:
        aliases = [
            "",
            "# ABET Assessment App aliases",
            "# Added by setup.py script",
            f"alias dbstart='python {PROJECT_ROOT / 'setup.py'} db start'",
            f"alias dbstop='python {PROJECT_ROOT / 'setup.py'} db stop'",
            f"alias dbmigrate='python {PROJECT_ROOT / 'setup.py'} db migrate'",
            f"alias dbinfo='python {PROJECT_ROOT / 'setup.py'} db info'",
            f"alias dbclean='python {PROJECT_ROOT / 'setup.py'} db clean'",
            f"alias dbrestart='python {PROJECT_ROOT / 'setup.py'} db restart'",
            f"alias dbsetup='python {PROJECT_ROOT / 'setup.py'} env setup'",
            "",
            "# Function to connect to MariaDB using Python flags",
            "dbconnect() {",
            f"  python {PROJECT_ROOT / 'setup.py'} db connect \"$@\"",
            "}",
            "",
            "# Function to load database credentials from .env",
            "dbcreds() {",
            "  if [ -f .env ]; then",
            "    export $(grep -v '^#' .env | xargs)",
            "    echo \"Database credentials loaded from .env\"",
            "  else",
            "    echo \"No .env file found\"",
            "  fi",
            "}",
            "",
            "# Function to show available commands",
            "dbhelp() {",
            "  echo 'Database management commands:'",
            "  echo '  dbstart   - Start the database container'",
            "  echo '  dbstop    - Stop the database container'",
            "  echo '  dbmigrate - Run database migrations'",
            "  echo '  dbinfo    - Show migration status'",
            "  echo '  dbclean   - Clean the database'",
            "  echo '  dbrestart - Restart database and run migrations'",
            "  echo '  dbsetup   - Set up environment variables'",
            "  echo '  dbcreds   - Load database credentials from .env file'",
            "  echo '  dbconnect - Connect to MariaDB (usage: dbconnect --user <user> --password <password> --database <database>)'",
            "  echo '  dbhelp    - Show this help message'",
            "}",
            "# End ABET Assessment App aliases",
            ""
        ]

    shell_config_path = Path(shell_config)
    shell_config_path.parent.mkdir(parents=True, exist_ok=True)
    if shell_config_path.exists():
        with open(shell_config_path, "r") as f:
            content = f.read()
        if "ABET Assessment App aliases" in content:
            pattern = r"\n# ABET Assessment App aliases.*?# End ABET Assessment App aliases\n"
            content = re.sub(pattern, "", content, flags=re.DOTALL)
        with open(shell_config_path, "w") as f:
            f.write(content)
            f.write("\n".join(aliases))
    else:
        with open(shell_config_path, "w") as f:
            f.write("\n".join(aliases))
    click.echo("Shell integration installed successfully!")
    if OS_SETTINGS["shell_type"] == "powershell":
        click.echo("To use the aliases in PowerShell, run: . $PROFILE")
    else:
        config_name = os.path.basename(str(shell_config))
        click.echo(f"To use the aliases in your shell, run: source ~/{config_name}")


@shell.command()
def remove():
    """Remove shell aliases and functions."""
    # For PowerShell, check multiple profile locations
    if OS_SETTINGS["shell_type"] == "powershell":
        ps_profile1 = Path.home() / "Documents" / "PowerShell" / "Microsoft.PowerShell_profile.ps1"
        ps_profile2 = Path.home() / "Documents" / "WindowsPowerShell" / "Microsoft.PowerShell_profile.ps1"
        if ps_profile1.exists():
            shell_config = ps_profile1
        elif ps_profile2.exists():
            shell_config = ps_profile2
        else:
            click.echo("No PowerShell profile found.")
            return
    else:
        shell_config = OS_SETTINGS["shell_config_file"]

    click.echo(f"Removing shell integration from {shell_config}...")
    shell_config_path = Path(shell_config)
    if shell_config_path.exists():
        with open(shell_config_path, "r") as f:
            content = f.read()
        if "ABET Assessment App aliases" in content:
            pattern = r"\n# ABET Assessment App aliases.*?# End ABET Assessment App aliases\n"
            content = re.sub(pattern, "", content, flags=re.DOTALL)
            with open(shell_config_path, "w") as f:
                f.write(content)
            click.echo("Shell integration removed successfully!")
        else:
            click.echo("No shell integration found.")
    else:
        click.echo(f"Shell config file not found: {shell_config}")


# Development environment command group
@cli.group()
def dev():
    """Development environment commands."""
    pass

@dev.command()
def setup():
    """Set up the complete development environment."""
    click.echo("Setting up development environment...")

    # 1. Environment setup
    click.echo("\n=== Setting up environment ===")
    env.setup.callback()

    # 2. Database setup
    click.echo("\n=== Setting up database ===")
    db.start.callback()

    # Wait a moment for the database to be ready
    time.sleep(5)

    # 3. Run migrations
    click.echo("\n=== Running database migrations ===")
    db.migrate.callback()

    # 4. Git hooks setup
    click.echo("\n=== Setting up Git hooks ===")
    hooks.setup.callback()

    # 5. Shell integration
    click.echo("\n=== Setting up shell integration ===")
    shell.install.callback()

    click.echo("\nDevelopment environment setup complete!")

if __name__ == "__main__":
    # Check a Python version
    if sys.version_info < (3, 6):
        print("Python 3.6 or higher is required.")
        sys.exit(1)

    # Check if script is run directly
    if os.path.basename(sys.argv[0]) == "setup.py":
        # Make the script executable if it isn't already
        try:
            current_script = Path(__file__).resolve()
            if not os.access(current_script, os.X_OK) and not OS_SETTINGS["is_windows"]:
                os.chmod(current_script, 0o755)
                print(f"Made {current_script} executable.")
        except:
            pass

    cli()