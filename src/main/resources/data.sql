-----------------------------------------
-- USERS
-----------------------------------------
INSERT INTO users (email, password_hash, name_first, name_last, name_title)
VALUES
    ('john.doe@university.edu', '$2b$10$e..jxMoUWUwlbkm84bgIwuc9YMEIQM46zPzy/yc.Ib4ZxUMVoB6lm', 'John', 'Doe', 'Dr.'),
    ('jane.smith@university.edu', '$2b$10$5.U1UcA1MeKVFEoJsPKUpe.Fk2jrQpGdQ3N3EZWDzWJtm7PMm8mZm', 'Jane', 'Smith', 'Prof.'),
    ('admin@university.edu', '$2b$10$MxfejV3BEA8oKEgsFILmUeIyDnvucN7U0x6drSEC.LMAUWte98JTa', 'System', 'Administrator', NULL),
    ('mary.johnson@university.edu', '$2b$10$P/.vn.eg6mPkOa64hZGElew2RmXywSp1tqO/r0GO0LBCxtmdXvta6', 'Mary', 'Johnson', 'Dr.'),
    ('rickardo.wade@university.edu', '$2b$10$5.U1UcA1MeKVFEoJsPKUpe.Fk2jrQpGdQ3N3EZWDzWJtm7PMm8mZm', 'Rickardo', 'Wade', 'Prof.');
------------------------------------------------------------
-- PROGRAMS (2 programs)
------------------------------------------------------------
INSERT INTO program (id, program_name, institution, is_active)
VALUES
    (1, 'Computer Engineering', 'Example University', TRUE),
    (2, 'Electrical Engineering', 'Example University', TRUE);

------------------------------------------------------------
-- PROGRAM_USER (maps users to programs)
------------------------------------------------------------
INSERT INTO program_user (id, isAdmin, program_id, user_id, is_active)
VALUES
    (1, FALSE, 1, 1, TRUE),
    (2, FALSE, 1, 2, TRUE),
    (3, TRUE, 1, 3, TRUE),
    (4, FALSE, 2, 4, TRUE),
    (5, FALSE, 2, 5, TRUE);

------------------------------------------------------------
-- SEMESTERS
------------------------------------------------------------
INSERT INTO semester (id, name, code, type, status, start_date, end_date, academic_year, description, program_id, is_current)
VALUES
    (1, 'Fall 2024', 'FALL-2024', 'FALL', 'COMPLETED', '2024-08-26', '2024-12-15', 2024, 'Fall semester for Computer Engineering program', 1, FALSE),
    (2, 'Fall 2025', 'FALL-2025', 'FALL', 'ACTIVE', '2025-08-25', '2025-12-14', 2025, 'Current fall semester for Computer Engineering program', 1, TRUE),
    (3, 'Spring 2024', 'SPRING-2024', 'SPRING', 'COMPLETED', '2024-01-15', '2024-05-10', 2024, 'Spring semester for Electrical Engineering program', 2, FALSE);



------------------------------------------------------------
-- STUDENT OUTCOMES
------------------------------------------------------------
INSERT INTO student_outcome (id, out_number, out_value, out_description, semester_id, is_active)
VALUES
    (1, 1, NULL, 'Ability to identify, formulate, and solve complex engineering problems', 1, TRUE),
    (2, 2, NULL, 'Ability to apply engineering design to produce solutions', 1, TRUE),
    (3, 3, NULL, 'Ability to communicate effectively with a range of audiences', 1, TRUE),
    (4, 4, NULL, 'Ability to function effectively on a team', 2, TRUE);

------------------------------------------------------------
-- PERFORMANCE INDICATORS
------------------------------------------------------------
INSERT INTO performance_indicator (id, ind_number, ind_value, ind_description,
                                   evaluation, student_outcome_id, threshold_percentage, is_active)
