package pikater.evolution.operators;

import pikater.evolution.Population;
import pikater.evolution.RandomNumberGenerator;
import pikater.evolution.individuals.SearchItemIndividual;
import pikater.ontology.messages.FloatSItem;
import pikater.ontology.messages.IntSItem;
import pikater.ontology.messages.SearchItem;
import pikater.ontology.messages.SetSItem;

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
                         if (o1.getSchema(j) instanceof SetSItem) {
                            o1.set(j, p1.getSchema(j).randomValue(rng.getRandom()));
                         }
                         if (o1.getSchema(j) instanceof FloatSItem) {
                             FloatSItem fs = (FloatSItem)o1.getSchema(j);
                             float val = Float.parseFloat(o1.get(j));
                             val += 0.3*(fs.getMax()-fs.getMin())*rng.nextGaussian();
                             val = Math.min(val, fs.getMax());
                             val = Math.max(val, fs.getMin());
                             o1.set(j, String.valueOf(val));
                         }
                         if (o1.getSchema(j) instanceof IntSItem) {
                             IntSItem fs = (IntSItem)o1.getSchema(j);
                             int val = Integer.parseInt(o1.get(j));
                             val += 0.3*(fs.getMax()-fs.getMin())*rng.nextGaussian();
                             val = Math.min(val, fs.getMax());
                             val = Math.max(val, fs.getMin());
                             o1.set(j, String.valueOf(val));
                         }
                     }
                 }
             }

             offspring.add(o1);
        }
        
        
        
    }
    
}
