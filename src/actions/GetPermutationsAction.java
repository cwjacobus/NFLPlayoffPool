package actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.NFLPlayoffsGame;
import data.NFLTeam;
import data.Pick;
import data.Pool;
import data.Standings;
import data.User;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;


public class GetPermutationsAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 1L;
	Map<String, Object> userSession;
	Pool pool;

	public String execute() throws Exception {
		// Must have picks in order of AFCChamp NFCChamp SBChamp
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		pool = (Pool) userSession.get("pool");
		@SuppressWarnings("unchecked")
		TreeMap<Integer, NFLPlayoffsGame> nflPlayoffsGameMap = (TreeMap<Integer, NFLPlayoffsGame>) userSession.get("nflPlayoffsGameMap");
		List<NFLPlayoffsGame> nflPlayoffsGameList = nflPlayoffsGameMap.values().stream().collect(Collectors.toList());
		int superBowlIndex = nflPlayoffsGameList.size() - 1;
		int superBowlPoints = nflPlayoffsGameList.get(superBowlIndex).getPointsValue();
		int champIndex = nflPlayoffsGameList.size() - 2;
		int champPoints = nflPlayoffsGameList.get(champIndex).getPointsValue();
		List<NFLTeam> finalFour = DAO.getFinalFour(pool.getYear()); // Get the last 4 remaining teams before champ games
		HashMap<Integer, User> usersMap = DAO.getUsersMap(pool.getPoolId());
		Map<Integer, List<Pick>> picksMap = DAO.getPicksMap(pool);
		TreeMap<String, Standings> standings = DAO.getStandings(false, pool.getYear(), pool.getPoolId());
		List<List<String>> possibleOutcomes = getPossibleOutcomes(finalFour);
		Map<String, Integer> possibleStandings;
		// Go through the 8 permutations and get the adjusted standings based on user's picks
		for (List<String> outcome : possibleOutcomes) {
			possibleStandings = new HashMap<>();
			for (Map.Entry<String, Standings> entry : standings.entrySet()) {
				int points = Integer.valueOf(entry.getValue().getPoints());
				String userName = entry.getValue().getUserName();
				List<Pick> usersPicks = new ArrayList<>();
				for (Map.Entry<Integer, List<Pick>> picks : picksMap.entrySet()) {
					if (usersMap.get(picks.getValue().get(0).getUserId()).getUserName().equals(entry.getValue().getUserName())) {
						usersPicks = picks.getValue();  // Get the user's picks
					}
				}
				// Add the points to the standings total if game was picked correctly
				if (usersPicks.get(champIndex - 1).getWinner().equals(outcome.get(outcome.size() - 3))) {
					points += champPoints;
				}
				if (usersPicks.get(champIndex).getWinner().equals(outcome.get(outcome.size() - 2))) {
					points += champPoints;
				}
				if (usersPicks.get(superBowlIndex).getWinner().equals(outcome.get(outcome.size() - 1))) {
					points += superBowlPoints;
				}
				possibleStandings.put(userName, points);
			}
			System.out.println(outcome);
			possibleStandings.entrySet()
			  .stream()
			  .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			  .forEach(System.out::println);
		}
	    return "success";
	}
	
	private List<List<String>> getPossibleOutcomes(List<NFLTeam> finalFour) {
		List<List<String>> possibleOutcomes = new ArrayList<>();
		
		for (NFLTeam a : finalFour) {
			List<String> outcome = new ArrayList<>();
			if (!a.getConference().equals("AFC")) {
				continue;
			}
			for (NFLTeam n : finalFour) {
				if (!n.getConference().equals("NFC")) {
					continue;
				}
				outcome = new ArrayList<>();
				outcome.add(a.getShortName());
				outcome.add(n.getShortName());
				outcome.add(a.getShortName());
				possibleOutcomes.add(outcome);
				outcome = new ArrayList<>();
				outcome.add(a.getShortName());
				outcome.add(n.getShortName());
				outcome.add(n.getShortName());
				possibleOutcomes.add(outcome);
				System.out.println(a.getShortName() + " " + n.getShortName() + " " + a.getShortName());
				System.out.println(a.getShortName() + " " + n.getShortName() + " " + n.getShortName());
			}
		}
		return possibleOutcomes;
	}
	
	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }
}
