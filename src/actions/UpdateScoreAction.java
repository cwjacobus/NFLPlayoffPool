package actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.NFLPlayoffsGame;


public class UpdateScoreAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private String winner;
	private String loser;
	private Integer gameIndex;
	private Integer year;

	public String execute() throws Exception {
		DAO.updateScore(winner, loser, gameIndex);
		
		List<NFLPlayoffsGame> nflPlayoffsGameList = DAO.getNFLPlayoffsGamesList(year);
		
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();

	    context.put("nflPlayoffsGameList", nflPlayoffsGameList);
	    stack.push(context);
		
	    return "success";
	}
	   
	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}
	
	public String getLoser() {
		return loser;
	}

	public void setLoser(String loser) {
		this.loser = loser;
	}

	public Integer getGameIndex() {
		return gameIndex;
	}

	public void setGameIndex(Integer gameIndex) {
		this.gameIndex = gameIndex;
	}
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
	   this.year = year;
	}

}
