package pikater;

import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import java.util.Random;
import pikater.evolution.MergingReplacement;
import pikater.evolution.Population;
import pikater.evolution.Replacement;
import pikater.evolution.SGAReplacement;
import pikater.evolution.individuals.Individual;
import pikater.evolution.individuals.SearchItemIndividual;
import pikater.evolution.operators.OnePtXOver;
import pikater.evolution.operators.Operator;
import pikater.evolution.operators.SearchItemIndividualMutation;
import pikater.evolution.selectors.Selector;
import pikater.evolution.selectors.TournamentSelector;
import pikater.ontology.messages.Option;
import pikater.ontology.messages.SearchItem;
import pikater.ontology.messages.SearchSolution;

public class Agent_EASearch extends Agent_Search {
    /*
     * Implementation of Genetic algorithm search
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
     * 
     * -K string
     * Type of the surrogate model (default linear regression)
     */

    //fitness is the error rate - the lower, the better!
    Population parents;
    Population offspring;
    Replacement replacement = new MergingReplacement();
    java.util.ArrayList<Selector> environmentalSelectors;
    java.util.ArrayList<Selector> matingSelectors;
    java.util.ArrayList<Operator> operators;
    double eliteSize = 0.1;
    int pop_size = 10;
    double mut_prob = 0.0;
    double xover_prob = 0.0;
    private int number_of_generations = 0;
    private double best_error_rate = Double.MAX_VALUE;
    private String modelType = "weka.classifiers.functions.LinearRegression";
    private int maximum_generations = 5;
    private double final_error_rate = 0.1;
    int tournament_size = 2;
    protected Random rnd_gen = new Random(1);
    /**
     *
     */
    private static final long serialVersionUID = -387458001824777077L;

    @Override
    protected List generateNewSolutions(List solutions, float[][] evaluations) {

        offspring = new Population();
        offspring.setPopulationSize(pop_size);
        
        number_of_generations++;
        
        if (evaluations == null) {
            //create new population
            
            matingSelectors = new java.util.ArrayList<Selector>();
            environmentalSelectors = new java.util.ArrayList<Selector>();
            operators = new java.util.ArrayList<Operator>();
            
            environmentalSelectors.add(new TournamentSelector());
            operators.add(new OnePtXOver(0.8));
            operators.add(new SearchItemIndividualMutation(0.5, 1.0));
            
            parents = new Population();
            parents.setPopulationSize(pop_size);
            
            List schema = getSchema();
            
            SearchItemIndividual sampleIndividual = new SearchItemIndividual(schema.size());
            Iterator it = schema.iterator();
            for (int i = 0; it.hasNext(); i++) {
                sampleIndividual.set(i, "");
                sampleIndividual.setSchema(i, (SearchItem)it.next());
            }
            
            parents.setSampleIndividual(sampleIndividual);
            
            number_of_generations = 0;
            best_error_rate = Double.MAX_VALUE;
            parents.createRandomInitialPopulation();
            
            return populationToList(parents);
        }
        
        Population matingPool = new Population();

        if (matingSelectors.size() > 0) {
            int mateSel = matingSelectors.size();
            int toSelect = parents.getPopulationSize()/mateSel;
            for (int i = 0; i < matingSelectors.size(); i++) {
                Population sel = new Population();
                matingSelectors.get(i).select(toSelect, parents, sel);
                matingPool.addAll((Population)sel.clone());
            }

            int missing = parents.getPopulationSize() - matingPool.getPopulationSize();
            if (missing > 0) {
                Population sel = new Population();
                matingSelectors.get(matingSelectors.size()-1).select(toSelect, parents, sel);
                matingPool.addAll((Population)sel.clone());
            }
        } else
        {
            matingPool = (Population)parents.clone();
        }
        
        offspring = null;
        for (Operator o : operators) {
            offspring = new Population();
            o.operate(matingPool, offspring);
            matingPool = offspring;
        }
        
        return populationToList(offspring);
        
    }

