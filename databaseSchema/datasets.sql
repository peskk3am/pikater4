CREATE TABLE `datasets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `filehash` varchar(45) NOT NULL,
  `filename` varchar(45) NOT NULL,
  `ownerId` int(11) NOT NULL,
  `globalMetadataId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `filehash_UNIQUE` (`filehash`),
  KEY `ownerFK_idx` (`ownerId`),
  KEY `globalMetadataFK_idx` (`globalMetadataId`),
  CONSTRAINT `globalMetadataFK` FOREIGN KEY (`globalMetadataId`) REFERENCES `global_metadata` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `ownerFK` FOREIGN KEY (`ownerId`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;