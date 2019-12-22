-- describe customer;

-- initialize table
insert into customer(id, name, code, gender, address) values(0, "youngster", "SjTu38324", "1", "SJTU");
-- goods
insert into goods(id, name, price, remain) values(0, "cpu", 3832.4, 5);
insert into goods(id, name, price, remain) values(1, "gpu", 14122.0, 3);
insert into goods(id, name, price, remain) values(2, "memory", 383.24, 10);
insert into goods(id, name, price, remain) values(3, "motherboard", 141.0, 2);
insert into goods(id, name, price, remain) values(4, "storage", 1412.2, 3);
-- suppose one customer can have mutiple carts
insert into cart(id, total_price, customer_id) values (0, 0.0, 0);
insert into cart(id, total_price, customer_id) values (1, 0.0, 0);
-- 

-- suppose the customer 0 add something to the cart
insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) values(0, 0, 0, 3, (select price from goods where id = 0));
insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) values(0, 0, 1, 2, (select price from goods where id = 1));
insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) values(0, 0, 2, 5, (select price from goods where id = 2));

insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) values(0, 1, 0, 3, (select price from goods where id = 0));
insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) values(0, 1, 1, 2, (select price from goods where id = 1));
insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) values(0, 1, 2, 5, (select price from goods where id = 2));

-- delete from customer where id = 1;

describe orders;
select * from order_details;
select * from orders; 

-- select * from customer;
select * from goods;
select * from cart;
select * from cart_details;

select * from customer;

insert into customer(id, name, code, gender, address) values(1, "young", "38324", "1", "SJTU");
insert into customer(id, name, code, gender, address) values(1, "young", "fdu38324", "1", "SJTU");
insert into customer(id, name, code, gender, address) values(1, "young", "FdU38324", "1", "SJTU");