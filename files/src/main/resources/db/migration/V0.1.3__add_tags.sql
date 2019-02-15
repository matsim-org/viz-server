--
-- Set character set the client will use to send SQL statements to the server
--
SET NAMES 'utf8';

--
-- Add Column tagSummary and fill it with empty strings, so that we can
-- add a NOT NULL constraint later
--
ALTER TABLE FileEntry
   ADD COLUMN tagSummary VARCHAR(64);

UPDATE FileEntry
  SET tagSummary='';

--
-- Implement stricter constraints on FileEntry Table
--
ALTER TABLE FileEntry
   MODIFY userFileName VARCHAR(255) NOT NULL;

ALTER TABLE FileEntry
   MODIFY persistedFileName VARCHAR(255) NOT NULL;

--
-- Add unique index which takes tags into account and drop the old one which just had project and filename
--
ALTER TABLE FileEntry
  DROP INDEX IF EXISTS UK7hxy3ulmjevc84428a6ecvs39;

ALTER TABLE FileEntry
  ADD UNIQUE INDEX projectId_tagSummary_userFileName(project_id, tagSummary, userFileName);

--
-- Create Tag Table and set Constraints
--
CREATE TABLE Tag(
  id VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  type VARCHAR(255) NOT NULL,
  project_id VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
)

CHARACTER SET utf8,
COLLATE utf8_general_ci;;

create table FileEntry_Tag (
   FileEntry_id varchar(255) not null,
    tags_id varchar(255) not null,
    primary key (FileEntry_id, tags_id)
)

CHARACTER SET utf8,
COLLATE utf8_general_ci;;

--
-- Create FileEntry_Tag table required by the ManyToMany relation
--
alter table FileEntry_Tag
   add constraint FKele0iduqwkk8io02is3mqj74q
   foreign key (tags_id)
   references Tag(id);

alter table FileEntry_Tag
   add constraint FKexd92fpkr55n2s1wj7sl2wati
   foreign key (FileEntry_id)
   references FileEntry(id);

ALTER TABLE Tag
  ADD UNIQUE INDEX name_project_id(name, project_id);

ALTER TABLE Tag
  ADD CONSTRAINT tag_project_fk FOREIGN KEY (project_id)
    REFERENCES Project(id);
