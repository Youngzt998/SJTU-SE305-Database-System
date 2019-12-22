DROP SCHEMA IF EXISTS `mydb`;
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;


#problem 1
set autocommit = 0;
drop table if exists test1;
create table if not exists test1 (
	test1_id int,
	PRIMARY KEY(test1_id)
) engine=InnoDB;
select connection_id();

insert into test1 values(1);
select * from test1 where test1_id = 1 for update;
insert into test1 values(2);
insert into test1 values(3);
commit;


#problem 2
set autocommit = 0;
drop table if exists test2;
create table if not exists test2 (
	test2_id int,
    val int,
	PRIMARY KEY(test2_id)
) engine=InnoDB;
select connection_id();
insert into test2 values(1,2);
insert into test2 values(2,3);
insert into test2 values(3,4);
select test2_id from test2 where val = 2 for update;
commit;

#problem 3
set autocommit = 0;
drop table if exists test3;
create table if not exists test3 (
	test3_id int,
    val int,
	PRIMARY KEY(test3_id)
) engine=InnoDB;
select connection_id();
insert into test3 values(1,2);
insert into test3 values(2,3);
insert into test3 values(3,4);
select test3_id from test3 where val = 2 lock in share mode;
unlock tables;
commit;

#problem 4
set autocommit = 0;
drop table if exists test4;
create table if not exists test4 (
	test4_id int,
    val int,
    col int,
	PRIMARY KEY(test4_id)
) engine=InnoDB;
select connection_id();
insert into test4 values(1,2,3);
insert into test4 values(2,3,4);
insert into test4 values(3,3,5);
insert into test4 values(4,5,6);
commit;
select * from test4 where test4_id = 3 lock in share mode;
update test4 set col= 10 where test4_id = 3;
rollback;

#problem 5
set autocommit = 0;
drop table if exists test5;
create table if not exists test5 (
	test5_id int,
    val int,
    col int,
	PRIMARY KEY(test5_id)
) engine=InnoDB;
select connection_id();
insert into test5 values(1,2,3);
insert into test5 values(2,3,4);
commit;
insert into test5 values(3,3,7);
select * from test5 where val = 3 for update;
rollback;
insert into test5 values(4,5,6);
commit;

#problem 6
set autocommit = 0;
drop table if exists test6;
create table if not exists test6 (
	test6_id int,
    val int,
    col int,
	PRIMARY KEY(test6_id)
) engine=InnoDB;
select connection_id();
insert into test6 values(1,2,3);
insert into test6 values(2,3,4);
commit;
insert into test6 values(3,3,7);
rollback;
insert into test6 values(4,5,6);
commit;


