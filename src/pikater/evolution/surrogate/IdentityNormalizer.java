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
public class IdentityNormalizer extends ModelInputNormalizer {

    @Override
    public double normalizeFloat(String dbl, FloatSItem schema) {
        return Float.parseFloat(dbl);
    }

    @Override
    public double normalizeInt(String n, IntSItem schema) {
        return Integer.parseInt(n);
    }
    
}
