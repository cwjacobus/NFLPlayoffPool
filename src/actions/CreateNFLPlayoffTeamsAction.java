package actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import data.Pool;

public class CreateNFLPlayoffTeamsAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private String createFirstGameDateTime;
	
	Map<String, Object> userSession;
	Pool pool;
	
	public String execute() throws Exception {	
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		pool = (Pool) userSession.get("pool");
		System.out.println("Create NFL Playoff Teams: " + pool.getYear());
		context.put("createFirstGameDateTime", createFirstGameDateTime);
	    stack.push(context);
	    return "success";
	}
	
	public String getCreateFirstGameDateTime() {
		return createFirstGameDateTime;
	}

	public void setCreateFirstGameDateTime(String createFirstGameDateTime) {
		this.createFirstGameDateTime = createFirstGameDateTime;
	}

	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }
	
	
}
