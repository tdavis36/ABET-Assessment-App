-- Initial test data for ABET Assessment App H2 database

-- Sample Users
INSERT INTO users (email, name_first, name_last) VALUES
                                                           ('john.doe@university.edu', 'John', 'Doe'),
                                                           ('jane.smith@university.edu', 'Jane', 'Smith'),
                                                           ('admin@university.edu', 'System', 'Administrator'),
                                                           ('mary.johnson@university.edu', 'Mary', 'Johnson');

--Sample Programs
INSERT INTO program (program_name, institution) VALUES
            ('YCP Computer Science', 'York College of Pennsylvania'),
            ('YCP Computer Engineering', 'York College of Pennsylvania'),
            ('University Computer Science', 'University'),
            ('University Engineering', 'University');

--Sample ProgramUsers
INSERT INTO program_user (isAdmin, program_id, user_id) VALUES
            (FALSE, 1, 1),  --John Doe is not an admin in YCP Computer Science
            (FALSE, 2, 2),  --Jane Smith is not an admin in YCP Computer Engineering
            (TRUE, 3, 3),   --Admin is an admin in University Computer Science
            (FALSE, 3, 4);  --Mary Johnson is not an admin in University Computer Science

--Sample semesters
INSERT INTO semester (season, semester_year, program_id) VALUES
            ('Fall', 2024, 1),      --Fall 2024 Semester of YCP Computer Science
            ('Spring', 2025, 1),    --Spring 2025 Semester of YCP Computer Science
            ('Fall', 2003, 2),      --Fall 2003 Semester of YCP Computer Engineering
            ('Spring', 2020, 3);    --Spring 2020 Semester of University Computer Science

--Sample courses
INSERT INTO course (course_code, course_name, course_description, semester_id) VALUES
            ('CS101', 'Fundamentals of Computer Science I', 'Basic programming concepts and problem solving', 1), --Fall 2024 YCP CS
            ('CS201', 'Fundamentals of Computer Science II', 'Algorithms and data structures implementation', 2), --Spring 2024 YCP CS
            ('ECE260', 'Fundamental of Computer Engineering', 'An introduction to the design and operation of digital computers', 3), --Fall 2003 YCP CE
            ('CS400', 'Senior Capstone', 'Capstone project demonstrating accumulated knowledge', 1); --Fall 2024 YCP CS

-- ABET Student Outcomes (common engineering outcomes)
INSERT INTO student_outcome (out_number, out_description, semester_id) VALUES
            ('1', 'Analyze a complex computing problem and apply principles of computing to identify solutions', 1), --Fall 2024 YCP CS
            ('2', 'Design, implement, and evaluate a computing-based solution to meet requirements', 1),             --Fall 2024 YCP CS
            ('3', 'Communicate effectively in a variety of professional contexts', 1),                               --Fall 2024 YCP CS
            ('1', 'Recognize professional responsibilities and make informed judgments', 2),                         --Spring 2024 YCP CS
            ('1', 'Function effectively as a member or leader of a team', 3),                                        --Fall 2003 YCP CE
            ('2', 'Apply security principles and practices to maintain operations', 3);                              --Fall 2003 YCP CE

-- Performance indicators for student outcomes
INSERT INTO performance_indicator (ind_number, ind_description, student_outcome_id, threshold_percentage) VALUES
            ('1', 'Identify and formulate computing problems', 1, 75.00),           --Fall 2024 YCP CS Outcome 1
            ('2', 'Apply computing principles to solve problems', 1, 70.00),        --Fall 2024 YCP CS Outcome 1
            ('1', 'Design software solutions meeting specifications', 2, 80.00),    --Fall 2024 YCP CS Outcome 2
            ('2', 'Implement and test software solutions', 2, 75.00),               --Fall 2024 YCP CS Outcome 2
            ('1', 'Present technical information clearly in writing', 3, 70.00),    --Fall 2003 YCP CE Outcome 1
            ('2', 'Present technical information clearly orally', 3, 70.00);        --Fall 2003 YCP CE Outcome 1


-- Course assignments (professors assigned to courses for specific semesters)
INSERT INTO course_instructor (programUser_id, course_id) VALUES
            -- John Doe teaching assignments
            (1, 1),  -- CS101 Fall 2024 YCP CS
            (1, 2),  -- CS201 Spring 2025 YCP CS

            -- Jane Smith teaching assignments
            (2, 3);  -- ECE260 Fall 2003 YCP CE

INSERT INTO course_indicator (indicator_id, course_id) VALUES
            (1, 1),     --CS101 Fall 2024 YCP CS Outcome 1 Indicator 1
            (5, 3),     --ECE260 Fall 2003 YCP CE Outcome Outcome 1 Indicator 1
            (2, 1),     --CS101 Fall 2024 YCP CS Outcome 1 Indicator 2
            (2, 2);     --CS201 Spring 2025 YCP CS Outcome 1 Indicator 2 

INSERT INTO measure (courseIndicator_id, measure_description) VALUES
            (1, 'Students will be given an exam question to find the error in a section of code'),
            (3, 'Students will be assigned complex programming projects'),
            (2, 'This measure should belong to ECE260');
