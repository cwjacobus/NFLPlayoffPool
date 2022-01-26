package data;

import java.sql.Timestamp;

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
	private String conference;
	private int homeScore;
	private int visScore;
	private int homeSeed;
	private int visSeed;
	private Timestamp dateTime;
	private int round;
	
	public NFLPlayoffsGame(int gameIndex, String description, String winner, String loser, int pointsValue, boolean completed, int year, 
			int home, int visitor, String conference, int homeScore, int visScore, int homeSeed, int visSeed, Timestamp dateTime, int round) {
		this.gameIndex = gameIndex;
		this.description = description;
		this.winner = winner;
		this.loser = loser;
		this.pointsValue = pointsValue;
		this.completed = completed;
		this.year = year;
		this.home = home;
		this.visitor = visitor;
		this.conference = conference;
		this.homeScore = homeScore;
		this.visScore = visScore;
		this.homeSeed = homeSeed;
		this.visSeed = visSeed;
		this.dateTime = dateTime;
		this.round = round;
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
	public String getConference() {
		return conference;
	}
	public void setConference(String conference) {
		this.conference = conference;
	}
	public int getHomeScore() {
		return homeScore;
	}
	public void setHomeScore(int homeScore) {
		this.homeScore = homeScore;
	}
	public int getVisScore() {
		return visScore;
	}
	public void setVisScore(int visScore) {
		this.visScore = visScore;
	}
	public int getHomeSeed() {
		return homeSeed;
	}
	public void setHomeSeed(int homeSeed) {
		this.homeSeed = homeSeed;
	}
	public int getVisSeed() {
		return visSeed;
	}
	public void setVisSeed(int visSeed) {
		this.visSeed = visSeed;
	}
	public Timestamp getDateTime() {
		return dateTime;
	}
	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
}
