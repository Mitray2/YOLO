SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `UserBlacklistTeam`
-- ----------------------------
DROP TABLE IF EXISTS `UserBlacklistTeam`;
CREATE TABLE `UserBlacklistTeam` (
`User_id`  bigint(20) NOT NULL ,
`Team_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`User_id`, `Team_id`),
FOREIGN KEY (`Team_id`) REFERENCES `command` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`User_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `FKDD94EEF47140EFE` USING BTREE (`User_id`),
INDEX `FKDD94EEFF0221DE4` USING BTREE (`Team_id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

-- ----------------------------
-- Table structure for `UserFavouriteTeam`
-- ----------------------------
DROP TABLE IF EXISTS `UserFavouriteTeam`;
CREATE TABLE `UserFavouriteTeam` (
`User_id`  bigint(20) NOT NULL ,
`Team_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`User_id`, `Team_id`),
FOREIGN KEY (`User_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`Team_id`) REFERENCES `command` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `FK584C207B47140EFE` USING BTREE (`User_id`),
INDEX `FK584C207BF0221DE4` USING BTREE (`Team_id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;