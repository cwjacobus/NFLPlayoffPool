package actions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.NFLPlayoffsGame;


public class UpdateScoreAction extends ActionSupport {
	
	private String winner;
	private String loser;
	private Integer gameIndex;
	private String year;

	public String execute() throws Exception {
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
        }
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/nflplayoffspool" + this.year + "?" +
				"user=root&password=PASSWORD");
		}
		catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		updateScore(conn, winner, loser, gameIndex);
		
		List<NFLPlayoffsGame> nflPlayoffsGameList = DAO.getNFLPlayoffsGamesList(conn);
		
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();

	    context.put("nflPlayoffsGameList", nflPlayoffsGameList);
	    stack.push(context);
		
	    return "success";
	}
	   
	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}
	
	public String getLoser() {
		return loser;
	}

	public void setLoser(String loser) {
		this.loser = loser;
	}

	public Integer getGameIndex() {
		return gameIndex;
	}

	public void setGameIndex(Integer gameIndex) {
		this.gameIndex = gameIndex;
	}
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
	   this.year = year;
	}

	// DB
	private void updateScore(Connection conn, String winner, String loser, Integer gameIndex) {
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("UPDATE NFLPlayoffsGame SET Winner = '" + winner + "',Loser = '" + loser + "',Completed = true WHERE GameIndex = " + gameIndex);
		}
		catch (SQLException e) {
		}
		return;
	}
}
