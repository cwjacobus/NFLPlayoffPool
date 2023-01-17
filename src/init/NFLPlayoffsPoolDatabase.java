package init;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.SQLException;  
public class NFLPlayoffsPoolDatabase { 
	
	private  Connection con;
	private String connectionString;
	
	public NFLPlayoffsPoolDatabase(String url,String username,String password) {  
        try {  
              Class.forName("com.mysql.cj.jdbc.Driver"); 
              connectionString = url + "?user=" + username + "&password=" + password + "&useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true";
              this.con = DriverManager.getConnection(connectionString);  
        } catch (ClassNotFoundException e) {   
              e.printStackTrace();  
        } catch (SQLException e) {    
        	System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());  
        }  
	}
	  
	public Connection getCon() {  
		return con;  
    }  
	
	public Connection reconnectAfterTimeout() {  
		try {
			this.con = DriverManager.getConnection(connectionString); 
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return con;  
    }
        
}
