package pikater.ontology.messages;

import jade.content.AgentAction;
import jade.content.onto.basic.Action;

public class GetMetadata implements AgentAction {

	private static final long serialVersionUID = -8760296402786723483L;
	
	private String external_filename;    
	private String internal_filename;
	
	public String getExternal_filename() {
		return external_filename;
	}
	public void setExternal_filename(String external_filename) {
		this.external_filename = external_filename;
	}
	
	public String getInternal_filename() {
		return internal_filename;
	}
	public void setInternal_filename(String internal_filename) {
		this.internal_filename = internal_filename;
	}    

}