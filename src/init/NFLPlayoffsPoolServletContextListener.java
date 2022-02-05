package init;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;  
import javax.servlet.ServletContextEvent;  
import javax.servlet.ServletContextListener;

public class NFLPlayoffsPoolServletContextListener implements ServletContextListener{  
      public void contextInitialized(ServletContextEvent event) {  
            ServletContext context = event.getServletContext();  
            
            Properties prop = new Properties();
            try {
            	InputStream input = context.getResourceAsStream("/WEB-INF/NFLPlayoffsPool.properties");
            	prop.load(input);
            }
            catch (Exception e) {
            	e.printStackTrace();
            }
            String url = prop.getProperty("db.url"); //context.getInitParameter("URL");  // from web.xml
            String database = prop.getProperty("db.database");  
            String username = prop.getProperty("db.username");  
            String password = prop.getProperty("db.password");  
            NFLPlayoffsPoolDatabase mydb  = new NFLPlayoffsPoolDatabase(url + database, username, password);  
            context.setAttribute("Database", mydb); 
            System.out.println("context initialized - DB Info: " + url + database + " " + username);
      }  
      public void contextDestroyed(ServletContextEvent event) {  
    	  System.out.println("context destroyed");
      }  
}
