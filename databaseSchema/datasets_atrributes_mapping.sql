CREATE TABLE `datasets_atrributes_mapping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `datasetId` int(11) NOT NULL,
  `attributeMetadataId` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `datasetFK_idx` (`datasetId`),
  KEY `attributeMetadataFK_idx` (`attributeMetadataId`),
  CONSTRAINT `datasetFK` FOREIGN KEY (`datasetId`) REFERENCES `datasets` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `attributeMetadataFK` FOREIGN KEY (`attributeMetadataId`) REFERENCES `attribute_metadata` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;