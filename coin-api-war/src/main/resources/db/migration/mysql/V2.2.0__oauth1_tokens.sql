drop table if exists `oauth1_tokens`;

CREATE TABLE `oauth1_tokens` (
  `token` varchar(255) NOT NULL,
  `callbackUrl` varchar(255) DEFAULT '',
  `verifier` varchar(255) DEFAULT '',
  `secret` varchar(255) NOT NULL,
  `consumerKey` varchar(255) NOT NULL,
  `isAccessToken` bit(1) NOT NULL,
  `tokenTimestamp` bigint(20) DEFAULT NULL,
  `userAuthentication` blob,
  PRIMARY KEY (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;