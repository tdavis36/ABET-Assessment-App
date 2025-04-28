-- db/migration/V3__add_outcomes_indicators.sql

-- At beginning of V3
INSERT INTO Migration_Comment (comment_text)
VALUES ('Starting V3 migration');

-- 1) Add Outcomes 3–6 (1 & 2 already in from V2)
INSERT IGNORE INTO Outcome (outcome_id, outcome_num, outcome_desc) VALUES
  (3, '3', 'Communicate effectively in a variety of professional contexts.'),
  (4, '4', 'Recognize professional responsibilities and make informed judgments in computing practice based on legal and ethical principles.'),
  (5, '5', 'Function effectively as a member or leader of a team engaged in activities appropriate to the program''s discipline.'),
  (6, '6', 'Apply computer science theory and software development fundamentals to produce computing-based solutions. [CS]');

-- 2) Add the remaining Indicators for outcome 1
--    (V2 covered only 1.1–1.2; here we add 1.3–1.6)
INSERT IGNORE INTO Indicator (outcome_id, indicator_num, indicator_desc) VALUES
  (1, 3, 'Student can define a solution to a computational problem'),
  (1, 4, 'Student can effectively collect and document system requirements'),
  (1, 5, 'Student can effectively analyze and model a problem domain'),
  (1, 6, 'Student can identify the relative efficiency of different algorithms using asymptotic notation');

-- 3) Add the remaining Indicators for outcome 2
--    (V2 covered only 2.1–2.2; here we add 2.3–2.7)
INSERT IGNORE INTO Indicator (outcome_id, indicator_num, indicator_desc) VALUES
  (2, 3, 'Student can effectively incorporate requirements outside the problem domain (e.g., a user interface) into the design model'),
  (2, 4, 'Student can plan and implement a testing strategy to ensure that system meets its quality goals'),
  (2, 5, 'Student can collect and analyze runtime benchmark data to characterize the efficiency of an algorithm or data structure'),
  (2, 6, 'Student can specify appropriate security concerns and requirements for a component or system'),
  (2, 7, 'Student can evaluate a component or system to identify security characteristics and identify vulnerabilities');

-- 4) Indicators for outcomes 3–6 (all new)
INSERT IGNORE INTO Indicator (outcome_id, indicator_num, indicator_desc) VALUES
  -- outcome 3
  (3, 1, 'Student can create and present a clear and well-organized technical presentation'),
  (3, 2, 'Student can effectively incorporate technical content into a presentation using appropriate visual, textual, and spoken content'),
  (3, 3, 'Student can write a clear and well-organized technical report'),
  -- outcome 4
  (4, 1, 'Student can analyze and explain the ethical issues surrounding a particular computing topic (for example, peer-to-peer file sharing)'),
  (4, 2, 'Student demonstrates recognition of his or her professional responsibilities as a member of the computing profession'),
  -- outcome 5
  (5, 1, 'Student demonstrates an ability to participate in and implement processes for team communication and coordination'),
  (5, 2, 'Student demonstrates an ability to work closely with other students to solve technical problems'),
  -- outcome 6
  (6, 1, 'Student is proficient in a current programming language'),
  (6, 2, 'Student can create user interfaces using current platforms'),
  (6, 3, 'Student can write programs that use concurrency'),
  (6, 4, 'Student can implement automated tests to satisfy the goals of a testing strategy'),
  (6, 5, 'Student can use appropriate implementation techniques and practices to meet security requirements and/or mitigate discovered vulnerabilities');

-- 5) Add new Courses (omit CS101, CS102, MATH201 — they’re already present)
INSERT IGNORE INTO Course (course_code, course_name) VALUES
  ('CS202', 'Object-Oriented Programming'),
  ('CS210', 'Discrete Mathematics'),
  ('CS220', 'Software Engineering'),
  ('CS230', 'Web Development'),
  ('CS250', 'Computer Organization and Architecture'),
  ('CS320', 'Software Testing and Quality Assurance'),
  ('CS330', 'Computer Security'),
  ('CS335', 'Computer Networks'),
  ('CS340', 'Operating Systems'),
  ('CS350', 'Theory of Computation'),
  ('CS360', 'Database Systems'),
  ('CS420', 'Computer Architecture'),
  ('CS456', 'Ethics in Computing'),
  ('CS481', 'Senior Design Project I'),
  ('CS400', 'Senior Design Project II');

-- 6) Add the rest of the Course→Outcome links
--    (V2 had CS101→1 and CS102→1; here we add everything else)
INSERT IGNORE INTO Course_Outcome (course_code, outcome_id) VALUES
  ('CS101', 6),                -- add the pairing CS101→6
  ('CS201', 1), ('CS201', 6),  -- assuming CS201 was seeded in V2
  ('CS320', 1), ('CS320', 2), ('CS320', 3), ('CS320', 5), ('CS320', 6),
  ('CS330', 2), ('CS330', 4),
  ('CS335', 2), ('CS335', 4), ('CS335', 6),
  ('CS340', 6),
  ('CS350', 1),
  ('CS360', 1), ('CS360', 2),
  ('CS420', 6),
  ('CS456', 4),
  ('CS481', 1), ('CS481', 2), ('CS481', 3), ('CS481', 5), ('CS481', 6),
  ('CS400', 1), ('CS400', 2), ('CS400', 3), ('CS400', 5), ('CS400', 6);

-- At end of V3
INSERT INTO Migration_Comment (comment_text)
VALUES ('V3 migration completed successfully');
