package actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import dao.DAO;
import data.NFLTeam;

public class ManageGamesAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	Map<String, Object> userSession;
	
	public String execute() throws Exception {
		HashMap<String, NFLTeam> nflTeamsMap =  DAO.getNFLTeamsMap();
		userSession.put("nflTeamsMap", nflTeamsMap);
	    return "success";
	}
	
	public void setSession(Map<String, Object> session) {
		   this.userSession = session ;
	}
}
