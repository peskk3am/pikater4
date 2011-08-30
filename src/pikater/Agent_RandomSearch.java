package pikater;

import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;

import pikater.ontology.messages.Evaluation;
import pikater.ontology.messages.Option;
import pikater.ontology.messages.Options;

public class Agent_RandomSearch extends Agent_MutationSearch {

	private static final long serialVersionUID = 2777277001533605329L;

	private int number_of_tries = 0;
	private float error_rate = 1;
	
	private int maximum_tries;
	private float final_error_rate;

	@Override
	protected void loadSearchOptions(){
		final_error_rate = (float) 0.1;
		maximum_tries = 10;
		List search_options = getSearch_options();
		// find maximum tries in Options
		Iterator itr = search_options.iterator();
		while (itr.hasNext()) {
			Option next = (Option) itr.next();
			if (next.getName().equals("E")){
				final_error_rate = Float.parseFloat(next.getValue()); 
			}
			if (next.getName().equals("M")){
				maximum_tries = Integer.parseInt(next.getValue()); 
			}
		}
		System.out.println(getLocalName()+" parameters are: ");
		System.out.println("   final_error_rate: " + final_error_rate);
		System.out.println("   maximum_tries: " + maximum_tries);		
	}
	
	@Override
	protected String getAgentType() {
		return "RandomSearch";
	}

	@Override
	protected boolean finished() {
		System.out.println("finished() error_rate: "+ error_rate + " final_error_rate: "+ final_error_rate);

		if (number_of_tries >= maximum_tries) {
			return true;
		}

		if (error_rate < final_error_rate) {
			return true;
		}
		return false;
	}

	@Override
	protected void updateFinished(List evaluations) {
		if (evaluations == null){
			error_rate = 1;
		}
		else{
			error_rate = ((Evaluation)(evaluations.get(0))).getError_rate();
			System.out.println("changing error_rate to: " + error_rate);
		}
	}
		
	@Override
	protected List generateNewOptions(List options, List evaluations) {
		// go through the Options Vector, generate random values
		List new_options = new ArrayList();
		Iterator itr = getOptions().iterator();
		while (itr.hasNext()) {
			Option opt = ((Option) itr.next()).copyOption();
			opt.setValue(randomOptValue(opt));
			new_options.add(opt);
		}		
		
		number_of_tries++;
		
		List options_list = new ArrayList();
		options_list.add(new Options(new_options));
		return options_list;
	}

}