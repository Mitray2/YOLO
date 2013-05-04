/* Topics */
update Topic set team_id = groupId where (select id from Command c where c.id = groupId) IS NOT NULL;
delete from Topic where (select id from Command c where c.id = team_id) IS NULL;
