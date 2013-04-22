SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `userblacklisttopic`
-- ----------------------------
DROP TABLE IF EXISTS `userblacklisttopic`;
CREATE TABLE `userblacklisttopic` (
`User_id`  bigint(20) NOT NULL ,
`Topic_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`User_id`, `Topic_id`),
FOREIGN KEY (`Topic_id`) REFERENCES `topic` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`User_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `FK_ubt_topic_id` USING BTREE (`Topic_id`),
INDEX `FK_ubt_user_id` USING BTREE (`User_id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

-- ----------------------------
-- Table structure for `userfavouritetopic`
-- ----------------------------
DROP TABLE IF EXISTS `userfavouritetopic`;
CREATE TABLE `userfavouritetopic` (
`User_id`  bigint(20) NOT NULL ,
`Topic_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`User_id`, `Topic_id`),
FOREIGN KEY (`Topic_id`) REFERENCES `topic` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`User_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `FK_ubt_topic_id` USING BTREE (`Topic_id`),
INDEX `FK_ubt_user_id` USING BTREE (`User_id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;
