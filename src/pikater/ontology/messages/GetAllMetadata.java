package pikater.ontology.messages;

import jade.content.AgentAction;
import jade.content.onto.basic.Action;
import jade.util.leap.List;

public class GetAllMetadata implements AgentAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5223603055736238437L;
	private List exceptions;
	private boolean results_required;
	
	public List getExceptions() {
		return exceptions;
	}
	public void setExceptions(List exceptions) {
		this.exceptions = exceptions;
	}
	public boolean getResults_required() {
		return results_required;
	}
	public void setResults_required(boolean results_required) {
		this.results_required = results_required;
	}
	

}
