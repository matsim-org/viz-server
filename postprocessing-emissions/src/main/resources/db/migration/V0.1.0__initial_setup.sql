CREATE TABLE `Agent` (
  `id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `FetchInformation` (
  `id` varchar(255) NOT NULL,
  `lastFetch` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `Permission` (
  `id` varchar(255) NOT NULL,
  `agent_id` varchar(255) NOT NULL,
  `visualization_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK605vcay96wytp55rjhwkwx8jt` (`agent_id`),
  CONSTRAINT `FK605vcay96wytp55rjhwkwx8jt` FOREIGN KEY (`agent_id`) REFERENCES `Agent` (`id`)
);

CREATE TABLE `Visualization` (
  `id` varchar(255) NOT NULL,
  `progress` int(11) DEFAULT NULL,
  `cellSize` double NOT NULL,
  `data` longtext DEFAULT NULL,
  `smoothingRadius` double NOT NULL,
  `timeBinSize` double NOT NULL,
  PRIMARY KEY (`id`)
);