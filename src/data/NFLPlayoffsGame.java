package data;

public class NFLPlayoffsGame {
	
	private int gameIndex;
	private String description;
	private String winner;
	private String loser;
	private int pointsValue;
	private boolean completed;
	private int year;
	private int home;
	private int visitor;
	
	public NFLPlayoffsGame (int gameIndex, String description, String winner, String loser, int pointsValue, boolean completed, int year, int home, int visitor) {
		this.gameIndex = gameIndex;
		this.description = description;
		this.winner = winner;
		this.loser = loser;
		this.pointsValue = pointsValue;
		this.completed = completed;
		this.year = year;
		this.home = home;
		this.visitor = visitor;
	}
	
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public int getGameIndex() {
		return gameIndex;
	}

	public void setGameIndex(int gameIndex) {
		this.gameIndex = gameIndex;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public int getPointsValue() {
		return pointsValue;
	}

	public void setPointsValue(int pointsValue) {
		this.pointsValue = pointsValue;
	}

	public String getLoser() {
		return loser;
	}

	public void setLoser(String loser) {
		this.loser = loser;
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getHome() {
		return home;
	}

	public void setHome(int home) {
		this.home = home;
	}

	public int getVisitor() {
		return visitor;
	}

	public void setVisitor(int visitor) {
		this.visitor = visitor;
	}

}
