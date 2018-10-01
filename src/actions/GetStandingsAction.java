package actions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import dao.DAO;
import data.Standings;

//import data.User;

public class GetStandingsAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private Boolean maxPoints;
	private String year;
	private String name;
	
	public String execute() throws Exception {
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
        }
		Connection conn = null;
		try {
		    conn = DriverManager.getConnection("jdbc:mysql://localhost/nflplayoffspool" + this.year + "?" +
		    	"user=root&password=PASSWORD");
		}
		catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		TreeMap<String, Standings> standings = DAO.getStandings(conn, maxPoints);
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
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();

	    context.put("maxPoints", maxPoints);
	    context.put("standings", standings);
	    stack.push(context);
	    System.out.println("User: " + this.name + " Year: " + this.year);
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
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
	   this.year = year;
	}
}
