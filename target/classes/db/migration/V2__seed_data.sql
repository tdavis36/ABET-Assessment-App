-- Disable FK checks to allow truncates without errors
SET FOREIGN_KEY_CHECKS = 0;

-- TRUNCATE all tables to start fresh
TRUNCATE TABLE ReportSnapshot;
TRUNCATE TABLE FCAR;
TRUNCATE TABLE ImprovementAction;
TRUNCATE TABLE FinalDetail;
TRUNCATE TABLE ReportDetail;
TRUNCATE TABLE AssignmentDetail;
TRUNCATE TABLE ExamDetail;
TRUNCATE TABLE AssessmentMethod;
TRUNCATE TABLE MethodType;
TRUNCATE TABLE TargetGoal;
TRUNCATE TABLE StudentExpectation;
TRUNCATE TABLE Expectation;
TRUNCATE TABLE ExpectationType;
TRUNCATE TABLE Indicator;
TRUNCATE TABLE Course_Outcome;
TRUNCATE TABLE Outcome;
TRUNCATE TABLE Course;
TRUNCATE TABLE User;
TRUNCATE TABLE Role;
TRUNCATE TABLE Permission;
TRUNCATE TABLE Department;

SET FOREIGN_KEY_CHECKS = 1;

-- ===================================
-- Department
-- ===================================
INSERT INTO Department (dept_name) VALUES
                                       ('Computer Science'),
                                       ('Mathematics'),
                                       ('Engineering');

-- ===================================
-- Permission
-- ===================================
INSERT INTO Permission (permission_desc) VALUES
                                             ('Manage Everything'),
                                             ('Manage Courses'),
                                             ('Manage Users'),
                                             ('View Only');

-- ===================================
-- Role
-- ===================================
-- Assume role_id 1 = Admin, 2 = Professor, 3 = Viewer, etc.
-- Adjust 'permission_id' references if needed
INSERT INTO Role (role_name, permission_id) VALUES
                                                ('Admin', 1),       -- "Manage Everything"
                                                ('Professor', 2),   -- "Manage Courses"
                                                ('Viewer', 4);      -- "View Only"

-- ===================================
-- User
-- ===================================
-- is_active = 1 means active, 0 means inactive
-- Adjust 'role_id' and 'dept_id' as needed
INSERT INTO User (first_name, last_name, email, password_hash, role_id, dept_id, is_active)
VALUES
    ('Alice', 'Admin', 'alice.admin@example.edu', 'hashed_pw_admin', 1, 1, 1),
    ('Bob', 'Prof', 'bob.prof@example.edu', 'hashed_pw_prof', 2, 1, 1),
    ('Charlie', 'Viewer', 'charlie@example.edu', 'hashed_pw_viewer', 3, 2, 1);

-- ===================================
-- Course
-- ===================================
-- Refer dept_id from Department
INSERT INTO Course (course_code, course_name, course_desc, dept_id, credits, semester_offered)
VALUES
    ('CS101', 'Intro to Computer Science', 'Basics of CS', 1, 3, 'Fall'),
    ('CS102', 'Data Structures', 'Intermediate CS topics', 1, 4, 'Spring'),
    ('MATH201', 'Calculus I', 'Fundamentals of Calculus', 2, 4, 'Fall');

-- ===================================
-- Outcome
-- ===================================
-- Insert a couple of outcomes
INSERT INTO Outcome (outcome_num, outcome_desc)
VALUES
    ('1', 'Demonstrate problem-solving ability'),
    ('2', 'Apply mathematical principles effectively');

-- ===================================
-- Course_Outcome
-- ===================================
-- Bridge table linking courses to outcomes
-- Suppose CS101 is linked to outcome 1, MATH201 is linked to outcome 2, etc.
INSERT INTO Course_Outcome (course_code, outcome_id)
VALUES
    ('CS101', 1),
    ('CS102', 1),
    ('MATH201', 2);

-- ===================================
-- Indicator
-- ===================================
-- Link indicators to existing outcomes
INSERT INTO Indicator (outcome_id, indicator_num, indicator_desc)
VALUES
    (1, '1.1', 'Identify appropriate data structures'),
    (1, '1.2', 'Implement algorithms efficiently'),
    (2, '2.1', 'Apply differentiation rules'),
    (2, '2.2', 'Use integrals in problem solving');

