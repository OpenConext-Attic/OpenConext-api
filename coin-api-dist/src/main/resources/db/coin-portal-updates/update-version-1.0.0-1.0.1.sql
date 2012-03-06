ALTER TABLE `shared_resource` MODIFY COLUMN `timestamp` BIGINT(20) DEFAULT NULL;

ALTER TABLE `gadgetdefinition` MODIFY COLUMN `title` VARCHAR(255) NOT NULL;

ALTER TABLE `gadgetdefinition` ADD COLUMN `custom_gadget` CHAR(1) DEFAULT "F";

ALTER TABLE `gadgetdefinition` ADD COLUMN `status` int(11) DEFAULT 0;
