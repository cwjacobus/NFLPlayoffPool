package migration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.NFLPlayoffsGame;
import data.Pick;
import data.User;

public class MigrateDB {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args == null || args.length != 2) {
			System.out.println("Invalid number of args.  Must be 2 ex: 14 nflplayoffsgame");
			return;
		}
		int fromYear = Integer.parseInt(args[0]);
		String table = args[1];
		
		Connection fromConn = null;
		Connection toConn = null;
		
		fromConn = setConnection(fromYear);
		Map<Integer, NFLPlayoffsGame> fromNFLPlayoffsGameMap = getNFLPlayoffsGamesMap(fromYear, fromConn, false);
		Map<Integer, User> fromUserMap = getUsersMap(fromYear, null, fromConn, false);
		toConn = setConnection(null);
		
		if (table.equalsIgnoreCase("nflplayoffsgame")) {
			if (getNFLPlayoffsGamesCount(fromYear, toConn) > 0 ) {
				System.out.println("20" + fromYear + " NFL Playoffs Games already migrated!");
				return;
			}
			for (Integer k : fromNFLPlayoffsGameMap.keySet()) {
				NFLPlayoffsGame npg = fromNFLPlayoffsGameMap.get(k); 
				createNFLPlayoffsGame(npg.getDescription(), npg.getWinner(), npg.getLoser(), npg.getPointsValue(), 
					fromYear, npg.isCompleted(), toConn);
			}
			System.out.println("20" + fromYear + " " + fromNFLPlayoffsGameMap.size() + " NFL Playoffs Games migrated");	
		}
		else if (table.equalsIgnoreCase("user")) {
			int poolId = getPoolIdFromYear(fromYear, toConn);
			if (getUsersCount(fromYear, poolId, toConn) > 0 ) {
				System.out.println("20" + fromYear + " Users already migrated!");
				return;
			}
			for (Integer k : fromUserMap.keySet()) {
				User u = fromUserMap.get(k); 
				createUser(u.getUserName(), fromYear, poolId, u.isAdmin(), toConn);
			}
			System.out.println("20" + fromYear + " " + fromUserMap.size() + " users migrated");
		}
		else if (table.equalsIgnoreCase("pick")) {
			int poolId = getPoolIdFromYear(fromYear, toConn);
			if (getPicksCount(fromYear, poolId, toConn) > 0) {
				System.out.println("20" + fromYear + " Picks already migrated!");
				return;
			}
			Map<Integer, NFLPlayoffsGame> toNFLPlayoffsGameMap = getNFLPlayoffsGamesMap(fromYear, toConn, true);
			Map<Integer, User> toUserMap = getUsersMap(fromYear, poolId, toConn, true);
			if (toNFLPlayoffsGameMap.size() == 0 || toUserMap.size() == 0) {
				System.out.println("20" + fromYear + " users or nflplayoffsgames do not exist");
				return;
			}
			Map<Integer, List<Pick>> fromPicksMap =  getPicksMap(fromConn);
			System.out.println(fromPicksMap.size() + " users in " + fromYear);
			List<Pick> migratedPicksList = new ArrayList<Pick>();
			for (Integer userId : fromPicksMap.keySet()) {
				List<Pick> userPicks = fromPicksMap.get(userId);
				for (Pick p : userPicks) {
					Integer newGameIndex = getGameIndexFromOldPick(fromNFLPlayoffsGameMap.get(p.getGameId()), toNFLPlayoffsGameMap);
					System.out.println(newGameIndex + " from: " + p.getGameId() + " " + fromNFLPlayoffsGameMap.get(p.getGameId()).getWinner() + " " + fromNFLPlayoffsGameMap.get(p.getGameId()).getLoser());
					Integer newUserId = getUserIdFromOldPick(fromUserMap.get(p.getUserId()), toUserMap);
					System.out.println(newUserId + " from: " + p.getUserId() + " " + fromUserMap.get(p.getUserId()).getUserName());
					Pick newPick = new Pick();
					newPick.setGameId(newGameIndex);
					newPick.setUserId(newUserId);
					newPick.setPoolId(poolId);
					newPick.setWinner(p.getWinner());
					migratedPicksList.add(newPick);
				}
			}
			createBatchPicks(migratedPicksList, poolId, toConn);
			System.out.println(migratedPicksList.size() + " migratedPicks");
		}
		else {
			System.out.println("Invalid args: " + args[1]);
		}
	}
	
	private static Integer getGameIndexFromOldPick(NFLPlayoffsGame oldNFLPlayoffsGame, Map<Integer, NFLPlayoffsGame> toNFLPlayoffsGameMap) {
		for (Integer gameId : toNFLPlayoffsGameMap.keySet()) {
			NFLPlayoffsGame npg = toNFLPlayoffsGameMap.get(gameId);
			
			if (oldNFLPlayoffsGame.getWinner().equals(npg.getWinner()) && oldNFLPlayoffsGame.getLoser().equals(npg.getLoser())) {
				return npg.getGameIndex();
			}
		}
		return null;
	}
	
	private static Integer getUserIdFromOldPick(User oldUser, Map<Integer, User> toUserGameMap) {
		for (Integer userId : toUserGameMap.keySet()) {
			User u = toUserGameMap.get(userId);
			
			if (oldUser.getUserName().equals(u.getUserName())) {
				return u.getUserId();
			}
		}
		return null;
	}
	
	//DB
	private static Connection setConnection(Integer year) {
		Connection conn = null;
		try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } 
		catch (Exception ex) {
        }
		try {
			String connString = "jdbc:mysql://localhost/nflplayoffspool";
			if (year != null && year.intValue() < 17) { // only append year before 2017
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
		return conn;
	}
	
	public static void createBatchPicks(List<Pick> picksList, Integer poolId, Connection conn) {
		try {
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
			int picksCount = 0;
			for (Pick p : picksList) {
				String insertSQL = "INSERT INTO Pick (UserId, GameId, Winner, PoolId, CreatedTime) VALUES (" + 
					p.getUserId() + ", " + p.getGameId() + ", '" + p.getWinner() + "', " + poolId + ", NOW());";
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
	
	public static void createNFLPlayoffsGame(String description, String winner, String loser, Integer pointsValue, 
		Integer year, boolean completed, Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO NFLPlayoffsGame (Description, Winner, Loser, PointsValue, Completed, Year) VALUES ('" + 
				description + "', '" + winner + "', '" + loser + "', " + pointsValue + ", 0, " + year + ");";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int createUser(String userName, Integer year, Integer poolId, boolean admin, Connection conn) {
		int userId = 0;
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO User (UserName, LastName, FirstName, Email, Year, admin, PoolId) VALUES ('" + 
				userName + "', '', '', '', " + year + "," + admin + ", " + poolId + ");");
			ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
			if (rs.next()) {
		       userId = rs.getInt(1);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return userId;
	}
	
	public static int getNFLPlayoffsGamesCount(Integer year, Connection conn) {
		int numberOfNFLPlayoffsGames = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from NFLPlayoffsGame where year = " + year);
			rs.next();
			numberOfNFLPlayoffsGames = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfNFLPlayoffsGames;
	}
	
	public static int getPicksCount(Integer year, Integer poolId, Connection conn) {
		int numberOfPicks = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from Pick p, NFLPlayoffsGame npg where p.gameId = npg.gameIndex and npg.year = " + 
			year + " and p.poolId = " + poolId);
			rs.next();
			numberOfPicks = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfPicks;
	}
	
	private static int getUsersCount(Integer year, Integer poolId, Connection conn) {
		int numberOfUsers = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from User where year = " + year + " and poolId = " + poolId);
			rs.next();
			numberOfUsers = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfUsers;
	}
	
	private static int getPoolIdFromYear(Integer year, Connection conn) {
		// Assumes one pool for the year
		int poolId = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select poolId from Pool where year = " + year);
			rs.next();
			poolId = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return poolId;
	}
	
	public static Map<Integer, NFLPlayoffsGame> getNFLPlayoffsGamesMap(Integer year, Connection conn, boolean useYearClause) {
		Map<Integer, NFLPlayoffsGame>nflPlayoffsGameMap = new HashMap<Integer, NFLPlayoffsGame>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM NFLPlayoffsGame" + (useYearClause ? " where " + getYearClause(year, null): ""));
			NFLPlayoffsGame nflPlayoffsGame;
			while (rs.next()) {
				nflPlayoffsGame = new NFLPlayoffsGame(rs.getInt("GameIndex"), rs.getString("Description"), rs.getString("Winner"),
					rs.getString("Loser"), rs.getInt("PointsValue"), rs.getBoolean("Completed"), (useYearClause(year) ? rs.getInt("Year") : 0), 
					rs.getInt("Home"), rs.getInt("Visitor"), rs.getInt("WinnerTeamId"), rs.getInt("LoserTeamId"));
				nflPlayoffsGameMap.put(nflPlayoffsGame.getGameIndex(), nflPlayoffsGame);
			}
		}
		catch (SQLException e) {
		}
		return nflPlayoffsGameMap;
	}
	
	private static Map<Integer, User> getUsersMap(Integer year, Integer poolId, Connection conn, boolean useYearClause) {
		Map<Integer, User> userMap = new HashMap<Integer, User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM User" + (useYearClause ? " where " + getYearClause(year, poolId) : ""));
			User user;
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"), rs.getString("FirstName"), 
					rs.getString("Email"), year, false, rs.getInt("PoolId"));
				userMap.put(user.getUserId(), user);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return userMap;
	}
	
	public static Map<Integer, List<Pick>> getPicksMap(Connection conn) {
		Map<Integer, List<Pick>> picksMap = new HashMap<Integer, List<Pick>>();
		ArrayList<Pick> picksList = new ArrayList<Pick>();
		Integer prevUserId = null; 
		Integer userId = null;
		Integer gameId = null;
		Integer pickId = null;
		String winner = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select p.* from Pick p, NFLPlayoffsGame npg where p.GameId = npg.GameIndex order by p.UserId, npg.GameIndex");
			while (rs.next()) {
				userId = rs.getInt("UserId");
				gameId = rs.getInt("GameId");
				pickId = rs.getInt("PickId");
				winner = rs.getString("Winner");
				if (prevUserId != null && userId.intValue()!= prevUserId.intValue()) {
					picksMap.put(prevUserId, picksList);
					picksList = new ArrayList<Pick>();
				}
				Pick p = new Pick(pickId, userId, gameId, winner, 0, null);
				picksList.add(p);
				prevUserId = userId;
			}
			// add last one
			if (userId != null && gameId != null && pickId != null && winner != null) {
				picksMap.put(userId, picksList);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return picksMap;
	}
	
	public static boolean useYearClause(Integer year) {
		boolean yearClause = false;
		
		if (year.intValue() >= 17) {
			yearClause = true;
		}
		return yearClause;
	}
	
	private static String getYearClause(Integer year, Integer poolId) {
		String yearClause = "year = " + year;
		if (poolId != null) {
			yearClause += " and PoolId = " + poolId;
		}
		return yearClause;
	}

}
