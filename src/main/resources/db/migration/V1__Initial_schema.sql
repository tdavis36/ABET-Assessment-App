-- User Management Tables

CREATE TABLE Department (
                            dept_id INT PRIMARY KEY AUTO_INCREMENT,
                            dept_name VARCHAR(100) NOT NULL
);

CREATE TABLE Permission_Status (
                                   permission_id INT PRIMARY KEY AUTO_INCREMENT,
                                   permission_desc VARCHAR(200) NOT NULL
);

CREATE TABLE Role_Data (
                           role_id INT PRIMARY KEY AUTO_INCREMENT,
                           role_name VARCHAR(100) NOT NULL,
                           permission_id INT,
                           FOREIGN KEY (permission_id) REFERENCES Permission_Status(permission_id)
);

CREATE TABLE User_Data (
                           user_id INT PRIMARY KEY AUTO_INCREMENT,
                           first_name VARCHAR(50) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           email VARCHAR(150) NOT NULL UNIQUE,
                           password_hash VARCHAR(255) NOT NULL,
                           role_id INT,
                           dept_id INT,
                           is_active BOOLEAN DEFAULT TRUE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (role_id) REFERENCES Role_Data(role_id),
                           FOREIGN KEY (dept_id) REFERENCES Department(dept_id)
);

-- Course Management Tables

CREATE TABLE Course (
                        course_code VARCHAR(20) PRIMARY KEY,
                        course_name VARCHAR(200) NOT NULL,
                        course_desc TEXT,
                        dept_id INT,
                        credits INT,
                        semester_offered VARCHAR(20),
                        FOREIGN KEY (dept_id) REFERENCES Department(dept_id)
);

-- Assessment Framework Tables

