-- 01-init.sql: Database initialization script with debug output
-- Place this in docker-entrypoint-initdb.d/01-init.sql

-- Log what variables we received
SELECT 'DEBUG: Starting initialization script' as message;
SELECT CONCAT('DEBUG: Using database name: ', '${MARIADB_DATABASE}') as message;
SELECT CONCAT('DEBUG: Using username: ', '${MARIADB_USER}') as message;

-- Create the database if it doesn't exist already
CREATE DATABASE IF NOT EXISTS ${MARIADB_DATABASE};
SELECT CONCAT('DEBUG: Database ', '${MARIADB_DATABASE}', ' created or already exists') as message;

-- Create the user with proper host permissions
CREATE USER IF NOT EXISTS '${MARIADB_USER}'@'%' IDENTIFIED BY '${MARIADB_PASSWORD}';
SELECT CONCAT('DEBUG: User ', '${MARIADB_USER}', '@''%'' created or already exists') as message;

-- Grant permissions to the user for the database
GRANT ALL PRIVILEGES ON ${MARIADB_DATABASE}.* TO '${MARIADB_USER}'@'%';
SELECT 'DEBUG: Permissions granted to user for any host' as message;

-- Grant permissions to the user for localhost access
CREATE USER IF NOT EXISTS '${MARIADB_USER}'@'localhost' IDENTIFIED BY '${MARIADB_PASSWORD}';
GRANT ALL PRIVILEGES ON ${MARIADB_DATABASE}.* TO '${MARIADB_USER}'@'localhost';
SELECT 'DEBUG: Permissions granted to user for localhost' as message;

-- Grant permissions to the user for Docker network access (common Docker subnet)
CREATE USER IF NOT EXISTS '${MARIADB_USER}'@'172.%' IDENTIFIED BY '${MARIADB_PASSWORD}';
GRANT ALL PRIVILEGES ON ${MARIADB_DATABASE}.* TO '${MARIADB_USER}'@'172.%';
SELECT 'DEBUG: Permissions granted to user for Docker network (172.*)' as message;

-- Apply privileges
FLUSH PRIVILEGES;
SELECT 'DEBUG: Privileges flushed - initialization complete' as message;

-- Show current users for verification
SELECT User, Host FROM '${MARIADB_DATABASE}'.user WHERE User = '${MARIADB_USER}';
SELECT 'DEBUG: Above shows all host entries for this user' as message;