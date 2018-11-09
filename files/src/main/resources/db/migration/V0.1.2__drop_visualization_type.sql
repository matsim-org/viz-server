--
-- Set character set the client will use to send SQL statements to the server
--
SET NAMES 'utf8';

--
-- add column type with an index
--
ALTER TABLE Visualization ADD COLUMN type VARCHAR(255);
ALTER TABLE Visualization ADD INDEX Index_Visualization_Type (type);

--
-- copy visualization types from VisualizationType table
--
UPDATE Visualization, VisualizationType
  SET Visualization.type = VisualizationType.typeName
  WHERE VisualizationType.id = Visualization.type_id;

--
-- remove fk constraint on type_id
--
ALTER TABLE Visualization DROP FOREIGN KEY FK4aj71ht71srgd6prjmurixx6;

--
-- remove column type_id
--
ALTER TABLE Visualization DROP COLUMN type_id;


--
-- Drop table `VisualizationType_requiredFileKeys`
--
DROP TABLE IF EXISTS VisualizationType_requiredFileKeys;

--
-- Drop table `VisualizationType_requiredParamKeys`
--
DROP TABLE IF EXISTS VisualizationType_requiredParamKeys;

--
-- Drop table `VisualizationType`
--
DROP TABLE IF EXISTS VisualizationType;