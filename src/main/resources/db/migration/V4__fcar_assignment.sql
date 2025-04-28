-- db/migration/V4__fcar_assignment.sql

-- At beginning of V4
INSERT INTO Migration_Comment (comment_text)
VALUES ('Starting V4 migration - fcar_assignment');

-- Create the assignment table
CREATE TABLE fcar_assignment (
                                 fcar_id        INT NOT NULL,
                                 instructor_id  INT NOT NULL,
                                 PRIMARY KEY (fcar_id, instructor_id),
                                 CONSTRAINT fk_fa_fcar FOREIGN KEY (fcar_id)
                                     REFERENCES `fcar` (fcar_id),
                                 CONSTRAINT fk_fa_user FOREIGN KEY (instructor_id)
                                     REFERENCES `user` (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Record completion
INSERT INTO Migration_Comment (comment_text)
VALUES ('V4 migration completed - fcar_assignment created');
