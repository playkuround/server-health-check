-- USERS
insert into users(token)
values ('asdfasdf');

-- EMAIL
insert into email(email)
values ('hsk4991149@naver.com');

-- TARGET
insert into target(host, health_checkurl, last_status, last_checked_at, consecutive_fail_count, today_send)
values ('a.com', 'https://playkuround.com/api/system-available', 200, DATEADD('DAY', -1, NOW()), 0, false);

insert into target(host, health_checkurl, last_status, last_checked_at, consecutive_fail_count, today_send)
values ('b.com', 'https://playkuround.com/api/system-available', 500, DATEADD('DAY', -1, NOW()), 1, false);

insert into target(host, health_checkurl, last_status, last_checked_at, consecutive_fail_count, today_send)
values ('c.com', 'https://playkuround.com/api/system-available', 100, DATEADD('DAY', -1, NOW()), 0, false);

-- RESULT
insert into result(target_id, health_checkurl, status, created_at, checked_at, error_log)
values (1, 'https://playkuround.com/api/system-available', 500, DATEADD('DAY', -1, NOW()), DATEADD('DAY', -1, NOW()),
        'server error');

insert into result(target_id, health_checkurl, status, created_at, checked_at, error_log)
values (1, 'https://playkuround.com/api/system-available', 201, DATEADD('DAY', -1, NOW()), DATEADD('DAY', -1, NOW()),
        null);

insert into result(target_id, health_checkurl, status, created_at, checked_at, error_log)
values (1, 'https://playkuround.com/api/system-available', 200, DATEADD('DAY', -1, NOW()), DATEADD('DAY', -1, NOW()),
        null);

insert into result(target_id, health_checkurl, status, created_at, checked_at, error_log)
values (2, 'https://playkuround.com/api/system-available', 500, DATEADD('DAY', -1, NOW()), DATEADD('DAY', -1, NOW()),
        'server error');

insert into result(target_id, health_checkurl, status, created_at, checked_at, error_log)
values (3, 'https://playkuround.com/api/system-available', 100, DATEADD('DAY', -1, NOW()), DATEADD('DAY', -1, NOW()),
        null);

-- REPORT
insert into report(target_id, date, success_count, fail_count, other_count)
values (1, DATEADD('DAY', -1, NOW()), 2, 1, 0);

insert into report(target_id, date, success_count, fail_count, other_count)
values (2, DATEADD('DAY', -1, NOW()), 0, 1, 0);

insert into report(target_id, date, success_count, fail_count, other_count)
values (3, DATEADD('DAY', -1, NOW()), 0, 0, 1);
