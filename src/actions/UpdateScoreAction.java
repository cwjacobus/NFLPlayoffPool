package actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import dao.DAO;
import data.NFLPlayoffsGame;
import data.NFLTeam;
import data.Pool;

public class UpdateScoreAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Integer visScore;
	private Integer homeScore;
	private Integer gameIndex;
	Map<String, Object> userSession;
	Pool pool;

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		pool = (Pool)userSession.get("pool");
		HashMap<Integer, NFLPlayoffsGame> nflPlayoffsGameMap = (HashMap<Integer, NFLPlayoffsGame>)userSession.get("nflPlayoffsGameMap");
		HashMap<String, NFLTeam> nflTeamsMap = (HashMap<String, NFLTeam>)userSession.get("nflTeamsMap");
		NFLPlayoffsGame game = nflPlayoffsGameMap.get(gameIndex);
		List<NFLTeam> nflTeamsList = new ArrayList<NFLTeam>(nflTeamsMap.values());
		String homeShortName = null;
		String visitorShortName = null;
		Optional<NFLTeam> homeMatch = 
			nflTeamsList
			.stream()
			.filter((p) -> p.getNflTeamId() == game.getHome())
			.findAny();
		if (homeMatch.isPresent()) {
			homeShortName = homeMatch.get().getShortName();
		}
		Optional<NFLTeam> visMatch = 
			nflTeamsList
			.stream()
			.filter((p) -> p.getNflTeamId() == game.getVisitor())
			.findAny();
		if (visMatch.isPresent()) {
			visitorShortName = visMatch.get().getShortName();
		}
		String winner = null;
		String loser = null;
		if (homeScore > visScore) {
			winner = homeShortName;
			loser = visitorShortName;
		}
		else {
			winner = visitorShortName;
			loser = homeShortName;
		}
		DAO.updateScore(visScore, homeScore, winner, loser, gameIndex);
		Thread.sleep(1000);
	    return "success";
	}

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
