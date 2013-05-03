/* Topics */
update topic set team_id = groupId where (select id from command c where c.id = groupId) IS NOT NULL;
delete from topic where (select id from command c where c.id = team_id) IS NULL
