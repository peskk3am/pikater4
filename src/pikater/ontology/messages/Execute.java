package pikater.ontology.messages;

import jade.content.AgentAction;
import jade.content.onto.basic.Action;

public class Execute implements AgentAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7170953913186078035L;
	private Task _task;
	private Agent _method;

	public void setTask(Task task) {
		_task = task;
	}

	public Task getTask() {
		return _task;
	}

	public Agent getMethod() {
		return _method;
	}

	public void setMethod(Agent _method) {
		this._method = _method;
	}

}