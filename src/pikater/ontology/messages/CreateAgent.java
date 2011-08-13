package pikater.ontology.messages;

import jade.content.onto.basic.Action;
import jade.util.leap.List;

public class CreateAgent extends Action{

	private static final long serialVersionUID = -5584350622664317441L;
	
	private String type;
	private String name;
	private List arguments;
	
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setArguments(List arguments) {
		this.arguments = arguments;
	}
	public List getArguments() {
		return arguments;
	}
}
