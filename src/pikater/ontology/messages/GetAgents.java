package pikater.ontology.messages;

import jade.content.AgentAction;
import jade.content.onto.basic.Action;

public class GetAgents implements AgentAction {

	private static final long serialVersionUID = -3423064460137052507L;
	private Agent agent;
    private int number;
    private Id task_id;
	
    public Agent getAgent() {
		return agent;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public Id getTask_id() {
		return task_id;
	}
	public void setTask_id(Id task_id) {
		this.task_id = task_id;
	}
}