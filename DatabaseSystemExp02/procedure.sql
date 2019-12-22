-- select `number` from cart_details where (cart_customer_id, cart_id, goods_id) = (0, 0, 0);

drop procedure if exists add_customer;
drop procedure if exists add_cart_details;
drop procedure if exists add_order_details;
drop procedure if exists purchase_X;
drop procedure if exists purchase_price_less_than_X;

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
	insert into cart_details(cart_customer_id, cart_id, goods_id, `number`, price) 
		values(_cart_customer_id, _cart_id, _goods_id, num, _price);
	update cart set total_price = total_price + _price * cast(num as double) where (id, customer_id) = (_cart_id, _cart_customer_id);
end
//


delimiter //
create procedure add_order_details(In _order_cart_customer_id int, In _order_cart_id int, In _order_id int, IN _goods_id int, In num int, In _price double)
begin
	if num<=0 || num > (select remain from goods where id = _goods_id)  
		|| num > (select `number` from cart_details where (cart_customer_id, cart_id, goods_id) = (_order_cart_customer_id, _order_cart_id, _goods_id) ) 
	then
		set @tmp=0;	-- do nothing
    else
		insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price) 
						   values(_order_cart_customer_id, _order_cart_id, _order_id, _goods_id , num,     _price);
                           
		update orders set price = price + _price * num 
			where (id, cart_id, cart_customer_id) = (_order_id, _order_cart_id, _order_cart_customer_id);
                           
		update goods set remain = remain - num 
			where id = _goods_id;
		-- select * from cart_details where (cart_customer_id, cart_id, goods_id) = (_order_cart_customer_id, _order_cart_id, _goods_id);
        
        update cart_details set `number` = `number` - num 
			where (cart_customer_id, cart_id, goods_id) = (_order_cart_customer_id, _order_cart_id, _goods_id);
		
        if 0 = (select `number` from cart_details where (cart_customer_id, cart_id, goods_id) = (_order_cart_customer_id, _order_cart_id, _goods_id))
		then delete from cart_details where (cart_customer_id, cart_id, goods_id) = (_order_cart_customer_id, _order_cart_id, _goods_id);
        end if;
        
       update cart set total_price = total_price - cast(num as double) * _price 
			where (id, customer_id) = (_order_cart_id, _order_cart_customer_id);
    end if;
end
//


-- buy certain number
delimiter //
create procedure purchase_X(In _customer_id int, In _cart_id int, In _order_id int, In _goods_id int, In num int)
begin
declare msg varchar(200);
if num > (select remain from goods where id = _goods_id) 
	|| num > (select `number` from cart_details 
				where (cart_customer_id, cart_id, goods_id) = (_customer_id, _cart_id, _goods_id)) 
then
	set msg = "no enough goods left";
	SIGNAL SQLSTATE '45000' SET message_text=msg;
else
	call add_order_details(_customer_id, _cart_id, _order_id, _goods_id, num, (select price from goods where id = _goods_id));
end if;
end
//


delimiter //
create procedure purchase_price_less_than_X(In _customer_id int, In _cart_id int, In max_price double, In num int)
begin
	declare tmp_order_id int;
	declare done int;
	declare tmp_goods_id int;
	declare cur cursor for 
		select goods_id as tmp_goods_id from cart_details 
        where  (cart_customer_id, cart_id) = (_customer_id, _cart_id)
			  && price <= max_price; 

	declare continue handler for not found set done=1;
    
	-- create order
	
    set tmp_order_id  = 1 + (select max(id) from orders);
    insert into orders(cart_customer_id, cart_id, id, `time`, `number`, price) 
		values(_customer_id, _cart_id, tmp_order_id, now(), "0000111122223334", 0);

	open cur;
    read_loop: loop
		if done=1 then leave read_loop; end if;
		fetch cur into tmp_goods_id;
        -- select tmp_goods_id;
        call purchase_X(_customer_id, _cart_id, @tmp_order_id, tmp_goods_id, num);
    end loop;
end
//