CREATE TABLE Outcomes (
                          outcome_id INT PRIMARY KEY AUTO_INCREMENT,
                          outcome_num VARCHAR(20) NOT NULL,
                          outcome_desc TEXT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE Indicators (
                            indicators_id INT PRIMARY KEY AUTO_INCREMENT,
                            outcome_id INT NOT NULL,
                            indicator_num VARCHAR(20) NOT NULL,
                            indicator_desc TEXT NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (outcome_id) REFERENCES Outcomes(outcome_id)
);

CREATE TABLE Expectation_Type (
                                  expectation_type_id INT PRIMARY KEY AUTO_INCREMENT,
                                  type_name VARCHAR(20) NOT NULL
);

CREATE TABLE Expectations (
                              expectation_id INT PRIMARY KEY AUTO_INCREMENT,
                              indicator_id INT NOT NULL,
                              expectation_type_id INT NOT NULL,
                              expectation_desc VARCHAR(200) NOT NULL,
                              FOREIGN KEY (indicator_id) REFERENCES Indicators(indicators_id),
                              FOREIGN KEY (expectation_type_id) REFERENCES Expectation_Type(expectation_type_id)
);

CREATE TABLE Student_Expectations (
                                      stud_expect_id INT PRIMARY KEY AUTO_INCREMENT,
                                      course_code VARCHAR(20) NOT NULL,
                                      start_year INT NOT NULL,
                                      end_year INT,
                                      expectation_type_id INT NOT NULL,
                                      student_num INT NOT NULL,
                                      FOREIGN KEY (course_code) REFERENCES Course(course_code),
                                      FOREIGN KEY (expectation_type_id) REFERENCES Expectation_Type(expectation_type_id)
);

CREATE TABLE Target_Goals (
                              goal_id INT PRIMARY KEY AUTO_INCREMENT,
                              goal_desc VARCHAR(200) NOT NULL,
                              outcome_id INT NOT NULL,
                              goal_value DECIMAL(5,2) NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              FOREIGN KEY (outcome_id) REFERENCES Outcomes(outcome_id)
);

-- Assessment Methods Tables

CREATE TABLE Method_Type (
                             type_id INT PRIMARY KEY AUTO_INCREMENT,
                             method_type VARCHAR(20) NOT NULL
);

CREATE TABLE Assessment_Methods (
                                    method_id INT PRIMARY KEY AUTO_INCREMENT,
                                    method_type VARCHAR(20) NOT NULL,
                                    type_id INT NOT NULL,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    FOREIGN KEY (type_id) REFERENCES Method_Type(type_id)
);

CREATE TABLE Exam_Details (
                              exam_detail_id INT PRIMARY KEY AUTO_INCREMENT,
                              method_id INT NOT NULL,
                              exam_num INT NOT NULL,
                              question_num INT NOT NULL,
                              sub_question VARCHAR(10),
                              exam_format VARCHAR(20) NOT NULL,
                              FOREIGN KEY (method_id) REFERENCES Assessment_Methods(method_id)
);

CREATE TABLE Assignment_Details (
                                    assignment_detail_id INT PRIMARY KEY AUTO_INCREMENT,
                                    method_id INT NOT NULL,
                                    assignment_num INT NOT NULL,
                                    milestone VARCHAR(50),
                                    topic VARCHAR(200),
                                    FOREIGN KEY (method_id) REFERENCES Assessment_Methods(method_id)
);

CREATE TABLE Report_Details (
                                report_detail_id INT PRIMARY KEY AUTO_INCREMENT,
                                method_id INT NOT NULL,
                                report_type VARCHAR(50) NOT NULL,
                                FOREIGN KEY (method_id) REFERENCES Assessment_Methods(method_id)
);

CREATE TABLE Final_Details (
                               final_detail_id INT PRIMARY KEY AUTO_INCREMENT,
                               method_id INT NOT NULL,
                               final_type VARCHAR(50) NOT NULL,
                               FOREIGN KEY (method_id) REFERENCES Assessment_Methods(method_id)
);

-- FCAR Tables

CREATE TABLE Improvement_Actions (
                                     action_id INT PRIMARY KEY AUTO_INCREMENT,
                                     action_desc TEXT NOT NULL,
                                     outcome_id INT NOT NULL,
                                     start_year INT NOT NULL,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     FOREIGN KEY (outcome_id) REFERENCES Outcomes(outcome_id)
);

CREATE TABLE FCAR_Data (
                           fcar_id INT PRIMARY KEY AUTO_INCREMENT,
                           course_code VARCHAR(20) NOT NULL,
                           semester VARCHAR(20) NOT NULL,
                           year INT NOT NULL,
                           instructor_id INT NOT NULL,
                           date_filled TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           outcome_id INT NOT NULL,
                           indicator_id INT NOT NULL,
                           goal_id INT NOT NULL,
                           method_id INT NOT NULL,
                           method_desc TEXT,
                           stud_expect_id INT,
                           summary_desc TEXT,
                           action_id INT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (course_code) REFERENCES Course(course_code),
                           FOREIGN KEY (instructor_id) REFERENCES User_Data(user_id),
                           FOREIGN KEY (outcome_id) REFERENCES Outcomes(outcome_id),
                           FOREIGN KEY (indicator_id) REFERENCES Indicators(indicators_id),
                           FOREIGN KEY (goal_id) REFERENCES Target_Goals(goal_id),
                           FOREIGN KEY (method_id) REFERENCES Assessment_Methods(method_id),
                           FOREIGN KEY (stud_expect_id) REFERENCES Student_Expectations(stud_expect_id),
                           FOREIGN KEY (action_id) REFERENCES Improvement_Actions(action_id)
);

-- Reporting Tables

CREATE TABLE Report_Snapshot (
                                 snapshot_id INT PRIMARY KEY AUTO_INCREMENT,
                                 report_name VARCHAR(200) NOT NULL,
                                 snapshot_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 gen_by_use_id INT NOT NULL,
                                 snapshot_data LONGTEXT,  -- Using LONGTEXT instead of CLOB for MariaDB compatibility
                                 notes VARCHAR(500),
                                 FOREIGN KEY (gen_by_use_id) REFERENCES User_Data(user_id)
);