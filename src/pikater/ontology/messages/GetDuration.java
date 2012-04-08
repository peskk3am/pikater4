package pikater.ontology.messages;

import jade.content.AgentAction;

public class GetDuration implements AgentAction {

	private static final long serialVersionUID = 7932839321818527345L;
	
	private int duration;

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
}