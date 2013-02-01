/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pikater.evolution.surrogate;

import pikater.evolution.FitnessFunction;
import pikater.evolution.individuals.Individual;
import pikater.evolution.individuals.SearchItemIndividual;
import weka.classifiers.Classifier;
import weka.core.Instance;

/**
 *
 * @author Martin Pilat
 */
public class SurrogateFitnessFunction implements FitnessFunction {

    Classifier surrogate;
    ModelInputNormalizer norm;
    
    public SurrogateFitnessFunction(Classifier surrogate, ModelInputNormalizer norm)  {
        this.surrogate = surrogate;
        this.norm = norm;
    }

    public void setSurrogate(Classifier surrogate) {
        this.surrogate = surrogate;
    }
    
    @Override
    public double evaluate(Individual ind) {
        SearchItemIndividual si = (SearchItemIndividual)ind;
        Instance in = si.toWekaInstance(norm);
        
        try {
            return surrogate.classifyInstance(in);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return 0.0;
    }
    
    
}
