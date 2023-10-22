-- 로컬에서 Init 위해 사용함
insert into members(is_deleted, receive_emails, email, name, role) values (false, false, "aa@naver.com", "김태훈", "USER");
insert into plans(title, is_deleted, is_public, star_cnt, view_cnt, owner_id) values ("제목",false, true, 0, 0, 1);
insert into members_of_plan(plan_id, member_id) values (1,1);

insert into tabs(first, version, plan_id, name) values (true, 1, 1, "투두");
insert into tabs(first, version, plan_id, name) values (false, 1, 1, "In Progress");
update tabs set next_id = 2 where tab_id = 1;
insert into tabs(first, version, plan_id, name) values (false, 1, 1, "세번째탭");
update tabs set next_id = 3 where tab_id = 2;

