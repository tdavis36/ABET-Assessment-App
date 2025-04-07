-- Updated V1 migration with consistent naming conventions
SET FOREIGN_KEY_CHECKS = 0;

-- Drop old tables if they exist to ensure clean startup
DROP TABLE IF EXISTS ReportSnapshot;
DROP TABLE IF EXISTS FCAR;
DROP TABLE IF EXISTS ImprovementAction;
DROP TABLE IF EXISTS FinalDetail;
DROP TABLE IF EXISTS ReportDetail;
DROP TABLE IF EXISTS AssignmentDetail;
DROP TABLE IF EXISTS ExamDetail;
DROP TABLE IF EXISTS AssessmentMethod;
DROP TABLE IF EXISTS MethodType;
DROP TABLE IF EXISTS TargetGoal;
DROP TABLE IF EXISTS StudentExpectation;
DROP TABLE IF EXISTS Expectation;
DROP TABLE IF EXISTS ExpectationType;
DROP TABLE IF EXISTS Indicator;
DROP TABLE IF EXISTS Outcome;
DROP TABLE IF EXISTS Course_Outcome;
DROP TABLE IF EXISTS Course;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Role;
DROP TABLE IF EXISTS Permission;
DROP TABLE IF EXISTS Department;

-- Drop any duplicate tables with plural/alternate names
DROP TABLE IF EXISTS FCAR_Data;
DROP TABLE IF EXISTS User_Data;
DROP TABLE IF EXISTS Role_Data;
DROP TABLE IF EXISTS Assessment_Methods;
DROP TABLE IF EXISTS Assignment_Details;
DROP TABLE IF EXISTS Exam_Details;
DROP TABLE IF EXISTS Final_Details;
DROP TABLE IF EXISTS Indicators;
DROP TABLE IF EXISTS Method_Type;
DROP TABLE IF EXISTS Outcomes;
DROP TABLE IF EXISTS Report_Details;
DROP TABLE IF EXISTS Report_Snapshot;
DROP TABLE IF EXISTS Improvement_Actions;
DROP TABLE IF EXISTS Target_Goals;
DROP TABLE IF EXISTS Expectation_Type;
DROP TABLE IF EXISTS Expectations;
DROP TABLE IF EXISTS Student_Expectations;
DROP TABLE IF EXISTS Permission_Status;

SET FOREIGN_KEY_CHECKS = 1;

