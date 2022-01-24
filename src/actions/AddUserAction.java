package actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.Pool;


public class AddUserAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private String userName;
	private String firstName;
	private String lastName;
	
	Map<String, Object> userSession;
	Pool pool;
	
	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
	    pool = (Pool) userSession.get("pool");
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		if (userName == null || userName.trim().length() == 0 ) {
	    	context.put("errorMsg", "Username is required!");
	    	stack.push(context);
	    	return "error";
	    }
		int userId = DAO.createUser(userName, firstName, lastName, pool.getYear(), pool.getPoolId());
		if (userId == 0) {
			context.put("errorMsg", "Username is already added!");
	    	stack.push(context);
	    	return "error";
		}
	    return "success";
	}
	
	public void setSession(Map<String, Object> session) {
		   this.userSession = session ;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
