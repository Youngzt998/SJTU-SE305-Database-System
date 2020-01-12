SELECT @@transaction_isolation;
describe customer;

-- initialize table
insert into customer(id, name, code, gender, address) values(0, "Youngster", "SjTu38324", "1", "SJTU");
insert into customer(id, name, code, gender, address) values(1, "youngster", "SjTu38324", "1", "SJTU");

-- goods
insert into goods(id, name, price, remain) values(1, "gpu", 14122.0, 50);
insert into goods(id, name, price, remain) values(2, "memory", 110, 100);
insert into goods(id, name, price, remain) values(3, "motherboard", 80, 200);

-- cart
insert into cart(id, total_price, customer_id) values (0, 0.0, 0);
insert into cart(id, total_price, customer_id) values (0, 0.0, 1);

insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) values(0, 0, 1, 50, (select price from goods where id = 0));
insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) values(0, 0, 2, 100, (select price from goods where id = 1));
insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) values(0, 0, 3, 200, (select price from goods where id = 2));

insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) values(1, 0, 1, 50, (select price from goods where id = 0));
insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) values(1, 0, 2, 100, (select price from goods where id = 1));
insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) values(1, 0, 3, 200, (select price from goods where id = 2));

select name from goods where price >= 100;
select * from goods;
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

