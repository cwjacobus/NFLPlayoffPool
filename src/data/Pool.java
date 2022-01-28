package data;

import java.sql.Timestamp;

public class Pool {
	
	private int poolId;
	private String poolName;
	private int year;
	private Timestamp firstGameDateTime;
	
	public Pool () {
	}
	
	public Pool (int poolId, String poolName, int year, Timestamp firstGameDateTime) {
		this.poolId = poolId;
		this.poolName = poolName;
		this.year = year;	
		this.firstGameDateTime = firstGameDateTime;
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

}
