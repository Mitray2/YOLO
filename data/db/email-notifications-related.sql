/* update all users with 'ru' lang by default */
update User set preferredLang = "ru";

/* update all users to send all notification by default */
insert into User_NotificationType (`User_id`, `notifications_id`) select u.id, n.id from User u, NotificationType n;