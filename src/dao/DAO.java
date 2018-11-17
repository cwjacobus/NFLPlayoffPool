package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import data.NFLPlayoffsGame;
import data.Pick;
import data.Standings;

public class DAO {
	
	public static Connection conn;
	
	public static List<NFLPlayoffsGame> getNFLPlayoffsGamesList() {
		List<NFLPlayoffsGame>nflPlayoffsGameList = new ArrayList<NFLPlayoffsGame>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM NFLPlayoffsGame");
			NFLPlayoffsGame nflPlayoffsGame;
			while (rs.next()) {
				nflPlayoffsGame = new NFLPlayoffsGame(rs.getInt("GameIndex"), rs.getString("Description"), rs.getString("Winner"),
					rs.getString("Loser"), rs.getInt("PointsValue"), rs.getBoolean("Completed"));
				nflPlayoffsGameList.add(nflPlayoffsGame);
			}
		}
		catch (SQLException e) {
		}
		return nflPlayoffsGameList;
	}
	
	// DB
	public static TreeMap<String, Standings> getStandings(boolean maxPoints) {
		TreeMap<String, Standings> standings = new TreeMap<String, Standings>(Collections.reverseOrder());
		HashMap<String, String> ptsStandings = new HashMap<String, String>();
		HashMap<String, String> maxPtsStandings = new HashMap<String, String>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT u.UserName, sum(g.PointsValue) from Pick p, User u, NFLPLayoffsGame g " + 
				"where p.userId = u.userId and g.gameIndex = p.gameId and g.completed = true and p.winner = g.winner " +  
				"group by u.UserName order by sum(g.PointsValue) desc, u.UserName");
			while (rs.next()) {
				String points = Integer.toString(rs.getInt(2));
				if (rs.getInt(2) < 10) {
					points = "0" + points;
				}
				ptsStandings.put(rs.getString(1), points);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		/*List<User> usersList = getUsersList(conn);
		// Merge any users with 0 points
		for (User u : usersList) {
			if (!standings.containsValue(u.getUserName())) {
				standings.put("00:" + u.getUserName()+ ":0", u.getUserName());
			}
		}*/
		
		// Max points
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select u.UserName, sum(g.PointsValue) from Pick p, User u, NFLPLayoffsGame g where " + 
			"p.userId= u.userId and g.gameIndex = p.gameId and " + 
			"((g.completed = true and p.winner = g.winner) or (g.completed = false and p.winner not in (select Loser from NFLPLayoffsGame where Loser is not null))) " + 
			"group by u.UserName order by sum(g.PointsValue) desc, u.UserName");
			
			while (rs.next()) {
				String maxPts = Integer.toString(rs.getInt(2));
				if (rs.getInt(2) < 10) {
					maxPts = "0" + maxPts;
				}
				maxPtsStandings.put(rs.getString(1), maxPts);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		Iterator<Entry<String, String>> it = maxPtsStandings.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> line = (Map.Entry<String, String>)it.next();
			String points = ptsStandings.get(line.getKey());
			if (points == null) {
				points="00";
			}
			Standings stand = new Standings();
			stand.setUserName(line.getKey());
			stand.setPoints(points);
			stand.setMaxPoints(line.getValue());
			//standings.put(points + ":" + line.getKey() + ":" + line.getValue(), line.getKey());
			if (maxPoints) {
				standings.put(line.getValue() + ":" + line.getKey(), stand);
			}
			else {
				standings.put(points + ":" + line.getKey(), stand);
			}
			
		}
				
		return standings;
	}
	
	// Not used
	public static Map<Integer, List<Pick>> getPicksMap() { 
		Map<Integer, List<Pick>> picksMap = new HashMap<Integer, List<Pick>>();
		ArrayList<Pick> picksList = new ArrayList<Pick>();
		Integer prevUserId = null; 
		Integer userId = null;
		Integer gameId = null;
		Integer pickId = null;
		String winner = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from Pick order by UserId, GameId");
			while (rs.next()) {
				userId = rs.getInt("UserId");
				gameId = rs.getInt("GameId");
				pickId = rs.getInt("PickId");
				winner = rs.getString("Winner");
				if (prevUserId != null && userId.intValue()!= prevUserId.intValue()) {
					picksMap.put(prevUserId, picksList);
					picksList = new ArrayList<Pick>();
				}
				Pick p = new Pick(pickId, userId, gameId, winner);
				picksList.add(p);
				prevUserId = userId;
			}
			// add last one
			if (userId != null && gameId != null && pickId != null && winner != null) {
				picksMap.put(userId, picksList);
			}
		}
		catch (SQLException e) {
		}
		return picksMap;
	}
	
	public static int getNumberOfCompletedGames() {
		int numberOfCompletedGames = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from NFLPlayoffsGame where Completed = 1");
			rs.next();
			numberOfCompletedGames = rs.getInt(1);
		}
		catch (SQLException e) {
		}
		return numberOfCompletedGames;
	}
	
	public static void updateScore(String winner, String loser, Integer gameIndex) {
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("UPDATE NFLPlayoffsGame SET Winner = '" + winner + "',Loser = '" + loser + "',Completed = true WHERE GameIndex = " + gameIndex);
		}
		catch (SQLException e) {
		}
		return;
	}
	
	public static boolean useYearClause(Integer year) {
		boolean yearClause = false;
		
		if (year.intValue() >= 17) {
			yearClause = true;
		}
		return yearClause;
	}
	
	private static String getYearClause(Integer year) {
		return "year = " + year;
	}
	
	private static String getYearClause(String prefix, Integer year) {
		return prefix + ".year = " + year;
	}
	
	public static void setConnection(Integer year) {
		try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } 
		catch (Exception ex) {
        }
		try {
			String connString = "jdbc:mysql://localhost/nflplayoffspool";
			if (year.intValue() < 17) { // only append year before 2017
			    connString += year;
			}
			connString += "?user=root&password=PASSWORD&useSSL=false";
			conn = DriverManager.getConnection(connString);
		}
		catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
		
	/*private static List<User> getUsersList(Connection conn) {
		List<User>userList = new ArrayList<User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM User");
			User user;
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"), rs.getString("FirstName"), rs.getString("Email"));
				userList.add(user);
			}
		}
		catch (SQLException e) {
		}
		return userList;
	}*/
}
