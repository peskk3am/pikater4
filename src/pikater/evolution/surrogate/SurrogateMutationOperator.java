package pikater.evolution.surrogate;

import java.util.logging.Level;
import java.util.logging.Logger;
import pikater.evolution.Population;
import pikater.evolution.operators.Operator;
import weka.classifiers.functions.SMOreg;
import weka.core.Instances;

/**
 *
 * @author Martin Pilat
 */
public class SurrogateMutationOperator implements Operator {
    
    SearchItemIndividualArchive archive;
    
    public SurrogateMutationOperator(SearchItemIndividualArchive archive) {
        this.archive = archive;
    }

    @Override
    public void operate(Population parents, Population offspring) {
        try {
            Instances train = archive.getWekaDataSet();
            SMOreg smo = new SMOreg();
            smo.buildClassifier(train);
            
            offspring.addAll(parents);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
        
    }
}
