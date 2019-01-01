package actions;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import dao.DAO;
import data.NFLPlayoffsGame;
import data.Pool;
import data.Standings;
import data.User;

//import data.User;

public class GetStandingsAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Boolean maxPoints;
	private Integer year = null;
	private String name;
	Map<String, Object> userSession;
	private Integer poolId = null;
	private Pool pool;
	
	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		
		userSession.put("year", year);
		DAO.setConnection(year);
		if (DAO.useYearClause(year)) {
			pool = DAO.getPool(poolId);
			userSession.put("pool", pool);
		}
		User user  = DAO.getUser(name, year, poolId);
		if (user != null || name.equalsIgnoreCase("admin")) { // Always allow admin to login to import users
			userSession.put("user", user);
		}
		else {
			context.put("errorMsg", "Invalid user!");
			stack.push(context);
			return "error";
		}
		TreeMap<String, Standings> standings = DAO.getStandings(maxPoints, year, poolId);
		List<NFLPlayoffsGame> nflPlayoffsGames = DAO.getNFLPlayoffsGamesList(year);
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
	    if ((user != null && user.isAdmin()) || name.equalsIgnoreCase("admin") || name.contains("CJ")) {
	    	allowAdmin = true;
	    }
	    context.put("allowAdmin", allowAdmin);  
	    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
	    Date date1 = sdf.parse("12-15-20" + (year + 1) + " 3:00"); // Time of first game in 2019
	    Calendar cal = Calendar.getInstance();
	   //TBD check times of games
	    if ((user != null && user.isAdmin()) || (nflPlayoffsGames.size() > 0 && date1.after(cal.getTime()))) {
	    	userSession.put("readOnly", false);
	    }
	    else {
	    	userSession.put("readOnly", true);
	    }
	    stack.push(context);
	    System.out.println("Login: " + name + " year: " + year + " poolId: " + poolId + " time: " + new Timestamp(new Date().getTime()));
	    return "success";
	}

	public Boolean getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(Boolean maxPoints) {
		this.maxPoints = maxPoints;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
	   this.name = name;
	}
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
	   this.year = year;
	}
	
	public Integer getPoolId() {
		return poolId;
	}

	public void setPoolId(Integer poolId) {
	   this.poolId = poolId;
	}
	
	public void setSession(Map<String, Object> session) {
		   this.userSession = session ;
	}
}
