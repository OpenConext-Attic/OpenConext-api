CREATE TABLE `oauth_code` (
  `code` varchar(255) NOT NULL,
  `authentication` blob NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
