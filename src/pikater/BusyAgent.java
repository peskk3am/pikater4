package pikater;

import jade.core.AID;

public class BusyAgent {
	private AID aid;
	private String task_id;
	
	public BusyAgent(AID _aid, String _task_id){
		aid = _aid;
		task_id = _task_id;
	}

	public AID getAid() {
		return aid;
	}

	public void setAid(AID aid) {
		this.aid = aid;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
}
