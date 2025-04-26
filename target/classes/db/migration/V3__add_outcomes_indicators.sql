-- Migration to add outcomes and indicators from CRITERION 4-ex.pdf

-- First, clear existing data to avoid conflicts
DELETE FROM Course_Outcome;
DELETE FROM Indicator;
DELETE FROM Outcome;

-- Add the outcomes
INSERT INTO Outcome (outcome_id, outcome_num, outcome_desc) VALUES
(1, '1', 'Analyze a complex computing problem and to apply principles of computing and other relevant disciplines to identify solutions.'),
(2, '2', 'Design, implement, and evaluate a computing-based solution to meet a given set of computing requirements in the context of the program''s discipline.'),
(3, '3', 'Communicate effectively in a variety of professional contexts.'),
(4, '4', 'Recognize professional responsibilities and make informed judgments in computing practice based on legal and ethical principles.'),
(5, '5', 'Function effectively as a member or leader of a team engaged in activities appropriate to the program''s discipline.'),
(6, '6', 'Apply computer science theory and software development fundamentals to produce computing-based solutions. [CS]');

-- Add the indicators for outcome 1
INSERT INTO Indicator (outcome_id, indicator_num, indicator_desc) VALUES
(1, 1, 'Student can correctly interpret a computational problem and define its parameters'),
(1, 2, 'Student can analyze a computation problem in order to choose mathematical and algorithmic principles that can be applied to solve the problem'),
(1, 3, 'Student can define a solution to a computational problem'),
(1, 4, 'Student can effectively collect and document system requirements'),
(1, 5, 'Student can effectively analyze and model a problem domain'),
(1, 6, 'Student can identify the relative efficiency of different algorithms using asymptotic notation');

-- Add the indicators for outcome 2
INSERT INTO Indicator (outcome_id, indicator_num, indicator_desc) VALUES
(2, 1, 'Student can identify and evaluate appropriate technologies to be used in a system'),
(2, 2, 'Student can effectively construct a design model of a system'),
(2, 3, 'Student can effectively incorporate requirements outside the problem domain (e.g., a user interface) into the design model'),
(2, 4, 'Student can plan and implement a testing strategy to ensure that system meets its quality goals'),
(2, 5, 'Student can collect and analyze runtime benchmark data to characterize the efficiency of an algorithm or data structure'),
(2, 6, 'Student can specify appropriate security concerns and requirements for a component or system'),
(2, 7, 'Student can evaluate a component or system to identify security characteristics and identify vulnerabilities');

-- Add the indicators for outcome 3
INSERT INTO Indicator (outcome_id, indicator_num, indicator_desc) VALUES
(3, 1, 'Student can create and present a clear and well-organized technical presentation'),
(3, 2, 'Student can effectively incorporate technical content into a presentation using appropriate visual, textual, and spoken content'),
(3, 3, 'Student can write a clear and well-organized technical report');

-- Add the indicators for outcome 4
INSERT INTO Indicator (outcome_id, indicator_num, indicator_desc) VALUES
(4, 1, 'Student can analyze and explain the ethical issues surrounding a particular computing topic (for example, peer-to-peer file sharing)'),
(4, 2, 'Student demonstrates recognition of his or her professional responsibilities as a member of the computing profession');

-- Add the indicators for outcome 5
INSERT INTO Indicator (outcome_id, indicator_num, indicator_desc) VALUES
(5, 1, 'Student demonstrates an ability to participate in and implement processes for team communication and coordination'),
(5, 2, 'Student demonstrates an ability to work closely with other students to solve technical problems');

-- Add the indicators for outcome 6
INSERT INTO Indicator (outcome_id, indicator_num, indicator_desc) VALUES
(6, 1, 'Student is proficient in a current programming language'),
(6, 2, 'Student can create user interfaces using current platforms'),
(6, 3, 'Student can write programs that use concurrency'),
(6, 4, 'Student can implement automated tests to satisfy the goals of a testing strategy'),
(6, 5, 'Student can use appropriate implementation techniques and practices to meet security requirements and/or mitigate discovered vulnerabilities');

-- Add course-outcome associations from Table 4-2
-- CS101 has outcomes 1 and 6
INSERT INTO Course_Outcome (course_code, outcome_id) VALUES
('CS101', 1),
('CS101', 6);

-- CS201 has outcomes 1, 2, 3, and 6
INSERT INTO Course_Outcome (course_code, outcome_id) VALUES
('CS201', 1),
('CS201', 2),
('CS201', 3),
('CS201', 6);

-- CS320 has outcomes 1, 2, 3, 4, 5, and 6
INSERT INTO Course_Outcome (course_code, outcome_id) VALUES
('CS320', 1),
('CS320', 2),
('CS320', 3),
('CS320', 4),
('CS320', 5),
('CS320', 6);

-- CS330 has outcomes 2 and 4
INSERT INTO Course_Outcome (course_code, outcome_id) VALUES
('CS330', 2),
('CS330', 4);

-- CS335 has outcomes 2, 4, and 6
INSERT INTO Course_Outcome (course_code, outcome_id) VALUES
('CS335', 2),
('CS335', 4),
('CS335', 6);

-- CS340 has outcome 6
INSERT INTO Course_Outcome (course_code, outcome_id) VALUES
('CS340', 6);

-- CS350 has outcome 1
INSERT INTO Course_Outcome (course_code, outcome_id) VALUES
('CS350', 1);

-- CS360 has outcomes 1 and 2
INSERT INTO Course_Outcome (course_code, outcome_id) VALUES
('CS360', 1),
('CS360', 2);

-- CS420 has outcome 6
INSERT INTO Course_Outcome (course_code, outcome_id) VALUES
('CS420', 6);

-- CS456 has outcome 4
INSERT INTO Course_Outcome (course_code, outcome_id) VALUES
('CS456', 4);

-- CS481/CS400 has outcomes 1, 2, 3, 5, and 6
INSERT INTO Course_Outcome (course_code, outcome_id) VALUES
('CS481', 1),
('CS481', 2),
('CS481', 3),
('CS481', 5),
('CS481', 6),
('CS400', 1),
('CS400', 2),
('CS400', 3),
('CS400', 5),
('CS400', 6);

-- Record this migration
INSERT INTO Migration_Comment (comment_text)
VALUES ('Added outcomes and indicators from CRITERION 4-ex.pdf');
