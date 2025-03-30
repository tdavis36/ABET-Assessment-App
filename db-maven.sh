#!/bin/bash
# Enhanced db-maven script with interactive input and CSV import
# Usage: ./db-maven.sh [operation] [options]
#
# Operations:
#   start    - Start the database container
#   stop     - Stop the database container
#   sync     - Sync the database schema (legacy method)
#   migrate  - Run Flyway migrations
#   info     - Show Flyway migration status
#   restart  - Restart the database container and apply migrations
#   setup    - Interactive setup to create .env file
#   import   - Import CSV file(s) into database
#
# Options:
#   --db-name=NAME     - Database name
#   --db-user=USER     - Database username
#   --db-pass=PASS     - Database password
#   --db-root=ROOT     - Database root password
#   --env=ENV          - Environment (dev/local)
#   --csv=FILE         - CSV file to import
#   --table=NAME       - Table to import CSV into
#   --delimiter=CHAR   - CSV delimiter (default: ,)
#   --skip-header      - Skip first row in CSV

# Default operation
OPERATION=${1:-migrate}
shift 2>/dev/null || true

# Default properties
MAVEN_PROFILES=""
MAVEN_PROPS=""

# Variables to store database credentials
DB_NAME=""
DB_USER=""
DB_PASS=""
DB_ROOT=""
ENV_TYPE=""

# CSV import settings
CSV_FILE=""
TARGET_TABLE=""
CSV_DELIMITER=","
SKIP_HEADER=false

# Interactive setup function
interactive_setup() {
  # Text colors
  GREEN='\033[0;32m'
  YELLOW='\033[1;33m'
  CYAN='\033[0;36m'
  NC='\033[0m' # No Color

  echo -e "${CYAN}=== Interactive Database Setup ===${NC}"

  # Check if .env exists and offer to load defaults
  if [ -f ".env" ]; then
    echo -e "${YELLOW}Existing .env file found. Load values as defaults? (y/n)${NC}"
    read -p "> " load_defaults

    if [[ $load_defaults =~ ^[Yy]$ ]]; then
      source .env
      DB_NAME=${DB_NAME:-$DB_NAME}
      DB_USER=${DB_USERNAME:-$DB_USER}
      DB_PASS=${DB_PASSWORD:-$DB_PASS}
      DB_ROOT=${DB_ROOT_PASSWORD:-$DB_ROOT}
    fi
  fi

  # Database name
  echo -e "${CYAN}Enter database name:${NC} [${DB_NAME:-your_db_name}]"
  read -p "> " input_db_name
  DB_NAME=${input_db_name:-${DB_NAME:-your_db_name}}

  # Database username
  echo -e "${CYAN}Enter database username:${NC} [${DB_USER:-dbuser}]"
  read -p "> " input_db_user
  DB_USER=${input_db_user:-${DB_USER:-dbuser}}

  # Database password
  echo -e "${CYAN}Enter database password:${NC} [${DB_PASS:-dbuserpassword}]"
  read -p "> " input_db_pass
  DB_PASS=${input_db_pass:-${DB_PASS:-dbuserpassword}}

  # Root password
  echo -e "${CYAN}Enter database root password:${NC} [${DB_ROOT:-rootpassword}]"
  read -p "> " input_db_root
  DB_ROOT=${input_db_root:-${DB_ROOT:-rootpassword}}

  # Environment
  echo -e "${CYAN}Enter environment (dev/local):${NC} [${ENV_TYPE:-dev}]"
  read -p "> " input_env
  ENV_TYPE=${input_env:-${ENV_TYPE:-dev}}

  # Confirm values
  echo -e "\n${CYAN}Please confirm the following settings:${NC}"
  echo -e "Database name: ${YELLOW}$DB_NAME${NC}"
  echo -e "Database username: ${YELLOW}$DB_USER${NC}"
  echo -e "Database password: ${YELLOW}$DB_PASS${NC}"
  echo -e "Root password: ${YELLOW}$DB_ROOT${NC}"
  echo -e "Environment: ${YELLOW}$ENV_TYPE${NC}"

  echo -e "\n${CYAN}Save these settings to .env file? (y/n)${NC}"
  read -p "> " save_settings

  if [[ $save_settings =~ ^[Yy]$ ]]; then
    create_env_file
    echo -e "\n${GREEN}Settings saved! You can now run other commands.${NC}"
    echo -e "${YELLOW}Example: ./db-maven.sh start${NC}"
  else
    echo -e "\n${YELLOW}Settings not saved. Exiting setup.${NC}"
    exit 0
  fi
}

