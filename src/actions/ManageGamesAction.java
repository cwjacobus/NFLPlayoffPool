package actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import dao.DAO;
import data.NFLPlayoffsGame;

public class ManageGamesAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String year;

	public String execute() throws Exception {
		
		List<NFLPlayoffsGame> nflPlayoffsGameList = DAO.getNFLPlayoffsGamesList();
		
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();

	    context.put("nflPlayoffsGameList", nflPlayoffsGameList);
	    stack.push(context);
	    return "success";
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
