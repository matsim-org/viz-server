CREATE TABLE `Bin` (
  `id` varchar(255) NOT NULL,
  `starttime` double DEFAULT NULL,
  `data` longtext DEFAULT NULL,
  `visualization_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_visualization_id` FOREIGN KEY (`visualization_id`) REFERENCES `Visualization` (`id`)
)
CHARACTER SET utf8,
COLLATE utf8_general_ci;
