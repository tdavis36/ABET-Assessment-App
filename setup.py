#!/usr/bin/env python3
import os
import sys
import platform
import subprocess
import shutil
import tempfile
import time
import re
if platform.system() == 'Windows':
    import winreg
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
    For Windows, try 'py' first, then fall back to 'python' or 'python3'.
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


# Update the get_os_specific_settings function to include mvn_cmd
def get_os_specific_settings():
    """Get OS-specific settings for the current platform."""
    import platform
    import shutil
    import subprocess

    # Base settings
    settings = {
        'platform': platform.system().lower(),
    }

    # Add platform-specific flags
    if settings['platform'] == 'windows':
        settings['is_windows'] = True
    else:
        settings['is_windows'] = False

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
        # Modern Docker has 'docker compose' (without a hyphen) as a subcommand
        compose_cmd = 'docker compose'

    # Store both versions of the compose command
    settings['compose_command'] = compose_cmd  # Keep for backward compatibility
    settings['docker_compose_cmd'] = compose_cmd  # Add the new key

    # Detect Maven command
    mvn_cmd = 'mvn'
    if shutil.which(mvn_cmd):
        settings['mvn_cmd'] = mvn_cmd
    else:
        print("Warning: Maven command not found in PATH. Some features may not work.")
        settings['mvn_cmd'] = mvn_cmd  # Set it anyway as a fallback

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
        # Shell config file for Windows (PowerShell profile)
        settings['shell_config_file'] = str(Path.home() / "Documents" / "PowerShell" / "Microsoft.PowerShell_profile.ps1")
    elif settings['platform'] == 'darwin':  # macOS
        settings['python_command'] = 'python3'
        settings['shell_type'] = 'bash'
        settings['shell_config_file'] = str(Path.home() / ".bash_profile")
    else:  # Linux and others
        settings['python_command'] = 'python3'
        settings['shell_type'] = 'bash'
        settings['shell_config_file'] = str(Path.home() / ".bashrc")

    print(f"Using settings for {settings['platform']}: Python command = {settings['python_command']}, "
          f"Shell = {settings['shell_type']}, Docker command = {settings.get('docker_cmd', 'N/A')}, "
          f"Docker Compose command = {settings.get('docker_compose_cmd', 'N/A')}, "
          f"Maven command = {settings['mvn_cmd']}")
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
        "db_password": env_vars.get("DB_PASSWORD", "pass"),
        "db_root_password": env_vars.get("DB_ROOT_PASSWORD", "rootpassword"),
        "db_host": env_vars.get("DB_HOST", "localhost"),
        "db_port": env_vars.get("DB_PORT", "3306")
    }

# Function to check for MariaDB installation on Windows
def is_mariadb_installed_windows():
    """Check if MariaDB is installed on Windows by looking at the registry"""
    try:
        # Check for both 32-bit and 64-bit registry
        reg_paths = [r"SOFTWARE\MariaDB", r"SOFTWARE\Wow6432Node\MariaDB"]

        for reg_path in reg_paths:
            try:
                registry_key = winreg.OpenKey(winreg.HKEY_LOCAL_MACHINE, reg_path)
                return True
            except FileNotFoundError:
                continue
            except Exception as e:
                print(f"Error checking registry: {e}")

        # Also check for common installation paths
        common_paths = [
            r"C:\Program Files\MariaDB",
            r"C:\Program Files (x86)\MariaDB",
            r"C:\MariaDB"
        ]

        for path in common_paths:
            if os.path.exists(path):
                return True

        # Check if mysql/mariadb client is in PATH
        if shutil.which("mysql") or shutil.which("mariadb"):
            return True

        return False
    except Exception as e:
        print(f"Error checking MariaDB installation: {e}")
        return False

# Function to get MariaDB service name on Windows
def get_mariadb_service_name():
    """Get the MariaDB service name from Windows registry or return the default"""
    try:
        # Try to find service name in registry
        reg_paths = [r"SOFTWARE\MariaDB", r"SOFTWARE\Wow6432Node\MariaDB"]

        for reg_path in reg_paths:
            try:
                registry_key = winreg.OpenKey(winreg.HKEY_LOCAL_MACHINE, reg_path)
                service_name, _ = winreg.QueryValueEx(registry_key, "ServiceName")
                return service_name
            except (FileNotFoundError, WindowsError):
                continue
            except Exception:
                pass

        # Default service name for MariaDB
        return "MariaDB"
    except Exception:
        # Return default if anything fails
        return "MariaDB"

