package pikater;

import java.util.Random;

import pikater.ontology.messages.Evaluation;
import pikater.ontology.messages.Option;
import pikater.ontology.messages.Options;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;

public class Agent_GASearch extends Agent_MutationSearch {
	/*
	 * Implementation of Genetic algorithm option search
	 * Half uniform crossover, tournament selection
	 * Options:
	 * -E float
	 * minimum error rate (default 0.1)
	 * 
	 * -M int 
	 * maximal number of generations (default 10)
	 * 
	 * -T float
	 * Mutation rate (default 0.2)
	 * 
	 * -X float
	 * Crossover probability (default 0.5)
	 * 
	 * -P int
	 * population size (default 5)
	 * 
	 * -S int
	 * Size of tournament in selection (default 2)
	 */
	private Random rnd_gen = null;
	private ArrayList population;
	//fitness is the error rate - the lower, the better!
	float fitnesses[];
	int pop_size = 0;
	double mut_prob = 0.0;
	double xover_prob = 0.0;
	private int number_of_generations = 0;
	private double best_error_rate = Double.MAX_VALUE;
	
	private int maximum_generations;
	private double final_error_rate;
	int tournament_size = 2;

	/**
	 * 
	 */
	private static final long serialVersionUID = -387458001824777077L;
	
	@Override
	protected List generateNewOptions(List options, List evaluations) {
		ArrayList new_population = new ArrayList(pop_size);
		if(evaluations==null){
			//create new population
			rnd_gen = new Random();
			number_of_generations = 0;
			best_error_rate = Double.MAX_VALUE;
			fitnesses = new float[pop_size];
			for(int i = 0; i < pop_size; i++){
				new_population.add(randomIndividual());
			}
		} else{
			//population from the old one
			for(int i = 0; i < (pop_size/2);i++){
				//pairs
				Options ind1 = cloneOpts(selectIndividual());
				Options ind2 = cloneOpts(selectIndividual());
				if(rnd_gen.nextDouble()<xover_prob){
					xoverIndividuals(ind1, ind2);
				}
				mutateIndividual(ind1);
				mutateIndividual(ind2);
				new_population.add(ind1);
				new_population.add(ind2);
			}
			if((pop_size%2)==1){
				//one more, not in pair, if the pop is odd
				Options ind = cloneOpts(selectIndividual());
				mutateIndividual(ind);
				new_population.add(ind);
			}
		}
		number_of_generations++;
		population= new_population;
		return population;
	}

	@Override
	protected void updateFinished(List evaluations) {
		//assign evaluations to the population as fitnesses
		if(evaluations == null){
			for(int i = 0; i < pop_size; i++){
				fitnesses[i]=1;
			}
		}else{
			for(int i = 0; i < evaluations.size(); i++){
				//fitness
				fitnesses[i]=((Evaluation)(evaluations.get(i))).getError_rate();
				//actualize best_error_rate
				if(fitnesses[i]<best_error_rate){
					best_error_rate = fitnesses[i];
				}
			}
		}

	}

	@Override
	protected boolean finished() {
		//number of generation, best error rate
		if (number_of_generations >= maximum_generations) {
			return true;
		}

		if (best_error_rate < final_error_rate) {
			return true;
		}
		return false;

	}

	
	@Override
	protected String getAgentType() {
		return "GASearch";
	}

	@Override
	protected void loadSearchOptions() {
		pop_size = 5;
		mut_prob = 0.2;
		xover_prob = 0.5;
		maximum_generations = 10;
		final_error_rate = 0.1;
		tournament_size = 2;
		List search_options = getSearch_options();
		// find maximum tries in Options
		Iterator itr = search_options.iterator();
		while (itr.hasNext()) {
			Option next = (Option) itr.next();
			if (next.getName().equals("E")){
				final_error_rate = Float.parseFloat(next.getValue()); 
			}
			if (next.getName().equals("M")){
				maximum_generations = Integer.parseInt(next.getValue()); 
			}
			if (next.getName().equals("T")){
				mut_prob = Float.parseFloat(next.getValue()); 
			}
			if (next.getName().equals("X")){
				xover_prob = Float.parseFloat(next.getValue()); 
			}
			if (next.getName().equals("P")){
				pop_size = Integer.parseInt(next.getValue()); 
			}
			if (next.getName().equals("S")){
				tournament_size = Integer.parseInt(next.getValue()); 
			}
		}

	}

	//new random options
	private Options randomIndividual(){
		List new_options = new ArrayList();
		Iterator itr = getOptions().iterator();
		while (itr.hasNext()) {
			Option opt = ((Option) itr.next()).copyOption();
			String opt_val = randomOptValue(opt);
			opt.setValue(opt_val);
			new_options.add(opt);
		}		
		
		return new Options(new_options);
	}
	
	//tournament selection (minimization)
	private Options selectIndividual(){
		float best_fit = Float.MAX_VALUE;
		int best_index = -1;
		for(int i = 0; i < tournament_size; i++){
			int ind= rnd_gen.nextInt(fitnesses.length);
			//MINIMIZATION!!!
			if(fitnesses[ind] < best_fit){
				best_fit = fitnesses[ind];
				best_index = ind;
			}
		}
		return (Options) population.get(best_index);
	}
	
	//Half uniform crossover
	private void xoverIndividuals(Options opts1, Options opts2){
		List new_options1 = new ArrayList();
		List new_options2 = new ArrayList();
		Iterator itr1 = opts1.getList().iterator();
		Iterator itr2 = opts2.getList().iterator();
		while (itr1.hasNext()) {
			Option opt1 = (Option) itr1.next();
			Option opt2 = (Option) itr2.next();
			if(rnd_gen.nextBoolean()){
				//The same...
				new_options1.add(opt1);
				new_options2.add(opt2);
			}else{
				//Gene exchange
				new_options1.add(opt2);
				new_options2.add(opt1);
			}
		}
		opts1.setList(new_options1);
		opts2.setList(new_options2);
	}
	
	//mutation of the option
	private void mutateIndividual(Options opts){
		Iterator itr = opts.getList().iterator();
		while (itr.hasNext()) {
			Option opt = ((Option) itr.next());
			String opt_val = mutateOptValue(opt, mut_prob);
			opt.setValue(opt_val);
		}		
	}
	
	
	//Clone options
	private Options cloneOpts(Options opts){
		List new_options = new ArrayList();
		Iterator itr = getOptions().iterator();
		while (itr.hasNext()) {
			Option o = (Option) itr.next();
			new_options.add(o.copyOption());
		}
		return new Options(new_options);
	}
	

	
}
