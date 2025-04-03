## Issue #1: Consolidate duplicate FCAR retrieval methods

**Labels:** refactoring, code-quality

**Description:**
Currently, there are overlapping methods for retrieving FCARs across different classes in the codebase:

- `FCARController` has `getFCAR()`, `getFCARsForCourse()`, and `getFCARsByProfessor()`
- `FCARFactory` has nearly identical methods with the same names
- `ProfessorServlet` contains a static `getAllFCARs()` method that bypasses the controller

This creates code duplication and violates the architecture's separation of concerns, especially in `ProfessorServlet.getAllFCARs()` which directly accesses `FCARFactory.getAllFCARs()`.

**Steps to reproduce:**
Examine the following files:
- `FCARController.java`
- `FCARFactory.java`
- `ProfessorServlet.java`

**Proposed solution:**
Use only `FCARController` as the single access point for all FCAR operations:
1. Keep the implementation details in `FCARFactory`
2. Have `FCARController` methods delegate to `FCARFactory`
3. Update all servlets to use `FCARController` methods instead of directly accessing `FCARFactory`
4. Remove the static `getAllFCARs()` method from `ProfessorServlet`

**Priority:** High

---

## Issue #2: Unify FCAR storage mechanisms to prevent data inconsistencies

**Labels:** refactoring, bug-risk

**Description:**
The application currently has two separate mechanisms for storing FCARs:
1. Primary storage in `FCARFactory` with in-memory `fcarMap`
2. Secondary storage in `SessionStorageHandler` for session-based FCAR data

These two storage systems aren't synchronized, which could lead to data inconsistencies where changes made through one system aren't reflected in the other.

**Steps to reproduce:**
1. Examine `FCARFactory.java` and `SessionStorageHandler.java`
2. Note that updates made to FCARs through `FCARFactory` aren't automatically reflected in session storage and vice versa

**Proposed solution:**
Either:
1. Remove `SessionStorageHandler` entirely and use only `FCARFactory`
2. Or implement proper synchronization between the two systems
3. Or modify `SessionStorageHandler` to serve as a cache that delegates to `FCARFactory` for all operations

**Priority:** High

---

## Issue #3: Centralize dashboard data generation to eliminate duplication

**Labels:** refactoring, code-quality

**Description:**
There's redundant functionality for generating dashboard data:
- `DisplaySystemController.generateDashboardData()` creates comprehensive dashboards
- `ProfessorServlet` and `AdminServlet` also contain similar code that manually gathers FCAR data

This duplication makes maintenance difficult and increases the risk of inconsistent behavior across different parts of the application.

**Steps to reproduce:**
Examine the dashboard generation logic in:
- `DisplaySystemController.java`
- `ProfessorServlet.java`
- `AdminServlet.java`

**Proposed solution:**
1. Move all dashboard data generation to `DisplaySystemController`
2. Modify servlets to use `DisplaySystemController` for retrieving dashboard data
3. Add any missing functionality from servlets to `DisplaySystemController` methods

**Priority:** Medium

---

## Issue #4: Streamline FCAR submission process to eliminate redundant implementations

**Labels:** refactoring, code-quality

**Description:**
The FCAR submission process is implemented in multiple places:
- In `FCAR` class with methods like `submit()`, `approve()`, etc.
- In `FCARController` with wrapper methods like `submitFCAR()`, `approveFCAR()`, etc.
- In `ProfessorServlet` with its own FCAR submission logic

This creates confusion about the correct way to submit FCARs and increases the risk of inconsistent behavior.

**Steps to reproduce:**
Examine the submission logic in:
- `FCAR.java`
- `FCARController.java`
- `ProfessorServlet.java`

**Proposed solution:**
1. Keep the state transition logic in the `FCAR` class
2. Ensure all submissions go through the `FCARController`
3. Update servlets to only use the `FCARController` methods
4. Remove redundant submission logic from servlets

**Priority:** Medium

---

## Issue #5: Implement proper centralized authentication system

**Labels:** enhancement, security

**Description:**
Currently, the application has:
- A base `User.authenticate()` method that checks username and password
- No centralized authentication system
- Authentication logic scattered across servlets

This creates inconsistent authentication behavior and makes it difficult to implement security improvements.

**Steps to reproduce:**
Examine:
- `User.java` - containing `authenticate()` method
- Various servlet classes that implement their own authentication logic