# Function to get MariaDB installation path on Windows
def get_mariadb_install_path():
    """Get the MariaDB installation path from Windows registry or check common locations"""
    try:
        # Try to find installation path in registry
        reg_paths = [r"SOFTWARE\MariaDB", r"SOFTWARE\Wow6432Node\MariaDB"]

        for reg_path in reg_paths:
            try:
                registry_key = winreg.OpenKey(winreg.HKEY_LOCAL_MACHINE, reg_path)
                install_location, _ = winreg.QueryValueEx(registry_key, "InstallLocation")
                return install_location
            except (FileNotFoundError, WindowsError):
                continue
            except Exception:
                pass

        # Check common installation paths
        common_paths = [
            r"C:\Program Files\MariaDB 10.11",
            r"C:\Program Files\MariaDB",
            r"C:\Program Files (x86)\MariaDB",
            r"C:\MariaDB"
        ]

        for path in common_paths:
            if os.path.exists(path):
                return path

        # If we can't find it, return None
        return None
    except Exception:
        return None

# Function to check if MariaDB service is running on Windows
def is_mariadb_service_running():
    """Check if the MariaDB service is running on Windows"""
    try:
        service_name = get_mariadb_service_name()
        result = subprocess.run(
            ["sc", "query", service_name],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            check=False
        )

        if result.returncode != 0:
            return False

        # Check if the output contains RUNNING
        return "RUNNING" in result.stdout
    except Exception as e:
        print(f"Error checking MariaDB service: {e}")
        return False

