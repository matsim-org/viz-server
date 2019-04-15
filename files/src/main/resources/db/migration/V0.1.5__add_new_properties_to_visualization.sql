--
-- Set character set the client will use to send SQL statements to the server
--
SET NAMES 'utf8';

--
-- Visualizations have new properties like title, thumbnail, and properties
--

ALTER TABLE Visualization
    ADD COLUMN title VARCHAR(255);
UPDATE Visualization
SET title = '';

ALTER TABLE Visualization
    ADD COLUMN thumbnail LONGTEXT;
UPDATE Visualization
SET thumbnail = '';

--
-- properties are a separate table
--
CREATE TABLE Visualization_properties
(
    Visualization_id varchar(255) NOT NULL,
    value            varchar(10000) DEFAULT NULL,
    properties_KEY   varchar(255) NOT NULL,
    PRIMARY KEY (Visualization_id, properties_KEY),
    CONSTRAINT FK556sp5rugb2160u30kcmyw99n FOREIGN KEY (Visualization_id) REFERENCES Visualization (id)
)
    CHARACTER SET utf8,
    COLLATE utf8_general_ci;
