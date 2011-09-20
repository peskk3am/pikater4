package pikater.ontology.messages;

import jade.content.AgentAction;
import jade.content.Concept;

public class Compute implements AgentAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4964195780078009343L;
	private Computation _computation;

	// Methods required to use this class to represent the TASK role
	public void setComputation(Computation computation) {
		_computation = computation;
	}

	public Computation getComputation() {
		return _computation;
	}

}
