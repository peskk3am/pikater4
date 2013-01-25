package pikater.evolution.selectors;

import pikater.evolution.Population;
import pikater.evolution.RandomNumberGenerator;
import pikater.evolution.individuals.Individual;
import pikater.evolution.individuals.SearchItemIndividual;

/**
 *
 * @author Martin Pilat
 */
public class TwoObjectiveTournament implements Selector{

    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    @Override
    public void select(int howMany, Population from, Population to) {

        for (int i = 0; i < howMany; i++) {
            int i1 = rng.nextInt(from.getPopulationSize());
            int i2 = rng.nextInt(from.getPopulationSize());
            
            float[] o1 = ((SearchItemIndividual)from.get(i1)).getObjectives();
            float[] o2 = ((SearchItemIndividual)from.get(i2)).getObjectives();

            if ((((o1[0] > o2[0]) || (o1[0] == o2[0] && o1[1] < o2[1]))) && rng.nextDouble() < 0.8) {
                to.add((Individual)from.get(i1).clone());
            }
            else {
                to.add((Individual)from.get(i2).clone());
            }
        }
    }

    

}
