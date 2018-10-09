--
-- Set character set the client will use to send SQL statements to the server
--
SET NAMES 'utf8';

--
-- Create table `Agent`
--
CREATE TABLE Agent (
  DTYPE VARCHAR(31) NOT NULL,
  id VARCHAR(255) NOT NULL,
  authId VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
)

AVG_ROW_LENGTH = 8192,
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create index `UK_5c3q8s3rtfo7j4pxd1or4rs0u` on table `Agent`
--
ALTER TABLE Agent
  ADD UNIQUE INDEX UK_5c3q8s3rtfo7j4pxd1or4rs0u(authId);

--
-- Create table `Project`
--
CREATE TABLE Project (
  id VARCHAR(255) NOT NULL,
  createdAt DATETIME(6) DEFAULT NULL,
  name VARCHAR(255) DEFAULT NULL,
  creator_id VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
)

CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create index `UKe8cg72bddju53fbeyaoiyfrsp` on table `Project`
--
ALTER TABLE Project
  ADD UNIQUE INDEX UKe8cg72bddju53fbeyaoiyfrsp(creator_id, name);

--
-- Create foreign key
--
ALTER TABLE Project
  ADD CONSTRAINT FKn60isyosnsr3ls3pje26n3doh FOREIGN KEY (creator_id)
    REFERENCES Agent(id);

--
-- Create table `FileEntry`
--
CREATE TABLE FileEntry (
  id VARCHAR(255) NOT NULL,
  createdAt DATETIME(6) DEFAULT NULL,
  contentType VARCHAR(255) DEFAULT NULL,
  persistedFileName VARCHAR(255) DEFAULT NULL,
  sizeInBytes BIGINT(20) NOT NULL,
  storageType INT(11) DEFAULT NULL,
  userFileName VARCHAR(255) DEFAULT NULL,
  project_id VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
)

CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create index `UK7hxy3ulmjevc84428a6ecvs39` on table `FileEntry`
--
ALTER TABLE FileEntry
  ADD UNIQUE INDEX UK7hxy3ulmjevc84428a6ecvs39(project_id, userFileName);

--
-- Create index `UKmfsdcggio8b85l1fw7pl2kuph` on table `FileEntry`
--
ALTER TABLE FileEntry
  ADD UNIQUE INDEX UKmfsdcggio8b85l1fw7pl2kuph(project_id, persistedFileName);

--
-- Create foreign key
--
ALTER TABLE FileEntry
  ADD CONSTRAINT FKae4icdax8k4geqsd8hpc0i33q FOREIGN KEY (project_id)
    REFERENCES Project(id);

--
-- Create table `PendingFileTransfer`
--
CREATE TABLE PendingFileTransfer (
  id VARCHAR(255) NOT NULL,
  status INT(11) DEFAULT NULL,
  toStorage INT(11) DEFAULT NULL,
  fileEntry_id VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
)

CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create foreign key
--
ALTER TABLE PendingFileTransfer
  ADD CONSTRAINT FK4gf7qi446714024o9inpu91a FOREIGN KEY (fileEntry_id)
    REFERENCES FileEntry(id);

--
-- Create table `Permission`
--
CREATE TABLE Permission (
  id VARCHAR(255) NOT NULL,
  type INT(11) DEFAULT NULL,
  agent_id VARCHAR(255) DEFAULT NULL,
  resource_id VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
)

CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create index `UKjy9ak7a2cqvt0wcmgu3j7lewf` on table `Permission`
--
ALTER TABLE Permission
  ADD UNIQUE INDEX UKjy9ak7a2cqvt0wcmgu3j7lewf(resource_id, agent_id);

--
-- Create foreign key
--
ALTER TABLE Permission
  ADD CONSTRAINT FK605vcay96wytp55rjhwkwx8jt FOREIGN KEY (agent_id)
    REFERENCES Agent(id);

--
-- Create table `VisualizationParameter`
--
CREATE TABLE VisualizationParameter (
  id VARCHAR(255) NOT NULL,
  parameterKey VARCHAR(255) DEFAULT NULL,
  value VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
)

CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create table `VisualizationType`
--
CREATE TABLE VisualizationType (
  id VARCHAR(255) NOT NULL,
  endpoint TINYBLOB DEFAULT NULL,
  requiresProcessing BIT(1) NOT NULL,
  typeName VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
)

