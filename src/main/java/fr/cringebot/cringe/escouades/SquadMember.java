package fr.cringebot.cringe.escouades;

public class SquadMember {

	private Long points;
	private final String id;

	public SquadMember(String id) {
		this.id = id;
		this.points = 0L;
	}

	public void addPoint(Long nb) {
		this.points = this.points + nb;
	}

	public void removepoint(Long nb) {
		this.points = this.points - nb;
	}

	public Long getPoints() {
		return points;
	}

	public String getId() {
		return id;
	}
}