# New function for MariaDB direct installation operations
def mariadb_operation(operation):
    """Manage MariaDB operations for native Windows installation"""
    if OS_SETTINGS['platform'] != 'windows':
        print("This function is only for Windows. On other platforms, use docker_compose_operation.")
        return False

    # Get connection parameters
    db_params = get_db_connection_params()

    # Check if MariaDB is installed
    if not is_mariadb_installed_windows():
        print("MariaDB is not installed on this system.")
        print("Please install MariaDB 10.11 from https://mariadb.org/download/")
        return False

    # Get service name
    service_name = get_mariadb_service_name()

    # Find mysql client executable
    mysql_client = shutil.which("mysql") or shutil.which("mariadb")
    if not mysql_client:
        # Try to find in installation directory
        install_path = get_mariadb_install_path()
        if install_path:
            bin_path = os.path.join(install_path, "bin")
            potential_clients = [os.path.join(bin_path, "mysql.exe"),
                                 os.path.join(bin_path, "mariadb.exe")]
            for client in potential_clients:
                if os.path.exists(client):
                    mysql_client = client
                    break

    if not mysql_client:
        print("MariaDB client executable not found. Make sure MariaDB is properly installed and 'mysql' is in your PATH.")
        return False

    # Maximum number of attempts for database operations
    max_attempts = 3

    try:
        if operation == "start":
            print(f"Starting MariaDB service ({service_name})...")

            # Check if service is already running
            is_running = is_mariadb_service_running()

            if not is_running:
                # Start the service
                result = subprocess.run(
                    ["sc", "start", service_name],
                    stdout=subprocess.PIPE,
                    stderr=subprocess.PIPE,
                    text=True,
                    check=False
                )

                if result.returncode != 0:
                    print(f"Failed to start MariaDB service: {result.stderr}")
                    return False

                # Wait for the service to be fully started
                print("Waiting for MariaDB service to start...")
                wait_attempts = 0
                while not is_mariadb_service_running() and wait_attempts < 10:
                    time.sleep(1)
                    wait_attempts += 1

                if not is_mariadb_service_running():
                    print("Warning: MariaDB service did not start within the expected time.")
                    return False

                print("MariaDB service started.")
                # Additional wait for the server to be ready for connections
                time.sleep(5)
            else:
                print("MariaDB service is already running.")

            # Verify the server is accepting connections before proceeding
            connection_check = False
            for attempt in range(3):
                try:
                    # Try a simple command to check connection
                    check_cmd = [
                        mysql_client,
                        "-u", "root",
                        f"-p{db_params['db_root_password']}",
                        "-e", "SELECT 1;"
                    ]

                    check_result = subprocess.run(
                        check_cmd,
                        stdout=subprocess.PIPE,
                        stderr=subprocess.PIPE,
                        text=True,
                        check=False
                    )

                    if check_result.returncode == 0:
                        connection_check = True
                        break
                    else:
                        print(f"Waiting for MariaDB to accept connections (attempt {attempt + 1}/{3})...")
                        time.sleep(3)
                except Exception as e:
                    print(f"Connection attempt {attempt + 1} failed: {e}")
                    time.sleep(3)

            if not connection_check:
                print("Warning: Could not establish a connection to MariaDB after starting the service.")
                print(f"Please verify the root password (current: {db_params['db_root_password']}).")
                return False

            # Now check if the database exists
            database_exists = False
            try:
                # Check if database exists
                check_db_cmd = [
                    mysql_client,
                    "-u", "root",
                    f"-p{db_params['db_root_password']}",
                    "-e", f"SHOW DATABASES LIKE '{db_params['db_name']}';"
                ]

                check_db_result = subprocess.run(
                    check_db_cmd,
                    stdout=subprocess.PIPE,
                    stderr=subprocess.PIPE,
                    text=True,
                    check=False
                )

                # If database exists, output will contain database name
                database_exists = db_params['db_name'] in check_db_result.stdout
            except Exception as e:
                print(f"Error checking if database exists: {e}")

            if database_exists:
                print(f"Database '{db_params['db_name']}' already exists.")
            else:
                print(f"Database '{db_params['db_name']}' does not exist. Creating it now...")

                # Create database and user if they don't exist
                for attempt in range(max_attempts):
                    try:
                        # Prepare commands to create database and user
                        commands = [
                            f"CREATE DATABASE IF NOT EXISTS {db_params['db_name']};",
                            f"CREATE USER IF NOT EXISTS '{db_params['db_username']}'@'localhost' IDENTIFIED BY '{db_params['db_password']}';",
                            f"CREATE USER IF NOT EXISTS '{db_params['db_username']}'@'%' IDENTIFIED BY '{db_params['db_password']}';",
                            f"GRANT ALL PRIVILEGES ON {db_params['db_name']}.* TO '{db_params['db_username']}'@'localhost';",
                            f"GRANT ALL PRIVILEGES ON {db_params['db_name']}.* TO '{db_params['db_username']}'@'%';",
                            f"FLUSH PRIVILEGES;"
                        ]

                        # Execute each command individually for better error handling
                        success = True
                        for cmd in commands:
                            mysql_cmd = [
                                mysql_client,
                                "-u", "root",
                                f"-p{db_params['db_root_password']}",
                                "-e", cmd
                            ]

                            cmd_result = subprocess.run(
                                mysql_cmd,
                                stdout=subprocess.PIPE,
                                stderr=subprocess.PIPE,
                                text=True,
                                check=False
                            )

                            if cmd_result.returncode != 0:
                                print(f"Command failed: {cmd}")
                                print(f"Error: {cmd_result.stderr}")
                                success = False
                                break

                        if success:
                            print(f"Database '{db_params['db_name']}' and user '{db_params['db_username']}' created/configured successfully.")
                            break
                        elif attempt < max_attempts - 1:
                            print(f"Retrying database creation (attempt {attempt + 2}/{max_attempts})...")
                            time.sleep(2)
                        else:
                            print("Failed to create database after multiple attempts.")
                            print("You may need to manually create the database and user with these commands:")
                            for cmd in commands:
                                print(f"    {cmd}")
                            return False

                    except Exception as e:
                        if attempt < max_attempts - 1:
                            print(f"Error during database creation (attempt {attempt + 1}): {e}")
                            print(f"Retrying in 2 seconds...")
                            time.sleep(2)
                        else:
                            print(f"Failed to create database after {max_attempts} attempts: {e}")
                            print("You may need to manually create the database and user.")
                            return False

            # Verify the user can connect with the specified credentials
            try:
                user_check_cmd = [
                    mysql_client,
                    "-u", db_params['db_username'],
                    f"-p{db_params['db_password']}",
                    db_params['db_name'],
                    "-e", "SELECT 1;"
                ]

                user_check_result = subprocess.run(
                    user_check_cmd,
                    stdout=subprocess.PIPE,
                    stderr=subprocess.PIPE,
                    text=True,
                    check=False
                )

                if user_check_result.returncode == 0:
                    print(f"Verified that user '{db_params['db_username']}' can connect to database '{db_params['db_name']}'.")
                else:
                    print(f"Warning: User '{db_params['db_username']}' cannot connect to database '{db_params['db_name']}'.")
                    print(f"Error: {user_check_result.stderr}")
                    print("You may need to check your credentials or permissions.")
            except Exception as e:
                print(f"Error verifying user connection: {e}")

            return True

        elif operation == "stop":
            print(f"Stopping MariaDB service ({service_name})...")

            # Check if service is running
            if not is_mariadb_service_running():
                print("MariaDB service is not running.")
                return True

            # Stop the service
            result = subprocess.run(
                ["sc", "stop", service_name],
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True,
                check=False
            )

            if result.returncode != 0:
                print(f"Failed to stop MariaDB service: {result.stderr}")
                return False

            # Wait for the service to fully stop
            wait_attempts = 0
            while is_mariadb_service_running() and wait_attempts < 10:
                time.sleep(1)
                wait_attempts += 1

            if is_mariadb_service_running():
                print("Warning: MariaDB service did not stop within the expected time.")
                return False

            print("MariaDB service stopped successfully.")
            return True

        elif operation == "status":
            # Check if service is running
            is_running = is_mariadb_service_running()
            status = "running" if is_running else "stopped"
            print(f"MariaDB service ({service_name}) is {status}.")
            return is_running

        elif operation == "restart":
            print(f"Restarting MariaDB service ({service_name})...")

            # Stop the service if it's running
            if is_mariadb_service_running():
                if not mariadb_operation("stop"):
                    print("Failed to stop MariaDB service for restart. Trying to start anyway...")
                else:
                    # Wait a moment for the service to fully stop
                    print("Waiting for service to fully stop before restart...")
                    time.sleep(3)

            # Start the service
            return mariadb_operation("start")

        else:
            print(f"Unknown operation: {operation}")
            return False
    except Exception as e:
        print(f"Error performing MariaDB operation '{operation}': {e}")
        return False