**Proposed solution:**
1. Create a centralized `AuthenticationService` class
2. Implement proper session management
3. Update servlets to use the centralized authentication service
4. Add proper error handling for authentication failures

**Priority:** High

---

## Issue #6: Consolidate duplicated database scripts for Windows and Unix

**Labels:** refactoring, code-quality

**Description:**
The `db-maven.sh` and `db-maven.bat` scripts have almost identical functionality but for different platforms. They both contain similar functions like `interactive_setup`, `create_env_file`, and `import_csv`.

This duplication makes maintenance difficult and increases the risk of divergent behavior between platforms.

**Steps to reproduce:**
Compare:
- `db-maven.sh`
- `db-maven.bat`

**Proposed solution:**
1. Create a cross-platform script framework
2. Implement platform detection to execute the appropriate commands
3. Share common functionality through a unified approach
4. Consider using a tool like Gradle or Maven to handle platform differences

**Priority:** Low

---

## Issue #7: Consolidate duplicated Git hooks setup scripts

**Labels:** refactoring, code-quality

**Description:**
The `setup-hooks.sh` and `setup-hooks.bat` scripts create identical Git hooks but for different platforms. This duplication increases maintenance overhead.

**Steps to reproduce:**
Compare:
- `setup-hooks.sh`
- `setup-hooks.bat`

**Proposed solution:**
1. Create a single setup script with platform detection
2. Use Git's built-in cross-platform capabilities where possible
3. Consider implementing the hooks directly in JavaScript or a language that's more platform-independent

**Priority:** Low

---

## Issue #8: Integrate Task Management system with FCAR workflow

**Labels:** enhancement, feature

**Description:**
The `TaskController` and related code appears to be disconnected from the rest of the application. It contains functionality for managing tasks but this isn't integrated with the FCAR or user management systems.

This creates a situation where task management exists as an isolated feature with no connection to the core functionality of the application.

**Steps to reproduce:**
Examine:
- `Task.java`
- `TaskController.java`
- The lack of references to these classes in FCAR-related code

**Proposed solution:**
1. Define clear relationships between tasks and FCARs
2. Update the data models to reflect these relationships
3. Integrate task management into the FCAR workflow
4. Add task-related UI elements to the FCAR management pages

**Priority:** Medium

---

## Issue #9: Create database schema for FCAR management system

**Labels:** enhancement, database, feature

**Description:**
The application currently stores all FCARs in memory using `fcarMap` in `FCARFactory`, which means data is lost when the application restarts. We need to implement proper database persistence for FCARs.

The database schema should support:
- FCAR records with all existing fields
- User records (Professors and Admins)
- Course records
- Relationships between these entities

**Steps to reproduce:**
1. Examine current in-memory storage in `FCARFactory.java`
2. Note the lack of database persistence for FCARs

**Proposed solution:**
1. Create SQL migration files in `src/main/resources/db/migration/` for:
   - Users table (with discriminator for user type)
   - Courses table
   - FCARs table
   - AssessmentMethods table (related to FCARs)
   - StudentOutcomes table (related to FCARs)
   - ImprovementActions table (related to FCARs)
2. Implement proper entity classes with JPA/Hibernate annotations
3. Create database repositories for each entity
4. Update controllers to use repositories instead of in-memory maps

**Priority:** High

---

## Issue #10: Configure database connection pooling for improved performance

**Labels:** enhancement, database, performance

**Description:**
The application is set up to use MariaDB but lacks proper connection pooling configuration. Connection pooling is essential for production environments to handle multiple concurrent requests efficiently.

**Steps to reproduce:**
1. Examine `docker-compose.yml` for database configuration
2. Note that the application doesn't have a connection pool configured

**Proposed solution:**
1. Add HikariCP as a dependency in `pom.xml`
2. Configure connection pool settings in a new `application.properties` file:
   - Maximum pool size
   - Minimum idle connections
   - Connection timeout
   - Idle timeout
   - Max lifetime
3. Update database connection code to use the connection pool
4. Add metrics and monitoring for connection pool health

**Priority:** Medium

---

## Issue #11: Establish proper database migration strategy using Flyway

**Labels:** enhancement, database, devops

**Description:**
While the project includes Flyway for database migrations, there's no clear strategy for managing migrations across environments. We need a comprehensive approach to handle schema changes reliably.

**Steps to reproduce:**
1. Examine `pom.xml` for Flyway configuration
2. Note that there are no migration files in `src/main/resources/db/migration/`

