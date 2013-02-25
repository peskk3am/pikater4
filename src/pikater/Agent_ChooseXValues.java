package pikater;

import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;

import pikater.ontology.messages.Option;
import pikater.ontology.messages.SearchItem;
import pikater.ontology.messages.SearchSolution;

public class Agent_ChooseXValues extends Agent_Search {
	/*
	 * Implementation of simple tabulation of solutions 
	 */
	private static final long serialVersionUID = 838429530327268572L;
	private int n = Integer.MAX_VALUE;
	private int ni = 0;
	private int default_number_of_values_to_try = 5;

	private List solutions_list ;
	//private Vector<String> sub_options_vector ;

	@Override
	protected boolean finished() {
		if (ni < n) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected String getAgentType() {
		return "ChooseXValues";
	}
	
	//TODO: Something less recursive
	private void generate(List cur_solution_part, List possible_solution_values, int beg_ind) {
		if (possible_solution_values.size()-beg_ind < 1) {//if we are at the end
			SearchSolution s= new SearchSolution();//then solution part is whole solution
			List vals = new ArrayList();
			Iterator itr = cur_solution_part.iterator();
			while(itr.hasNext()){
				vals.add(itr.next());
			}
			s.setValues(vals);      
			solutions_list.add(s);
			return;
		}
		List pos_vals = (List)possible_solution_values.get(beg_ind);
		for (int i = 0; i < pos_vals.size(); i++) {//For each possible value on the index beg_ind
			cur_solution_part.add(pos_vals.get(i));//append the value to the part of the solution
			
			generate(cur_solution_part,	possible_solution_values, beg_ind+1);//recursion
			cur_solution_part.remove(cur_solution_part.size()-1);//undo append
		}
	}


	private void generateSolutions_list(List Schema) {
		List possible_solutions = new ArrayList();
		Iterator itr = Schema.iterator();
		while (itr.hasNext()) {
			SearchItem next = (SearchItem) itr.next();			
			if (next.getNumber_of_values_to_try() == 0){
				next.setNumber_of_values_to_try(default_number_of_values_to_try);
			}
			possible_solutions.add(next.possibleValues());
		}
		generate(new ArrayList(), possible_solutions,0);
		n = solutions_list.size();
	}

	@Override
	protected List generateNewSolutions(List solutions, float[][] evaluations) {
		
		if (n == 0)
			return null;
		/*SearchSolution new_solution = (SearchSolution)solutions_list.get(ni++);
		new ArrayList();
		res_solutions.add(new_solution);*/
		ni+=n;
		return solutions_list;
	}


	@Override
	protected void loadSearchOptions() {
		List search_options = getSearch_options();
		Iterator itr = search_options.iterator();
		while (itr.hasNext()) {
			Option next = (Option) itr.next();
			
			if (next.getName().equals("N")){
				default_number_of_values_to_try = Integer.parseInt(next.getValue());
			}
		}
		List Schema =getSchema();
		n = Integer.MAX_VALUE;
		ni = 0;
		solutions_list = new ArrayList();
		generateSolutions_list(Schema);
		query_block_size = n;
	}

	@Override
	protected void updateFinished(float[][] evaluations) {
		//???
	}
}