-- User Management
CREATE TABLE Department (
                            dept_id INT AUTO_INCREMENT PRIMARY KEY,
                            dept_name VARCHAR(100),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE Permission (
                            permission_id INT AUTO_INCREMENT PRIMARY KEY,
                            permission_desc VARCHAR(200)
) ENGINE=InnoDB;

CREATE TABLE Role (
                      role_id INT AUTO_INCREMENT PRIMARY KEY,
                      role_name VARCHAR(100),
                      permission_id INT,
                      FOREIGN KEY (permission_id) REFERENCES Permission(permission_id)
) ENGINE=InnoDB;

CREATE TABLE User (
                      user_id INT AUTO_INCREMENT PRIMARY KEY,
                      first_name VARCHAR(50),
                      last_name VARCHAR(100),
                      email VARCHAR(150),
                      password_hash VARCHAR(255),
                      role_id INT,
                      dept_id INT,
                      is_active BOOLEAN,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      FOREIGN KEY (role_id) REFERENCES Role(role_id),
                      FOREIGN KEY (dept_id) REFERENCES Department(dept_id)
                          ON DELETE RESTRICT
                          ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Course Management
CREATE TABLE Course (
                        course_code VARCHAR(20) PRIMARY KEY,
                        course_name VARCHAR(200),
                        course_desc TEXT,
                        dept_id INT,
                        credits INT,
                        semester_offered VARCHAR(20),
                        FOREIGN KEY (dept_id) REFERENCES Department(dept_id)
) ENGINE=InnoDB;

-- Assessment Framework
CREATE TABLE Outcome (
                         outcome_id INT AUTO_INCREMENT PRIMARY KEY,
                         outcome_num VARCHAR(20),
                         outcome_desc TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Optional bridging table for Course <-> Outcome relationships
CREATE TABLE Course_Outcome (
                                course_code VARCHAR(20),
                                outcome_id INT,
                                PRIMARY KEY (course_code, outcome_id),
                                FOREIGN KEY (course_code) REFERENCES Course(course_code),
                                FOREIGN KEY (outcome_id) REFERENCES Outcome(outcome_id)
) ENGINE=InnoDB;

CREATE TABLE Indicator (
                           indicator_id INT AUTO_INCREMENT PRIMARY KEY,
                           outcome_id INT,
                           indicator_num VARCHAR(20),
                           indicator_desc TEXT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (outcome_id) REFERENCES Outcome(outcome_id)
) ENGINE=InnoDB;

CREATE TABLE ExpectationType (
                                 expectation_type_id INT AUTO_INCREMENT PRIMARY KEY,
                                 type_name VARCHAR(20)
) ENGINE=InnoDB;

CREATE TABLE Expectation (
                             expectation_id INT AUTO_INCREMENT PRIMARY KEY,
                             indicator_id INT,
                             expectation_type_id INT,
                             expectation_desc VARCHAR(200),
                             FOREIGN KEY (indicator_id) REFERENCES Indicator(indicator_id),
                             FOREIGN KEY (expectation_type_id) REFERENCES ExpectationType(expectation_type_id)
) ENGINE=InnoDB;

CREATE TABLE StudentExpectation (
                                    stud_expect_id INT AUTO_INCREMENT PRIMARY KEY,
                                    course_code VARCHAR(20),
                                    start_year INT,
                                    end_year INT,
                                    expectation_type_id INT,
                                    student_num INT,
                                    FOREIGN KEY (course_code) REFERENCES Course(course_code),
                                    FOREIGN KEY (expectation_type_id) REFERENCES ExpectationType(expectation_type_id)
) ENGINE=InnoDB;

CREATE TABLE TargetGoal (
                            goal_id INT AUTO_INCREMENT PRIMARY KEY,
                            goal_desc VARCHAR(200),
                            outcome_id INT,
                            goal_value DECIMAL(5,2),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (outcome_id) REFERENCES Outcome(outcome_id)
) ENGINE=InnoDB;

-- Assessment Methods
CREATE TABLE MethodType (
                            type_id INT AUTO_INCREMENT PRIMARY KEY,
                            method_type VARCHAR(20)
) ENGINE=InnoDB;

CREATE TABLE AssessmentMethod (
                                  method_id INT AUTO_INCREMENT PRIMARY KEY,
                                  type_id INT,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  FOREIGN KEY (type_id) REFERENCES MethodType(type_id)
) ENGINE=InnoDB;

CREATE TABLE ExamDetail (
                            exam_detail_id INT AUTO_INCREMENT PRIMARY KEY,
                            method_id INT,
                            exam_num INT,
                            question_num INT,
                            sub_question VARCHAR(10),
                            exam_format VARCHAR(20),
                            FOREIGN KEY (method_id) REFERENCES AssessmentMethod(method_id)
) ENGINE=InnoDB;

CREATE TABLE AssignmentDetail (
                                  assignment_detail_id INT AUTO_INCREMENT PRIMARY KEY,
                                  method_id INT,
                                  assignment_num INT,
                                  milestone VARCHAR(50),
                                  topic VARCHAR(200),
                                  FOREIGN KEY (method_id) REFERENCES AssessmentMethod(method_id)
) ENGINE=InnoDB;

CREATE TABLE ReportDetail (
                              report_detail_id INT AUTO_INCREMENT PRIMARY KEY,
                              method_id INT,
                              report_type VARCHAR(50),
                              FOREIGN KEY (method_id) REFERENCES AssessmentMethod(method_id)
) ENGINE=InnoDB;

CREATE TABLE FinalDetail (
                             final_detail_id INT AUTO_INCREMENT PRIMARY KEY,
                             method_id INT,
                             final_type VARCHAR(50),
                             FOREIGN KEY (method_id) REFERENCES AssessmentMethod(method_id)
) ENGINE=InnoDB;

-- FCAR
CREATE TABLE ImprovementAction (
                                   action_id INT AUTO_INCREMENT PRIMARY KEY,
                                   action_desc TEXT,
                                   outcome_id INT,
                                   start_year INT,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   FOREIGN KEY (outcome_id) REFERENCES Outcome(outcome_id)
) ENGINE=InnoDB;

CREATE TABLE FCAR (
                      fcar_id INT AUTO_INCREMENT PRIMARY KEY,
                      course_code VARCHAR(20),
                      semester VARCHAR(20),
                      year INT,
                      instructor_id INT,
                      date_filled TIMESTAMP,
                      outcome_id INT,
                      indicator_id INT,
                      goal_id INT,
                      method_id INT,
                      method_desc TEXT,
                      stud_expect_id INT,
                      summary_desc TEXT,
                      action_id INT,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      FOREIGN KEY (course_code) REFERENCES Course(course_code),
                      FOREIGN KEY (instructor_id) REFERENCES User(user_id),
                      FOREIGN KEY (outcome_id) REFERENCES Outcome(outcome_id),
                      FOREIGN KEY (indicator_id) REFERENCES Indicator(indicator_id),
                      FOREIGN KEY (goal_id) REFERENCES TargetGoal(goal_id),
                      FOREIGN KEY (method_id) REFERENCES AssessmentMethod(method_id),
                      FOREIGN KEY (stud_expect_id) REFERENCES StudentExpectation(stud_expect_id),
                      FOREIGN KEY (action_id) REFERENCES ImprovementAction(action_id)
) ENGINE=InnoDB;

-- Reporting
CREATE TABLE ReportSnapshot (
                                snapshot_id INT AUTO_INCREMENT PRIMARY KEY,
                                report_name VARCHAR(200),
                                snapshot_date TIMESTAMP,
                                gen_by_user_id INT,
                                snapshot_data LONGTEXT,
                                notes VARCHAR(500),
                                FOREIGN KEY (gen_by_user_id) REFERENCES User(user_id)
) ENGINE=InnoDB;

-- Table for storing FCAR status (since status isn't in the core schema)
CREATE TABLE FCAR_Status (
                             fcar_id INT PRIMARY KEY,
                             status VARCHAR(50) NOT NULL DEFAULT 'Draft',
                             FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table for storing FCAR assessment methods
CREATE TABLE FCAR_Assessment_Methods (
                                         fcar_id INT,
                                         method_key VARCHAR(100),
                                         method_value TEXT,
                                         PRIMARY KEY (fcar_id, method_key),
                                         FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table for storing FCAR student outcomes
CREATE TABLE FCAR_Student_Outcomes (
                                       fcar_id INT,
                                       outcome_key VARCHAR(100),
                                       achievement_level INT,
                                       PRIMARY KEY (fcar_id, outcome_key),
                                       FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table for storing FCAR improvement actions
CREATE TABLE FCAR_Improvement_Actions (
                                          fcar_id INT,
                                          action_key VARCHAR(100),
                                          action_value TEXT,
                                          PRIMARY KEY (fcar_id, action_key),
                                          FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Create a table to log migration runs
CREATE TABLE Migration_Comment (
                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                   comment_text TEXT NOT NULL,
                                   migration_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;