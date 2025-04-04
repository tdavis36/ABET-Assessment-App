-- Seed Department Data
INSERT INTO Department (dept_name) VALUES
                                       ('Computer Science'),
                                       ('Electrical Engineering'),
                                       ('Mechanical Engineering'),
                                       ('Civil Engineering'),
                                       ('Mathematics');

-- Seed Permission Data
INSERT INTO Permission_Status (permission_desc) VALUES
                                                    ('View FCAR'),
                                                    ('Edit FCAR'),
                                                    ('Submit FCAR'),
                                                    ('Approve FCAR'),
                                                    ('Manage Users'),
                                                    ('Manage Courses'),
                                                    ('View Reports'),
                                                    ('Generate Reports');

-- Seed Role Data
INSERT INTO Role_Data (role_name, permission_id) VALUES
                                                     ('Administrator', 5),  -- Manage Users permission
                                                     ('Professor', 2),      -- Edit FCAR permission
                                                     ('Department Head', 4),-- Approve FCAR permission
                                                     ('Staff', 1);          -- View FCAR permission

-- Seed User Data (Using bcrypt hashed password 'password' for all users)
INSERT INTO User_Data (first_name, last_name, email, password_hash, role_id, dept_id, is_active) VALUES
                                                                                                     ('Admin', 'User', 'admin@university.edu', '$2a$10$8tPbzL3C8yTlSV0ZJXaN0ugD7V0OIi5Gzm7xqFqPGgWVDXgZrKUoK', 1, 1, TRUE),
                                                                                                     ('John', 'Smith', 'john.smith@university.edu', '$2a$10$8tPbzL3C8yTlSV0ZJXaN0ugD7V0OIi5Gzm7xqFqPGgWVDXgZrKUoK', 2, 1, TRUE),
                                                                                                     ('Jane', 'Doe', 'jane.doe@university.edu', '$2a$10$8tPbzL3C8yTlSV0ZJXaN0ugD7V0OIi5Gzm7xqFqPGgWVDXgZrKUoK', 2, 2, TRUE),
                                                                                                     ('Robert', 'Johnson', 'robert.johnson@university.edu', '$2a$10$8tPbzL3C8yTlSV0ZJXaN0ugD7V0OIi5Gzm7xqFqPGgWVDXgZrKUoK', 3, 1, TRUE),
                                                                                                     ('Mary', 'Williams', 'mary.williams@university.edu', '$2a$10$8tPbzL3C8yTlSV0ZJXaN0ugD7V0OIi5Gzm7xqFqPGgWVDXgZrKUoK', 4, 3, TRUE);

-- Seed Course Data
INSERT INTO Course (course_code, course_name, course_desc, dept_id, credits, semester_offered) VALUES
                                                                                                   ('CS101', 'Introduction to Computer Science', 'An introductory course covering the basics of computer science and programming.', 1, 3, 'Fall'),
                                                                                                   ('CS220', 'Data Structures', 'A course on fundamental data structures and algorithms.', 1, 4, 'Spring'),
                                                                                                   ('EE200', 'Circuit Analysis', 'Basic principles of electrical circuit analysis.', 2, 3, 'Fall'),
                                                                                                   ('ME150', 'Engineering Mechanics', 'Principles of statics and dynamics in engineering.', 3, 4, 'Spring'),
                                                                                                   ('MATH240', 'Linear Algebra', 'Introduction to linear algebra and its applications.', 5, 3, 'Fall,Spring');

-- Seed Outcomes Data (Based on ABET Student Outcomes for Engineering)
INSERT INTO Outcomes (outcome_num, outcome_desc) VALUES
                                                     ('1', 'An ability to identify, formulate, and solve complex engineering problems by applying principles of engineering, science, and mathematics.'),
                                                     ('2', 'An ability to apply engineering design to produce solutions that meet specified needs with consideration of public health, safety, and welfare, as well as global, cultural, social, environmental, and economic factors.'),
                                                     ('3', 'An ability to communicate effectively with a range of audiences.'),
                                                     ('4', 'An ability to recognize ethical and professional responsibilities in engineering situations and make informed judgments, which must consider the impact of engineering solutions in global, economic, environmental, and societal contexts.'),
                                                     ('5', 'An ability to function effectively on a team whose members together provide leadership, create a collaborative and inclusive environment, establish goals, plan tasks, and meet objectives.'),
                                                     ('6', 'An ability to develop and conduct appropriate experimentation, analyze and interpret data, and use engineering judgment to draw conclusions.'),
                                                     ('7', 'An ability to acquire and apply new knowledge as needed, using appropriate learning strategies.');

-- Seed Indicators Data
INSERT INTO Indicators (outcome_id, indicator_num, indicator_desc) VALUES
                                                                       (1, '1.1', 'Identifies and formulates engineering problems'),
                                                                       (1, '1.2', 'Applies engineering principles to solve complex problems'),
                                                                       (2, '2.1', 'Designs solutions considering multiple factors'),
                                                                       (2, '2.2', 'Evaluates design solutions against requirements'),
                                                                       (3, '3.1', 'Communicates technical information effectively in writing'),
                                                                       (3, '3.2', 'Delivers effective oral presentations on technical topics'),
                                                                       (4, '4.1', 'Recognizes ethical issues in engineering practice'),
                                                                       (5, '5.1', 'Works effectively in team environments'),
                                                                       (6, '6.1', 'Designs and conducts experiments properly'),
                                                                       (6, '6.2', 'Analyzes and interprets experimental data'),
                                                                       (7, '7.1', 'Demonstrates ability to learn independently');

