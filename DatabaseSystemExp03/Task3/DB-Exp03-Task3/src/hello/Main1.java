package hello;
import com.mysql.jdbc.jdbc2.optional.MysqlXAConnection;
import com.mysql.jdbc.jdbc2.optional.MysqlXid;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;


public class Main1 {

    public static void main(String[] args) throws ClassNotFoundException {
        boolean additional = true;

        try {

            if(!additional){//非附加题
                //连接数据库
                Connection conn1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true", "Youngzt","2147483647");
                Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true", "Youngzt","2147483647");
                conn1.setAutoCommit(false);
                conn2.setAutoCommit(false);

                excute(conn1, "drop table if exists testTable");
                excute(conn1, "create table `mydb`.testTable(`id` char not null)");
                init();

                System.out.println("任意输入开始场景1");
                System.in.read();
                Thread1 thread1=new Thread1(conn1, 1);
                Thread2 thread2=new Thread2(conn2, 1);
                thread1.start();
                thread2.start();
                thread1.join();
                thread2.join();
                System.out.println("场景1结束");

                System.out.println("任意输入开始场景2");
                System.in.read();
                thread1=new Thread1(conn1, 2);
                thread2=new Thread2(conn2, 2);
                thread1.start();
                thread2.start();
                thread1.join();
                thread2.join();
                System.out.println("场景2结束");

                System.out.println("任意输入开始演示幻读");
                System.in.read();
                thread1=new Thread1(conn1, 3);
                thread2=new Thread2(conn2, 3);
                thread1.start();
                thread2.start();
                thread1.join();
                thread2.join();
                System.out.println("幻读演示结束");




            }else {     //附加题
                //连接数据库
                Connection conn1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true", "Youngzt","2147483647");
                Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true", "Youngzt","2147483647");
                conn1.setAutoCommit(false);
                conn2.setAutoCommit(false);

//                System.out.println("修改隔离级别为后，输入任意键开始演示附加题1- mode: 4-脏读/5-幻读/6-不可重复读");
//                System.in.read();
//                Thread1 thread1=new Thread1(conn1, 4);
//                Thread2 thread2=new Thread2(conn2, 4);
//                thread1.start();
//                thread2.start();
//                thread1.join();
//                thread2.join();
//                System.out.println("附加题1-脏读结束");

//                System.out.println("修改隔离级别为后，输入任意键开始演示附加题1-幻读");
//                System.in.read();
//                Thread1 thread1=new Thread1(conn1, 5);
//                Thread2 thread2=new Thread2(conn2, 5);
//                thread1.start();
//                thread2.start();
//                thread1.join();
//                thread2.join();
//                System.out.println("附加题1-幻读结束");
//
                System.out.println("修改隔离级别为后，输入任意键开始演示附加题1-不可重复读");
                System.in.read();
                Thread1 thread1=new Thread1(conn1, 6);
                Thread2 thread2=new Thread2(conn2, 6);
                thread1.start();
                thread2.start();
                thread1.join();
                thread2.join();
                System.out.println("附加题1-不可重复读结束");

            }




        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }


    private static void init(){

    }


    public static boolean excute(Connection conn, String sql) {
        Boolean rs = false;
        try {
            Statement stat = conn.createStatement();
            rs = stat.execute(sql);
            //if(!rs) System.out.println("excute failed");
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}


//模拟用户A
class Thread1 extends Thread{
    private Connection conn;
    private int mode = 0;
    public Thread1(Connection conn, int mode) {
        this.conn=conn;
        this.mode=mode;
    }

    public void run() {


        if(mode == 1){      //场景1
            try {
                    System.out.println("Thread1 running, user A - situation 1");
                    Statement stat = conn.createStatement();
                    String sql = "select name from goods where price >= 100;";
                    ResultSet rs =  stat.executeQuery(sql);
                    while (rs.next()) {
                        System.out.println("Thread1 situation 1 read:  " + rs.getString("name"));
                    }
                    rs.close();

                    conn.commit();
                    System.out.println("Thread1 situation 1 exits");
                }
            catch (SQLException ex) {
                ex.printStackTrace();
                try {
                    //An error occured so we rollback the changes.
                    this.conn.rollback();
                } catch (SQLException ex1) {
                    ex1.printStackTrace();
                }
            }
        }
        else if (mode==2){ //场景2
            System.out.println("Thread1 running, user A - situation 2");
            try {
                //创建订单
                Statement stat = conn.createStatement();
                //加入订单详情
                stat.execute("insert into orders(cart_customer_id, cart_id, id, `time`, `number`, price) " +
                        "values(0, 0, 0, now(), \"0000111122223333\", 0);");
                stat.execute("insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price)" +
                        "values(0, 0, 0, 1, 30, 0);");
                stat.execute("insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price)" +
                        "values(0, 0, 0, 2, 50, 0);");
                conn.commit();

            }catch (SQLException e){
                e.printStackTrace();
            }

            System.out.println("Thread1 situation 2 exits");
        }
        else if (mode==3){ //幻读演示
            try{
                System.out.println("Thread1 running, user A - situation 3");
                //第一次读取
                Statement stat = conn.createStatement();
                String sql = "select name from goods where price >= 100;";
                ResultSet rs =  stat.executeQuery(sql);
                while (rs.next()) {
                    System.out.println("Thread1 situation 3 first read:  " + rs.getString("name"));
                }
                rs.close();


                //暂停，用户B插入一些数据
                sleep(400);



                //第二次读取
                stat = conn.createStatement();
                sql = "select name from goods where price >= 100;";
                rs =  stat.executeQuery(sql);
                while (rs.next()) {
                    System.out.println("Thread1 situation 3 second read:  " + rs.getString("name"));
                }
                rs.close();

                conn.commit();
            }catch (SQLException | InterruptedException e){
                e.printStackTrace();
            }

        }
        else if (mode==4){ //附加题1-脏读
            try{
                System.out.println("Thread1 running, user A - situation 4");
                //第一次读取
                Statement stat = conn.createStatement();
                String sql = "select name from goods where price >= 100;";
                ResultSet rs =  stat.executeQuery(sql);
                while (rs.next()) {
                    System.out.println("Thread1 situation 4 first read:  " + rs.getString("name"));
                }
                rs.close();


                //暂停，用户B插入一些数据
                sleep(400);


                //第二次读取
                stat = conn.createStatement();
                sql = "select name from goods where price >= 100;";
                rs =  stat.executeQuery(sql);
                while (rs.next()) {
                    System.out.println("Thread1 situation 4 second read:  " + rs.getString("name"));
                }
                rs.close();

                conn.commit();
            }catch (SQLException | InterruptedException e){
                e.printStackTrace();
            }
        }
        else if (mode==5){ //附加题1-幻读
            try{
                System.out.println("Thread1 running, user A - situation 5");
                //第一次读取
                Statement stat = conn.createStatement();
                String sql = "select name from goods where price >= 100;";
                ResultSet rs =  stat.executeQuery(sql);
                while (rs.next()) {
                    System.out.println("Thread1 situation 5 first read:  " + rs.getString("name"));
                }
                rs.close();


                //暂停，用户B插入一些数据
                sleep(400);



                //第二次读取
                stat = conn.createStatement();
                sql = "select name from goods where price >= 100;";
                rs =  stat.executeQuery(sql);
                while (rs.next()) {
                    System.out.println("Thread1 situation 5 second read:  " + rs.getString("name"));
                }
                rs.close();

                conn.commit();
            }catch (SQLException | InterruptedException e){
                e.printStackTrace();
            }
        }
        else if (mode==6){ //附加题1-不可重复读
            try{
                System.out.println("Thread1 running, user A - situation 5");
                //第一次读取
                Statement stat = conn.createStatement();
                String sql = "select * from goods;";
                ResultSet rs =  stat.executeQuery(sql);
                while (rs.next()) {
                    System.out.println("Thread1 situation 6 first read:  " + rs.getString("name"));
                }
                rs.close();


                //暂停，用户B修改一些数据
                sleep(400);


                //第二次读取
                stat = conn.createStatement();
                sql = "select * from goods;";
                rs =  stat.executeQuery(sql);
                while (rs.next()) {
                    System.out.println("Thread1 situation 6 second read:  " + rs.getString("name"));
                }
                rs.close();

                conn.commit();
            }catch (SQLException | InterruptedException e){
                e.printStackTrace();
            }
        }
        else if (mode==7){ //附加题2

        }


        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}


//模拟用户B
class Thread2 extends Thread {
    private Connection conn;
    private int mode;

    public Thread2(Connection conn, int mode) {
        this.conn = conn;
        this.mode = mode;
    }

    public void run() {
        if(mode==1){       //场景1
            try {

                System.out.println("Thread2 running, user B - situation 1");
                Statement stat = conn.createStatement();
                stat.executeUpdate("UPDATE goods SET price = price*0.9;");
                conn.commit();


                System.out.println("Thread2 situation 1 exits");
            } catch (SQLException ex) {
                ex.printStackTrace();
                try {
                    //An error occured so we rollback the changes.
                    this.conn.rollback();
                } catch (SQLException ex1) {
                    ex1.printStackTrace();
                }
            }
        }
        else if (mode==2){//场景2
            System.out.println("Thread2 running, user B - situation 2");
            try {
                //创建订单
                Statement stat = conn.createStatement();
                //加入订单详情
                stat.execute("insert into orders(cart_customer_id, cart_id, id, `time`, `number`, price) " +
                        "values(1, 0, 0, now(), \"0000111122224444\", 0);");
                stat.execute("insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price)" +
                        "values(1, 0, 0, 2, 70, 0);");
                stat.execute("insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price)" +
                        "values(1, 0, 0, 3, 50, 0);");
                conn.commit();
            }catch (SQLException e){
                e.printStackTrace();
            }
            System.out.println("Thread2 situation 2 exits");

        }
        else if (mode==3){ //幻读演示
            try {
                //用户A第一次读取
                sleep(200);
                System.out.println("Thread2 situation 2 insert something");
                Statement stat = conn.createStatement();
                stat.execute("insert into goods(id, name, price, remain) values(4, \"keyboard\", 800, 100);");
                conn.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if (mode==4){ //附加题1-脏读
            try {
                //用户A第一次读取
                sleep(200);

                //用户B插入一个数据
                System.out.println("Thread2 situation  insert something");
                Statement stat = conn.createStatement();
                stat.execute("insert into goods(id, name, price, remain) values(4, \"keyboard\", 800, 100);");

                sleep(400);

                //B回滚
                conn.rollback();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (mode==5){ //附加题1-幻读
            try {
                //用户A第一次读取
                sleep(200);

                //用户B插入一个数据
                System.out.println("Thread2 situation 5 insert something");
                Statement stat = conn.createStatement();
                stat.execute("insert into goods(id, name, price, remain) values(4, \"keyboard\", 800, 100);");

                //B提交
                conn.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (mode==6){ //附加题1-不可重复读
            try {
                //用户A第一次读取
                sleep(200);

                //用户B修改一个数据
                System.out.println("Thread2 situation 6 change something");
                Statement stat = conn.createStatement();
                stat.execute("update goods set name = \"hhhhhhhh\" where id = 1;");

                //B提交
                conn.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (mode==7){ //附加题2

        }


        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}




