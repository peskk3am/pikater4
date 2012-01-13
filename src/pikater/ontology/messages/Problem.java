package pikater.ontology.messages;

import jade.content.Concept;
import jade.util.leap.List;

public class Problem implements Concept {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7185046750972524624L;
	private String _id;
	private String _gui_id;
	private boolean _sent;
	private List _agents;
	private List _data;
	private int _timeout;
	private Agent _method;
	private String _start;
	private String _get_results; // after_each_computation, after_each_task
	private String _gui_agent;
	private boolean _save_results;
	private String _name;

	public void setAgents(List agents) {
		_agents = agents;
	}

	public List getAgents() {
		return _agents;
	}

	public void setData(List data) {
		_data = data;
	}

	public List getData() {
		return _data;
	}

	public void setId(String id) {
		_id = id;
	}

	public String getId() {
		return _id;
	}

	public void setGui_id(String gui_id) {
		_gui_id = gui_id;
	}

	public String getGui_id() {
		return _gui_id;
	}

	public void setTimeout(int timeout) {
		_timeout = timeout;
	}

	public int getTimeout() {
		return _timeout;
	}

	public boolean getSent() {
		return _sent;
	}

	public void setSent(boolean sent) {
		_sent = sent;
	}

	public Agent getMethod() {
		return _method;
	}

	public void setMethod(Agent method) {
		_method = method;
	}

	public void setStart(String _start) {
		this._start = _start;
	}

	public String getStart() {
		return _start;
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

	public void setName(String _name) {
		this._name = _name;
	}

	public String getName() {
		return _name;
	}
	
}