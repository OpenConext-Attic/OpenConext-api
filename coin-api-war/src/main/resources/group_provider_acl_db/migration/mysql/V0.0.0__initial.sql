drop table if exists `service_provider_group`;

CREATE TABLE `service_provider_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sp_entity_id` varchar(255) DEFAULT '',
  `team_id` varchar(255) DEFAULT '',
  `created_at` varchar(255) NOT NULL,
  `updated_at` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

create index sp_team_index on service_provider_group(sp_entity_id, team_id);
