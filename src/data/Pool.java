package data;

import java.sql.Timestamp;

public class Pool {
	
	private int poolId;
	private String poolName;
	private int year;
	private Timestamp firstGameDateTime;
	// the point values are only used in CreateNFLPlayoffsGame to populate NFLPlayoffsGame.PointsValue
	private int pointsRd1;
	private int pointsRd2;
	private int pointsChamp;
	private int pointsSB;
	
	public Pool () {
	}
	
	public Pool (int poolId, String poolName, int year, Timestamp firstGameDateTime, int pointsRd1, int pointsRd2, int pointsChamp, int pointsSB) {
		this.poolId = poolId;
		this.poolName = poolName;
		this.year = year;	
		this.firstGameDateTime = firstGameDateTime;
		this.pointsRd1 = pointsRd1;
		this.pointsRd2 = pointsRd2;
		this.pointsChamp = pointsChamp;
		this.pointsSB = pointsSB;
	}

	public int getPoolId() {
		return poolId;
	}

	public void setPoolId(int poolId) {
		this.poolId = poolId;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Timestamp getFirstGameDateTime() {
		return firstGameDateTime;
	}

	public void setFirstGameDateTime(Timestamp firstGameDateTime) {
		this.firstGameDateTime = firstGameDateTime;
	}

	public int getPointsRd1() {
		return pointsRd1;
	}

	public void setPointsRd1(int pointsRd1) {
		this.pointsRd1 = pointsRd1;
	}

	public int getPointsRd2() {
		return pointsRd2;
	}

	public void setPointsRd2(int pointsRd2) {
		this.pointsRd2 = pointsRd2;
	}

	public int getPointsChamp() {
		return pointsChamp;
	}

	public void setPointsChamp(int pointsChamp) {
		this.pointsChamp = pointsChamp;
	}

	public int getPointsSB() {
		return pointsSB;
	}

	public void setPointsSB(int pointsSB) {
		this.pointsSB = pointsSB;
	}

}
