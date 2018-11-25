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
import data.User;

public class DAO {
	
	public static Connection conn;
	
	public static void createBatchPicks(List<Pick> picksList) {
		try {
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
			int picksCount = 0;
			for (Pick p : picksList) {
				String insertSQL = "INSERT INTO Pick (UserId, GameId, Winner) VALUES (" + 
					p.getUserId() + ", " + p.getGameId() + ", '" + p.getWinner() + "');";
				stmt.addBatch(insertSQL);
				picksCount++;
				// Every 500 lines, insert the records
				if (picksCount % 250 == 0) {
					System.out.println("Insert picks " + (picksCount - 250) + " : " + picksCount);
					stmt.executeBatch();
					conn.commit();
					stmt.close();
					stmt = conn.createStatement();
				}
			}
			// Insert the remaining records
			System.out.println("Insert remaining picks " + (picksCount - (picksCount % 250)) + " : " + picksCount);
			stmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true); // set auto commit back to true for next inserts
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createNFLPlayoffsGame(String description, Integer pointsValue, Integer year) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO NFLPlayoffsGame (Description, PointsValue, Completed, Year) VALUES ('" + 
				description + "', " + pointsValue + ", 0, " + year + ");";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int createUser(String userName, Integer year) {
		int userId = 0;
		try {
			Statement stmt = conn.createStatement();
			boolean admin = userName.equalsIgnoreCase("Jacobus") ? true : false;
			stmt.executeUpdate("INSERT INTO User (UserName, LastName, FirstName, Email, Year, admin) VALUES ('" + 
				userName + "', '', '', '', " + year + "," + admin + ");");
			ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
			if (rs.next()) {
		       userId = rs.getInt(1);
			}
			//System.out.println("ID: " + userId);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return userId;
	}
	
	public static void createPick(Integer userId, Integer gameId, String winner) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO Pick (UserId, GameId, Winner) VALUES (" + 
				userId + ", " + gameId + ", '" + winner + "');";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static List<NFLPlayoffsGame> getNFLPlayoffsGamesList(Integer year) {
		List<NFLPlayoffsGame>nflPlayoffsGameList = new ArrayList<NFLPlayoffsGame>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM NFLPlayoffsGame" + (useYearClause(year) ? " where " + getYearClause(year): ""));
			NFLPlayoffsGame nflPlayoffsGame;
			while (rs.next()) {
				nflPlayoffsGame = new NFLPlayoffsGame(rs.getInt("GameIndex"), rs.getString("Description"), rs.getString("Winner"),
					rs.getString("Loser"), rs.getInt("PointsValue"), rs.getBoolean("Completed"), (useYearClause(year) ? rs.getInt("Year") : 0));
				nflPlayoffsGameList.add(nflPlayoffsGame);
			}
		}
		catch (SQLException e) {
		}
		return nflPlayoffsGameList;
	}
	
	// DB
	public static TreeMap<String, Standings> getStandings(boolean maxPoints, Integer year) {
		TreeMap<String, Standings> standings = new TreeMap<String, Standings>(Collections.reverseOrder());
		HashMap<String, String> ptsStandings = new HashMap<String, String>();
		HashMap<String, String> maxPtsStandings = new HashMap<String, String>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT u.UserName, sum(g.PointsValue) from Pick p, User u, NFLPLayoffsGame g " + 
				"where p.userId = u.userId and g.gameIndex = p.gameId and g.completed = true and p.winner = g.winner " +  
				(useYearClause(year) ? "and " + getYearClause("g", year) : "") +
				" group by u.UserName order by sum(g.PointsValue) desc, u.UserName");
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
			"((g.completed = true and p.winner = g.winner) or (g.completed = false and p.winner not in (select Loser from NFLPLayoffsGame where Loser is not null" + 
			(useYearClause(year) ? " and " + getYearClause(year) : "") + "))) " + (useYearClause(year) ? "and " + getYearClause("g", year) : "") +
			" group by u.UserName order by sum(g.PointsValue) desc, u.UserName");
			
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
	
	public static int getUsersCount(Integer year) {
		int numberOfUsers = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from User" + (useYearClause(year) ? " where " + getYearClause(year): ""));
			rs.next();
			numberOfUsers = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfUsers;
	}
	
	public static int getNFLPlayoffsGamesCount(Integer year) {
		int numberOfNFLPlayoffsGames = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from NFLPlayoffsGame" + (useYearClause(year) ? " where " + getYearClause(year): ""));
			rs.next();
			numberOfNFLPlayoffsGames = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfNFLPlayoffsGames;
	}
	
	public static int getPicksCount(Integer year) {
		int numberOfPicks = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from Pick" + 
				(useYearClause(year) ? " p, NFLPlayoffsGame npg where p.gameId = npg.gameIndex and npg.year = " + year: ""));
			rs.next();
			numberOfPicks = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfPicks;
	}
	
	public static int getFirstGameIndexForAYear(int year) {
		int firstGameIndex = 1;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select min(GameIndex) from NFLPlayoffsGame where year = " + year);
			rs.next();
			firstGameIndex = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		return firstGameIndex;
	}
	
	/*
	public static boolean isThereDataForAYear(int year) {
		int totalDataCount = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select (select count(*) from NFLPlayoffsGame where year = " +
				year + ") + (select count(*) from User where year = " + year + ") as total_rows from dual");
			rs.next();
			totalDataCount = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return totalDataCount == 0;
	}*/
	
	public static int getNumberOfCompletedGames(Integer year) {
		int numberOfCompletedGames = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from NFLPlayoffsGame where Completed = 1" + (useYearClause(year) ? " where " + getYearClause(year): ""));
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
	
	public static List<User> getUsersList(Integer year) {
		List<User>userList = new ArrayList<User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM User" + (useYearClause(year) ? " where " + getYearClause(year): ""));
			User user;
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"), rs.getString("FirstName"), 
					rs.getString("Email"), (useYearClause(year) ? rs.getInt("Year") : 0), (useYearClause(year) ? rs.getBoolean("admin" ): false));
				userList.add(user);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}
}
