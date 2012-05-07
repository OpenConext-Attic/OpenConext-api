drop table if exists `api_call_log`;

CREATE TABLE `api_call_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `log_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_id` varchar(1000) DEFAULT NULL,
  `spentity_id` varchar(1000) DEFAULT NULL,
  `ip_address` varchar(1000) DEFAULT NULL,
  `api_version` varchar(1000) DEFAULT NULL,
  `resource_url` varchar(1000) DEFAULT NULL,
  `consumer_key` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;