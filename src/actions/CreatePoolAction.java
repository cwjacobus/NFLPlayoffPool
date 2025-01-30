package actions;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import init.NFLPlayoffsPoolDatabase;

public class CreatePoolAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private Integer year;
	private String poolName;
	private Integer pointsRd1;
	private Integer pointsRd2;
	private Integer pointsChamp;
	private Integer pointsSB;
	private String copyUsers;
	
	public String execute() throws Exception {	
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
	    
	    NFLPlayoffsPoolDatabase bowlPoolDB = (NFLPlayoffsPoolDatabase)ServletActionContext.getServletContext().getAttribute("Database");  
        Connection con = bowlPoolDB.getCon();
		DAO.setConnection(con);
		
		if (!((year >= 24 && year <= 99) || (year >= 2024 && year <= 2099))) {   // years must be 24-99 or 2024-2099
			context.put("errorMsg", "Invalid year: " + year);
			stack.push(context);
			return "error";
		}
		if (year >= 2024) {  // Adjust 2 digit years
			year = year - 2000;
		}
		System.out.println("Create Pool: " + year + " Copy Users: " + (copyUsers != null ? "true" : "false"));
		if (DAO.createPool(poolName + " 20" + year, year, pointsRd1, pointsRd2, pointsChamp, pointsSB)) {
			if (copyUsers != null && copyUsers.length() > 0) {
				DAO.copyUsersFromPreviousYear(year, poolName); // Populate the pool with previous years users
			}
			return "success";
		}
		else {
			context.put("errorMsg", "Pool already exists for: " + poolName + " " + year);
			stack.push(context);
			return "error";
		}
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

	public Integer getPointsRd1() {
		return pointsRd1;
	}

	public void setPointsRd1(Integer pointsRd1) {
		this.pointsRd1 = pointsRd1;
	}

	public Integer getPointsRd2() {
		return pointsRd2;
	}

	public void setPointsRd2(Integer pointsRd2) {
		this.pointsRd2 = pointsRd2;
	}

	public Integer getPointsChamp() {
		return pointsChamp;
	}

	public void setPointsChamp(Integer pointsChamp) {
		this.pointsChamp = pointsChamp;
	}

	public Integer getPointsSB() {
		return pointsSB;
	}

	public void setPointsSB(Integer pointsSB) {
		this.pointsSB = pointsSB;
	}

	public String getCopyUsers() {
		return copyUsers;
	}

	public void setCopyUsers(String copyUsers) {
		this.copyUsers = copyUsers;
	}

	
}
