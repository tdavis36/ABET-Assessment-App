# ABET Assessment App

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Project Overview

The ABET Assessment Application streamlines the ABET accreditation process by automating assessment data collection, analysis, and reporting. It provides a comprehensive platform for educational institutions to manage their ABET assessment needs.

## Features

### Core Features

1. **User Authentication & Role Management**
   - Secure login system with role-based access control
   - Different permission levels for administrators and professors
   - User account management with activation status

2. **Course-to-Professor Mapping**
   - Associate courses with professors for streamlined assessment
   - Manage course metadata and learning objectives
   - Track course-level assessment data

3. **Course Assessment & Reporting**
   - Create, edit, and submit Faculty Course Assessment Reports (FCARs)
   - Track assessment status (Draft, Submitted, Approved, Rejected)
   - Document learning outcomes and indicators

4. **Database & Export Functionality**
   - Store all assessment data in a structured database
   - Export data in standard formats (CSV, Excel)
   - Generate reports for accreditation purposes

### Advanced Features

1. **Admin Dashboard**
   - Comprehensive overview of assessment status
   - User and course management
   - Assessment approval workflow

2. **Professor Dashboard**
   - View assigned courses and FCARs
   - Track FCAR submission status
   - Access to historical assessment data

3. **Development Tools**
   - Automated environment setup with Python script
   - Docker-based database management
   - Database migration with Flyway
   - Shell integration for common operations

4. **Data Visualization**
   - View assessment results with visual indicators
   - Track performance trends over time
   - Monitor assessment completion status

## Technology Stack

### Frontend
- Jakarta Server Pages (JSP)
- Servlets
- HTML/CSS/JavaScript

### Backend
- Java 17
- Jakarta EE 10
- Jetty Web Server

### Database
- MariaDB 10.11
- Flyway Migration
- HikariCP Connection Pool

### Development Tools
- Maven
- Python (for setup and management scripts)
- Docker (for database containerization)
- Git Hooks (for workflow automation)

## Getting Started

### Requirements
- Java JDK 17 or newer
- Apache Maven
- Python 3.6 or newer
- Docker

### Quick Setup

```bash
# Clone the repository
git clone https://github.com/tdavis36/ABET-Assessment-App.git
cd ABET-Assessment-App

# Set up the complete development environment
python setup.py dev setup

# Start the application
mvn jetty:run
```

This will:
- Configure your environment
- Start a Docker-based MariaDB instance
- Run database migrations
- Set up Git hooks
- Install shell aliases

### Access the Application

Once running, access the application at:
```
http://localhost:8081
```

Default login credentials:
- **Admin**: alice.admin@example.edu / hashed_pw_admin
- **Professor**: bob.prof@example.edu / hashed_pw_prof

## Database Management

The application includes a comprehensive database management system:

```bash
# Start the database
python setup.py db start

# Run migrations
python setup.py db migrate

# View migration status
python setup.py db info

# Connect to database
python setup.py db connect

# Restart database
python setup.py db restart

# Clean database (delete all data)
python setup.py db clean
```

For more information on database management, see the [Database Management](https://github.com/tdavis36/ABET-Assessment-App/wiki/Database-Management) wiki page.

## Shell Integration

The setup script installs shell aliases for common operations:

### Linux/macOS
```bash
# After running: python setup.py shell install
source ~/.bashrc   # or ~/.bash_profile

# Now you can use aliases like:
dbstart    # Start the database
dbstop     # Stop the database
dbmigrate  # Run migrations
dbconnect  # Connect to database
dbhelp     # Show all available commands
```

### Windows PowerShell
```powershell
# After running: python setup.py shell install
. $PROFILE

# Now you can use aliases like:
dbstart    # Start the database
dbstop     # Stop the database
dbmigrate  # Run migrations
dbconnect  # Connect to database
dbhelp     # Show all available commands
```

## Documentation

Comprehensive documentation is available in the [GitHub Wiki](https://github.com/tdavis36/ABET-Assessment-App/wiki).

Key documentation pages:
- [Home](https://github.com/tdavis36/ABET-Assessment-App/wiki/Home)
- [Architecture](https://github.com/tdavis36/ABET-Assessment-App/wiki/Architecture)
- [Database Management](https://github.com/tdavis36/ABET-Assessment-App/wiki/Database-Management)
- [Setup and Installation](https://github.com/tdavis36/ABET-Assessment-App/wiki/Setup-and-Installation)

## Contributing

Contributions are welcome! Please open an issue or PR for feedback, improvements, or bug reports.

### Development Workflow

1. Fork the repository
2. Create a feature branch
3. Implement your changes
4. Run tests and ensure all checks pass
5. Submit a pull request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.