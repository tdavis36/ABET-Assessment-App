-- Initial test data for ABET Assessment App H2 database

-- Sample users (professors and admins)
INSERT INTO users (email, first_name, last_name, role) VALUES
                                                           ('john.doe@university.edu', 'John', 'Doe', 'PROFESSOR'),
                                                           ('jane.smith@university.edu', 'Jane', 'Smith', 'PROFESSOR'),
                                                           ('admin@university.edu', 'System', 'Administrator', 'ADMIN'),
                                                           ('mary.johnson@university.edu', 'Mary', 'Johnson', 'PROFESSOR');

-- Sample courses
INSERT INTO courses (course_code, course_name, description) VALUES
            ('CS101', 'Fundamentals of Computer Science I', 'Basic programming concepts and problem solving'),
            ('CS201', 'Fundamentals of Computer Science II', 'Algorithms and data structures implementation'),
            ('CS320', 'Software Engineering and Design', 'Software development lifecycle and methodologies'),
            ('CS400', 'Senior Capstone', 'Capstone project demonstrating accumulated knowledge');

-- ABET Student Outcomes (common engineering outcomes)
INSERT INTO student_outcomes (code, description) VALUES
            ('O1', 'Analyze a complex computing problem and apply principles of computing to identify solutions'),
            ('O2', 'Design, implement, and evaluate a computing-based solution to meet requirements'),
            ('O3', 'Communicate effectively in a variety of professional contexts'),
            ('O4', 'Recognize professional responsibilities and make informed judgments'),
            ('O5', 'Function effectively as a member or leader of a team'),
            ('O6', 'Apply security principles and practices to maintain operations');

-- Performance indicators for student outcomes
INSERT INTO performance_indicators (code, description, student_outcome_id, threshold_percentage) VALUES
            ('PI1.1', 'Identify and formulate computing problems', 1, 75.00),
            ('PI1.2', 'Apply computing principles to solve problems', 1, 70.00),
            ('PI2.1', 'Design software solutions meeting specifications', 2, 80.00),
            ('PI2.2', 'Implement and test software solutions', 2, 75.00),
            ('PI3.1', 'Present technical information clearly in writing', 3, 70.00),
            ('PI3.2', 'Present technical information clearly orally', 3, 70.00);

-- Course assignments (professors assigned to courses for specific semesters)
INSERT INTO course_assignments (course_id, professor_id, semester, academic_year) VALUES
            -- John Doe teaching assignments
            (1, 1, 'FALL', 2024),    -- CS101 Fall 2024
            (2, 1, 'SPRING', 2025),  -- CS201 Spring 2025

            -- Jane Smith teaching assignments
            (3, 2, 'FALL', 2024),    -- CS301 Fall 2024
            (4, 2, 'SPRING', 2025),  -- CS401 Spring 2025

            -- Mary Johnson teaching assignments
            (1, 4, 'SPRING', 2024),  -- CS101 Spring 2024
            (2, 4, 'FALL', 2024);    -- CS201 Fall 2024