    private List populationToList(Population pop) {
        
        List ret = new ArrayList();
        for (Individual i : pop.getSortedIndividuals()) {
            SearchItemIndividual si = (SearchItemIndividual)i;
            ArrayList vals = new ArrayList();
            
            for (int j = 0; j < si.length(); j++) {
                vals.add(si.get(j).toString());
            }
            
            SearchSolution ss = new SearchSolution();
            ss.setValues(vals);
            ret.add(ss);
        }
        return ret;
    }
    
    @Override
    protected void updateFinished(float[][] evaluations) {
        //assign evaluations to the population as fitnesses		
        if (evaluations == null) {
            for (int i = 0; i < pop_size; i++) {
                offspring.get(i).setFitnessValue(1);
            }
            return;
        }
        
        //initial generation -- evaluate the random population
        if (number_of_generations == 0) {
            for (int i = 0; i < evaluations.length; i++) {
                parents.get(i).setFitnessValue(-evaluations[i][0]);
                if (evaluations[i][0] < best_error_rate) {
                    best_error_rate = evaluations[i][0];
                }
            }
            return;
        }
        
        for (int i = 0; i < evaluations.length; i++) {
            offspring.get(i).setFitnessValue(-evaluations[i][0]);
            if (evaluations[i][0] < best_error_rate) {
                best_error_rate = evaluations[i][0];
            }
        }
        
        Population selected = new Population();

        java.util.ArrayList<Individual> sortedOld = parents.getSortedIndividuals();
        for (int i = 0; i < eliteSize*parents.getPopulationSize(); i++) {
            selected.add(sortedOld.get(i));
        }
        
        Population combined = replacement.replace(parents, offspring);
        
        System.err.println("COMBINED: " + combined.getPopulationSize());
        
        int envSel = environmentalSelectors.size();
        int toSelect = (parents.getPopulationSize() - selected.getPopulationSize())/envSel;
        for (int i = 0; i < environmentalSelectors.size(); i++) {
            Population sel = new Population();
            environmentalSelectors.get(i).select(toSelect, combined, sel);
            selected.addAll((Population)sel.clone());
        }

        int missing = parents.getPopulationSize() - selected.getPopulationSize();
        if (missing > 0) {
            Population sel = new Population();
            environmentalSelectors.get(environmentalSelectors.size() - 1).select(toSelect, combined, sel);
            selected.addAll((Population)sel.clone());
        }

        parents.clear();
        parents.addAll(selected);
        
        System.err.println("PARENTS: " + parents.getPopulationSize());

    }

    @Override
    protected boolean finished() {
        //number of generations, best error rate
        
        System.err.println("FINISHED_GEN: " + number_of_generations + "/" + maximum_generations);
        System.err.println("FINISHED_ERR: " + best_error_rate + "/" + final_error_rate);
        
        if (number_of_generations >= maximum_generations) {
            return true;
        }

        if (best_error_rate <= final_error_rate) {
            return true;
        }
        return false;

    }

    @Override
    protected String getAgentType() {
        return "EASearch";
    }

    @Override
    protected void loadSearchOptions() {
        pop_size = 10;
        mut_prob = 0.2;
        xover_prob = 0.5;
        maximum_generations = 5;
        final_error_rate = 0.02;
        tournament_size = 2;
        List search_options = getSearch_options();
        // find maximum tries in Options
        Iterator itr = search_options.iterator();
        while (itr.hasNext()) {
            Option next = (Option) itr.next();
            if (next.getName().equals("E")) {
                final_error_rate = Float.parseFloat(next.getValue());
            }
            if (next.getName().equals("M")) {
                maximum_generations = Integer.parseInt(next.getValue());
            }
            if (next.getName().equals("T")) {
                mut_prob = Float.parseFloat(next.getValue());
            }
            if (next.getName().equals("X")) {
                xover_prob = Float.parseFloat(next.getValue());
            }
            if (next.getName().equals("P")) {
                pop_size = Integer.parseInt(next.getValue());
            }
            if (next.getName().equals("S")) {
                tournament_size = Integer.parseInt(next.getValue());
            }
            if (next.getName().equals("K")) {
                modelType = next.getValue();
            }
        }
        query_block_size = pop_size;

    }

}
