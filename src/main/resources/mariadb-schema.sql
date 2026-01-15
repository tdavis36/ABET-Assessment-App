-- MariaDB Compatible Schema for ABET Assessment App
-- Run this to create/recreate your database structure

-- Drop tables in correct order (respecting foreign keys)
DROP TABLE IF EXISTS measure;
DROP TABLE IF EXISTS course_indicator;
DROP TABLE IF EXISTS course_instructor;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS performance_indicator;
DROP TABLE IF EXISTS student_outcome;
DROP TABLE IF EXISTS semester;
DROP TABLE IF EXISTS program_user;
DROP TABLE IF EXISTS program;
DROP TABLE IF EXISTS users;

-- Users table
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                       name_first VARCHAR(100) NOT NULL,
                       name_last VARCHAR(100) NOT NULL,
                       name_title VARCHAR(50) NULL,
    -- From BaseEntity
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                       version BIGINT DEFAULT 0,
                       deleted BOOLEAN DEFAULT FALSE NOT NULL,
                       deleted_at TIMESTAMP NULL,
    -- Users-specific
                       is_active BOOLEAN DEFAULT TRUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Program table
CREATE TABLE program (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         program_name VARCHAR(255) NOT NULL,
                         institution VARCHAR(255) NOT NULL,
    -- From BaseEntity
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                         version BIGINT DEFAULT 0,
                         deleted BOOLEAN DEFAULT FALSE NOT NULL,
                         deleted_at TIMESTAMP NULL,
    -- Program-specific
                         is_active BOOLEAN DEFAULT TRUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ProgramUser table
CREATE TABLE program_user (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              isAdmin BOOLEAN NOT NULL DEFAULT FALSE,
                              program_id BIGINT NOT NULL,
                              user_id BIGINT NOT NULL,
    -- From BaseEntity
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                              version BIGINT DEFAULT 0,
                              deleted BOOLEAN DEFAULT FALSE NOT NULL,
                              deleted_at TIMESTAMP NULL,
    -- ProgramUser-specific
                              is_active BOOLEAN DEFAULT TRUE NOT NULL,
                              FOREIGN KEY (program_id) REFERENCES program(id) ON DELETE CASCADE,
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                              INDEX idx_program_user (program_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Semester table
CREATE TABLE semester (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(50) NOT NULL,
                          code VARCHAR(20) UNIQUE NOT NULL,
                          type VARCHAR(10) NOT NULL,
                          status VARCHAR(15) NOT NULL DEFAULT 'UPCOMING',
                          start_date DATE NULL,
                          end_date DATE NULL,
                          academic_year INT NULL,
                          description VARCHAR(500) NULL,
                          program_id BIGINT NOT NULL,
                          is_current BOOLEAN NOT NULL DEFAULT FALSE,
    -- From BaseEntity
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                          version BIGINT DEFAULT 0,
                          deleted BOOLEAN DEFAULT FALSE NOT NULL,
                          deleted_at TIMESTAMP NULL,
                          FOREIGN KEY (program_id) REFERENCES program(id) ON DELETE CASCADE,
                          INDEX idx_semester_program (program_id),
                          INDEX idx_semester_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Student outcomes table
CREATE TABLE student_outcome (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 out_number TINYINT NOT NULL,
                                 out_value TINYINT NULL,
                                 out_description TEXT NOT NULL,
                                 evaluation TEXT NULL,
                                 semester_id BIGINT NOT NULL,
    -- From BaseEntity
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                                 version BIGINT DEFAULT 0,
                                 deleted BOOLEAN DEFAULT FALSE NOT NULL,
                                 deleted_at TIMESTAMP NULL,
    -- StudentOutcome-specific
                                 is_active BOOLEAN DEFAULT TRUE NOT NULL,
                                 FOREIGN KEY (semester_id) REFERENCES semester(id) ON DELETE CASCADE,
                                 INDEX idx_outcome_semester (semester_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Performance indicators table
CREATE TABLE performance_indicator (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       ind_number TINYINT NOT NULL,
                                       ind_value TINYINT NULL,
                                       ind_description TEXT NOT NULL,
                                       evaluation TEXT NULL,
                                       student_outcome_id BIGINT NOT NULL,
                                       threshold_percentage DECIMAL(5,2) DEFAULT 70.00,
    -- From BaseEntity
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                                       version BIGINT DEFAULT 0,
                                       deleted BOOLEAN DEFAULT FALSE NOT NULL,
                                       deleted_at TIMESTAMP NULL,
    -- PerformanceIndicator-specific
                                       is_active BOOLEAN DEFAULT TRUE NOT NULL,
                                       FOREIGN KEY (student_outcome_id) REFERENCES student_outcome(id) ON DELETE CASCADE,
                                       INDEX idx_indicator_outcome (student_outcome_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Course table
CREATE TABLE course (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        course_code VARCHAR(20) NOT NULL,
                        course_name VARCHAR(255) NOT NULL,
                        course_description TEXT NOT NULL,
                        semester_id BIGINT NOT NULL,
                        student_count INT NULL,
    -- From BaseEntity
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                        version BIGINT DEFAULT 0,
                        deleted BOOLEAN DEFAULT FALSE NOT NULL,
                        deleted_at TIMESTAMP NULL,
    -- Course-specific
                        is_active BOOLEAN DEFAULT TRUE NOT NULL,
                        FOREIGN KEY (semester_id) REFERENCES semester(id) ON DELETE CASCADE,
                        INDEX idx_course_semester (semester_id),
                        INDEX idx_course_code (course_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CourseInstructor table
CREATE TABLE course_instructor (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   program_user_id BIGINT NOT NULL,
                                   course_id BIGINT NOT NULL,
    -- From BaseEntity
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                                   version BIGINT DEFAULT 0,
                                   deleted BOOLEAN DEFAULT FALSE NOT NULL,
                                   deleted_at TIMESTAMP NULL,
    -- CourseInstructor-specific
                                   is_active BOOLEAN DEFAULT TRUE NOT NULL,
                                   FOREIGN KEY (program_user_id) REFERENCES program_user(id) ON DELETE CASCADE,
                                   FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
                                   INDEX idx_instructor_program_user (program_user_id),
                                   INDEX idx_instructor_course (course_id),
                                   UNIQUE KEY unique_instructor_course (program_user_id, course_id, is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CourseIndicator table
CREATE TABLE course_indicator (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  course_id BIGINT NOT NULL,
                                  indicator_id BIGINT NOT NULL,
    -- From BaseEntity
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                                  version BIGINT DEFAULT 0,
                                  deleted BOOLEAN DEFAULT FALSE NOT NULL,
                                  deleted_at TIMESTAMP NULL,
    -- CourseIndicator-specific
                                  is_active BOOLEAN DEFAULT TRUE NOT NULL,
                                  FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
                                  FOREIGN KEY (indicator_id) REFERENCES performance_indicator(id) ON DELETE CASCADE,
                                  INDEX idx_course_indicator_course (course_id),
                                  INDEX idx_course_indicator_indicator (indicator_id),
                                  UNIQUE KEY unique_course_indicator (course_id, indicator_id, is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Measure table
CREATE TABLE measure (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         course_indicator_id BIGINT NOT NULL,
                         measure_description TEXT NOT NULL,
                         observation TEXT NULL,
                         recommended_action TEXT NULL,
                         fcar TEXT NULL,
                         met SMALLINT NULL,
                         exceeded SMALLINT NULL,
                         below SMALLINT NULL,
                         m_status VARCHAR(10) DEFAULT 'InProgress' NOT NULL,
    -- From BaseEntity
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                         version BIGINT DEFAULT 0,
                         deleted BOOLEAN DEFAULT FALSE NOT NULL,
                         deleted_at TIMESTAMP NULL,
    -- Measure-specific
                         is_active BOOLEAN DEFAULT TRUE NOT NULL,
                         FOREIGN KEY (course_indicator_id) REFERENCES course_indicator(id) ON DELETE CASCADE,
                         INDEX idx_measure_course_indicator (course_indicator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;