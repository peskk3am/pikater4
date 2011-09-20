package pikater.ontology.messages;

import jade.content.Concept;

public class Task implements Concept {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8242598855481511427L;
	private String _id;
	private String _computation_id;
	private String _problem_id;
	private Evaluation _result;
	private Agent _agent;
	private Data _data;
	
	private String _save_mode = "message";  // if not null -> save the agent
								//    message (agent is sent in the message with the results)
								//    file (agent is stored in the file by agentManager) --> TODO database

	private String _get_results;
	private String _gui_agent;
	private boolean _save_results;
	
	private int userID;
	private String start;
	private String finish;

	
	public void setAgent(Agent agent) {
		_agent=agent;
	}
	public Agent getAgent() {
		return _agent;
	}
	public void setData(Data data) {
		_data=data;
	}
	public Data getData() {
		return _data;
	}
	public void setId(String id) {
		_id=id;
	}
	public String getId() {
		return _id;
	}
	public void setComputation_id(String computation_id) {
		_computation_id=computation_id;
	}
	public String getComputation_id() {
		return _computation_id;
	}
	public void setProblem_id(String problem_id) {
		_problem_id=problem_id;
	}
	public String getProblem_id() {
		return _problem_id;
	}
	public void setResult(Evaluation result) {
		_result = result;
	}
	public Evaluation getResult() {
		return _result;
	}	

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getUserID() {
		return userID;
	}

	public void setSave_mode(String _save_mode) {
		this._save_mode = _save_mode;
	}

	public String getSave_mode() {
		return _save_mode;
	}
	public void setGet_results(String _get_results) {
		this._get_results = _get_results;
	}
	public String getGet_results() {
		return _get_results;
	}
	public void setGui_agent(String _gui_agent) {
		this._gui_agent = _gui_agent;
	}
	public String getGui_agent() {
		return _gui_agent;
	}
	public void setSave_results(boolean _save_results) {
		this._save_results = _save_results;
	}
	public boolean getSave_results() {
		return _save_results;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getStart() {
		return start;
	}
	public void setFinish(String finish) {
		this.finish = finish;
	}
	public String getFinish() {
		return finish;
	}

}