# Function to create .env file
create_env_file() {
  # Check if .env is in .gitignore
  if [ ! -f ".gitignore" ]; then
    echo "Creating .gitignore file"
    echo ".env" > ".gitignore"
  elif ! grep -q "^\.env$" ".gitignore"; then
    echo "Warning: .env is not in .gitignore. Adding it now."
    echo ".env" >> ".gitignore"
  fi

  # Create .env file with passed parameters
  echo "Creating .env file with your settings"
  cat > ".env" << EOF
# Database configuration created interactively
# Created on $(date)
DB_ROOT_PASSWORD=$DB_ROOT
DB_NAME=$DB_NAME
DB_USERNAME=$DB_USER
DB_PASSWORD=$DB_PASS
ENV_TYPE=$ENV_TYPE
EOF

  echo ".env file created successfully"
}

# Function to import CSV file
import_csv() {
  # Text colors
  GREEN='\033[0;32m'
  YELLOW='\033[1;33m'
  RED='\033[0;31m'
  CYAN='\033[0;36m'
  NC='\033[0m' # No Color

  if [ -z "$CSV_FILE" ]; then
    echo -e "${RED}Error: No CSV file specified. Use --csv=FILE${NC}"
    exit 1
  fi

  if [ -z "$TARGET_TABLE" ]; then
    echo -e "${RED}Error: No target table specified. Use --table=NAME${NC}"
    exit 1
  fi

  if [ ! -f "$CSV_FILE" ]; then
    echo -e "${RED}Error: CSV file not found: $CSV_FILE${NC}"
    exit 1
  fi

  # Ensure all variables are set
  if [ -z "$DB_NAME" ] || [ -z "$DB_USER" ] || [ -z "$DB_PASS" ]; then
    echo -e "${RED}Error: Database credentials not set. Check your .env file or provide --db-* options${NC}"
    exit 1
  fi

  # Create a temporary SQL file
  TEMP_SQL=$(mktemp)

  # Create LOAD DATA INFILE statement
  echo -e "${CYAN}Preparing to import CSV data...${NC}"

  # Copy the file to the Docker container
  echo -e "${CYAN}Copying CSV file to Docker container...${NC}"
  docker cp "$CSV_FILE" "java_project_db:/tmp/$(basename "$CSV_FILE")"

  # Generate the SQL import script
  cat > "$TEMP_SQL" << EOF
USE $DB_NAME;

-- Create temporary table to handle imports
DROP TABLE IF EXISTS temp_import;
CREATE TABLE temp_import (
  $(head -n 1 "$CSV_FILE" | sed 's/,/ VARCHAR(255),/g' | sed 's/$/ VARCHAR(255)/')
);

-- Import the CSV file
LOAD DATA INFILE '/tmp/$(basename "$CSV_FILE")'
INTO TABLE temp_import
FIELDS TERMINATED BY '$CSV_DELIMITER'
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
$([ "$SKIP_HEADER" = true ] && echo "IGNORE 1 ROWS")
;

-- Display the imported data for verification
SELECT * FROM temp_import LIMIT 10;

-- Ask for confirmation before copying to real table
-- You will need to manually run:
-- INSERT INTO $TARGET_TABLE SELECT * FROM temp_import;
-- DROP TABLE temp_import;
EOF

  echo -e "${CYAN}Executing import...${NC}"
  docker exec -i java_project_db mariadb -u "$DB_USER" -p"$DB_PASS" < "$TEMP_SQL"

  if [ $? -eq 0 ]; then
    echo -e "${GREEN}CSV data imported to temporary table.${NC}"
    echo -e "${YELLOW}To complete the import, connect to the database and run:${NC}"
    echo -e "${CYAN}INSERT INTO $TARGET_TABLE SELECT * FROM temp_import;${NC}"
    echo -e "${CYAN}DROP TABLE temp_import;${NC}"
  else
    echo -e "${RED}Import failed.${NC}"
  fi

  # Clean up
  rm "$TEMP_SQL"
}

