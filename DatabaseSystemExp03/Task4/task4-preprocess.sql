SELECT @@transaction_isolation;
describe customer;

-- initialize table
insert into customer(id, name, code, gender, address) values(0, "Youngster", "SjTu38324", "1", "SJTU");
insert into customer(id, name, code, gender, address) values(1, "youngster", "SjTu38324", "1", "SJTU");


select name from goods where price >= 100;
-- select * from goods;
select * from orders;
select * from order_details;

-- set x = (select price from goods where id = 1);
-- insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price)values(0, 0, 0, 1, 30, 0);

set session transaction isolation level read uncommitted;
SELECT @@transaction_isolation;

CREATE TABLE IF NOT EXISTS `mydb`.`test7` (
	`number` INT UNSIGNED NULL,
    `number1` int unsigned null
)

