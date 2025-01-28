package actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.NFLPlayoffsGame;
import data.NFLTeam;
import data.Pool;

public class CreateNFLPlayoffGamesAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private List<String> afcSeed;
	private List<String> nfcSeed;
	Map<String, Object> userSession;
	Map<Integer, NFLPlayoffsGame> nflPlayoffsGameMap;
	private String createFirstGameDateTime;

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
	    nflPlayoffsGameMap = (Map<Integer, NFLPlayoffsGame>)userSession.get("nflPlayoffsGameMap");
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		for (String seededTeam : afcSeed) {
			if (seededTeam == null || seededTeam.length() == 0) {
				context.put("errorMsg", "Not all AFC playoff teams have been entered!");
				stack.push(context);
				return "error";
			}
		}
		for (String seededTeam : nfcSeed) {
			if (seededTeam == null || seededTeam.length() == 0) {
				context.put("errorMsg", "Not all NFC playoff teams have been entered!");
				stack.push(context);
				return "error";
			}
		}
		if (createFirstGameDateTime == null || createFirstGameDateTime.length() == 0) {
			context.put("errorMsg", "Must provide first game start date/time!");
			stack.push(context);
			return "error";
		}
		Pool pool = (Pool)userSession.get("pool");
		if (nflPlayoffsGameMap != null && nflPlayoffsGameMap.size() != 0) {
			DAO.deleteNFLPlayoffsGamesByYear(pool.getYear());
		}
		HashMap<Integer, NFLTeam> nflTeamsMapById = (HashMap<Integer, NFLTeam>)userSession.get("nflTeamsMapById");
		
		// the Pool point values are only used here to populate NFLPlayoffsGame.PointsValue
		DAO.createNFLPlayoffsGame("AFC Rd 1 Game 1", pool.getPointsRd1(), pool.getYear(), getNFLTeamIdFromShortName(nflTeamsMapById, afcSeed.get(1)), 
			getNFLTeamIdFromShortName(nflTeamsMapById, afcSeed.get(6)), "AFC", null, null, 2, 7, 1, null);
		DAO.createNFLPlayoffsGame("AFC Rd 1 Game 2", pool.getPointsRd1(), pool.getYear(), getNFLTeamIdFromShortName(nflTeamsMapById, afcSeed.get(2)), 
			getNFLTeamIdFromShortName(nflTeamsMapById, afcSeed.get(5)), "AFC", null, null, 3, 6, 1, null);
		DAO.createNFLPlayoffsGame("AFC Rd 1 Game 3", pool.getPointsRd1(), pool.getYear(), getNFLTeamIdFromShortName(nflTeamsMapById, afcSeed.get(3)), 
			getNFLTeamIdFromShortName(nflTeamsMapById, afcSeed.get(4)), "AFC", null, null, 4, 5, 1, null);
		DAO.createNFLPlayoffsGame("NFC Rd 1 Game 1", pool.getPointsRd1(), pool.getYear(), getNFLTeamIdFromShortName(nflTeamsMapById, nfcSeed.get(1)), 
			getNFLTeamIdFromShortName(nflTeamsMapById, nfcSeed.get(6)), "NFC", null, null, 2, 7, 1, null);
		DAO.createNFLPlayoffsGame("NFC Rd 1 Game 2", pool.getPointsRd1(), pool.getYear(), getNFLTeamIdFromShortName(nflTeamsMapById, nfcSeed.get(2)), 
			getNFLTeamIdFromShortName(nflTeamsMapById, nfcSeed.get(5)), "NFC", null, null, 3, 6, 1, null);
		DAO.createNFLPlayoffsGame("NFC Rd 1 Game 3", pool.getPointsRd1(), pool.getYear(), getNFLTeamIdFromShortName(nflTeamsMapById, nfcSeed.get(3)), 
			getNFLTeamIdFromShortName(nflTeamsMapById, nfcSeed.get(4)), "NFC", null, null, 4, 5, 1, null);
		
		DAO.createNFLPlayoffsGame("AFC Rd 2 Game 1", pool.getPointsRd2(), pool.getYear(), getNFLTeamIdFromShortName(nflTeamsMapById, afcSeed.get(0)), 
			null, "AFC", null, null, 1, null, 2, null);
		DAO.createNFLPlayoffsGame("AFC Rd 2 Game 2", pool.getPointsRd2(), pool.getYear(), null, null, "AFC", null, null, null, null, 2, null);
		DAO.createNFLPlayoffsGame("NFC Rd 2 Game 1", pool.getPointsRd2(), pool.getYear(), getNFLTeamIdFromShortName(nflTeamsMapById, nfcSeed.get(0)), 
			null, "NFC", null, null, 1, null, 2, null);
		DAO.createNFLPlayoffsGame("NFC Rd 2 Game 2", pool.getPointsRd2(), pool.getYear(), null, null, "NFC", null, null, null, null, 2, null);
		
		DAO.createNFLPlayoffsGame("AFC Champ", pool.getPointsChamp(), pool.getYear(), null, null, "AFC", null, null, null, null, 3, null);
		DAO.createNFLPlayoffsGame("NFC Champ", pool.getPointsChamp(), pool.getYear(), null, null, "NFC", null, null, null, null, 3, null);
		
		DAO.createNFLPlayoffsGame("Super Bowl", pool.getPointsSB(), pool.getYear(), null, null, null, null, null, null, null, 4, null);
		
		DAO.updatePoolFirstGameDateTime(createFirstGameDateTime, pool.getPoolId());
		
		context.put("successMsg", "NFL Playoff Games successfully created for: 20" + pool.getYear());
		stack.push(context);
	    return "success";
	}
	
	private Integer getNFLTeamIdFromShortName(HashMap<Integer, NFLTeam> nflTeamsMapById, String nflTeamShortName) {
		Optional<Integer> nflTeamId = nflTeamsMapById
				.entrySet()
				.stream()
				.filter(entry -> Objects.equals(entry.getValue().getShortName(), nflTeamShortName))
				.map(Map.Entry::getKey)
				.findFirst();
		
		return nflTeamId.get();
	}


	public List<String> getAfcSeed() {
		return afcSeed;
	}


	public void setAfcSeed(List<String> afcSeed) {
		this.afcSeed = afcSeed;
	}


	public List<String> getNfcSeed() {
		return nfcSeed;
	}


	public void setNfcSeed(List<String> nfcSeed) {
		this.nfcSeed = nfcSeed;
	}


	public String getCreateFirstGameDateTime() {
		return createFirstGameDateTime;
	}


	public void setCreateFirstGameDateTime(String createFirstGameDateTime) {
		this.createFirstGameDateTime = createFirstGameDateTime;
	}


	public Map<String, Object> getUserSession() {
		return userSession;
	}


	public void setUserSession(Map<String, Object> userSession) {
		this.userSession = userSession;
	}


	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }

}