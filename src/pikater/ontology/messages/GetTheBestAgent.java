package pikater.ontology.messages;

import jade.content.onto.basic.Action;

public class GetTheBestAgent extends Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5959112027209211332L;
	private String _nearest_file_name;

	public void setNearest_file_name(String _nearest_file_name) {
		this._nearest_file_name = _nearest_file_name;
	}

	public String getNearest_file_name() {
		return _nearest_file_name;
	}

}
