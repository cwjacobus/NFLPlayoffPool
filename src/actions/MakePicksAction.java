package actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.Pick;
import data.Pool;
import data.User;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;


public class MakePicksAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 1L;
	Map<String, Object> userSession;
	User user;
	Pool pool;

	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		user = (User) userSession.get("user");
		pool = (Pool) userSession.get("pool");
		Map<Integer, List<Pick>> picksMap = DAO.getPicksMap(pool);
	    HashMap<Integer, User> usersMap = DAO.getUsersMap(pool.getPoolId());
	    List<String> eliminatedTeams = DAO.getEliminatedTeams(pool.getYear());
	    List<String> round2WinningTeams = DAO.getRound2WinningTeams(pool.getYear());
	    context.put("picksMap", picksMap);
	    context.put("usersMap", usersMap);
	    context.put("eliminatedTeams", eliminatedTeams);
	    context.put("round2WinningTeams", round2WinningTeams);
	    stack.push(context);
	    return "success";
	}
	
	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }
}
