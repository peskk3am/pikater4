package pikater.ontology.messages;

import jade.content.Concept;

public class Task implements Concept, Cloneable {
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
	
	private String _save_mode = null;  // if not null -> save the agent
								//    message (agent is sent in the message with the results)
								//    file (agent is stored in the file by agentManager) --> TODO database

	private String _get_results;
	private String _gui_agent;
	private boolean _save_results;
	
	private int userID;
	private String start;
	private String finish;

	private String _problem_name;
	private String _note;
	private EvaluationMethod _evaluation_method;

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
	
	public void setProblem_name(String _problem_name) {
		this._problem_name = _problem_name;
	}

	public String getProblem_name() {
		return _problem_name;
	}

	public void setNote(String _note) {
		this._note = _note;
	}

	public String getNote() {
		return _note;
	}
	
	public void setEvaluation_method(EvaluationMethod _evaluation_method) {
		this._evaluation_method = _evaluation_method;
	}
	
	public EvaluationMethod getEvaluation_method() {
		return _evaluation_method;
	}
	
    public Object clone() {
        
        Task task = new Task();
    	task.setId(this._id);
    	task.setComputation_id(this._computation_id);
    	task.setProblem_id(this._problem_id);
    	task.setResult(this._result);
    	task.setAgent(this._agent);
    	task.setData(this._data);    	
    	task.setSave_mode(this._save_mode);
    	task.setGet_results(this._get_results);
    	task.setGui_agent(this._gui_agent);
    	task.setSave_results(this._save_results);
    	task.setUserID(this.userID);
    	task.setStart(this.start);
    	task.setFinish(this.finish);
    	task.setEvaluation_method(this._evaluation_method);

        return task;
    }

}
