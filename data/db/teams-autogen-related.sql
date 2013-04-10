/* marks all the users to take part in auto teams generation by default */
update User set takePartInAutoTeams = 1 where role <> 0

/* removes all empty teams */
delete c.* from Command c where (select count(*) from User u where u.command_id = c.id) = 0

/* modifies (actually corrupts) all user emails for safe testing */
update User set email = CONCAT(email, '.test') where id not in (1, 91, 103, 106, 177)

/* update Command related foreign keys restrictions */
ALTER TABLE `TeamMemberActivity` DROP FOREIGN KEY `FK65C48BA6F0221DE4`
ALTER TABLE `TeamMemberActivity` ADD CONSTRAINT `FK65C48BA6F0221DE4` FOREIGN KEY (`team_id`) REFERENCES `Command` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
ALTER TABLE `TeamMemberActivity` DROP FOREIGN KEY `FK65C48BA647140EFE`
ALTER TABLE `TeamMemberActivity` ADD CONSTRAINT `FK65C48BA647140EFE` FOREIGN KEY (`user_id`) REFERENCES `User` (`id`) ON DELETE CASCADE ON UPDATE CASCADE

ALTER TABLE `Command_Topic` DROP FOREIGN KEY `FK4C856AFB74A93236`
ALTER TABLE `Command_Topic` ADD CONSTRAINT `FK4C856AFB74A93236` FOREIGN KEY (`Command_id`) REFERENCES `Command` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
ALTER TABLE `Command_Topic` DROP FOREIGN KEY `FK4C856AFBC26F92A1`
ALTER TABLE `Command_Topic` ADD CONSTRAINT `FK4C856AFBC26F92A1` FOREIGN KEY (`topics_id`) REFERENCES `Topic` (`id`) ON DELETE CASCADE ON UPDATE CASCADE