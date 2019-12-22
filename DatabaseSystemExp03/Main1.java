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

class Thread1 extends Thread{  
	private Connection conn; 
	public Thread1(Connection conn) { 
		this.conn=conn; 
	    }  
    public void run() {  
    	try {
    		System.out.println("Thread1 running");  
			Statement stat = conn.createStatement();
			for(int i=1;i<=10;i++){ 
				String sql = "select * from test7 where test7_id = " + String.valueOf(i) + ";";
				ResultSet rs =  stat.executeQuery(sql);
				while (rs.next()) { 
					System.out.println("val of id = " + String.valueOf(i)+": " + rs.getString("val"));  
		        }  
				rs.close();
			}	
			conn.commit();
			System.out.println("Thread1 exits");  
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
}  

class Thread2 extends Thread{  
    private Connection conn; 
    public Thread2(Connection conn) {  
       this.conn=conn; 
    }  
    public void run() { 
        try {

    		System.out.println("Thread2 running"); 	
			Statement stat = conn.createStatement();
			stat.executeUpdate("UPDATE test7 SET val = val + 1 where test7_id=1;");
			stat.executeUpdate("UPDATE test7 SET val = val + 1 where test7_id=1;");
			conn.commit();
			System.out.println("Thread2 exits");  
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
}

public class Main {  
    public static void main(String[] args) throws ClassNotFoundException {  
    	try { 
    		Connection conn1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/sys?useSSL=false", "admin","admin");
    		Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/sys?useSSL=false", "admin","admin");
			conn1.setAutoCommit(false);
			conn2.setAutoCommit(false);
    		Thread1 mTh1=new Thread1(conn1);  
    		Thread2 mTh2=new Thread2(conn2);  
    		mTh1.start();  
    		mTh2.start();  
    	}
    	 catch( Exception e )  
        {  
            e.printStackTrace();  
        }  
    }  

}  


