--
-- Set character set the client will use to send SQL statements to the server
--
SET NAMES 'utf8';

--
-- Resources now have a updateAt property. We must alter all Resources tables which are
-- Project, FileEntry and Visualization
--

--
-- Add updatedAt property to FileEntry and set it to 'now'
--
ALTER TABLE FileEntry
    ADD COLUMN updatedAt DATETIME;

UPDATE FileEntry
SET updatedAt = CURRENT_TIMESTAMP();

--
-- Add updatedAt property to Visualization and set it to 'now'
--
ALTER TABLE Visualization
    ADD COLUMN updatedAt DATETIME;

UPDATE Visualization
SET updatedAt = CURRENT_TIMESTAMP();

--
-- Add updatedAt property to Project and set it to 'now'
--
ALTER TABLE Project
    ADD COLUMN updatedAt DATETIME;

UPDATE Project
SET updatedAt = CURRENT_TIMESTAMP();

--
-- Tags are now assignable through the Taggable class which FileEntry and Visualization derive from now
-- We need to Create a Taggable_Tag table and copy the old values from FileEntry_Tag
-- At last we delete the old FileEntry_Tag table
--

--
-- Create Table Taggable_Tag
--
CREATE TABLE Taggable_Tag
(
    Taggable_id VARCHAR(255) NOT NULL,
    tags_id     VARCHAR(255) NOT NULL,
    PRIMARY KEY (Taggable_id, tags_id),
    KEY FKq7cqx9j002d4ywlft24j0ox3e (tags_id),
    CONSTRAINT FKq7cqx9j002d4ywlft24j0ox3e FOREIGN KEY (tags_id) REFERENCES Tag (id)
)
    CHARACTER SET utf8,
    COLLATE utf8_general_ci;

--
-- Also give a tag summary column to Visualization
--
ALTER TABLE Visualization
    ADD COLUMN tagSummary VARCHAR(64);

UPDATE Visualization
SET tagSummary = '';

--
-- Copy all values from FileEntry_Tag
--
UPDATE Taggable_Tag, FileEntry_Tag
SET Taggable_Tag.Taggable_id = FileEntry_Tag.FileEntry_id;
UPDATE Taggable_Tag, FileEntry_Tag
SET Taggable_Tag.tags_id=FileEntry_Tag.tags_id;

--
-- Drop old FileEntry table
--
DROP TABLE IF EXISTS FileEntry_Tag;