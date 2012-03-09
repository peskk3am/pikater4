package pikater.ontology.messages;

import jade.content.AgentAction;
import jade.util.leap.List;

public class GetParameters implements AgentAction{

	private static final long serialVersionUID = -4554163588726699351L;
	
	private List schema; // List of Options
	private List search_options;
	
	public List getSchema() {
		return schema;
	}
	public void setSchema(List schema) {
		this.schema = schema;
	}
	public List getSearch_options() {
		return search_options;
	}
	public void setSearch_options(List search_options) {
		this.search_options = search_options;
	}

}