-- ===================================
-- ExpectationType
-- ===================================
INSERT INTO ExpectationType (type_name)
VALUES
    ('High'),
    ('Moderate'),
    ('Low');

-- ===================================
-- Expectation
-- ===================================
-- Link an expectation to an indicator
INSERT INTO Expectation (indicator_id, expectation_type_id, expectation_desc)
VALUES
    (1, 1, 'Students will choose the correct data structure for a given problem'),
    (2, 2, 'Students implement the algorithm with moderate efficiency'),
    (3, 1, 'Students apply differentiation accurately'),
    (4, 1, 'Students apply integrals at a high level');

-- ===================================
-- StudentExpectation
-- ===================================
-- Suppose we track how many students are expected to reach a certain level
INSERT INTO StudentExpectation (course_code, start_year, end_year, expectation_type_id, student_num)
VALUES
    ('CS101', 2023, 2024, 1, 30),
    ('CS102', 2023, 2024, 2, 25),
    ('MATH201', 2023, 2024, 1, 40);

-- ===================================
-- TargetGoal
-- ===================================
-- Suppose each outcome has a numeric performance goal
INSERT INTO TargetGoal (goal_desc, outcome_id, goal_value)
VALUES
    ('CS101 Mastery Goal', 1, 80.00),
    ('MATH201 Mastery Goal', 2, 75.00);

-- ===================================
-- MethodType
-- ===================================
INSERT INTO MethodType (method_type)
VALUES
    ('Exam'),
    ('Assignment'),
    ('Project'),
    ('Report');

-- ===================================
-- AssessmentMethod
-- ===================================
-- Link to the method_type by type_id
INSERT INTO AssessmentMethod (type_id)
VALUES
    (1),   -- Exam
    (2),   -- Assignment
    (4);   -- Report

-- ===================================
-- ExamDetail
-- ===================================
INSERT INTO ExamDetail (method_id, exam_num, question_num, sub_question, exam_format)
VALUES
    (1, 1, 10, 'a', 'MultipleChoice'),
    (1, 1, 10, 'b', 'ShortAnswer');

-- ===================================
-- AssignmentDetail
-- ===================================
INSERT INTO AssignmentDetail (method_id, assignment_num, milestone, topic)
VALUES
    (2, 1, 'DataStructureReview', 'Implement a linked list'),
    (2, 2, 'ProjectPlanning', 'Plan out final data structures');

-- ===================================
-- ReportDetail
-- ===================================
INSERT INTO ReportDetail (method_id, report_type)
VALUES
    (3, 'Lab Report'),
    (3, 'Term Paper');

-- ===================================
-- FinalDetail
-- ===================================
INSERT INTO FinalDetail (method_id, final_type)
VALUES
    (1, 'Final Exam'),
    (3, 'Final Report');

-- ===================================
-- ImprovementAction
-- ===================================
-- Suppose we link to outcome_id 1 or 2
INSERT INTO ImprovementAction (action_desc, outcome_id, start_year)
VALUES
    ('Revise data-structure assignment to improve understanding', 1, 2023),
    ('Provide extra calculus workshops', 2, 2023);

-- ===================================
-- FCAR (Faculty Course Assessment Report)
-- ===================================
-- We'll create one example FCAR for CS101 referencing outcome 1, indicator 1, etc.
INSERT INTO FCAR (
    course_code,
    semester,
    year,
    instructor_id,
    date_filled,
    outcome_id,
    indicator_id,
    goal_id,
    method_id,
    method_desc,
    stud_expect_id,
    summary_desc,
    action_id
)
VALUES
    ('CS101', 'Fall', 2023, 2, NOW(), 1, 1, 1, 1, 'Exam-based assessment', 1, 'Overall good performance', 1);

-- ===================================
-- ReportSnapshot
-- ===================================
-- Example snapshot of some report
INSERT INTO ReportSnapshot (
    report_name,
    snapshot_date,
    gen_by_user_id,
    snapshot_data,
    notes
)
VALUES
    ('Fall 2023 Assessment Summary', NOW(), 1, 'All FCAR data in JSON or XML', 'Snapshot created by admin');
