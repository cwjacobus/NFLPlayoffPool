package actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import dao.DAO;
import data.NFLPlayoffsTeam;

public class ManageGamesAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	Map<String, Object> userSession;
	
	public String execute() throws Exception {
		HashMap<String, NFLPlayoffsTeam> nflPlayoffsTeamsMap =  DAO.getNFLPlayoffsTeamsMap();
		userSession.put("nflPlayoffsTeamsMap", nflPlayoffsTeamsMap);
	    return "success";
	}
	
	public void setSession(Map<String, Object> session) {
		   this.userSession = session ;
	}
}
