-- This script will fix your database issues immediately
-- Run this in your MariaDB client or management tool

-- Drop professor_courses table if it exists (from previous attempt)
DROP TABLE IF EXISTS `professor_courses`;

-- Drop fcar_assignment table if it exists (from previous attempt)
DROP TABLE IF EXISTS `fcar_assignment`;

-- Create the assigned_courses table if it doesn't exist
CREATE TABLE IF NOT EXISTS `assigned_courses` (
                                                  `id` INT PRIMARY KEY AUTO_INCREMENT,
                                                  `professor_id` INT NOT NULL,
                                                  `course_code` VARCHAR(20) NOT NULL,
                                                  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                  UNIQUE KEY `unique_professor_course` (`professor_id`, `course_code`)
);

-- Create indexes for faster queries
CREATE INDEX IF NOT EXISTS `idx_assigned_courses_professor_id` ON `assigned_courses` (`professor_id`);
CREATE INDEX IF NOT EXISTS `idx_assigned_courses_course_code` ON `assigned_courses` (`course_code`);

-- Populate the table with data from FCARs where professors are already assigned
INSERT IGNORE INTO `assigned_courses` (`professor_id`, `course_code`)
SELECT DISTINCT f.instructor_id, f.course_code
FROM `FCAR` f
WHERE f.instructor_id IS NOT NULL;

-- Show the data to verify
SELECT * FROM `assigned_courses`;
