--
-- Database: `coin_portal`
--

-- --------------------------------------------------------

--
-- Table structure for table `tab`
--

CREATE TABLE IF NOT EXISTS `tab` (
  `object_type` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL auto_increment,
  `creation_timestamp` bigint(20) default NULL,
  `name` varchar(255) default NULL,
  `favorite` char(1) default NULL,
  `tab_order` int(11) default NULL,
  `owner_id` varchar(255) default NULL,
  `team` varchar(255) default NULL,
  `team_title` varchar(255) default NULL,
  INDEX (`owner_id` ASC),
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `gadgetdefinition`
--

CREATE TABLE IF NOT EXISTS `gadgetdefinition` (
  `id` bigint(20) NOT NULL auto_increment,
  `added` datetime default NULL,
  `approved` char(1) NOT NULL,
  `author` varchar(255) default NULL,
  `author_email` varchar(255) default NULL,
  `custom_gadget` char(1) default "F",
  `description` longtext,
  `install_count` int(11) NOT NULL,
  `screenshot` varchar(255) default NULL,
  `status` int(11) default 0,
  `supports_groups` char(1) NOT NULL,
  `supportssso` char(1) NOT NULL,
  `thumbnail` varchar(255) default NULL,
  `title` varchar(255) NOT NULL,
  `url` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `url` (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `gadget`
--

CREATE TABLE IF NOT EXISTS `gadget` (
  `id` bigint(20) NOT NULL auto_increment,
  `creation_timestamp` bigint(20) default NULL,
  `gadget_column` int(11) default NULL,
  `gadget_order` int(11) default NULL,
  `has_permission` char(1) NOT NULL,
  `prefs` longtext,
  `definition` bigint(20) default NULL,
  `tab_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKB549144C9814CAC0` (`definition`),
  KEY `FKB549144C87F32C2C` (`tab_id`),
  CONSTRAINT `FKB549144C87F32C2C` FOREIGN KEY (`tab_id`) REFERENCES `tab` (`id`),
  CONSTRAINT `FKB549144C9814CAC0` FOREIGN KEY (`definition`) REFERENCES `gadgetdefinition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `shared_resource`
--

CREATE TABLE IF NOT EXISTS `shared_resource` (
  `object_type` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL auto_increment,
  `shared_by` varchar(255) default NULL,
  `shared_by_display_name` varchar(255) default NULL,
  `team_title` varchar(255) default NULL,
  `timestamp` bigint(20) default NULL,
  `gadget_id` bigint(20) default NULL,
  `tab_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK8676AA887F32C2C` (`tab_id`),
  KEY `FK8676AA87CDEBE68` (`gadget_id`),
  CONSTRAINT `FK8676AA87CDEBE68` FOREIGN KEY (`gadget_id`) REFERENCES `gadget` (`id`),
  CONSTRAINT `FK8676AA887F32C2C` FOREIGN KEY (`tab_id`) REFERENCES `tab` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `invite`
--

CREATE TABLE IF NOT EXISTS `invite` (
  `id` bigint(20) NOT NULL auto_increment,
  `creation_timestamp` bigint(20) default NULL,
  `email` varchar(255) default NULL,
  `invitee` varchar(255) default NULL,
  `status` varchar(255) default NULL,
  `shared_resource_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKB9722F09FB43F7D3` (`shared_resource_id`),
  CONSTRAINT `FKB9722F09FB43F7D3` FOREIGN KEY (`shared_resource_id`) REFERENCES `shared_resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `template_tab`
--

CREATE TABLE IF NOT EXISTS `template_tab` (
  `id` bigint(20) NOT NULL auto_increment,
  `creation_timestamp` bigint(20) default NULL,
  `name` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `template_tab_gadget_definitions`
--

CREATE TABLE IF NOT EXISTS `template_tab_gadget_definitions` (
  `template_tab` bigint(20) NOT NULL,
  `gadget_definitions` bigint(20) NOT NULL,
  PRIMARY KEY  (`template_tab`,`gadget_definitions`),
  KEY `FKC8FC6C5C81970B9D` (`template_tab`),
  KEY `FKC8FC6C5C78466B7A` (`gadget_definitions`),
  CONSTRAINT `FKC8FC6C5C78466B7A` FOREIGN KEY (`gadget_definitions`) REFERENCES `gadgetdefinition` (`id`),
  CONSTRAINT `FKC8FC6C5C81970B9D` FOREIGN KEY (`template_tab`) REFERENCES `template_tab` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;