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
public abstract class ModelInputNormalizer {
    
    public abstract double normalizeFloat(String dbl, FloatSItem schema);
    public abstract double normalizeInt(String n, IntSItem schema);
    
}
