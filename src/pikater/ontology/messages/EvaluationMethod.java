package pikater.ontology.messages;

import jade.content.Concept;
import jade.util.leap.List;

public class EvaluationMethod implements Concept {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9024769565945696142L;
	private String _name;
	private List options;

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setOptions(List options) {
		this.options = options;
	}

	public List getOptions() {
		return options;
	}
}
