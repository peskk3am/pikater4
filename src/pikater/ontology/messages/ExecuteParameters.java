package pikater.ontology.messages;

import jade.content.AgentAction;
import jade.util.leap.List;

public class ExecuteParameters implements AgentAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 599262534378620154L;
	private List solutions; // list of lists of Options

	public void setSolutions(List solutions) {
		this.solutions = solutions;
	}

	public List getSolutions() {
		return solutions;
	}
}
