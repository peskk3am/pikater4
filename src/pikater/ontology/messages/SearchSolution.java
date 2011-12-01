package pikater.ontology.messages;

import jade.content.Concept;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;


public class SearchSolution implements Concept {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5183991490097709263L;
	private List values;//list of string-values

	public List getValues() {
		if(values!=null)
			return values;
		return new ArrayList();
	}

	public void setValues(List values) {
		this.values = values;
	}
	
	public void printContent(){
		Iterator itr = getValues().iterator();
		boolean start = true;
		while(itr.hasNext()){
			if(!start)
				System.out.print(",");
			System.out.print(itr.next().toString());
			start = false;
		}
	}
}
