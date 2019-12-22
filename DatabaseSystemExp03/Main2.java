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
 

public class Main {
   public static void main(String[] args) throws SQLException {
      //true represents print XA logs for debugging
      boolean logXaCommands = true;
      //Gain the instance of resource management(RM1)
      Connection conn1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/sys?useSSL=false", "admin", "admin");
      XAConnection xaConn1 = new MysqlXAConnection((com.mysql.jdbc.Connection) conn1, logXaCommands);
      XAResource rm1 = xaConn1.getXAResource();
    // Gain the instance of resource management(RM2)
      Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb?useSSL=false", "admin","admin");
      XAConnection xaConn2 = new MysqlXAConnection((com.mysql.jdbc.Connection) conn2, logXaCommands);
      XAResource rm2 = xaConn2.getXAResource();
      // Executing a distributed transaction using 2PC(2-phase-commit)
      try {
         // TM generates the transaction id on rm1
         Xid xid1;
         // Executing the transaction on rm1
         rm1.start(xid1, XAResource.TMNOFLAGS);//One of TMNOFLAGS, TMJOIN, or TMRESUME.
         PreparedStatement ps1 = conn1.prepareStatement("INSERT into test7 VALUES (1,1)");
         ps1.execute();
         rm1.end(xid1, XAResource.TMSUCCESS);
         // TM generates the transaction id on rm1/
         Xid xid2;
          // Executing the transaction on rm1
         rm2.start(xid2, XAResource.TMNOFLAGS);
         PreparedStatement ps2 = conn2.prepareStatement("INSERT into test VALUES (2,2)");
         ps2.execute();
         rm2.end(xid2, XAResource.TMSUCCESS);
         // ===================Two Phase Commit================================
         // phase1：

         // phase2：  
      
      } catch (XAException e) {
         e.printStackTrace();
      }
   }
}


