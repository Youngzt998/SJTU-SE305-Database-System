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


public class Main2 {
    public static void main(String[] args) throws SQLException {
        //true represents print XA logs for debugging
        boolean logXaCommands = true;

        //Gain the instance of resource management(RM1)
        Connection conn1 = DriverManager.getConnection("jdbc:mysql://202.120.38.131:3306/db517021910683?useSSL=false",
                "517021910683", "123456");
        XAConnection xaConn1 = new MysqlXAConnection((com.mysql.jdbc.Connection) conn1, logXaCommands);
        XAResource rm1 = xaConn1.getXAResource();

        // Gain the instance of resource management(RM2)
        Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true",
                "Youngzt","2147483647");
        XAConnection xaConn2 = new MysqlXAConnection((com.mysql.jdbc.Connection) conn2, logXaCommands);
        XAResource rm2 = xaConn2.getXAResource();
        // Executing a distributed transaction using 2PC(2-phase-commit)
        try {
            // TM generates the transaction id on rm1
            Xid xid1 = new MysqlXid("test1".getBytes(), "b1".getBytes(), 1);
            // Executing the transaction on rm1
            rm1.start(xid1, XAResource.TMNOFLAGS);//One of TMNOFLAGS, TMJOIN, or TMRESUME.
            PreparedStatement ps1 = conn1.prepareStatement("INSERT into test7 VALUES (1,1);");
            ps1.execute();

            ps1 = conn1.prepareStatement("insert into orders(cart_customer_id, cart_id, id, `time`, `number`, price) " +
                    "values(0, 0, 0, now(), \"0000111122223333\", 0);");
            ps1.execute();

            ps1 = conn1.prepareStatement("insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price)" +
                    "values(0, 0, 0, 1, 30, 0);");
            ps1.execute();

            ps1 = conn1.prepareStatement("insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price)" +
                    "values(0, 0, 0, 2, 50, 0);");
            ps1.execute();


            ps1 = conn1.prepareStatement("insert into orders(cart_customer_id, cart_id, id, `time`, `number`, price) " +
                    "values(1, 0, 0, now(), \"0000111122224444\", 0);");
            ps1.execute();

            ps1 = conn1.prepareStatement("insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price)" +
                    "values(1, 0, 0, 2, 10, 0);");
            ps1.execute();

            ps1 = conn1.prepareStatement("insert into order_details(order_cart_customer_id, order_cart_id, order_id, goods_id, `number`, price)" +
                    "values(1, 0, 0, 7, 50, 0);");
            ps1.execute();



            rm1.end(xid1, XAResource.TMSUCCESS);


            // TM generates the transaction id on rm1
            Xid xid2 = new MysqlXid("test2".getBytes(), "b2".getBytes(), 1);
            // Executing the transaction on rm1
            rm2.start(xid2, XAResource.TMNOFLAGS);
            PreparedStatement ps2 = conn2.prepareStatement("select name from goods where price >= 100;");

            ResultSet rs = ps2.executeQuery();
            while(rs.next()) {
                System.out.println(""+rs.getString("name"));
            }
            rs.close();

            ps2 = conn2.prepareStatement("UPDATE goods SET price = price*0.9;");
            ps2.execute();
            rm2.end(xid2, XAResource.TMSUCCESS);

            // ===================Two Phase Commit================================
            // phase1：
            int pre1, pre2;
            pre1 = rm1.prepare(xid1);
            pre2 = rm2.prepare(xid2);

            // phase2：
            if(pre1==XAResource.XA_OK && pre2 == XAResource.XA_OK){
                rm1.commit(xid1, false);
                rm2.commit(xid2, false);
            }
            else {
                rm1.rollback(xid1);
                rm2.rollback(xid2);
            }

        } catch (XAException e) {
            e.printStackTrace();
        }
    }
}


