package pikater.evolution.surrogate;

import pikater.evolution.EvolutionaryAlgorithm;
import pikater.evolution.MergingReplacement;
import pikater.evolution.Population;
import pikater.evolution.RandomNumberGenerator;
import pikater.evolution.individuals.SearchItemIndividual;
import pikater.evolution.operators.OnePtXOver;
import pikater.evolution.operators.Operator;
import pikater.evolution.operators.SearchItemIndividualMutation;
import pikater.evolution.selectors.TournamentSelector;
import weka.classifiers.functions.GaussianProcesses;
import weka.core.Instances;

/**
 *
 * @author Martin Pilat
 */
public class SurrogateMutationOperator implements Operator {

    SearchItemIndividualArchive archive;
    double mutProbability;

    public SurrogateMutationOperator(SearchItemIndividualArchive archive, double mutProbability) {
        this.archive = archive;
        this.mutProbability = mutProbability;
    }

    @Override
    public void operate(Population parents, Population offspring) {
        try {
            
            if (archive.size() < 50) {
                offspring.addAll(parents);
                return;
            }
            
            Instances train = archive.getWekaDataSet();
            GaussianProcesses smo = new GaussianProcesses();
            smo.buildClassifier(train);

            for (int i = 0; i < parents.getPopulationSize(); i++) {
                if (RandomNumberGenerator.getInstance().nextDouble() > mutProbability) {
                    offspring.add((SearchItemIndividual)parents.get(i).clone());
                    continue;
                }

                Population innerPopulation = new Population();
                for (int j = 0; j < 20; j++) {
                    innerPopulation.add(parents.get(i));
                }

                SearchItemIndividualMutation siim = new SearchItemIndividualMutation(1.0, 0.25, 0.1);
                
                Population perturbed = new Population();
                siim.operate(parents, perturbed);
                    
                innerPopulation.addAll(perturbed);
                innerPopulation.add(parents.get(i));
                innerPopulation.setSampleIndividual((SearchItemIndividual) parents.get(0).clone());

                EvolutionaryAlgorithm ea = new EvolutionaryAlgorithm();

                ea.addOperator(new OnePtXOver(0.8));
                ea.addOperator(new SearchItemIndividualMutation(0.8, 0.5, 0.1));
                ea.addEnvironmentalSelector(new TournamentSelector());
                ea.setReplacement(new MergingReplacement());

                ea.setElite(0.1);
                ea.setFitnessFunction(new SurrogateFitnessFunction(smo));
                
                for (int j = 0; j < 10; j++) {
                    ea.evolve(innerPopulation);
                }
              

                SearchItemIndividual bestIndividual = (SearchItemIndividual)innerPopulation.getSortedIndividuals().get(0);
                offspring.add(bestIndividual);
                System.err.println("SURROGATE_OUT" + bestIndividual.toString() + " " + bestIndividual.getFitnessValue());

            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }



    }
}
