package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import data.NFLPlayoffsGame;
import data.NFLTeam;
import data.Pick;
import data.Pool;
import data.Standings;
import data.User;

public class DAO {
	
	public static Connection conn;
	
	public static int copyUsersFromAnotherPool(Integer year, Integer toPoolId, Integer fromPoolId) {
		int numberOfRows = 0;
		try {
			Statement stmt = conn.createStatement();
			String adminSQL = "CASE WHEN User.LastName like '%Jacobus%' AND User.FirstName like '%Chris%' THEN true ELSE false END";
			String insertSQL = "INSERT INTO User (UserName, LastName, FirstName, Year, Admin, PoolId) " + 
				"SELECT User.UserName, User.LastName, User.FirstName, " + year + ", " + adminSQL + ", " + toPoolId + " FROM User WHERE User.poolId = " + fromPoolId;
			stmt.executeUpdate(insertSQL);
			ResultSet rs = stmt.executeQuery("SELECT ROW_COUNT()");
			if (rs.next()) {
				numberOfRows = rs.getInt(1);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfRows;
	}
	
	public static void copyUsersFromPreviousYear(Integer year, String fromPoolNameBase) {
		// Assumes pool name is Jacobus/Sculley 20XX
		try {
			Statement stmt = conn.createStatement();
			String toPoolIdSQL = "(SELECT poolId FROM Pool where year=" + year + " AND poolName = '" + (fromPoolNameBase + " 20" + year) + "')";
			String fromPoolIdSQL = "(SELECT poolId FROM Pool where year=" + (year-1) + " AND poolName = '" + (fromPoolNameBase + " 20" + (year-1)) + "')";
			String insertSQL = "INSERT INTO User (UserName, LastName, FirstName, Year, Admin, PoolId) " + 
				"SELECT User.UserName, User.LastName, User.FirstName, " + year + ", User.admin," + toPoolIdSQL + 
				" FROM User WHERE User.poolId = " + fromPoolIdSQL;
			stmt.executeUpdate(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}
	
	public static void createBatchPicks(List<Pick> picksList, Integer poolId) {
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
	
	public static void createNFLPlayoffsGame(String description, Integer pointsValue, Integer year, Integer home, Integer visitor, String conference,
			Integer homeScore, Integer visScore, Integer homeSeed, Integer visSeed, Integer round, Timestamp dateTime) {
		try {
			Statement stmt = conn.createStatement();
			String conferenceString = conference != null ? "'" + conference + "'" : null;
			String insertSQL = "INSERT INTO NFLPlayoffsGame (Description, PointsValue, Completed, Year, Home, Visitor, Conference, HomeSeed, " +
				" VisSeed, Round, DateTime) VALUES ('" + description + "', " + pointsValue + ", 0, " + year + "," + home + "," + visitor + ", " + conferenceString + 
				", " + homeSeed + ", " + visSeed + "," + round + "," + (dateTime != null ? "'" + dateTime + "'" : null) + ");";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
		
	public static void createNFLTeam(String teamId, String fullName, String shortName) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO NFLTeam VALUES (" + teamId + ", '" + fullName + "', '" + shortName + "');";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createPick(Integer userId, Integer gameId, String winner, Integer poolId) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO Pick (UserId, GameId, Winner, PoolId, CreatedTime) VALUES (" + 
				userId + ", " + gameId + ", '" + winner + "', " + poolId + ", NOW());";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean createPool(String poolName, Integer year, Integer pointsRd1, Integer pointsRd2, Integer pointsChamp, Integer pointsSB) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO Pool (PoolName ,Year, PointsRd1, PointsRd2, PointsChamp, PointsSB) VALUES ('" + 
				poolName + "', " + year + ", " + pointsRd1 + ", " + pointsRd2 + ", " + pointsChamp + ", " + pointsSB + ");";
			stmt.execute(insertSQL);
		}
		catch (SQLIntegrityConstraintViolationException ie) {
			return false;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static int createUser(String userName, String firstName, String lastName, Integer year, Integer poolId) {
		int userId = 0;
		try {
			Statement stmt = conn.createStatement();
			boolean admin = userName.contains("Jacobus") ? true : false;
			String insertSQL = "INSERT INTO User (UserName, LastName, FirstName, Email, Year, admin, PoolId) VALUES ('" + userName + "', " +
				(firstName != null && firstName.trim().length() > 0 ? "'" + firstName + "', " : "null,") + (lastName != null && lastName.trim().length() > 0 ? "'" + lastName + "', " : "null,") + 
					" null, " + year + "," + admin + ", " + poolId + ");";
			stmt.executeUpdate(insertSQL);
			ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
			if (rs.next()) {
		       userId = rs.getInt(1);
			}
			//System.out.println("ID: " + userId);
		}
		catch (SQLIntegrityConstraintViolationException sie) {
			System.out.println("Duplicate user: " + userName);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return userId;
	}
	
	public static void deleteNFLPlayoffsGamesByYear(Integer year) {
		try {
			Statement stmt = conn.createStatement();
			String deleteSQL = "DELETE from NFLPlayoffsGame WHERE year = " + year;
			stmt.execute(deleteSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deletePicksByUserIdAndPoolId(Integer userId, Integer poolId) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "DELETE from Pick WHERE userId = " + userId + " and poolId = " + poolId;
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getEliminatedTeams(Integer year) {
		List<String> eliminatedTeams = new ArrayList<String>();
		//select case when homescore > visscore then visitor when visscore > homescore then home end from nflplayoffsgame where completed = 1 and year = 21
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT loser FROM NFLPlayoffsGame where " + getYearClause(year, null));
			while (rs.next()) {
				eliminatedTeams.add(rs.getString(1));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return eliminatedTeams;
	}
	
	public static TreeMap<Integer, NFLPlayoffsGame> getNFLPlayoffsGamesMap(Integer year) {
		TreeMap<Integer, NFLPlayoffsGame> nflPlayoffsGameMap = new TreeMap<Integer, NFLPlayoffsGame>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM NFLPlayoffsGame where " + getYearClause(year, null) + " order by gameIndex");
			NFLPlayoffsGame nflPlayoffsGame;
			while (rs.next()) {
				nflPlayoffsGame = new NFLPlayoffsGame(rs.getInt("GameIndex"), rs.getString("Description"), rs.getString("Winner"),
					rs.getString("Loser"), rs.getInt("PointsValue"), rs.getBoolean("Completed"), rs.getInt("Year"), rs.getInt("Home"), rs.getInt("Visitor"),
					rs.getString("Conference"), rs.getInt("HomeScore"), rs.getInt("VisScore"), rs.getInt("HomeSeed"), rs.getInt("VisSeed"), 
					rs.getTimestamp("DateTime"), rs.getInt("Round"));
				nflPlayoffsGameMap.put(nflPlayoffsGame.getGameIndex(), nflPlayoffsGame);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return nflPlayoffsGameMap;
	}
	
	public static HashMap<Integer, String> getNFLPlayoffsTeamsMapByConference(int year, HashMap<Integer, NFLTeam> nflTeamsMap, String conference) {
		HashMap<Integer, String> nflPlayoffsTeamsMap = new HashMap<>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT distinct Home, HomeSeed, Visitor, VisSeed from NFLPlayoffsGame where year=" + year + 
				" and Conference= '" + conference + "'");
			while (rs.next()) {
				Integer home = rs.getInt(1);
				Integer homeSeed = rs.getInt(2);
				Integer visitor = rs.getInt(3);
				Integer visSeed = rs.getInt(4);
				if ((home != null && home != 0) && (homeSeed != null && homeSeed != 0) && nflTeamsMap.get(homeSeed) != null) {
					nflPlayoffsTeamsMap.put(homeSeed, nflTeamsMap.get(home).getShortName());
				}
				if ((visitor != null && visitor != 0) && (visSeed != null && visSeed != 0) && nflTeamsMap.get(homeSeed) != null) {
					nflPlayoffsTeamsMap.put(visSeed, nflTeamsMap.get(visitor).getShortName());
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return nflPlayoffsTeamsMap;
	}
	
	public static HashMap<Integer, NFLTeam> getNFLTeamsMapById() {
		HashMap<Integer, NFLTeam> nflTeamsMap = new HashMap<Integer, NFLTeam>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM NFLTeam order by ShortName");
			NFLTeam nflTeam;
			while (rs.next()) {
				nflTeam = new NFLTeam(rs.getInt("NFLTeamId"), rs.getString("LongName"), rs.getString("ShortName"), rs.getString("Conference"));
				nflTeamsMap.put(nflTeam.getNflTeamId(), nflTeam);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return nflTeamsMap;
	}
	
	public static List<NFLTeam> getFinalFour(Integer year) {
		List<NFLTeam> finalFour = new ArrayList<>();
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT t.* FROM NFLPlayoffsGame npg, NFLTeam t WHERE " + 
					"npg.winner = t.shortName AND " + 
					"(npg.gameindex >= (SELECT max(gameIndex) - 6 FROM NFLPlayoffsGame WHERE year = " + year + ") AND " + 
					"npg.gameindex <= (SELECT max(gameIndex) - 3 FROM NFLPlayoffsGame WHERE year = " + year + ")) ORDER BY t.conference";
			ResultSet rs = stmt.executeQuery(sql);
			NFLTeam nflTeam;
			while (rs.next()) {
				nflTeam = new NFLTeam(rs.getInt("NFLTeamId"), rs.getString("LongName"), rs.getString("ShortName"), rs.getString("Conference"));
				finalFour.add(nflTeam);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return finalFour;
	}
	
	public static List<String> getRound2WinningTeams(Integer year) {
		List<String> round2WinningTeams = new ArrayList<String>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT winner FROM NFLPlayoffsGame where round = 2 and " + getYearClause(year, null));
			while (rs.next()) {
				round2WinningTeams.add(rs.getString(1));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return round2WinningTeams;
	}
	
	public static TreeMap<String, Standings> getStandings(boolean maxPoints, Integer year, Integer poolId) {
		TreeMap<String, Standings> standings = new TreeMap<String, Standings>(Collections.reverseOrder());
		HashMap<String, String> ptsStandings = new HashMap<String, String>();
		HashMap<String, String> maxPtsStandings = new HashMap<String, String>();
		String winnerSql = "";
		try {
			Statement stmt = conn.createStatement();
			/*
			if (year < 20) {
				winnerSql = "= g.winner";
			}
			else {  // If 2020 on use scores to determine winner
				winnerSql = "= (SELECT " + 
						    "CASE" + 
						    " WHEN g2.homescore > g2.visscore THEN t1.shortname" + 
						    " WHEN g2.visscore > g2.homescore THEN t2.shortname" + 
						    " ELSE 'Tie' " + 
						    "END " + 
						    "FROM nflplayoffsgame g2, nflteam t1, nflteam t2 where g2.home = t1.nflteamid and g2.visitor = t2.nflteamid and g2.gameindex = g.gameindex)";
			}*/
			// Count as a win if the team won any game in that round
			winnerSql = "in (SELECT winner FROM NFLPLayoffsGame where pointsvalue = g.pointsvalue and year = g.year)";
			String sql = "SELECT u.UserName, sum(g.PointsValue) from Pick p, User u, NFLPLayoffsGame g " + 
					"where p.userId = u.userId and g.gameIndex = p.gameId and g.completed = true and p.winner " + winnerSql +  
					" and " + getYearClause("g", year, "p", poolId) +
					" group by u.UserName order by sum(g.PointsValue) desc, u.UserName";
			ResultSet rs = stmt.executeQuery(sql);
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
		List<User> usersList = getUsersWithPicksList(year, poolId != null ? poolId : null);
		// Merge any users with 0 points
		// TBD Make sure users with 0 pts and maxPts > 0 display correctly
		for (User u : usersList) {
			if (!ptsStandings.containsKey(u.getUserName())) {
				ptsStandings.put(u.getUserName(), "00");
			}
		}
		// Max points
		try {
			Statement stmt = conn.createStatement();
			String sql = "select u.UserName, sum(g.PointsValue) from Pick p, User u, NFLPLayoffsGame g where " + 
				"p.userId= u.userId and g.gameIndex = p.gameId and " + 
				"((g.completed = true and p.winner " + winnerSql + ") or " +
				"(g.completed = false and p.winner not in (select Loser from NFLPLayoffsGame where Loser is not null" + 
				" and " + getYearClause(year, null) + "))) and " + getYearClause("g", year, "p", poolId)  +
				" group by u.UserName order by sum(g.PointsValue) desc, u.UserName";
			ResultSet rs = stmt.executeQuery(sql);
			
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
		
		Iterator<Entry<String, String>> it = ptsStandings.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> line = (Map.Entry<String, String>)it.next();
			String maxPts = maxPtsStandings.get(line.getKey());
			if (maxPts == null) {
				maxPts= "00";
			}
			Standings stand = new Standings();
			stand.setUserName(line.getKey());
			stand.setPoints(line.getValue());
			stand.setMaxPoints(maxPts);
			if (maxPoints) {
				standings.put(maxPts + ":" + line.getKey(), stand);
			}
			else {
				standings.put(line.getValue() + ":" + line.getKey(), stand);
			}
		}
		return standings;
	}
	
	public static Map<Integer, List<Pick>> getPicksMap(Pool pool) { 
		Map<Integer, List<Pick>> picksMap = new HashMap<Integer, List<Pick>>();
		ArrayList<Pick> picksList = new ArrayList<Pick>();
		Integer prevUserId = null; 
		Integer userId = null;
		Integer gameId = null;
		Integer pickId = null;
		String winner = null;
		Integer poolId = null;
		Timestamp createdTime = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from Pick where poolId = " + pool.getPoolId() + " order by UserId, GameId");
			while (rs.next()) {
				userId = rs.getInt("UserId");
				gameId = rs.getInt("GameId");
				pickId = rs.getInt("PickId");
				winner = rs.getString("Winner");
				poolId = rs.getInt("PoolId");
				createdTime = rs.getTimestamp("CreatedTime");
				if (prevUserId != null && userId.intValue()!= prevUserId.intValue()) {
					picksMap.put(prevUserId, picksList);
					picksList = new ArrayList<Pick>();
				}
				Pick p = new Pick(pickId, userId, gameId, winner, poolId, createdTime);
				picksList.add(p);
				prevUserId = userId;
			}
			// add last one
			if (userId != null && gameId != null && pickId != null && winner != null && poolId != null && createdTime != null) {
				picksMap.put(userId, picksList);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return picksMap;
	}
	
	public static int getUsersCount(Integer year, Integer poolId) {
		int numberOfUsers = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from User where " + getYearClause(year, poolId));
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
			ResultSet rs = stmt.executeQuery("select count(*) from NFLPlayoffsGame where " + getYearClause(year, null));
			rs.next();
			numberOfNFLPlayoffsGames = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfNFLPlayoffsGames;
	}
	
	public static int getPicksCount(Integer year, Integer poolId) {
		int numberOfPicks = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from Pick p, NFLPlayoffsGame npg where p.gameId = npg.gameIndex and npg.year = " 
				+ year + " and p.poolId = " + poolId);
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
	
	// Not currently used
	public static Timestamp getFirstGameDateTime(int year) {
		Timestamp dt = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select min(dateTime) from NFLPlayoffsGame where year = " + year);
			rs.next();
			dt = rs.getTimestamp(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return dt;
	}
	
	public static Timestamp getFirstGameDateTimeFromPool(Integer poolId) {
		Timestamp dt = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select firstGameDateTime from Pool where poolId = " + poolId);
			rs.next();
			dt = rs.getTimestamp(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return dt;
	}
	
	public static Pool getPoolByPoolName(String poolName) {
		Pool pool = null;
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM Pool where PoolName = '" + poolName + "'";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				pool = new Pool(rs.getInt("PoolId"), rs.getString("PoolName"), rs.getInt("Year"), rs.getTimestamp("FirstGameDateTime"), rs.getInt("PointsRd1"),
					rs.getInt("PointsRd2"), rs.getInt("PointsChamp"), rs.getInt("PointsSB"));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return pool;
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
			ResultSet rs = stmt.executeQuery("select count(*) from NFLPlayoffsGame where Completed = 1 and " + getYearClause(year, null));
			rs.next();
			numberOfCompletedGames = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfCompletedGames;
	}
	
	/*
	public static boolean useYearClause(Integer year) {
		boolean yearClause = false;
		
		if (year.intValue() >= 17) {
			yearClause = true;
		}
		return yearClause;
	}*/
	
	private static String getYearClause(Integer year, Integer poolId) {
		String yearClause = "year = " + year;
		if (poolId != null) {
			yearClause += " and PoolId = " + poolId;
		}
		return yearClause;
	}
	
	private static String getYearClause(String yearPrefix, Integer year, String poolIdPrefix, Integer poolId) {
		String yearClause = yearPrefix + ".year = " + year;
		if (poolId != null) {
			yearClause += " and " + poolIdPrefix + ".PoolId = " + poolId;
		}
		return yearClause;
	}
	
	public static void setConnection(Connection connection) {
		conn = connection;
	}
	
	public static List<User> getUsersList(Integer year, Integer poolId) {
		List<User> userList = new ArrayList<User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM User where " + getYearClause(year, poolId));
			User user;
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"), rs.getString("FirstName"), 
					rs.getString("Email"), rs.getInt("Year"), rs.getBoolean("admin"), rs.getInt("PoolId"));
				userList.add(user);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}
	
	public static List<User> getUsersWithPicksList(Integer year, Integer poolId) {
		List<User>userList = new ArrayList<User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT distinct u.* FROM User u, Pick p where u.userId = p.userId and p.poolID = " + poolId);
			User user;
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"), rs.getString("FirstName"), 
					rs.getString("Email"), rs.getInt("Year"), rs.getBoolean("admin"), rs.getInt("PoolId"));
				userList.add(user);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}
	
	public static HashMap<Integer, User> getUsersMap(Integer poolId) {
		 HashMap<Integer, User> userMap = new HashMap<Integer, User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM User where poolId = " + poolId);
			User user;
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"), rs.getString("FirstName"), 
					rs.getString("Email"), rs.getInt("Year"), rs.getBoolean("admin"), rs.getInt("PoolId"));
				userMap.put(user.getUserId(), user);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return userMap;
	}
	
	public static User getUser(String name, Integer year, Integer poolId) {
		User user = null;
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM User where userName = '" + name + "' and " + getYearClause(year, poolId);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"), rs.getString("FirstName"), 
					rs.getString("Email"), rs.getInt("Year"), rs.getBoolean("admin"), rs.getInt("PoolId"));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public static void pingDatabase() throws SQLException {
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM Pool";
		stmt.executeQuery(sql);
	}
	
	public static void updateNFLPlayoffsGame(Integer visScore, Integer homeScore, String winner, String loser, Integer visitor, Integer home, Integer gameIndex) {
		try {
			Statement stmt = conn.createStatement();
			String sql = "UPDATE NFLPlayoffsGame SET VisScore = " + visScore + ", HomeScore = " + homeScore + ", Winner = '" + winner + "', Loser = '" + loser + 
				"', visitor = " + visitor + ", home = " + home + ", Completed = true WHERE GameIndex = " + gameIndex;
			stmt.execute(sql);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}
	
	public static void updatePoolFirstGameDateTime(String firstGameDateTime, Integer poolId) {
		try {
			Statement stmt = conn.createStatement();
			String sql = "UPDATE Pool SET FirstGameDateTime = '" + firstGameDateTime + "' WHERE PoolId = " + poolId;
			stmt.execute(sql);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}
}
