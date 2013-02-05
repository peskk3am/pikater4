package pikater.evolution.selectors;

import pikater.evolution.Population;

/**
 *
 * @author Martin Pilat
 */
public interface Selector {

    public void select(int howMany, Population from, Population to);

}