-- Seed Expectation Type Data
INSERT INTO Expectation_Type (type_name) VALUES
                                             ('Exceeds'),
                                             ('Meets'),
                                             ('Developing'),
                                             ('Unsatisfactory');

-- Seed Expectations Data
INSERT INTO Expectations (indicator_id, expectation_type_id, expectation_desc) VALUES
                                                                                   (1, 1, 'Student expertly identifies and clearly formulates complex engineering problems'),
                                                                                   (1, 2, 'Student adequately identifies and formulates engineering problems'),
                                                                                   (1, 3, 'Student partially identifies engineering problems but has difficulty formulating them'),
                                                                                   (1, 4, 'Student cannot identify or formulate engineering problems'),
                                                                                   (2, 1, 'Student expertly applies engineering principles to solve complex problems'),
                                                                                   (2, 2, 'Student adequately applies engineering principles to solve problems'),
                                                                                   (2, 3, 'Student applies some engineering principles but struggles with complex problems'),
                                                                                   (2, 4, 'Student cannot apply engineering principles to solve problems');

-- Seed Method Type Data
INSERT INTO Method_Type (method_type) VALUES
                                          ('Exam'),
                                          ('Assignment'),
                                          ('Project'),
                                          ('Presentation'),
                                          ('Lab Report'),
                                          ('Final Exam');

-- Seed Assessment Methods Data
INSERT INTO Assessment_Methods (method_type, type_id) VALUES
                                                          ('Midterm Exam', 1),
                                                          ('Final Exam', 6),
                                                          ('Homework', 2),
                                                          ('Project', 3),
                                                          ('Lab Report', 5),
                                                          ('Presentation', 4);

-- Seed Target Goals Data
INSERT INTO Target_Goals (goal_desc, outcome_id, goal_value) VALUES
                                                                 ('Target for Problem Solving', 1, 75.00),
                                                                 ('Target for Design Solutions', 2, 80.00),
                                                                 ('Target for Communication', 3, 70.00),
                                                                 ('Target for Ethics', 4, 75.00),
                                                                 ('Target for Teamwork', 5, 85.00),
                                                                 ('Target for Experimentation', 6, 80.00),
                                                                 ('Target for Continuous Learning', 7, 75.00);

-- Seed Student Expectations Data
INSERT INTO Student_Expectations (course_code, start_year, end_year, expectation_type_id, student_num) VALUES
                                                                                                           ('CS101', 2023, 2024, 1, 15),
                                                                                                           ('CS101', 2023, 2024, 2, 25),
                                                                                                           ('CS101', 2023, 2024, 3, 8),
                                                                                                           ('CS101', 2023, 2024, 4, 2),
                                                                                                           ('CS220', 2023, 2024, 1, 10),
                                                                                                           ('CS220', 2023, 2024, 2, 18),
                                                                                                           ('CS220', 2023, 2024, 3, 5),
                                                                                                           ('CS220', 2023, 2024, 4, 1);

-- Seed Improvement Actions Data
INSERT INTO Improvement_Actions (action_desc, outcome_id, start_year) VALUES
                                                                          ('Revise problem sets to include more complex, real-world scenarios', 1, 2024),
                                                                          ('Incorporate more team-based design projects', 2, 2024),
                                                                          ('Add more writing assignments with peer review', 3, 2024),
                                                                          ('Include ethics case studies in course content', 4, 2024),
                                                                          ('Restructure lab assignments to emphasize experimental design', 6, 2024);

-- Create some example FCAR Data
INSERT INTO FCAR_Data (course_code, semester, year, instructor_id, outcome_id, indicator_id, goal_id, method_id, method_desc, summary_desc) VALUES
                                                                                                                                                ('CS101', 'Fall', 2023, 2, 1, 1, 1, 1, 'Midterm exam questions focused on problem identification and formulation', 'Overall, students performed well in identifying problems but struggled with complex formulations'),
                                                                                                                                                ('CS220', 'Spring', 2023, 2, 2, 3, 2, 3, 'Final project requiring students to design a solution for a specific problem', 'Most students were able to design effective solutions, with some exceptional work noted'),
                                                                                                                                                ('EE200', 'Fall', 2023, 3, 6, 9, 6, 5, 'Lab reports analyzing circuit performance', 'Students showed good experimental technique but need improvement in data analysis');

-- Create a Report Snapshot
INSERT INTO Report_Snapshot (report_name, gen_by_use_id, snapshot_data, notes) VALUES
                                                                                   ('Fall 2023 CS Department Assessment Summary', 1, '{"departmentId": 1, "semester": "Fall", "year": 2023, "summaryData": {"overallMet": 78.5, "areasForImprovement": ["Problem formulation", "Data analysis"], "strengths": ["Team collaboration", "Design skills"]}}', 'Initial assessment report for department review'),
                                                                                   ('Spring 2023 Engineering Programs Assessment', 1, '{"programs": ["CS", "EE", "ME"], "semester": "Spring", "year": 2023, "comparativeData": {"CS": 82.3, "EE": 79.8, "ME": 81.2}}', 'Cross-department comparative assessment');