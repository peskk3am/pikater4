CREATE TABLE `attribute_numerical_metadata` (
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `baseMetFK` FOREIGN KEY (`id`) REFERENCES `attribute_metadata` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;