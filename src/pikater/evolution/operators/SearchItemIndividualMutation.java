package pikater.evolution.operators;

import pikater.evolution.Population;
import pikater.evolution.RandomNumberGenerator;
import pikater.evolution.individuals.SearchItemIndividual;
import pikater.ontology.messages.SearchItem;

/**
 *
 * @author Martin Pilat
 */
public class SearchItemIndividualMutation implements Operator {

    double mutationProbability;
    double geneChangeProbability;
    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    public SearchItemIndividualMutation(double mutationProbability, double geneChangeProbability) {
        this.mutationProbability = mutationProbability;
        this.geneChangeProbability = geneChangeProbability;
    }
    
    @Override
    public void operate(Population parents, Population offspring) {
        
        int size = parents.getPopulationSize();

        for (int i = 0; i < size; i++) {

             SearchItemIndividual p1 = (SearchItemIndividual) parents.get(i);
             SearchItemIndividual o1 = (SearchItemIndividual) p1.clone();

             if (rng.nextDouble() < mutationProbability) {
                 for (int j = 0; j < o1.length(); j++) {
                     if (rng.nextDouble() < geneChangeProbability) {
                         o1.set(j, p1.getSchema(j).randomValue(rng.getRandom()));
                     }
                 }
             }

             offspring.add(o1);
        }
        
        
        
    }
    
}
