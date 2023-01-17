package actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.NFLPlayoffsGame;
import data.NFLTeam;
import data.Pool;

public class UpdateScoreAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Integer visScore;
	private Integer homeScore;
	private String visitor;
	private String home;
	private Integer gameIndex;
	Map<String, Object> userSession;
	Pool pool;

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		pool = (Pool)userSession.get("pool");
		if (home == null || home.trim().length() == 0 || visitor == null || visitor.trim().length() == 0) {
			context.put("errorMsg", "Visitor and Home teams are required!");
			stack.push(context);
			return "error";
		}
		if (visScore == null || homeScore == null) {
			context.put("errorMsg", "Scores are required!");
			stack.push(context);
			return "error";
		}
		HashMap<Integer, NFLTeam> nflTeamsMapById = (HashMap<Integer, NFLTeam>)userSession.get("nflTeamsMapById");
		Integer visitorId = getNFLTeamIdFromShortName(visitor, nflTeamsMapById);
		Integer homeId = getNFLTeamIdFromShortName(home, nflTeamsMapById);
		if (visitorId == null || homeId == null) {
			context.put("errorMsg", "Unknown Visitor and Home teams!");
			stack.push(context);
			return "error";
		}
		TreeMap<Integer, NFLPlayoffsGame> nflPlayoffsGameMap = (TreeMap<Integer, NFLPlayoffsGame>)userSession.get("nflPlayoffsGameMap");
		NFLPlayoffsGame game = nflPlayoffsGameMap.get(gameIndex);
		if ((game.getVisitor() != visitorId.intValue() && game.getVisitor() != 0) || (game.getHome() != homeId.intValue() && game.getHome() != 0)) {
			context.put("errorMsg", "Invalid Visitor or Home team for this game!");
			stack.push(context);
			return "error";
		}
		
		String winner = null;
		String loser = null;
		if (homeScore > visScore) {
			winner = home;
			loser = visitor;
		}
		else {
			winner = visitor;
			loser = home;
		}
		DAO.updateNFLPlayoffsGame(visScore, homeScore, winner, loser, visitorId, homeId, gameIndex);
		Thread.sleep(1000);
		nflPlayoffsGameMap = DAO.getNFLPlayoffsGamesMap(pool.getYear());
		userSession.put("nflPlayoffsGameMap", nflPlayoffsGameMap);
	    return "success";
	}
	
	public Integer getNFLTeamIdFromShortName(String shortName, HashMap<Integer, NFLTeam> nflTeamsMap) {
		// TBD Commonize
		Integer teamId = null;
		List<NFLTeam> nflTeamsList = new ArrayList<NFLTeam>(nflTeamsMap.values());
		Optional<NFLTeam> homeMatch = 
			nflTeamsList
			.stream()
			.filter((p) -> p.getShortName().equals(shortName))
			.findAny();
		if (homeMatch.isPresent()) {
			teamId = homeMatch.get().getNflTeamId();
		}	
		return teamId;
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
	
	public String getVisitor() {
		return visitor;
	}

	public void setVisitor(String visitor) {
		this.visitor = visitor;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public void setSession(Map<String, Object> session) {
		   this.userSession = session ;
	}

}
