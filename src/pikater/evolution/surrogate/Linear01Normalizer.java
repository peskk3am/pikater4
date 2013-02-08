/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pikater.evolution.surrogate;

import pikater.ontology.messages.FloatSItem;
import pikater.ontology.messages.IntSItem;

/**
 *
 * @author Martin Pilat
 */
public class Linear01Normalizer extends ModelInputNormalizer {

    @Override
    public double normalizeFloat(String dbl, FloatSItem schema) {
        double range = schema.getMax() - schema.getMin();
        double val = Double.parseDouble(dbl);
        val -= schema.getMin();
        return val/range;
        
    }

    @Override
    public double normalizeInt(String n, IntSItem schema) {
        double range = schema.getMax() - schema.getMin();
        double val = Integer.parseInt(n);
        val -= schema.getMin();
        return val/range;
    }
    
}
