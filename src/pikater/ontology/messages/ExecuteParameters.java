package pikater.ontology.messages;

import jade.content.AgentAction;
import jade.util.leap.List;

public class ExecuteParameters implements AgentAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 599262534378620154L;
	private List parameters; // list of lists of Options

	public void setParameters(List parameters) {
		this.parameters = parameters;
	}

	public List getParameters() {
		return parameters;
	}
}
