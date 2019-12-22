insert into customers (account, code, name, gender, age, tel )
	values ('0005', 'SJTU', 'Alice', 'female', 19, '23333333');
insert into customers (account, code, name, gender, age, tel )
	values ('0006', 'SJTU', 'Bob', 'male', 55, '23333333');

insert into providers (account, code, name, gender, age, tel )
	values ('0005', 'FDU', 'Charlie', 'male', 45, '23333333');
insert into providers (account, code, name, gender, age, tel )
	values ('0006', 'FDU', 'David', 'male', 21, '23333333');

select name from customers 
	where age>30;

select name, age from providers;

SET SQL_SAFE_UPDATES = 0;
delete from providers where name = 'Charlie';

update providers 
	set age = 22
		where name = 'David';

select name, age from providers;