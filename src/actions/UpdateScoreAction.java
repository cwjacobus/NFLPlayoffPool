package actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import dao.DAO;
import data.NFLPlayoffsGame;
import data.Pool;

public class UpdateScoreAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	//private String winner;
	//private String loser;
	private Integer visScore;
	private Integer homeScore;
	private Integer gameIndex;
	Map<String, Object> userSession;
	Pool pool;

	public String execute() throws Exception {
		pool = (Pool)userSession.get("pool");
		//DAO.updateScore(winner, loser, gameIndex);
		DAO.updateScore(visScore, homeScore, gameIndex);
		Thread.sleep(1000);
		HashMap<Integer, NFLPlayoffsGame> nflPlayoffsGameMap = DAO.getNFLPlayoffsGamesMap(pool.getYear());
		userSession.put("nflPlayoffsGameMap", nflPlayoffsGameMap);
	    return "success";
	}
	   
	/*public String getWinner() {
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
	}*/

	public Integer getVisScore() {
		return visScore;
	}

	public void setVisScore(Integer visScore) {
		this.visScore = visScore;
	}

	public Integer getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(Integer homeScore) {
		this.homeScore = homeScore;
	}

	public Integer getGameIndex() {
		return gameIndex;
	}

	public void setGameIndex(Integer gameIndex) {
		this.gameIndex = gameIndex;
	}
	
	public void setSession(Map<String, Object> session) {
		   this.userSession = session ;
	}

}
