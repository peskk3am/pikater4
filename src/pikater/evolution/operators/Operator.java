package pikater.evolution.operators;

import pikater.evolution.Population;

/**
 *
 * @author Martin Pilat
 */
public interface Operator {

    public void operate(Population parents, Population offspring);

}
