package data;

public class NFLTeam {
	
	private int nflTeamId;
	private String longName;
	private String shortName;
	private String conference;
	
	public NFLTeam (int nflTeamId, String longName, String shortName, String conference) {
		this.nflTeamId = nflTeamId;
		this.longName = longName;
		this.shortName = shortName;
		this.conference = conference;
	}

	public int getNflTeamId() {
		return nflTeamId;
	}

	public void setNflTeamId(int nflTeamId) {
		this.nflTeamId = nflTeamId;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getConference() {
		return conference;
	}

	public void setConference(String conference) {
		this.conference = conference;
	}
	
	public String toString() {
		return this.longName;
	}
	
	

}