VALUES
    (1, 1, NULL, 'Solves engineering problems analytically', NULL, 1, 70.00, TRUE),
    (2, 2, NULL, 'Applies mathematical principles correctly', NULL, 1, 70.00, TRUE),
    (3, 1, NULL, 'Produces design alternatives meeting constraints', NULL, 2, 70.00, TRUE),
    (4, 2, NULL, 'Uses design tools effectively', NULL, 2, 70.00, TRUE),
    (5, 1, NULL, 'Delivers clear written communication', NULL, 3, 70.00, TRUE),
    (6, 2, NULL, 'Delivers clear oral communication', NULL, 3, 70.00, TRUE),
    (7, 1, NULL, 'Works effectively within diverse teams', NULL, 4, 70.00, TRUE),
    (8, 2, NULL, 'Contributes to team decisions', NULL, 4, 70.00, TRUE),
    (9, 3, NULL, 'Leadership within team environment', NULL, 4, 70.00, TRUE),
    (10, 4, NULL, 'Conflict resolution skills', NULL, 4, 70.00, TRUE);

------------------------------------------------------------
-- COURSES
------------------------------------------------------------
INSERT INTO course (id, course_code, course_name, course_description,
                    semester_id, student_count, is_active)
VALUES
    (1, 'CE101', 'Intro to Engineering', 'Fundamentals of engineering practice.', 1, 45, TRUE),
    (2, 'CE202', 'Circuit Analysis', 'Analysis of electric circuits.', 1, 40, TRUE),
    (3, 'CE350', 'Systems Design', 'Design and analysis of engineering systems.', 2, 30, TRUE),
    (4, 'EE210', 'Digital Logic', 'Digital systems and logic circuits.', 3, 50, TRUE);

------------------------------------------------------------
-- COURSE_INSTRUCTOR
------------------------------------------------------------
INSERT INTO course_instructor (id, program_user_id, course_id, is_active)
VALUES
    (1, 2, 1, TRUE), -- Bob at CE101
    (2, 3, 2, TRUE), -- Carol at CE202
    (3, 2, 3, TRUE), -- Bob at CE350
    (4, 4, 4, TRUE); -- Dave at EE210

------------------------------------------------------------
-- COURSE_INDICATOR (map PIs to courses)
------------------------------------------------------------
INSERT INTO course_indicator (id, course_id, indicator_id, is_active)
VALUES
    (1, 1, 1, TRUE),
    (2, 1, 5, TRUE),
    (3, 2, 2, TRUE),
    (4, 2, 3, TRUE),
    (5, 3, 7, TRUE),
    (6, 3, 8, TRUE),
    (7, 4, 4, TRUE),
    (8, 4, 6, TRUE);

------------------------------------------------------------
-- MEASURES
------------------------------------------------------------
INSERT INTO measure (id, course_indicator_id, measure_description,
                     observation, recommended_action, fcar, met, exceeded, below, m_status, is_active)
VALUES
    (1, 1, 'Exam 1 performance', 'Strong performance overall', 'Continue approach',
     'Detailed FCAR text', 25, 10, 10, 'InProgress', TRUE),

    (2, 2, 'Communication rubric evaluation', 'Students struggled with clarity', 'Increase practice assignments',
     'FCAR text', 15, 5, 25, 'Submitted', TRUE),

    (3, 3, 'Circuit problem exam section', 'Incorrect simplification common', 'Add supplemental workshop',
     'FCAR details', 20, 8, 12, 'InProgress', TRUE),

    (4, 4, 'Design assignment scoring', 'Good use of tools', 'Expand assignment scope',
     'More FCAR details', 30, 5, 5, 'Complete', TRUE),

    (5, 5, 'Teamwork assessment', 'Teams performed well', 'Maintain group rotations',
     'Team FCAR', 22, 8, 0, 'InReview', TRUE),

    (6, 6, 'Team decision analysis', 'Some groups lacked cohesion', 'Add leadership module',
     'More content', 10, 5, 15, 'InProgress', TRUE),

    (7, 7, 'Logic lab exam', 'Hands-on performance good', 'Increase lab difficulty',
     'Logic FCAR', 28, 12, 10, 'Submitted', TRUE),

    (8, 8, 'Presentation rubric', 'Oral skills improving', 'More group presentations',
     'Communication FCAR', 20, 10, 20, 'Complete', TRUE);