# Function to handle DB operations based on platform
def db_operation(operation):
    """Execute database operations using Docker container or native MariaDB based on platform"""
    if OS_SETTINGS['platform'] == 'windows':
        # Use native MariaDB on Windows
        return mariadb_operation(operation)
    else:
        # Use Docker container on other platforms
        return docker_compose_operation(operation)

# Docker Compose operation function (for non-Windows platforms)
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
    compose_cmd = docker_compose_command()  # Use the function that properly detects compose command

    # Generate a temporary docker-compose file with our configuration
    with tempfile.NamedTemporaryFile(delete=False, mode='w', suffix='.yml') as temp:
        temp_file = temp.name
        # Write the docker-compose configuration - Remove 'version' attribute
        temp.write("""
services:
  db:
    container_name: abetapp_db
    image: mariadb:10.11
    environment:
      MARIADB_DATABASE: abetapp
      MARIADB_USER: user
      MARIADB_PASSWORD: pass
      MARIADB_ROOT_PASSWORD: rootpassword
    ports:
      - "3306:3306"
    volumes:
      - mariadbdata:/var/lib/mysql
    restart: unless-stopped

volumes:
  mariadbdata:
""")

    try:
        # Perform the requested operation
        if operation == "start":
            print("Starting database container...")
            # Check if the container is already running
            container_name = "abetapp_db"
            check_cmd = [OS_SETTINGS["docker_cmd"], "ps", "-q", "--filter", f"name={container_name}"]
            result = subprocess.run(check_cmd, check=True, capture_output=True, text=True)

            if result.stdout.strip():
                print(f"Container {container_name} is already running.")
                return True

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
            container_name = "abetapp_db"
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
                    print(f"Container {container_name} is {'running' if is_running else 'stopped'}.")
                    return is_running
                print(f"Container {container_name} does not exist.")
                return False
            except subprocess.CalledProcessError as e:
                print(f"Command failed: {e}")
                if e.stderr:
                    print(f"Error output: {e.stderr}")
                return False
    except subprocess.CalledProcessError as e:
        print(f"Error executing docker-compose operation: {e}")
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
    mvn_executable = shutil.which("mvn")
    if not mvn_executable:
        click.echo("Maven (mvn) is not found in your PATH.")
        click.echo("Please install Maven and add it to your PATH or provide the full path to mvn.cmd")

        # Try to find Maven in common installation locations on Windows
        if OS_SETTINGS['platform'] == 'windows':
            potential_locations = [
                r"C:\Program Files\apache-maven\bin\mvn.cmd",
                r"C:\Program Files\Maven\bin\mvn.cmd",
                r"C:\maven\bin\mvn.cmd",
                r"C:\ProgramData\chocolatey\bin\mvn.cmd"
            ]

            for location in potential_locations:
                if os.path.exists(location):
                    click.echo(f"Found Maven at: {location}")
                    mvn_executable = location
                    break

            if not mvn_executable and click.confirm("Would you like to open the Maven download page?"):
                import webbrowser
                webbrowser.open("https://maven.apache.org/download.cgi")

        return False

    # Build the Maven command
    mvn_cmd = [
        mvn_executable,  # Use the actual path to mvn executable
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
    click.echo(f"Using Maven command: {mvn_executable}")

    try:
        result = run_command(mvn_cmd, cwd=PROJECT_ROOT, capture_output=False)

        if result:
            click.echo(f"Flyway {operation} completed successfully.")
            return True
        else:
            click.echo(f"Flyway {operation} failed.")
            return False
    except Exception as e:
        click.echo(f"Error running Maven command: {e}")
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
    """Start the database (MariaDB service on Windows, container on other platforms)."""
    click.echo("Starting database...")

    if OS_SETTINGS['platform'] == 'windows':
        if not is_mariadb_installed_windows():
            click.echo("MariaDB is not installed. Please install MariaDB 10.11 from https://mariadb.org/download/")
            if click.confirm("Would you like to open the MariaDB download page?"):
                import webbrowser
                webbrowser.open("https://mariadb.org/download/")
            return
    else:
        # Check if docker is available for non-Windows platforms
        if not shutil.which(OS_SETTINGS["docker_cmd"]):
            click.echo("Docker is not installed or not in PATH. Please install Docker first.")
            return

    # Start the database
    db_operation("start")

@db.command()
@click.option("--user", "-u", default=None, help="Database username")
@click.option("--password", "-p", default=None, help="Database password")
@click.option("--database", "-d", default=None, help="Database name")
def connect(user, password, database):
    """Connect to the MariaDB database using command-line client."""
    click.echo("Connecting to MariaDB database...")

    # Load database connection parameters and override with flags if provided
    db_params = get_db_connection_params()
    if user is not None:
        db_params['db_username'] = user
    if password is not None:
        db_params['db_password'] = password
    if database is not None:
        db_params['db_name'] = database

    # Platform-specific database connection
    if OS_SETTINGS['platform'] == 'windows':
        # Check if MariaDB is installed
        if not is_mariadb_installed_windows():
            click.echo("MariaDB is not installed or not in PATH.")
            return

        # Check if the service is running
        if not is_mariadb_service_running():
            click.echo("MariaDB service is not running. Starting it now...")
            if not mariadb_operation("start"):
                click.echo("Failed to start MariaDB service. Please start it manually.")
                return

        # Find client exe
        mysql_client = shutil.which("mysql") or shutil.which("mariadb")
        if not mysql_client:
            # Try to find in installation directory
            install_path = get_mariadb_install_path()
            if install_path:
                bin_path = os.path.join(install_path, "bin")
                potential_clients = [os.path.join(bin_path, "mysql.exe"),
                                     os.path.join(bin_path, "mariadb.exe")]
                for client in potential_clients:
                    if os.path.exists(client):
                        mysql_client = client
                        break

            if not mysql_client:
                click.echo("MariaDB client not found in PATH or installation directory.")
                return

        # Build command to connect
        connect_cmd = [
            mysql_client,
            f"-u{db_params['db_username']}"
        ]
        if db_params['db_password']:
            connect_cmd.append(f"-p{db_params['db_password']}")
        connect_cmd.append(db_params['db_name'])
    else:
        # For non-Windows platforms, use Docker
        # Check if the container is running
        check_cmd = ["docker", "ps", "--filter", "name=abetapp_db", "--format", "{{.Names}}"]
        result = run_command(check_cmd, capture_output=True)
        if not result or "abetapp_db" not in result:
            click.echo("Database container is not running. Starting it now...")
            if not docker_compose_operation("start"):
                click.echo("Failed to start database container. Please run 'db start' first.")
                return

        # Build command to connect to MariaDB in Docker container
        connect_cmd = [
            "docker", "exec", "-it", "abetapp_db",
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
    """Stop the database (MariaDB service on Windows, container on other platforms)."""
    click.echo("Stopping database...")

    # Check platform and execute appropriate stop command
    db_operation("stop")

@db.command()
def status():
    """Check the database status (MariaDB service on Windows, container on other platforms)."""
    click.echo("Checking database status...")

    # Check platform and execute appropriate status command
    db_operation("status")

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

    if db_operation("restart"):
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
    db_password = click.prompt("Database password", default="pass")
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
        db.start.callback()
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
            "    Write-Host '  dbstart   - Start the database' -ForegroundColor Cyan",
            "    Write-Host '  dbstop    - Stop the database' -ForegroundColor Cyan",
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
            "  echo '  dbstart   - Start the database'",
            "  echo '  dbstop    - Stop the database'",
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