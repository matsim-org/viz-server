--
-- Set character set the client will use to send SQL statements to the server
--
SET NAMES 'utf8';

--
-- Create table `Agent`
--
CREATE TABLE Agent (
  id VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
)
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create table `Visualization`
--
CREATE TABLE Visualization (
  id VARCHAR(255) NOT NULL,
  firstTimestep DOUBLE NOT NULL,
  lastTimestep DOUBLE NOT NULL,
  maxEasting DOUBLE NOT NULL,
  maxNorthing DOUBLE NOT NULL,
  minEasting DOUBLE NOT NULL,
  minNorthing DOUBLE NOT NULL,
  progress INT(11) DEFAULT NULL,
  timestepSize DOUBLE NOT NULL,
  PRIMARY KEY (id)
)
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create table `Snapshot`
--
CREATE TABLE Snapshot (
  id VARCHAR(255) NOT NULL,
  data LONGBLOB DEFAULT NULL,
  timestep DOUBLE NOT NULL,
  visualization_id VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
)
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create foreign key
--
ALTER TABLE Snapshot
  ADD CONSTRAINT FKorxbyygvq9kg4a4j036kg6g9k FOREIGN KEY (visualization_id)
    REFERENCES Visualization(id);

--
-- Create table `Plan`
--
CREATE TABLE Plan (
  id VARCHAR(255) NOT NULL,
  geoJson LONGTEXT DEFAULT NULL,
  idIndex INT(11) NOT NULL,
  visualization_id VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
)
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create foreign key
--
ALTER TABLE Plan
  ADD CONSTRAINT FKfqvljurukomn9l855q21vql4k FOREIGN KEY (visualization_id)
    REFERENCES Visualization(id);

--
-- Create table `Permission`
--
CREATE TABLE Permission (
  id VARCHAR(255) NOT NULL,
  agent_id VARCHAR(255) NOT NULL,
  visualization_id VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
)
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create foreign key
--
ALTER TABLE Permission
  ADD CONSTRAINT FK1vq5kq341al5fqy1l27id8u6n FOREIGN KEY (visualization_id)
    REFERENCES Visualization(id);

--
-- Create foreign key
--
ALTER TABLE Permission
  ADD CONSTRAINT FK605vcay96wytp55rjhwkwx8jt FOREIGN KEY (agent_id)
    REFERENCES Agent(id);

--
-- Create table `MatsimNetwork`
--
CREATE TABLE MatsimNetwork (
  id VARCHAR(255) NOT NULL,
  data LONGBLOB DEFAULT NULL,
  visualization_id VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
)
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create foreign key
--
ALTER TABLE MatsimNetwork
  ADD CONSTRAINT FKj1lnn0pebk0gi96q59hh2wogj FOREIGN KEY (visualization_id)
    REFERENCES Visualization(id);

--
-- Create table `FetchInformation`
--
CREATE TABLE FetchInformation (
  id VARCHAR(255) NOT NULL,
  lastFetch DATETIME DEFAULT NULL,
  PRIMARY KEY (id)
)
CHARACTER SET utf8,
COLLATE utf8_general_ci;