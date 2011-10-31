package pikater;

import java.util.Random;

import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;

import pikater.ontology.messages.Evaluation;
import pikater.ontology.messages.Option;
import pikater.ontology.messages.SearchItem;
import pikater.ontology.messages.SearchSolution;

public class Agent_RandomSearch extends Agent_Search {

	private static final long serialVersionUID = 2777277001533605329L;

	private int number_of_tries = 0;
	private float error_rate = 1;
	
	private int maximum_tries;
	private float final_error_rate;
	protected Random rnd_gen = new Random(1);

	@Override
	protected void loadSearchOptions(){
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
		if (number_of_tries >= maximum_tries) {
			return true;
		}

		if (error_rate <= final_error_rate) {
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
		}
	}
		
	@Override
	protected List generateNewSolutions(List solutions, List evaluations) {
		// go through the solutions Vector, generate random values
		List new_solution = new ArrayList();
		Iterator itr = getSchema().iterator();
		while (itr.hasNext()) {
			SearchItem si = (SearchItem) itr.next();
			//opt.setValue(randomOptValue(opt));
			new_solution.add(si.randomValue(rnd_gen));
		}		
		
		number_of_tries++;
		
		List solutions_list = new ArrayList();
		SearchSolution sol = new SearchSolution();
		sol.setValues(new_solution);
		solutions_list.add(sol);
		return solutions_list;
	}

}