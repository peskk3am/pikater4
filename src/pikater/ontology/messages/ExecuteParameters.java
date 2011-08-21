package pikater.ontology.messages;

import jade.content.onto.basic.Action;
import jade.util.leap.List;

public class ExecuteParameters extends Action{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7419779148077301449L;
	
	private List parameters; // list of lists of Options

	public void setParameters(List parameters) {
		this.parameters = parameters;
	}

	public List getParameters() {
		return parameters;
	}
}
