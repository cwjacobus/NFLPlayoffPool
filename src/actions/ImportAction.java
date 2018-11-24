package actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.Pick;
import data.User;

public class ImportAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private String usersCB;
	private String gamesCB;
	private String picksCB;
	private String inputFileName;
	
	boolean usersImport = false;
	boolean picksImport = false;
	boolean nflPlayoffsGamesImport = false;
	
	Map<String, Object> userSession;
	
	Integer year;

	public String execute() throws Exception {	
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		year = (Integer) userSession.get("year");
		System.out.println("Import: " + year);
		
		InputStream input = ServletActionContext.getServletContext().getResourceAsStream("/WEB-INF/NFLPlayoffsPool.properties");
		Properties prop = new Properties();
		prop.load(input);
		System.out.println("Input file path: " + prop.getProperty("inputFilePath"));
		
	    System.out.println("Import " + usersCB + " " + gamesCB + " " + picksCB + " " + inputFileName);
	    if (usersCB == null && gamesCB == null && picksCB == null) {
	    	context.put("errorMsg", "Nothing selected to import!");
	    	stack.push(context);
	    	return "error";
	    }
	    else if (inputFileName == null || inputFileName.length() == 0) {
	    	context.put("errorMsg", "No file selected to import!");
	    	stack.push(context);
	    	return "error";
	    }
	    else {
	    	if (usersCB != null) {
	    		usersImport = true; 
	    		// Check for users already imported
	    		if (DAO.getUsersCount(year) > 0) {
	    			context.put("errorMsg", "Users already imported for 20" + year + "!  Delete and reimport.");
	    			stack.push(context);
	    			return "error";
	    		}
	    	}
	    	if (picksCB != null) {
	    		picksImport = true;
	    		// Check for games imported
	    		if (DAO.getNFLPlayoffsGamesCount(year) == 0) {
	    			context.put("errorMsg", "NFLPlayoffs Games not imported for 20" + year + "!  Import Bowl Games.");
	    			stack.push(context);
	    			return "error";
	    		}
	    		// Check for picks already imported
	    		if (DAO.getPicksCount(year) > 0) {
	    			context.put("errorMsg", "Picks already imported for 20" + year + "!  Delete and reimport.");
	    			stack.push(context);
	    			return "error";
	    		}
	    	}
	    	if (gamesCB != null) {
	    		nflPlayoffsGamesImport = true; 
	    		// Check for games already imported
	    		if (DAO.getNFLPlayoffsGamesCount(year) > 0) {
	    			context.put("errorMsg", "NFLPlayoffs Games already imported for 20" + year + "!  Delete and reimport.");
	    			stack.push(context);
	    			return "error";
	    		}
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
	    }
	    stack.push(context);
	    return "success";
	}
	
	private void importUsersAndPicks(HSSFWorkbook hWorkbook) {
		try {  
			List<User> userList = DAO.getUsersList(year);
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
	        			DAO.createUser(userName, year);
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
	        			int gameIndex = 1;
	        			while (cellIter.hasNext()){
	        				Cell cell = (Cell)cellIter.next();
	        				if (cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1) {
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
	        				if ((pick != null)) {
	        					System.out.print("PICK" + gameIndex + ": " + pick.toUpperCase() + " ");
	        					DAO.createPick(user.getUserId(), gameIndex, pick.toUpperCase());
	        				}
	        				gameIndex++;
	        	        }
	        			System.out.println();
	        		}
	        	}
	        	prevUser = userName;
	        }
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	private void importNFLPlayoffsGames(HSSFWorkbook hWorkbook) {	
		try {  
			HSSFSheet sheet = hWorkbook.getSheetAt(0);
	        System.out.println(sheet.getSheetName());
	        Iterator<Row> rowIterator = sheet.iterator();
	        Row pointsValueRow;
	        while (rowIterator.hasNext()) {
	        	Row row = rowIterator.next();
	        	String gameDesc = getStringFromCell(row, 2);
	        	if (gameDesc!= null && gameDesc.indexOf("@") != -1) {
	        		rowIterator.next();
	        		pointsValueRow = rowIterator.next();
	        		Iterator<Cell> cellIter = row.cellIterator();
	        		while (cellIter.hasNext()){
	        			Cell cell = cellIter.next();
	        			if (cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1) {
	        				continue;
	        			}
	        			gameDesc = cell.getStringCellValue();
	        			if (gameDesc == null || gameDesc.length() == 0) {
	        				break;
	        			}
	        			System.out.println(gameDesc);
		        		String pointsValueString = getStringFromCell(pointsValueRow, cell.getColumnIndex());
		        		pointsValueString = pointsValueString.split(" ")[0].replace("(", "");
		        		int pointsValue = Integer.parseInt(pointsValueString);
		        		DAO.createNFLPlayoffsGame(gameDesc, "", pointsValue, "",  year);
	        		}
	        		
	        		// Manually add Champ games and SB
	        		DAO.createNFLPlayoffsGame("AFC Champ", "", 10, "",  year);
	        		DAO.createNFLPlayoffsGame("NFC Champ", "", 10, "",  year);
	        		DAO.createNFLPlayoffsGame("Super Bowl", "", 20, "",  year);
	        		break;
	        	}
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
	
	// DB
	


	public String getUsersCB() {
		return usersCB;
	}

	public void setUsersCB(String usersCB) {
		this.usersCB = usersCB;
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
	
	private String getNFLTeamFromAlias(String alias) {
		
		String nflTeam = alias;
		
		if (nflTeam.equalsIgnoreCase("CINC") || nflTeam.equalsIgnoreCase("CINCI") || nflTeam.equalsIgnoreCase("CINCY")) {
			nflTeam = "CIN";
		}
		else if (nflTeam.equalsIgnoreCase("PITT") || nflTeam.equalsIgnoreCase("STEELERS")) {
			nflTeam = "PIT";
		}
		else if (nflTeam.equalsIgnoreCase("ARIZ") || nflTeam.equalsIgnoreCase("ARI") || nflTeam.equalsIgnoreCase("AZ")) {
			nflTeam = "ARZ";
		}
		else if (nflTeam.equalsIgnoreCase("BALT")) {
			nflTeam = "BAL";
		}
		else if (nflTeam.equalsIgnoreCase("INDY")) {
			nflTeam = "IND";
		}
		else if (nflTeam.equalsIgnoreCase("DENV")) {
			nflTeam = "DEN";
		}
		else if (nflTeam.equalsIgnoreCase("SEAT") || nflTeam.equalsIgnoreCase("SEATTLE")) {
			nflTeam = "SEA";
		}
		else if (nflTeam.equalsIgnoreCase("WASH")) {
			nflTeam = "WAS";
		}
		else if (nflTeam.equalsIgnoreCase("HOUSTON")) {
			nflTeam = "HOU";
		}
		else if (nflTeam.equalsIgnoreCase("LA") || nflTeam.equalsIgnoreCase("RAMS")  || nflTeam.equalsIgnoreCase("LAR")) {
			nflTeam = "LARAMS";
		}
		else if (nflTeam.equalsIgnoreCase("PHILLY") || nflTeam.equalsIgnoreCase("PHILA")) {
			nflTeam = "PHI";
		}
		else if (nflTeam.equalsIgnoreCase("TENN") || nflTeam.equalsIgnoreCase("TITANS")) {
			nflTeam = "TEN";
		}
		else if (nflTeam.equalsIgnoreCase("BILLS")) {
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
		
		return nflTeam;
		
	}
}
