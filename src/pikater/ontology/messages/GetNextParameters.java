package pikater.ontology.messages;

import jade.content.onto.basic.Action;
import jade.util.leap.List;

public class GetNextParameters extends Action{

	private static final long serialVersionUID = -4554163588726699351L;
	
	private List options; // List of Options
	private List search_options;
	
	public List getOptions() {
		return options;
	}
	public void setOptions(List options) {
		this.options = options;
	}
	public List getSearch_options() {
		return search_options;
	}
	public void setSearch_options(List search_options) {
		this.search_options = search_options;
	}

}
