package actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import data.Pool;
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
	        			DAO.createUser(userName, pool.getYear(), pool.getPoolId());
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
		        		DAO.createNFLPlayoffsGame(gameDesc, pointsValue, pool.getYear());
	        		}
	        		
	        		// Manually add Champ games and SB
	        		DAO.createNFLPlayoffsGame("AFC Champ", 10, pool.getYear());
	        		DAO.createNFLPlayoffsGame("NFC Champ", 10, pool.getYear());
	        		DAO.createNFLPlayoffsGame("Super Bowl", 20, pool.getYear());
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
}
