package actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import dao.DAO;
import data.NFLPlayoffsGame;
import data.NFLTeam;
import data.Pool;

public class UpdateScoreAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private String winner;
	private String loser;
	private Integer gameIndex;
	Map<String, Object> userSession;
	Pool pool;

	public String execute() throws Exception {
		pool = (Pool)userSession.get("pool");
		@SuppressWarnings("unchecked")
		HashMap<String, NFLTeam> nflTeamsMap = (HashMap<String, NFLTeam>)userSession.get("nflTeamsMap");
		Integer winnerTeamId = nflTeamsMap.get(winner) != null ? nflTeamsMap.get(winner).getNflTeamId() : null;
		Integer loserTeamId = nflTeamsMap.get(loser) != null ? nflTeamsMap.get(loser).getNflTeamId() : null;
		DAO.updateScore(winner, loser, winnerTeamId, loserTeamId, gameIndex);
		Thread.sleep(1000);
		HashMap<Integer, NFLPlayoffsGame> nflPlayoffsGameMap = DAO.getNFLPlayoffsGamesMap(pool.getYear());
		userSession.put("nflPlayoffsGameMap", nflPlayoffsGameMap);
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
	
	public void setSession(Map<String, Object> session) {
		   this.userSession = session ;
	}

}
