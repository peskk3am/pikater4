/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pikater.evolution.surrogate;

import java.util.ArrayList;
import pikater.evolution.individuals.SearchItemIndividual;

/**
 *
 * @author Martin Pilat
 */
public interface ModelValueProvider {
    
    public double getModelValue(SearchItemIndividual si, SearchItemIndividualArchive archive, ModelInputNormalizer norm);
    public void reset();
}
