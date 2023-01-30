package data;

import java.sql.Timestamp;

public class Pick {
	
	private int pickId;
	private int userId;
	private int gameId;
	private String winner;
	int poolId;
	Timestamp createdTime;
	
	public Pick () {
	}
	
	public Pick (int pickId, int userId, int gameId, String winner, int poolId, Timestamp createdTime) {
		this.gameId = gameId;
		this.userId = userId;
		this.pickId = pickId;
		this.winner = winner;
		this.poolId = poolId;
		this.createdTime = createdTime;
	}
	
	public int getPickId() {
		return pickId;
	}

	public void setPickId(int pickId) {
		this.pickId = pickId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getGameId() {
		return gameId;
	}
	
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	
	public String getWinner() {
		return winner;
	}
	
	public void setWinner(String winner) {
		this.winner = winner;
	}

	public int getPoolId() {
		return poolId;
	}

	public void setPoolId(int poolId) {
		this.poolId = poolId;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
	
	public String toString() {
		return this.winner;
	}

}
