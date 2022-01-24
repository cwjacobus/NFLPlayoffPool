package actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONArray;
import org.json.JSONObject;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.NFLTeam;
import data.Pick;
import data.Pool;
import data.User;

public class ImportAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private String usersCB;
	private String usersFPCB;
	private Integer fromPoolId;
	private String gamesCB;
	private String picksCB;
	private String teamsCB;
	private String inputFileName;
	
	boolean usersImport = false;
	boolean usersFYImport = false;
	boolean picksImport = false;
	boolean nflPlayoffsGamesImport = false;
	boolean nflTeamsImport = false;
	
	Map<String, Object> userSession;
	Pool pool;
	
	public String execute() throws Exception {	
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		pool = (Pool) userSession.get("pool");
		System.out.println("Import: " + pool.getYear());
		InputStream input = ServletActionContext.getServletContext().getResourceAsStream("/WEB-INF/NFLPlayoffsPool.properties");
		Properties prop = new Properties();
		prop.load(input);
		System.out.println("Input file path: " + prop.getProperty("inputFilePath"));
		
	    System.out.println("Import " + usersCB + " " + usersFPCB + " " + gamesCB + " " + picksCB + " " + teamsCB + " " + inputFileName);
	    if (usersCB == null && usersFPCB == null && gamesCB == null && picksCB == null && teamsCB == null) {
	    	context.put("errorMsg", "Nothing selected to import!");
	    	stack.push(context);
	    	return "error";
	    }
	    else if ((inputFileName == null || inputFileName.length() == 0) && (usersCB != null || gamesCB != null || picksCB != null)) {
	    	context.put("errorMsg", "No file selected to import!");
	    	stack.push(context);
	    	return "error";
	    }
	    else {
	    	if (usersCB != null || usersFPCB != null) {
	    		if (usersCB != null) {
	    			usersImport = true; 
	    		}
	    		if (usersFPCB != null) {
	    			usersFYImport = true; 
	    		}
	    		// Check for users already imported
	    		if (DAO.getUsersCount(pool.getYear(), pool.getPoolId()) > 0) {
	    			context.put("errorMsg", "Users already imported for 20" + pool.getYear() + "!  Delete and reimport.");
	    			stack.push(context);
	    			return "error";
	    		}
	    	}
	    	if (picksCB != null) {
	    		picksImport = true;
	    		// Check for games imported
	    		if (DAO.getNFLPlayoffsGamesCount(pool.getYear()) == 0) {
	    			context.put("errorMsg", "NFLPlayoffs Games not imported for 20" + pool.getYear() + "!  Import Bowl Games.");
	    			stack.push(context);
	    			return "error";
	    		}
	    		// Check for picks already imported
	    		if (DAO.getPicksCount(pool.getYear(), pool.getPoolId()) > 0) {
	    			context.put("errorMsg", "Picks already imported for 20" + pool.getYear() + "!  Delete and reimport.");
	    			stack.push(context);
	    			return "error";
	    		}
	    	}
	    	if (gamesCB != null) {
	    		nflPlayoffsGamesImport = true; 
	    		// Check for games already imported
	    		if (DAO.getNFLPlayoffsGamesCount(pool.getYear()) > 0) {
	    			context.put("errorMsg", "NFLPlayoffs Games already imported for 20" + pool.getYear() + "!  Delete and reimport.");
	    			stack.push(context);
	    			return "error";
	    		}
	    	}
	    	if (teamsCB != null) {
	    		nflTeamsImport = true;
	    		// Check for teams already imported
	    	}
	    	if (usersImport || picksImport || nflPlayoffsGamesImport) {
	    		File inputFile = new File(prop.getProperty("inputFilePath") + inputFileName);
	    		FileInputStream spreadSheetFile = new FileInputStream(inputFile);
	    		//Create Workbook instance holding reference to .xls file
	    		HSSFWorkbook hWorkbook = new HSSFWorkbook(spreadSheetFile);
	    		if (usersImport || picksImport) {
	    			importUsersAndPicks(hWorkbook);
	    		}
	    		if (nflPlayoffsGamesImport) {
	    			importNFLPlayoffsGames(hWorkbook);
	    		}
	    	}
	    	if (usersFYImport) {
	    		if (fromPoolId != null) {
	    			int numberOfUsersImported = importUsersFromPoolId(pool);
	    			if (numberOfUsersImported == 0) {
	    				context.put("errorMsg", "No users imported!");
		    			stack.push(context);
		    			return "error";
	    			}
	    			System.out.println(numberOfUsersImported + " users imported from pool ID: " + fromPoolId);
	    		}
	    		else {
	    			context.put("errorMsg", "Pool ID is required!");
	    			stack.push(context);
	    			return "error";
	    		}
	    	}
	    	if (nflTeamsImport) {
	    		importNFLTeamsFromWS();
    		}
	    }
	    stack.push(context);
	    return "success";
	}
	
	private void importUsersAndPicks(HSSFWorkbook hWorkbook) {
		// Does not like when first line is blank
		List<Pick> picksList = new ArrayList<Pick>();
		List<User> userList = DAO.getUsersList(pool.getYear(), pool.getPoolId());
		try {  
			HSSFSheet sheet = hWorkbook.getSheetAt(0);
	        System.out.println(sheet.getSheetName());
	        Iterator<Row> rowIterator = sheet.iterator();
	        boolean usersFound = false;
	        String prevUser = null;
	        while (rowIterator.hasNext()) {
	        	Row row = rowIterator.next();
	        	String userName = getStringFromCell(row, 0);
	        	if (userName!= null && userName.equalsIgnoreCase("NAME")) {
	        		usersFound = true;
	        		continue;
	        	}
	        	if (!usersFound) {
	        		continue;
	        	}
	        	if (usersFound && userName == null && prevUser == null) {
	        		break;
	        	}
	        	if (userName != null) {
	        		System.out.println(userName);
	        		//int userId = 0;
	        		if (usersImport) {
	        			DAO.createUser(userName, null, null, pool.getYear(), pool.getPoolId());
	        		}
	        		if (picksImport) {
	        			User user = null;
	        			for (User u : userList) {
        					if (u.getUserName().equalsIgnoreCase(userName)) {
        						user = u;
        						break;
        					}
        				}
	        			Iterator<Cell> cellIter = row.cellIterator();
	        			// Get first game index - assume consecutive indexes
	        			int gameIndex = 0;
	        			int firstGameIndex = DAO.getFirstGameIndexForAYear(pool.getYear());
	        			while (cellIter.hasNext()){
	        				Cell cell = (Cell)cellIter.next();
	        				if (cell.getColumnIndex() == 0) {
	        					continue;
	        				}
	        				String pick = getStringFromCell(row, cell.getColumnIndex());
	        				pick = pick.replace(".", "");
	        				pick = getNFLTeamFromAlias(pick);
	        				Double totalPoints = null;
	        				try {
	        					totalPoints = Double.parseDouble(pick);
	        				}
	        				catch (NumberFormatException e) {
	        				}
	        				if (totalPoints != null) {
	        					break;
	        				}
	        				// Special case for 2018 since the game indices are not in the same order for
	        				// spreadsheet import and making picks through app
	        				int pickGameIndex = firstGameIndex + gameIndex;
	        				if (pool.getYear() == 18) {
	        					if (gameIndex == 0 || gameIndex == 2) {
	        						pickGameIndex += 1;
	        					}
	        					else if (gameIndex == 1 || gameIndex == 3) {
	        						pickGameIndex -= 1;
	        					}
	        				}
	        				if ((pick != null)) {
	        					System.out.print("PICK" + pickGameIndex + ": " + pick.toUpperCase() + " ");
	        					//DAO.createPick(user.getUserId(), gameIndex, pick.toUpperCase());
	        					picksList.add(new Pick(0, user.getUserId(), pickGameIndex, pick.toUpperCase(), 
	        						pool.getPoolId(), new Timestamp(new Date().getTime())));
	        				}
	        				gameIndex++;
	        	        }
	        			System.out.println();
	        		}
	        	}
	        	prevUser = userName;
	        }
	        if (picksList.size() > 0) {
	        	DAO.createBatchPicks(picksList, pool.getPoolId());
	        }
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	private int importUsersFromPoolId(Pool pool) {
		return DAO.copyUsersFromAnotherPool(pool.getYear(), pool.getPoolId(), fromPoolId);
	}
	
	private void importNFLPlayoffsGames(HSSFWorkbook hWorkbook) {	
		try {  
			HSSFSheet sheet = hWorkbook.getSheetAt(0);
	        System.out.println(sheet.getSheetName());
	        Iterator<Row> rowIterator = sheet.iterator();
	        Row pointsValueRow;
	        Row seedingRow;
	        Row conferenceRow = null;
	        @SuppressWarnings("unchecked")
			HashMap<Integer, NFLTeam> nflTeamsMapById = (HashMap<Integer, NFLTeam>)userSession.get("nflTeamsMap");
	        while (rowIterator.hasNext()) {
	        	Row row = rowIterator.next();
	        	String gameDesc = getStringFromCell(row, 1);
	        	if (gameDesc!= null && gameDesc.contains("Round")) {
	        		conferenceRow = row;
	        	}
	        	if (gameDesc!= null && gameDesc.indexOf("@") != -1) {
	        		seedingRow = rowIterator.next();
	        		pointsValueRow = rowIterator.next();
	        		Iterator<Cell> cellIter = row.cellIterator();
	        		String conferenceString;
	        		String conference= null;
	        		while (cellIter.hasNext()){
	        			Cell cell = cellIter.next();
	        			if (cell.getColumnIndex() == 0/* || cell.getColumnIndex() == 1*/) {
	        				continue;
	        			}
	        			gameDesc = cell.getStringCellValue();
	        			if (gameDesc == null || gameDesc.length() == 0 || gameDesc.contains("mid")) { // skip empty or "@ mid"
	        				continue;
	        			}
	        			if (gameDesc.equalsIgnoreCase("SB")) {
	        				break;
	        			}
	        			System.out.println(gameDesc);
		        		String pointsValueString = getStringFromCell(pointsValueRow, cell.getColumnIndex());
		        		String seedingString = getStringFromCell(seedingRow, cell.getColumnIndex());
		        		conferenceString = getStringFromCell(conferenceRow, cell.getColumnIndex());
		        		if (conferenceString != null) {
		        			conference = conferenceString.contains("AFC") ? "AFC" : "NFC";
		        		}
		        		String homeTeam = null;
		        		String visitorTeam = null;
		        		Integer homeSeed = null;
		        		Integer visitorSeed = null;
		        		String[] teamsStringArray = gameDesc.split("@");
		        		if (teamsStringArray.length == 2) {
		        			homeTeam = teamsStringArray[1].trim();
		        			visitorTeam = teamsStringArray[0].trim();
		        			homeTeam = getNFLTeamFromAlias(homeTeam); // In case name is different in spread sheet
		        			visitorTeam = getNFLTeamFromAlias(visitorTeam); // In case name is different in spread sheet
		        		}
		        		String[] seedingStringArray = seedingString.split("@");
		        		if (seedingStringArray.length == 2) {
		        			homeSeed = convertSeedingStringToNumber(seedingStringArray[1]);
		        			visitorSeed = convertSeedingStringToNumber(seedingStringArray[0]);
		        		}
		        		pointsValueString = pointsValueString.split(" ")[0].replace("(", "");
		        		int pointsValue = Integer.parseInt(pointsValueString);
		        		System.out.println("VIS: " + visitorTeam + " " + visitorSeed + " HOME: " + homeTeam + " " + homeSeed);
		        		//Integer homeNflTeamId = nflTeamsMap.get(homeTeam) != null ? nflTeamsMap.get(homeTeam).getNflTeamId() : null;
		        		Integer homeNflTeamId = getNFLTeamIdFromShortName(homeTeam, nflTeamsMapById);
		        		Integer visitorNflTeamId = null;
		        		if (visitorTeam != null && visitorTeam.length() > 0 && visitorSeed != null) {
		        			//visitorNflTeamId = nflTeamsMap.get(visitorTeam) != null ? nflTeamsMap.get(visitorTeam).getNflTeamId() : null;
		        			visitorNflTeamId = getNFLTeamIdFromShortName(visitorTeam, nflTeamsMapById);
		        		}
		        		DAO.createNFLPlayoffsGame(gameDesc, pointsValue, pool.getYear(), homeNflTeamId, visitorNflTeamId, conference, null, null, homeSeed, visitorSeed, null);
		        		// TBD Add R2 game here
		        		if (visitorNflTeamId == null) {  // Is a R2 game?
		        			if (conference.equalsIgnoreCase("AFC")) {
		        				DAO.createNFLPlayoffsGame("AFC R2 Game 2", 5, pool.getYear(), null, null, "AFC", null, null, 1, null, null); // Placeholder for second R2 game
		        			}
		        			else {
		        				DAO.createNFLPlayoffsGame("NFC R2 Game 2", 5, pool.getYear(), null, null, "NFC", null, null, 1, null, null); // Placeholder for second R2 game
		        			}
		        		}
	        		}
	        		// Manually add Champ games and SB
	        		DAO.createNFLPlayoffsGame("AFC Champ", 10, pool.getYear(), null, null, "AFC", null, null, null, null, null);
	        		DAO.createNFLPlayoffsGame("NFC Champ", 10, pool.getYear(), null, null, "NFC", null, null, null, null, null);
	        		DAO.createNFLPlayoffsGame("Super Bowl", 20, pool.getYear(), null, null, null, null, null, null, null, null);
	        		break;
	        	}
	        }
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	private void importNFLTeamsFromWS() {
		System.out.println("Import teams from web service");
		try {
			String uRL;
			uRL = "https://api.sportsdata.io/v3/nfl/scores/json/Teams?key=eea45baa72c64bc6bd003b511e9e36d0";
			URL obj = new URL(uRL);
			HttpURLConnection con = (HttpURLConnection)obj.openConnection();
			//int responseCode = con.getResponseCode();
			//System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); 
			JSONArray all = new JSONArray(in.readLine());
			in.close();
			System.out.println(all.length() + " teams");
			
			for (int i = 0; i < all.length(); i++) {
				JSONObject team = all.getJSONObject(i);
			    
				String fullName = team.getString("FullName");
				String shortName = team.getString("Key");
				String teamId = team.getString("TeamID");
				System.out.println(teamId + ": " + shortName + ":" + fullName);
				
				DAO.createNFLTeam(teamId, fullName, shortName);
			}
		 }
		catch (Exception e) {
			e.printStackTrace();
        }
	}
	
	private String getStringFromCell(Row row, int index) {
		String cellString;
		   
		if (row.getCell(index) == null || row.getCell(index).getCellType() == CellType.BLANK) {
		      return null; 
		}
		   
		if (row.getCell(index).getCellType() == CellType.STRING) {
		    cellString = row.getCell(index).getStringCellValue().trim(); 
		}
		else  {
			String dblValString = Double.toString(row.getCell(index).getNumericCellValue());
		    if (dblValString.indexOf(".") != -1) {
		    	cellString = Double.toString((double)row.getCell(index).getNumericCellValue()); 
		    }
		    else {
		    	cellString = Long.toString((long)row.getCell(index).getNumericCellValue()); 
		    } 
		 }
		 return cellString;
	}
	
	public Double getNumberFromCell(Row row, int index) {
	    Double cellNumber;
	    
	    if (row.getCell(index) == null || row.getCell(index).getCellType() == CellType.BLANK) {
	       return null; 
	   }
	    
	    if (row.getCell(index).getCellType() == CellType.NUMERIC) {
	    cellNumber = row.getCell(index).getNumericCellValue(); 
	    }
	    else  {
	    try {
	       cellNumber = new Double(row.getCell(index).getStringCellValue().trim());
	    }
	    catch (NumberFormatException e) { return new Double(0);}
	   }
	    return cellNumber; 
	}
	
	private Integer convertSeedingStringToNumber(String seedingString) {
		Integer seedingNumber = null;
		String convertedSeedingString = "";
		if (seedingString != null && seedingString.length() > 0) {
			for (int i = 0 ; i < seedingString.length(); i++) {
				if (Character.isDigit(seedingString.charAt(i))) {
					convertedSeedingString += seedingString.charAt(i);
				}
			}
			if (convertedSeedingString.length() > 0) {
				seedingNumber = Integer.parseInt(convertedSeedingString);
			}
		}
		return seedingNumber;		
	}
	
	private String getNFLTeamFromAlias(String alias) {
		String nflTeam = alias;
		if (nflTeam.equalsIgnoreCase("CINC") || nflTeam.equalsIgnoreCase("CINCI") || nflTeam.equalsIgnoreCase("CINCY")) {
			nflTeam = "CIN";
		}
		else if (nflTeam.equalsIgnoreCase("PITT") || nflTeam.equalsIgnoreCase("STEELERS")) {
			nflTeam = "PIT";
		}
		else if (nflTeam.equalsIgnoreCase("ARIZ") || nflTeam.equalsIgnoreCase("ARZ") || nflTeam.equalsIgnoreCase("AZ")) {
			nflTeam = "ARI";
		}
		else if (nflTeam.equalsIgnoreCase("BALT") || nflTeam.equalsIgnoreCase("BALTIMORE")) {
			nflTeam = "BAL";
		}
		else if (nflTeam.equalsIgnoreCase("INDY") || nflTeam.equalsIgnoreCase("COLTS")) {
			nflTeam = "IND";
		}
		else if (nflTeam.equalsIgnoreCase("DENV")) {
			nflTeam = "DEN";
		}
		else if (nflTeam.equalsIgnoreCase("SEAT") || nflTeam.equalsIgnoreCase("SEATTLE") || 
				nflTeam.equalsIgnoreCase("SEAHAWKS") || nflTeam.equalsIgnoreCase("SEATLE")) {
			nflTeam = "SEA";
		}
		else if (nflTeam.equalsIgnoreCase("WASH")) {
			nflTeam = "WAS";
		}
		else if (nflTeam.equalsIgnoreCase("HOUSTON") || nflTeam.equalsIgnoreCase("HOUS")) {
			nflTeam = "HOU";
		}
		else if (nflTeam.equalsIgnoreCase("LARAMS") || nflTeam.equalsIgnoreCase("RAMS")) {
			nflTeam = "LAR";
		}
		else if (nflTeam.equalsIgnoreCase("PHILLY") || nflTeam.equalsIgnoreCase("PHILA")) {
			nflTeam = "PHI";
		}
		else if (nflTeam.equalsIgnoreCase("TENN") || nflTeam.equalsIgnoreCase("TITANS")) {
			nflTeam = "TEN";
		}
		else if (nflTeam.equalsIgnoreCase("BILLS") || nflTeam.equalsIgnoreCase("BUFF")) {
			nflTeam = "BUF";
		}
		else if (nflTeam.equalsIgnoreCase("SAINTS") || nflTeam.equalsIgnoreCase("NEW ORLEANS")) {
			nflTeam = "NO";
		}
		else if (nflTeam.equalsIgnoreCase("FALCONS")) {
			nflTeam = "ATL";
		}
		else if (nflTeam.equalsIgnoreCase("CHIEFS")) {
			nflTeam = "KC";
		}
		else if (nflTeam.equalsIgnoreCase("MINN")) {
			nflTeam = "MIN";
		}
		else if (nflTeam.equalsIgnoreCase("PATS")) {
			nflTeam = "NE";
		}
		else if (nflTeam.equalsIgnoreCase("JAC")) {
			nflTeam = "JAX";
		}
		else if (nflTeam.equalsIgnoreCase("DALLAS")) {
			nflTeam = "DAL";
		}
		else if (nflTeam.equalsIgnoreCase("CHARGERS")) {
			nflTeam = "LAC";
		}
		else if (nflTeam.equalsIgnoreCase("BEARS") || nflTeam.equalsIgnoreCase("CHICAGO")) {
			nflTeam = "CHI";
		}
		return nflTeam;
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

	public String getUsersCB() {
		return usersCB;
	}

	public void setUsersCB(String usersCB) {
		this.usersCB = usersCB;
	}

	public String getUsersFPCB() {
		return usersFPCB;
	}

	public void setUsersFPCB(String usersFPCB) {
		this.usersFPCB = usersFPCB;
	}

	public Integer getFromPoolId() {
		return fromPoolId;
	}

	public void setFromPoolId(Integer fromPoolId) {
		this.fromPoolId = fromPoolId;
	}

	public String getGamesCB() {
		return gamesCB;
	}

	public void setGamesCB(String gamesCB) {
		this.gamesCB = gamesCB;
	}

	public String getPicksCB() {
		return picksCB;
	}

	public void setPicksCB(String picksCB) {
		this.picksCB = picksCB;
	}
	
	public String getTeamsCB() {
		return teamsCB;
	}

	public void setTeamsCB(String teamsCB) {
		this.teamsCB = teamsCB;
	}

	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}
	
	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }
	
	
}
