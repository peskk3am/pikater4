package pikater.ontology.messages;

import java.util.Date;

import jade.content.AgentAction;

public class GetDuration implements AgentAction {

	private static final long serialVersionUID = 7932839321818527345L;
	
	private Duration duration;

	public Duration getDuration() {
		return duration;
	}
	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	
}