AVG_ROW_LENGTH = 4096,
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create index `UK_4bm6putif44t7dtm4vd5kagrh` on table `VisualizationType`
--
ALTER TABLE VisualizationType
  ADD UNIQUE INDEX UK_4bm6putif44t7dtm4vd5kagrh(typeName);

--
-- Create table `VisualizationType_requiredParamKeys`
--
CREATE TABLE VisualizationType_requiredParamKeys (
  VisualizationType_id VARCHAR(255) NOT NULL,
  requiredParamKeys VARCHAR(255) DEFAULT NULL
)

AVG_ROW_LENGTH = 16384,
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create foreign key
--
ALTER TABLE VisualizationType_requiredParamKeys
  ADD CONSTRAINT FKnpqwmxt94i49k70g599g3uw7h FOREIGN KEY (VisualizationType_id)
    REFERENCES VisualizationType(id);

--
-- Create table `VisualizationType_requiredFileKeys`
--
CREATE TABLE VisualizationType_requiredFileKeys (
  VisualizationType_id VARCHAR(255) NOT NULL,
  requiredFileKeys VARCHAR(255) DEFAULT NULL
)

AVG_ROW_LENGTH = 1820,
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create foreign key
--
ALTER TABLE VisualizationType_requiredFileKeys
  ADD CONSTRAINT FK9u7xgmgnuaky3qe1qvnfwgkgn FOREIGN KEY (VisualizationType_id)
    REFERENCES VisualizationType(id);

--
-- Create table `Visualization`
--
CREATE TABLE Visualization (
  id VARCHAR(255) NOT NULL,
  createdAt DATETIME(6) DEFAULT NULL,
  project_id VARCHAR(255) NOT NULL,
  type_id VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
)

CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create foreign key
--
ALTER TABLE Visualization
  ADD CONSTRAINT FK4aj71ht71srgd6prjmurixx6 FOREIGN KEY (type_id)
    REFERENCES VisualizationType(id);

--
-- Create foreign key
--
ALTER TABLE Visualization
  ADD CONSTRAINT FKkdhre9kttui13t7ebfkkvcm4s FOREIGN KEY (project_id)
    REFERENCES Project(id);

--
-- Create table `VisualizationInput`
--
CREATE TABLE VisualizationInput (
  id VARCHAR(255) NOT NULL,
  inputKey VARCHAR(255) DEFAULT NULL,
  fileEntry_id VARCHAR(255) DEFAULT NULL,
  visualization_id VARCHAR(255) NOT NULL,
  inputFiles_KEY VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
)

CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create foreign key
--
ALTER TABLE VisualizationInput
  ADD CONSTRAINT FK4ctxp3e9f59ttpidi0nnyhxq8 FOREIGN KEY (fileEntry_id)
    REFERENCES FileEntry(id);

--
-- Create foreign key
--
ALTER TABLE VisualizationInput
  ADD CONSTRAINT FKegvh5xqjg9jtdogy3a75mrxhk FOREIGN KEY (visualization_id)
    REFERENCES Visualization(id);

--
-- Create table `Visualization_VisualizationParameter`
--
CREATE TABLE Visualization_VisualizationParameter (
  Visualization_id VARCHAR(255) NOT NULL,
  parameters_id VARCHAR(255) NOT NULL,
  parameters_KEY VARCHAR(255) NOT NULL,
  PRIMARY KEY (Visualization_id, parameters_KEY)
)

CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create index `UK_5pyapp8gsh0g2le672qwc8um5` on table `Visualization_VisualizationParameter`
--
ALTER TABLE Visualization_VisualizationParameter
  ADD UNIQUE INDEX UK_5pyapp8gsh0g2le672qwc8um5(parameters_id);

--
-- Create foreign key
--
ALTER TABLE Visualization_VisualizationParameter
  ADD CONSTRAINT FK6k1t6kbxbwmh5dg0nbilga6o5 FOREIGN KEY (parameters_id)
    REFERENCES VisualizationParameter(id);

--
-- Create foreign key
--
ALTER TABLE Visualization_VisualizationParameter
  ADD CONSTRAINT FKsxopco0jy6n9najftfbfa8krn FOREIGN KEY (Visualization_id)
    REFERENCES Visualization(id);