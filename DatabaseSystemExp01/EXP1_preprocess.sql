-- drop schema mydb;
describe goods;
describe goods_has_orders;
insert into providers (account, code, name, gender, age, tel )
	values ('0001', 'SJTU', 'Tianmao', 'female', 10, '54749110');
insert into providers (account, code, name, gender, age, tel )
	values ('0002', 'FDU', 'Taobao', 'female', 10, '54749111');
insert into providers (account, code, name, gender, age, tel )
	values ('0003', 'THU', 'Jindong', 'male', 10, '54749112');
insert into providers (account, code, name, gender, age, tel )
	values ('0004', 'PKU', 'Tencent', 'male', 10, '54749113');
    
    
insert into customers (account, code, name, gender, age, tel )
	values ('0001', 'SJTU', 'AAAA', 'male', 20, '23333333');
insert into customers (account, code, name, gender, age, tel )
	values ('0002', 'SJTU', 'BBBB', 'female', 20, '23333334');
insert into customers (account, code, name, gender, age, tel )
	values ('0003', 'SJTU', 'AAAA', 'male', 20, '23333335');
insert into customers (account, code, name, gender, age, tel )
	values ('0004', 'SJTU', 'AAAA', 'male', 20, '23333336');
    
    
-- a simple test of query, insert and delete
select 	name, account, code from customers;
insert into customers (account, code, name) values ("delete_now!", "FDU", "Garbage" );
select 	name, account, code from customers;
delete from customers where account = "delete_now!";
select 	name, account, code from customers;

