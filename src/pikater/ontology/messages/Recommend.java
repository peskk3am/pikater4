package pikater.ontology.messages;

import jade.content.AgentAction;

public class Recommend implements AgentAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4556943676301959461L;
	
	private Data data; 
	
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
}