# Parse additional arguments
while [ $# -gt 0 ]; do
  case "$1" in
    --db-name=*)
      DB_NAME="${1#*=}"
      MAVEN_PROPS="$MAVEN_PROPS -Ddb.name=$DB_NAME"
      ;;
    --db-user=*)
      DB_USER="${1#*=}"
      MAVEN_PROPS="$MAVEN_PROPS -Ddb.username=$DB_USER"
      ;;
    --db-pass=*)
      DB_PASS="${1#*=}"
      MAVEN_PROPS="$MAVEN_PROPS -Ddb.password=$DB_PASS"
      ;;
    --db-root=*)
      DB_ROOT="${1#*=}"
      MAVEN_PROPS="$MAVEN_PROPS -Ddb.root.password=$DB_ROOT"
      ;;
    --env=*)
      ENV_TYPE="${1#*=}"
      MAVEN_PROFILES="$MAVEN_PROFILES,$ENV_TYPE"
      ;;
    --csv=*)
      CSV_FILE="${1#*=}"
      ;;
    --table=*)
      TARGET_TABLE="${1#*=}"
      ;;
    --delimiter=*)
      CSV_DELIMITER="${1#*=}"
      ;;
    --skip-header)
      SKIP_HEADER=true
      ;;
    *)
      # Unknown argument, just pass to Maven
      MAVEN_PROPS="$MAVEN_PROPS $1"
      ;;
  esac
  shift
done

# Handle setup operation for interactive mode
if [ "$OPERATION" = "setup" ]; then
  interactive_setup
  exit 0
fi

# Handle CSV import operation
if [ "$OPERATION" = "import" ]; then
  # Source .env file if it exists for credentials
  if [ -f ".env" ]; then
    source .env
    DB_NAME=${DB_NAME:-$DB_NAME}
    DB_USER=${DB_USER:-$DB_USERNAME}
    DB_PASS=${DB_PASS:-$DB_PASSWORD}
  fi

  import_csv
  exit 0
fi

# Source .env file if it exists for regular operations
if [ -f ".env" ]; then
  echo "Loading environment from .env file"
  source .env

  # Export variables for Docker Compose
  export DB_ROOT_PASSWORD DB_NAME DB_USERNAME DB_PASSWORD

  # Only use .env values if command line params weren't provided
  if [ -z "$DB_NAME" ] && [ -n "$DB_NAME" ]; then
    MAVEN_PROPS="$MAVEN_PROPS -Ddb.name=$DB_NAME"
  fi
  if [ -z "$DB_USER" ] && [ -n "$DB_USERNAME" ]; then
    MAVEN_PROPS="$MAVEN_PROPS -Ddb.username=$DB_USERNAME"
  fi
  if [ -z "$DB_PASS" ] && [ -n "$DB_PASSWORD" ]; then
    MAVEN_PROPS="$MAVEN_PROPS -Ddb.password=$DB_PASSWORD"
  fi
  if [ -z "$DB_ROOT" ] && [ -n "$DB_ROOT_PASSWORD" ]; then
    MAVEN_PROPS="$MAVEN_PROPS -Ddb.root.password=$DB_ROOT_PASSWORD"
  fi

  # Add environment type if specified
  if [ -n "$ENV_TYPE" ]; then
    MAVEN_PROFILES="$MAVEN_PROFILES,$ENV_TYPE"
  fi
fi

# Set operation-specific profiles
case "$OPERATION" in
  start)
    MAVEN_PROFILES="$MAVEN_PROFILES,db-start"
    ;;
  stop)
    MAVEN_PROFILES="$MAVEN_PROFILES,db-stop"
    ;;
  sync)
    MAVEN_PROFILES="$MAVEN_PROFILES,db-sync"
    ;;
  migrate)
    MAVEN_PROFILES="$MAVEN_PROFILES,db-migrate"
    ;;
  info)
    MAVEN_PROFILES="$MAVEN_PROFILES,db-info"
    ;;
  restart)
    MAVEN_PROFILES="$MAVEN_PROFILES,db-stop,db-start,db-migrate"
    ;;
  *)
    echo "Unknown operation: $OPERATION"
    exit 1
    ;;
esac

# Remove initial comma if present
MAVEN_PROFILES=${MAVEN_PROFILES#,}

# Execute Maven command
echo "Running: mvn process-resources -P$MAVEN_PROFILES $MAVEN_PROPS"
if [ -f "./mvnw" ]; then
  ./mvnw process-resources -P$MAVEN_PROFILES $MAVEN_PROPS
else
  mvn process-resources -P$MAVEN_PROFILES $MAVEN_PROPS
fi