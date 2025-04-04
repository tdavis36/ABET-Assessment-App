# ABET Assessment App Code Review

After reviewing the codebase for the ABET Assessment Application, I've identified several areas that need attention regarding best practices, conflicting functionality, and updates needed for the new database setup. Here's my analysis:

## Database Setup Issues

### DataSourceFactory Configuration
The `DataSourceFactory` class creates a HikariCP connection pool for MariaDB, with configuration loaded from environment variables (falling back to a properties file or defaults). This implementation works with the new Docker-based database setup from `setup.py`, but could use some improvements:

1. **Configuration Consistency:**
    - The current code checks for environment variables `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD`, falling back to `database.properties` file if not found
    - This should be synchronized with the `.env` file that `setup.py` creates

2. **Error Recovery:**
    - There's basic error logging when checking database schema, but no robust error handling mechanism if the database is completely unavailable at application startup

3. **Database Lifecycle Management:**
    - The `closeDataSource()` method exists but there's no evidence it's called during application shutdown, which could lead to connection leaks

## Repository Implementation Issues

1. **Inconsistent Repository Pattern:**
    - There's a proper JDBC-based `FCARRepository` implementation of the `IFCARRepository` interface
    - Also an `InMemoryFCARRepository` implementation which may cause confusion about which one should be used

2. **Service Layer:**
    - The `FCARService` directly instantiates a `FCARRepository` instead of using dependency injection, making it difficult to test or switch implementations

3. **ID Type Inconsistency:**
    - The `FCAR` class uses `String` for IDs, but repository methods operate on integers, requiring frequent parsing and potential `NumberFormatException`s

## Inconsistent Data Access Patterns

1. **Multiple Cache Mechanisms:**
    - `SessionStorageHandler` stores FCARs in the session
    - `DisplaySystemController` maintains its own cache for users and courses
    - This could lead to inconsistent data if both mechanisms are used for the same data

2. **Controller Design:**
    - The `FCARController` class is a singleton that manages a service instance
    - The `DisplaySystemController` is also a singleton that references the `FCARController`

3. **FCARFactory Redundancy:**
    - The `FCARFactory` class seems to be a legacy pattern that now just delegates to the repository

## Servlet / MVC Issues

1. **Inconsistent Controller Usage:**
    - Some servlets directly use `FCARFactory` (legacy pattern)
    - Others use `FCARController` or `DisplaySystemController`
    - Inconsistent transaction boundaries and error handling

2. **Frontend Code Mixing:**
    - JSP files contain large amounts of JavaScript mixed with Java code
    - No clear separation between view and controller logic

3. **Session Handling:**
    - `SessionStorageHandler` provides a caching mechanism but isn't consistently used
    - Servlet session management is inconsistent

## Recommended Updates

### Database Configuration

1. **Standardize Environment Variables:**
   ```java
   // Update DataSourceFactory to directly use values from .env
   dbName = (dbName != null) ? dbName : System.getenv("DB_NAME");
   dbUsername = (dbUsername != null) ? dbUsername : System.getenv("DB_USERNAME");
   dbPassword = (dbPassword != null) ? dbPassword : System.getenv("DB_PASSWORD");
   ```

2. **Implement Proper Connection Pooling Lifecycle:**
   ```java
   // Add a shutdown hook to ensure connections are closed
   Runtime.getRuntime().addShutdownHook(new Thread(() -> {
       closeDataSource();
   }));
   ```

3. **Add Better Error Handling:**
   ```java
   // Add more robust error handling for database connection failures
   try {
       // Connection setup code
   } catch (Exception e) {
       logger.severe("Failed to initialize database connection: " + e.getMessage());
       // Consider implementing a retry mechanism or fallback
   }
   ```

### Repository Standardization

1. **Remove InMemoryFCARRepository or Make It Test-Only:**
    - Move `InMemoryFCARRepository` to a test package if it's for testing
    - Otherwise, remove it to avoid confusion

2. **Implement Dependency Injection:**
   ```java
   public class FCARService {
       private final IFCARRepository fcarRepository;
       
       public FCARService(IFCARRepository fcarRepository) {
           this.fcarRepository = fcarRepository;
       }
       
       // Service methods
   }
   ```

3. **Standardize ID Types:**
   ```java
   // Update FCAR to use int for IDs consistently
   private int fcarId;
   
   // Or update repositories to use String IDs consistently
   public FCAR findById(String fcarId) {
       // Implementation
   }
   ```

### Cache and Controller Refactoring

1. **Centralize Caching Strategy:**
    - Define one caching mechanism (application-level or session-level)
    - Implement consistent cache invalidation

2. **Refactor Controller Hierarchy:**
    - Centralize common controller functionality
    - Implement proper dependency injection

3. **Phase Out FCARFactory:**
   ```java
   // Replace FCARFactory calls with direct controller or service calls
   // Instead of:
   FCAR fcar = FCARFactory.getFCAR(fcarId);
   
   // Use:
   FCAR fcar = fcarController.getFCAR(fcarId);
   ```

### Servlet and Frontend Improvements

1. **Separate JavaScript from JSP:**
    - Move JavaScript to separate .js files
    - Use modern frontend patterns (MVC, components)

2. **Standardize Servlet Error Handling:**
   ```java
   try {
       // Operation
   } catch (Exception e) {
       request.setAttribute("errorMessage", e.getMessage());
       request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
   }
   ```

3. **Implement RESTful API Pattern:**
    - Convert servlets to RESTful endpoints
    - Use JSON for data exchange

## Implementation Plan

1. **Phase 1: Database and Repository Refactoring**
    - Update DataSourceFactory
    - Standardize repository pattern
    - Fix ID type inconsistencies

2. **Phase 2: Controller and Service Layer Refactoring**
    - Implement proper dependency injection
    - Standardize caching strategy
    - Remove FCARFactory pattern

3. **Phase 3: Frontend Improvements**
    - Separate JavaScript from JSP
    - Implement better error handling
    - Consider migration to a modern frontend framework

4. **Phase 4: Documentation and Testing**
    - Document the architecture
    - Implement unit and integration tests
    - Ensure proper database connection cleanup

This refactoring will make the application more maintainable, testable, and align it with modern Java web application best practices.