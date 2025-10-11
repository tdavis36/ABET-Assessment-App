-- H2 Compatible Schema for ABET Assessment App
-- User table
CREATE TABLE users (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                email VARCHAR(255) UNIQUE NOT NULL,
                password_hash VARCHAR(255),
                name_first VARCHAR(100) NOT NULL,
                name_last VARCHAR(100) NOT NULL,
                name_title VARCHAR(50) NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE
);

-- Program table
CREATE TABLE program (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                program_name VARCHAR(255) NOT NULL,
                institution VARCHAR(255) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE
);

-- ProgramUser table
CREATE TABLE program_user (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                isAdmin BOOLEAN NOT NULL DEFAULT FALSE,
                program_id BIGINT NOT NULL,
                user_id BIGINT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (program_id) REFERENCES program(id),
                FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Semester table
CREATE TABLE semester (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                season VARCHAR(6) NOT NULL,
                semester_year SMALLINT NOT NULL,
                program_id BIGINT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (program_id) REFERENCES program(id)
);

-- Student outcomes table
CREATE TABLE student_outcome (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                out_number TINYINT NOT NULL,                --Number represents order of outcomes
                out_value TINYINT NULL,                     --Value represents number assigned during evaluation
                out_description TEXT NOT NULL,
                evaluation TEXT NULL,
                semester_id BIGINT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (semester_id) REFERENCES semester(id)
);

-- Performance indicators table
CREATE TABLE performance_indicator (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                ind_number TINYINT NOT NULL,                --Number represents order of outcomes
                ind_value TINYINT NULL,                     --Value represents number assigned during evaluation
                ind_description TEXT NOT NULL,
                evaluation TEXT NULL,
                student_outcome_id BIGINT,
                threshold_percentage DECIMAL(5,2) DEFAULT 70.00,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (student_outcome_id) REFERENCES student_outcome(id)
);

-- Course table
CREATE TABLE course (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                course_code VARCHAR(20) NOT NULL,
                course_name VARCHAR(255) NOT NULL,
                course_description TEXT NOT NULL,
                semester_id BIGINT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (semester_id) REFERENCES semester(id)
);

-- CourseInstructor table
CREATE TABLE course_instructor (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                programUser_id BIGINT NOT NULL,
                course_id BIGINT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (programUser_id) REFERENCES program_user(id),
                FOREIGN KEY (course_id) REFERENCES course(id)
);

-- CourseIndicator table
CREATE TABLE course_indicator (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                course_id BIGINT NOT NULL,
                indicator_id BIGINT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE,
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
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (courseIndicator_id) REFERENCES course_indicator(id)
);