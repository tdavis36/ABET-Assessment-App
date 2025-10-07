-- H2 Compatible Schema for ABET Assessment App
-- User table
CREATE TABLE users (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                email VARCHAR(255) UNIQUE NOT NULL,
                password_hash VARCHAR(255),
                first_name VARCHAR(100) NOT NULL,
                last_name VARCHAR(100) NOT NULL,
                role VARCHAR(20) NOT NULL CHECK (role IN ('PROFESSOR', 'ADMIN')),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE
);

-- Courses table
CREATE TABLE courses (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                course_code VARCHAR(20) NOT NULL,
                course_name VARCHAR(255) NOT NULL,
                description TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE
);

-- Student outcomes table
CREATE TABLE student_outcomes (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                code VARCHAR(10) NOT NULL,
                description TEXT NOT NULL,
                is_active BOOLEAN DEFAULT TRUE
);

-- Performance indicators table
CREATE TABLE performance_indicators (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                code VARCHAR(20) NOT NULL,
                description TEXT NOT NULL,
                student_outcome_id BIGINT,
                threshold_percentage DECIMAL(5,2) DEFAULT 70.00,
                is_active BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (student_outcome_id) REFERENCES student_outcomes(id)
);

-- Course assignments (professor to course)
CREATE TABLE course_assignments (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                course_id BIGINT NOT NULL,
                professor_id BIGINT NOT NULL,
                semester VARCHAR(20) NOT NULL,
                academic_year INTEGER NOT NULL,
                FOREIGN KEY (course_id) REFERENCES courses(id),
                FOREIGN KEY (professor_id) REFERENCES users(id)
);