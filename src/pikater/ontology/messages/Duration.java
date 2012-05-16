package pikater.ontology.messages;

import java.util.Date;

import jade.content.Concept;

public class Duration implements Concept {

	private static final long serialVersionUID = -7310795521154346932L;
	
	private Date start;
	private int duration; // ms
	private float LR_duration;

	public float getLR_duration() {
		return LR_duration;
	}

	public void setLR_duration(float LR_duration) {
		this.LR_duration = LR_duration;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

}