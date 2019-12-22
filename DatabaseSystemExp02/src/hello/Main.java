package hello;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.sql.ResultSet;

public class Main {
    public static void main(String[] args) {

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true", "Youngzt","2147483647");
            Boolean result = false;
            excute(conn,"insert into orders(cart_customer_id, cart_id, id, `time`, `number`, price) values(0, 0, 0, now(), \"0000111122223333\", 0);");

            double price = 0;
            price = queryDouble(conn, "select price from goods where id = 0");
            //System.out.println(price);
            add_order_details(conn, 0, 0, 0, 0, 3, price);

            price = queryDouble(conn, "select price from goods where id = 1");
            add_order_details(conn, 0, 0, 0, 1, 2, price);

            price = queryDouble(conn, "select price from goods where id = 2");
            add_order_details(conn, 0, 0, 0, 2, 1, price);


            // too many
            price = queryDouble(conn, "select price from goods where id = 0");
            add_order_details(conn, 0, 1, 0, 0, 100, price);

            add_customer(conn, 1, "test", "12345", 0, "FDU");
            add_customer(conn, 1, "test", "SjTu12345", 0, "FDU");
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    public static double queryDouble(Connection conn, String sql)
    {
        double result = 0;
        try {
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next())
            {
                result = rs.getDouble(1);
            }
            rs.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static int queryInt(Connection conn, String sql)
    {
        int result = 0;
        try {
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next())
            {
                result = rs.getInt(1);
            }
            rs.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean excute(Connection conn, String sql)
    {
        Boolean rs = false;
        try {
            Statement stat = conn.createStatement();
            rs = stat.execute(sql);
            //if(!rs) System.out.println("excute failed");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    public static void add_order_details(Connection conn, int customer_id, int cart_id, int order_id, int goods_id, int number, double price)
    {
        int remain = 0;
        remain = queryInt(conn, "select `number` from cart_details where (cart_customer_id, cart_id, goods_id) = (" + customer_id + "," + cart_id + "," + goods_id + ")" );
        if(remain < number)
        {
            System.out.println("no enough remaining");
            return;
        }

        excute(conn, "insert into order_details(order_cart_customer_id, order_cart_id, order_id ,goods_id, `number`, price)" +
                "\t\tvalues(" + customer_id + ", " + cart_id + "," + order_id + "," + goods_id + "," + number + ", " + price + ");"
              );

        excute(conn, "update orders set price = price +"   + price + "*" + number + " where (id, cart_id, cart_customer_id) = (" + order_id + "," + cart_id + "," +  customer_id + ");" );
        excute(conn, "update goods set remain = remain - " + number + " where id = " + goods_id + ";");
        excute(conn, "update cart_details set `number` = `number` - " + number + " where (cart_customer_id, cart_id, goods_id) = (" + customer_id + "," + cart_id + "," + goods_id +")");

        int tmp = queryInt(conn, "select `number` from cart_details where (cart_customer_id, cart_id, goods_id) = (" + customer_id + "," + cart_id + "," + goods_id +")");
        if (tmp==0)
        {
            excute(conn, "delete from cart_details where (cart_customer_id, cart_id, goods_id) = (" + customer_id + "," + cart_id + "," + goods_id +")");
        }

        //excute(conn, "update cart set price = price -"+ price + "*" + number + " where (id, customer_id) = (" + cart_id + "," +  customer_id + ");" );

    }


    public static void add_customer(Connection conn, int id, String name, String code, int gender, String address)
    {
        if(!isValidCode(code))
        {
            System.out.println("code is too simple or not valid!");
            return;
        }
        if(name.length()>20)
        {
            System.out.println("name is too long!");
            return;
        }

        if(gender!=0 && gender !=1)
        {
            System.out.println("gender not valied!");
            return;
        }

       Boolean result = excute(conn, "insert into customer(id, name, code, gender, address) values(" + id + "," + "\"" + name + "\"" + "," +  "\"" + code + "\"" + "," + gender + "," + "\"" + address + "\"" + ");" );

        if(result)
            System.out.println("add customer success!");
        else
            System.out.println("add customer failed!");
    }

    public static final String REG_NUMBER = ".*\\d+.*";
    public static final String REG_UPPERCASE = ".*[A-Z]+.*";
    public static final String REG_LOWERCASE = ".*[a-z]+.*";
    public static boolean isValidCode(String password)
    {
        int i =0;
        if(password == null || password.length() < 8)
            return false;

        if (password.matches(REG_NUMBER)) i++;
        if (password.matches(REG_LOWERCASE))i++;
        if (password.matches(REG_UPPERCASE)) i++;

        if (i  < 2 )  return false;

        return true;
    }


}






