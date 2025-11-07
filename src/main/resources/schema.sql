-- H2 Compatible Schema for ABET Assessment App
CREATE SCHEMA IF NOT EXISTS public;
SET SCHEMA PUBLIC;

-- User table
DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       name_first VARCHAR(100) NOT NULL,
                       name_last VARCHAR(100) NOT NULL,
                       name_title VARCHAR(50) NULL,
    -- From BaseEntity
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       version BIGINT DEFAULT 0,
                       deleted BOOLEAN DEFAULT FALSE NOT NULL,
                       deleted_at TIMESTAMP NULL,
    -- Users-specific
                       is_active BOOLEAN DEFAULT TRUE NOT NULL
);

-- Program table
CREATE TABLE program (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         program_name VARCHAR(255) NOT NULL,
                         institution VARCHAR(255) NOT NULL,
    -- From BaseEntity
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         version BIGINT DEFAULT 0,
                         deleted BOOLEAN DEFAULT FALSE NOT NULL,
                         deleted_at TIMESTAMP NULL,
    -- Program-specific
                         is_active BOOLEAN DEFAULT TRUE NOT NULL
);

-- ProgramUser table
CREATE TABLE program_user (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              isAdmin BOOLEAN NOT NULL DEFAULT FALSE,
                              program_id BIGINT NOT NULL,
                              user_id BIGINT NOT NULL,
    -- From BaseEntity
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                              version BIGINT DEFAULT 0,
                              deleted BOOLEAN DEFAULT FALSE NOT NULL,
                              deleted_at TIMESTAMP NULL,
    -- ProgramUser-specific
                              is_active BOOLEAN DEFAULT TRUE NOT NULL,
                              FOREIGN KEY (program_id) REFERENCES program(id),
                              FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Semester table
CREATE TABLE semester (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          season VARCHAR(6) NOT NULL,
                          semester_year SMALLINT NOT NULL,
                          program_id BIGINT NOT NULL,
    -- From BaseEntity
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          version BIGINT DEFAULT 0,
                          deleted BOOLEAN DEFAULT FALSE NOT NULL,
                          deleted_at TIMESTAMP NULL,
    -- Semester-specific
                          is_active BOOLEAN DEFAULT TRUE NOT NULL,
                          FOREIGN KEY (program_id) REFERENCES program(id)
);

-- Student outcomes table
CREATE TABLE student_outcome (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 out_number TINYINT NOT NULL,
    -- Number represents order of outcomes
                                 out_value TINYINT NULL,
    -- Value represents number assigned during evaluation
                                 out_description TEXT NOT NULL,
                                 evaluation TEXT NULL,
                                 semester_id BIGINT NOT NULL,
    -- From BaseEntity
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                 version BIGINT DEFAULT 0,
                                 deleted BOOLEAN DEFAULT FALSE NOT NULL,
                                 deleted_at TIMESTAMP NULL,
    -- StudentOutcome-specific
                                 is_active BOOLEAN DEFAULT TRUE NOT NULL,
                                 FOREIGN KEY (semester_id) REFERENCES semester(id)
);

-- Performance indicators table
CREATE TABLE performance_indicator (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       ind_number TINYINT NOT NULL,
    -- Number represents order of outcomes
                                       ind_value TINYINT NULL,
    -- Value represents number assigned during evaluation
                                       ind_description TEXT NOT NULL,
                                       evaluation TEXT NULL,
                                       student_outcome_id BIGINT NOT NULL,
                                       threshold_percentage DECIMAL(5,2) DEFAULT 70.00,
    -- From BaseEntity
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                       version BIGINT DEFAULT 0,
                                       deleted BOOLEAN DEFAULT FALSE NOT NULL,
                                       deleted_at TIMESTAMP NULL,
    -- PerformanceIndicator-specific
                                       is_active BOOLEAN DEFAULT TRUE NOT NULL,
                                       FOREIGN KEY (student_outcome_id) REFERENCES student_outcome(id)
);

-- Course table
CREATE TABLE course (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        course_code VARCHAR(20) NOT NULL,
                        course_name VARCHAR(255) NOT NULL,
                        course_description TEXT NOT NULL,
                        semester_id BIGINT NOT NULL,
    -- From BaseEntity
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        version BIGINT DEFAULT 0,
                        deleted BOOLEAN DEFAULT FALSE NOT NULL,
                        deleted_at TIMESTAMP NULL,
    -- Course-specific
                        is_active BOOLEAN DEFAULT TRUE NOT NULL,
                        FOREIGN KEY (semester_id) REFERENCES semester(id)
);

-- CourseInstructor table
CREATE TABLE course_instructor (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   programUser_id BIGINT NOT NULL,
                                   course_id BIGINT NOT NULL,
    -- From BaseEntity
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                   version BIGINT DEFAULT 0,
                                   deleted BOOLEAN DEFAULT FALSE NOT NULL,
                                   deleted_at TIMESTAMP NULL,
    -- CourseInstructor-specific
                                   is_active BOOLEAN DEFAULT TRUE NOT NULL,
                                   FOREIGN KEY (programUser_id) REFERENCES program_user(id),
                                   FOREIGN KEY (course_id) REFERENCES course(id)
);

-- CourseIndicator table
CREATE TABLE course_indicator (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  course_id BIGINT NOT NULL,
                                  indicator_id BIGINT NOT NULL,
    -- From BaseEntity
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                  version BIGINT DEFAULT 0,
                                  deleted BOOLEAN DEFAULT FALSE NOT NULL,
                                  deleted_at TIMESTAMP NULL,
    -- CourseIndicator-specific
                                  is_active BOOLEAN DEFAULT TRUE NOT NULL,
                                  FOREIGN KEY (course_id) REFERENCES course(id),
                                  FOREIGN KEY (indicator_id) REFERENCES performance_indicator(id)
);

-- Measure table
CREATE TABLE measure (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         courseIndicator_id BIGINT NOT NULL,
                         measure_description TEXT NOT NULL,
                         observation TEXT NULL,
                         recommended_action TEXT NULL,
                         fcar TEXT NULL,
                         met SMALLINT NULL,
                         exceeded SMALLINT NULL,
                         below SMALLINT NULL,
    -- From BaseEntity
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         version BIGINT DEFAULT 0,
                         deleted BOOLEAN DEFAULT FALSE NOT NULL,
                         deleted_at TIMESTAMP NULL,
    -- Measure-specific
                         is_active BOOLEAN DEFAULT TRUE NOT NULL,
                         FOREIGN KEY (courseIndicator_id) REFERENCES course_indicator(id)
);