**Proposed solution:**
1. Create a baseline migration script for the initial schema
2. Establish naming convention for migration files (e.g., `V1__Create_initial_schema.sql`)
3. Set up separate profiles for different environments (dev, test, prod)
4. Configure Flyway to handle baseline migrations for new environments
5. Add documentation for how to create and apply new migrations
6. Implement version tracking in the application to ensure compatibility

**Priority:** High

---

## Issue #12: Implement automated database backup and recovery procedure

**Labels:** enhancement, database, devops

**Description:**
The application lacks a backup and recovery strategy for the database. This is critical for ensuring data safety and disaster recovery capabilities.

**Steps to reproduce:**
N/A - Feature is missing

**Proposed solution:**
1. Create database backup scripts for:
   - Full backups (daily)
   - Incremental backups (hourly)
2. Configure automated backup schedule using cron jobs
3. Implement secure storage for backup files
4. Create recovery procedures and documentation
5. Set up backup verification to ensure backup integrity
6. Add monitoring for backup job success/failure

**Priority:** Medium

---

## Issue #13: Create database access layer using repository pattern

**Labels:** enhancement, database, architecture

**Description:**
The application needs a proper database access layer using the repository pattern to separate business logic from data access concerns.

**Steps to reproduce:**
N/A - Feature is missing

**Proposed solution:**
1. Create repository interfaces for each entity:
   - `UserRepository`
   - `CourseRepository`
   - `FCARRepository`
2. Implement concrete repository classes using JDBC or JPA/Hibernate
3. Add transaction management
4. Implement proper error handling and logging
5. Create unit tests for repositories
6. Update controllers to use repositories

**Priority:** High

---

## Issue #14: Optimize database performance with proper indexing

**Labels:** enhancement, database, performance

**Description:**
The database schema needs proper indexing to ensure good query performance, especially as the data volume grows.

**Steps to reproduce:**
N/A - Feature is missing

**Proposed solution:**
1. Analyze query patterns from the application
2. Create migration script to add appropriate indexes:
   - Primary key indexes (automatically created)
   - Foreign key indexes
   - Combined indexes for frequently queried fields
   - Full-text indexes if needed
3. Benchmark query performance before and after indexing
4. Document indexing strategy
5. Add monitoring for slow queries

**Priority:** Medium

---

## Issue #15: Enhance database security with best practices implementation

**Labels:** enhancement, database, security

**Description:**
The current database setup in `docker-compose.yml` lacks comprehensive security measures. We need to implement database security best practices to protect sensitive data.

**Steps to reproduce:**
1. Examine `docker-compose.yml` for database configuration
2. Note that security settings are minimal

**Proposed solution:**
1. Implement proper password policies for database users
2. Configure TLS/SSL for database connections
3. Implement row-level security for multi-tenant data
4. Set up database user roles with least privilege access
5. Add auditing for sensitive data access
6. Configure network security for database access
7. Implement data encryption for sensitive fields
8. Create security documentation and guidelines

**Priority:** High

---

## Issue #16: Implement database change tracking and auditing system

**Labels:** enhancement, database, security

**Description:**
The application needs a system to track changes to important database records, especially FCARs, for auditing and compliance purposes.

**Steps to reproduce:**
N/A - Feature is missing

**Proposed solution:**
1. Create audit tables for:
   - FCAR changes
   - User account changes
   - Security-related events
2. Implement triggers or application-level code to capture:
   - Who made the change
   - When the change was made
   - What was changed (old and new values)
   - IP address of the user
3. Add audit log viewer in the admin interface
4. Implement retention policies for audit data
5. Add reporting capabilities for audit data

**Priority:** Medium

---

## Issue #17: Configure database for different deployment environments

**Labels:** enhancement, database, devops

**Description:**
The database configuration is currently hardcoded for a single environment. We need to implement proper configuration for multiple environments (development, testing, production).

**Steps to reproduce:**
1. Examine `docker-compose.yml` and `.env` handling
2. Note the lack of environment-specific configurations

**Proposed solution:**
1. Create environment-specific configurations:
   - Development: MariaDB in Docker with debug settings
   - Testing: Ephemeral database with test data
   - Production: Robust configuration with security and performance optimizations
2. Update scripts to load the appropriate configuration
3. Configure CI/CD pipelines to use the correct environment
4. Add documentation for environment setup
5. Implement automated environment provisioning

**Priority:** Medium

