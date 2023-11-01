-- 로컬에서 Init 위해 사용함
insert into members(member_id, is_deleted, receive_emails, email, name, role) values (1, false, false, "aa@naver.com", "김태훈", "USER"),
                                                                                     (2, false, false, "bb@gmail.com", "신우성", "USER");

insert into plans(plan_id, title, is_deleted, is_public, star_cnt, view_cnt, owner_id) values (1, "제목",false, true, 0, 0, 1),
                                                                                              (2, "또다른 플랜",false, true, 0, 0, 1);
insert into members_of_plan(plan_id, member_id) values (1,1), (1,2), (2,1);

insert into labels(label_id, plan_id, name) values (1, 1, "공부"),
                                                   (2, 1, "운동"),
                                                   (3, 2, "휴식");

insert into tabs(tab_id, first, version, plan_id, name) values (1, true, 1, 1, "투두");
insert into tabs(tab_id, first, version, plan_id, name) values (2, false, 1, 1, "In Progress");
update tabs set next_id = 2 where tab_id = 1;
insert into tabs(tab_id, first, version, plan_id, name) values (3, false, 1, 1, "세번째탭");
update tabs set next_id = 3 where tab_id = 2;

insert into tasks(task_id, tab_id, name, is_deleted, version) values (1, 1, "투두 첫번째", false, 1);
insert into tasks(task_id, tab_id, name, is_deleted, version) values (2, 1, "투두의 두번째", false, 1);
update tasks set next_id = 2 where task_id = 1;
update tasks set prev_id = 1 where task_id = 2;
insert into tasks(task_id, tab_id, name, is_deleted, version) values (3, 1, "투두의 세번째", false, 1);
update tasks set next_id = 3 where task_id = 2;
update tasks set prev_id = 2 where task_id = 3;
update tabs set last_task_id = 3 where tab_id = 1;

insert into tasks(task_id, tab_id, name, is_deleted, version) values (4, 2, "In Progress의 첫번째", false, 1);
update tabs set last_task_id = 4 where tab_id = 2;
