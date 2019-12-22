-- now the customer 0 add an orders
insert into orders(cart_customer_id, cart_id, id, `time`, `number`, price) values(0, 0, 0, now(), "0000111122223333", 0);

-- test: "no remaining enough", it should throw an exception
insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price) 
				   values(0, 						0, 				0,		0,			100,   (select price from goods where id = 0)		);


-- test: "empty the cart 0"
-- declare tmp_goods_id int; decla
set @tmp_goods_id = 0; 
set @tmp_price = (select price from goods where id = @tmp_goods_id);
insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price) 
				   values(0, 						0, 				0,		@tmp_goods_id,			3,   @tmp_price		);
                   
set @tmp_goods_id = 1; 
set @tmp_price = (select price from goods where id = @tmp_goods_id);                  
insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price) 
				   values(0, 						0, 				0,		@tmp_goods_id,			2,   @tmp_price		);
                   
set @tmp_goods_id = 2; 
set @tmp_price = (select price from goods where id = @tmp_goods_id);                  
insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price) 
				   values(0, 						0, 				0,		@tmp_goods_id,			1,   @tmp_price		);



describe orders;
select * from order_details;
select * from orders; 

select * from customer;
select * from goods;
select * from cart;
select * from cart_details;