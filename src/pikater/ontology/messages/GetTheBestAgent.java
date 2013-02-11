package pikater.ontology.messages;

import jade.content.AgentAction;
import jade.content.onto.basic.Action;

public class GetTheBestAgent implements AgentAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5959112027209211332L;
	private String _nearest_file_name;
        private int numberOfAgents;

    public int getNumberOfAgents() {
        return numberOfAgents;
    }

    public void setNumberOfAgents(int numberOfAgents) {
        this.numberOfAgents = numberOfAgents;
    }

	public void setNearest_file_name(String _nearest_file_name) {
		this._nearest_file_name = _nearest_file_name;
	}

	public String getNearest_file_name() {
		return _nearest_file_name;
	}

}