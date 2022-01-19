package actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;


public class ManageGamesAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	Map<String, Object> userSession;
	
	public String execute() throws Exception {
	    return "success";
	}
	
	public void setSession(Map<String, Object> session) {
		   this.userSession = session ;
	}
}
