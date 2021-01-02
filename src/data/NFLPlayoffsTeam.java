package data;

public class NFLPlayoffsTeam {
	private int nflPlayoffsTeamId;
	private int nflTeamId;
	private int seed;
	private int year;
	
	public NFLPlayoffsTeam(int nflPlayoffsTeamId, int nflTeamId, int seed, int year) {
		this.nflPlayoffsTeamId = nflPlayoffsTeamId;
		this.nflTeamId = nflTeamId;
		this.seed = seed;
		this.year = year;
	}

	public int getNflPlayoffsTeamId() {
		return nflPlayoffsTeamId;
	}

	public void setNflPlayoffsTeamId(int nflPlayoffsTeamId) {
		this.nflPlayoffsTeamId = nflPlayoffsTeamId;
	}

	public int getNflTeamId() {
		return nflTeamId;
	}

	public void setNflTeamId(int nflTeamId) {
		this.nflTeamId = nflTeamId;
	}

	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	
	
	
	

}
