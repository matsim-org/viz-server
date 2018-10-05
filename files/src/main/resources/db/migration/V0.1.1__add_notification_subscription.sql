-- 
-- Set character set the client will use to send SQL statements to the server
--
SET NAMES 'utf8';

--
-- Create Table NotificationType
--
CREATE TABLE NotificationType (
  id VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
)
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Create Table Subscriptions
--
CREATE TABLE Subscription (
  id VARCHAR(255) NOT NULL,
  callback TINYBLOB NOT NULL,
  expiresAt TIMESTAMP,
  type_id VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
)

CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Set unique constraints on tables
--
ALTER TABLE NotificationType
       ADD CONSTRAINT UK_opo80ii7rpop7nfv50fi6aqix unique (name);

ALTER TABLE Subscription
       ADD CONSTRAINT UKfuufemhxwxix6s7ec39a4b5h2 unique (type_id, callback(255));

ALTER TABLE Subscription
       ADD CONSTRAINT FK4btyl2lo5tgh2fn8swx1hbqnp
        FOREIGN KEY (type_id) REFERENCES NotificationType(id);
