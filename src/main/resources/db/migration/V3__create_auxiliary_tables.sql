-- This migration adds auxiliary tables for FCAR extended data storage
-- These tables handle data that doesn't directly map to the core schema

-- Table for storing FCAR assessment methods
CREATE TABLE IF NOT EXISTS FCAR_Assessment_Methods (
                                                       fcar_id INT,
                                                       method_key VARCHAR(100),
                                                       method_value TEXT,
                                                       PRIMARY KEY (fcar_id, method_key),
                                                       FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table for storing FCAR student outcomes
CREATE TABLE IF NOT EXISTS FCAR_Student_Outcomes (
                                                     fcar_id INT,
                                                     outcome_key VARCHAR(100),
                                                     achievement_level INT,
                                                     PRIMARY KEY (fcar_id, outcome_key),
                                                     FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table for storing FCAR improvement actions
CREATE TABLE IF NOT EXISTS FCAR_Improvement_Actions (
                                                        fcar_id INT,
                                                        action_key VARCHAR(100),
                                                        action_value TEXT,
                                                        PRIMARY KEY (fcar_id, action_key),
                                                        FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table for storing FCAR status (since status isn't in the core schema)
CREATE TABLE IF NOT EXISTS FCAR_Status (
                                           fcar_id INT PRIMARY KEY,
                                           status VARCHAR(50) NOT NULL DEFAULT 'Draft',
                                           FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Add status column to FCAR table if we decide to include it in the core schema
-- (commented out since we're using a separate table for now)
-- ALTER TABLE FCAR ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'Draft';