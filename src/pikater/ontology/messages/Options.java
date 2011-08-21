package pikater.ontology.messages;

import jade.content.Concept;
import jade.util.leap.List;

public class Options implements Concept{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8578686409784032991L;
	private List list;
	
	public void setList(List list) {
		this.list = list;
	}
	public List getList() {
		return list;
	}
	
	public Options(List list){
		setList(list);
	}
	public Options(){
	}
}
