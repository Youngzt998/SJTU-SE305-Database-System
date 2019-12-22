-- select `number` from cart_details where (cart_customer_id, cart_id, goods_id) = (0, 0, 0);

drop procedure if exists add_customer;
drop procedure if exists add_cart_details;
drop procedure if exists add_order_details;
drop procedure if exists add_discount;

delimiter //
create procedure add_customer(In _id int, In _name varchar(30), In _code varchar(30), In _gender boolean, In _address varchar(30))
begin
declare i int;
declare msg varchar(200);
set i =0;

if binary _code regexp '[A-Z]'
then	set i = i + 1;
end if;

if binary _code regexp binary'[a-z]'
then	set i = i + 1;
end if;

if binary _code regexp '[0-9]'
then	set i = i + 1;
end if;

select i;

if i <2 then
	set msg = "password not valid";
	SIGNAL SQLSTATE '45001' SET message_text=msg;
else
	insert into customer(id, name, code, gender, address) values(_id, _name, _code, _gender, _address);
end if;



end
//


delimiter //
create procedure add_cart_details(In _cart_customer_id int, In _cart_id int, In _goods_id int, In num int, In _price double)
begin
declare rate double;
set rate = (select discount_rate from discount where (customer_id, goods_id) = (_cart_customer_id, _goods_id)  );
if rate = null then set rate = 1; end if;

	insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) 
		values(_cart_customer_id, _cart_id, _goods_id, num, _price * rate);
	update cart set total_price = total_price + rate * _price * cast(num as double) where (id, customer_id) = (_cart_id, _cart_customer_id);
end
//


delimiter //
create procedure add_order_details(In _order_cart_customer_id int, In _order_cart_id int, In _order_id int, IN _goods_id int, In num int, In _price double)
begin

	declare rate double;
	set rate = (select discount_rate from discount where (customer_id, goods_id) = (_car_customer_id, _goods_id)  );
	if rate = null then set rate = 1; end if;

	if num<=0 || num > (select remain from goods where id = _goods_id)  
		|| num > (select `number` from cart_details where (cart_customer_id, cart_id, goods_id) = (_order_cart_customer_id, _order_cart_id, _goods_id) ) 
	then
		set @tmp=0;	-- do nothing
    else
		insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price) 
						   values(_order_cart_customer_id, _order_cart_id, _order_id, _goods_id , num,     _price * rate);
                           
		update orders set price = price + _price * num * rate
			where (id, cart_id, cart_customer_id) = (_order_id, _order_cart_id, _order_cart_customer_id);
                           
		update goods set remain = remain - num 
			where id = _goods_id;
		-- select * from cart_details where (cart_customer_id, cart_id, goods_id) = (_order_cart_customer_id, _order_cart_id, _goods_id);
        
        update cart_details set `number` = `number` - num 
			where (cart_customer_id, cart_id, goods_id) = (_order_cart_customer_id, _order_cart_id, _goods_id);
		
        if 0 = (select `number` from cart_details where (cart_customer_id, cart_id, goods_id) = (_order_cart_customer_id, _order_cart_id, _goods_id))
		then delete from cart_details where (cart_customer_id, cart_id, goods_id) = (_order_cart_customer_id, _order_cart_id, _goods_id);
        end if;
        
       update cart set total_price = total_price - cast(num as double) * _price * rate
			where (id, customer_id) = (_order_cart_id, _order_cart_customer_id);
    end if;
end
//




-- delimiter //
-- create procedure cancel_orders(In _order_cart_customer_id int, In _order_cart_id int, In _order_id int, IN _goods_id int, In num int, In _price double)
-- begin

-- unpdate order set 

-- end
-- //

delimiter //
create procedure add_discount(In _customer_id int, In _goods_id int, In _discount double)
begin
declare msg varchar(80);
if _discount > 1 || _discount < 0 then
	set msg = "wrong discount";
	SIGNAL SQLSTATE '45002' SET message_text=msg;
else 
	if null = (select id from customer where id = _customer_id) then
		set msg = "no customer";
		SIGNAL SQLSTATE '45003' SET message_text=msg;
	else 
		if null = (select id from goods where id = _goods_id) then
			set msg = "no such goods";
			SIGNAL SQLSTATE '45004' SET message_text=msg;
		else
			insert into discount(customer_id, goods_id, discount_rate) values (_customer_id, _goods_id, _discount);
        end if;
	end if;
end if;    

end
//
