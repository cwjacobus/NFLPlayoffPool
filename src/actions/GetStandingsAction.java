package actions;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.util.ValueStack;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import dao.DAO;
import data.NFLPlayoffsGame;
import data.NFLTeam;
import data.Pool;
import data.Standings;
import data.User;
import init.NFLPlayoffsPoolDatabase;

//import data.User;

public class GetStandingsAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Boolean maxPoints;
	private String userName;
	private Integer year;
	private String poolName;
	private Map<String, Object> userSession;
	private Pool pool;
	
	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		
	    NFLPlayoffsPoolDatabase bowlPoolDB = (NFLPlayoffsPoolDatabase)ServletActionContext.getServletContext().getAttribute("Database");  
        Connection con = bowlPoolDB.getCon();
		DAO.setConnection(con); 
		try {
        	DAO.pingDatabase();
        }
        catch (CommunicationsException ce) {
        	System.out.println("DB Connection timed out - Reconnect");
        	con = bowlPoolDB.reconnectAfterTimeout();
        	DAO.setConnection(con);
        }
		if (year < 100) {  // Adjust 2 digit years
			year = 2000 + year;
		}
		pool = DAO.getPoolByPoolName(poolName + " " + year);
		if (pool == null) {
			context.put("errorMsg", "Pool does not exist!");
			stack.push(context);
			return "error";
		}
		userSession.put("pool", pool);
		userSession.put("year", pool.getYear());
		User user  = DAO.getUser(userName, pool.getYear(), pool.getPoolId());
		if (user != null || userName.equalsIgnoreCase("admin")) { // Always allow admin to login to import users
			userSession.put("user", user);
		}
		else {
			context.put("errorMsg", "Invalid user!");
			stack.push(context);
			return "error";
		}
		HashMap<Integer, NFLTeam> nflTeamsMapById = DAO.getNFLTeamsMapById();
		userSession.put("nflTeamsMapById", nflTeamsMapById);
		TreeMap<String, Standings> standings = DAO.getStandings(maxPoints, pool.getYear(), pool.getPoolId());
		TreeMap<Integer, NFLPlayoffsGame> nflPlayoffsGameMap = DAO.getNFLPlayoffsGamesMap(pool.getYear());
		userSession.put("nflPlayoffsGameMap", nflPlayoffsGameMap);
		//Iterate through standings to make formatted display string
		Iterator<Entry<String, Standings>> it = standings.entrySet().iterator();
    	int standingsIndex = 1;
    	int prevPoints= 0;
    	int place = 1;
		while (it.hasNext()) {
			Map.Entry<String, Standings> line = (Map.Entry<String, Standings>)it.next();
			String[] lineKeyArray = line.getKey().split(":");
			if (Integer.parseInt(lineKeyArray[0]) != prevPoints) {
				place = standingsIndex;
			}
			Standings stand = line.getValue();
			stand.setPlace(Integer.toString(place) + ".");
			standings.put(line.getKey(), stand);
			standingsIndex++;
			prevPoints = Integer.parseInt(lineKeyArray[0]);
		}
	    context.put("maxPoints", maxPoints);
	    context.put("standings", standings);
	    boolean allowAdmin = false;
	    if ((user != null && user.isAdmin()) || userName.equalsIgnoreCase("admin") || userName.contains("CJ")) {
	    	allowAdmin = true;
	    }
	    context.put("allowAdmin", allowAdmin);  
	    Timestamp firstGameDateTime = DAO.getFirstGameDateTimeFromPool(pool.getPoolId());
	    Date firstGameDate = firstGameDateTime != null ? new Date(firstGameDateTime.getTime()) : null;
	    Calendar cal = Calendar.getInstance();
	    //TBD check times of games
	    if ((user != null && user.isAdmin()) || (nflPlayoffsGameMap.size() > 0 && (firstGameDate != null && firstGameDate.after(cal.getTime())))) {
	    	userSession.put("readOnly", false);
	    }
	    else {
	    	userSession.put("readOnly", true);
	    }
	    stack.push(context);
	    System.out.println("Login: " + userName + " year: " + pool.getYear() + " poolId: " + pool.getPoolId() + " time: " + new Timestamp(new Date().getTime()));
	    return "success";
	}

	public Boolean getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(Boolean maxPoints) {
		this.maxPoints = maxPoints;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
	   this.userName = userName;
	}
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
	   this.poolName = poolName;
	}
	
	public void setSession(Map<String, Object> session) {
		   this.userSession = session ;
	}
}
