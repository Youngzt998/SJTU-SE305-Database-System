


insert into customer(id, name, code, gender, address) values(0, "youngster", "38324", "1", "SJTU");
-- goods
insert into goods(id, name, price, remain) values(0, "cpu", 3832.4, 5);
insert into goods(id, name, price, remain) values(1, "gpu", 14122.0, 3);
insert into goods(id, name, price, remain) values(2, "memory", 383.24, 10);
insert into goods(id, name, price, remain) values(3, "motherboard", 141.0, 2);
insert into goods(id, name, price, remain) values(4, "storage", 1412.2, 3);

-- suppose one customer can have mutiple carts
insert into cart(id, total_price, customer_id) values (0, 0.0, 0);
insert into cart(id, total_price, customer_id) values (1, 0.0, 0);

call add_cart_details(0, 0, 0, 3, (select price from goods where id = 0));
call add_cart_details(0, 0, 1, 2, (select price from goods where id = 1));
call add_cart_details(0, 0, 2, 5, (select price from goods where id = 2));

call add_cart_details(0, 1, 0, 3, (select price from goods where id = 0));
call add_cart_details(0, 1, 1, 2, (select price from goods where id = 1));
call add_cart_details(0, 1, 2, 5, (select price from goods where id = 2));
call add_cart_details(0, 1, 3, 5, (select price from goods where id = 3));

-- now the customer 0 add an orders
insert into orders(cart_customer_id, cart_id, id, `time`, `number`, price) values(0, 0, 0, now(), "0000111122223333", 0);


set @tmp_goods_id = 0; 
set @tmp_price = (select price from goods where id = @tmp_goods_id);
call add_order_details(0, 						0, 				0,		@tmp_goods_id,			1000,   @tmp_price);


set @tmp_goods_id = 0; 
set @tmp_price = (select price from goods where id = @tmp_goods_id);
call add_order_details(0, 						0, 				0,		@tmp_goods_id,			3,   @tmp_price);

set @tmp_goods_id = 1; 
set @tmp_price = (select price from goods where id = @tmp_goods_id);
call add_order_details(0, 						0, 				0,		@tmp_goods_id,			2,   @tmp_price);

set @tmp_goods_id = 2; 
set @tmp_price = (select price from goods where id = @tmp_goods_id);
call add_order_details(0, 						0, 				0,		@tmp_goods_id,			1,   @tmp_price);

select * from order_details;
select * from orders;
-- select * from cart_details;
-- select * from cart;
select * from goods;


call purchase_price_less_than_X(0, 1, 500, 2);

call add_customer(1, "young", "14122", "1", "SJTU");
call add_customer(1, "young", "SjTu14122", "1", "SJTU");
select * from customer;

