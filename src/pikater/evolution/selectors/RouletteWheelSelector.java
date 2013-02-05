package pikater.evolution.selectors;

import pikater.evolution.Population;
import pikater.evolution.RandomNumberGenerator;
import pikater.evolution.individuals.Individual;

/**
 *
 * @author Martin Pilat
 */
public class RouletteWheelSelector implements Selector{

    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    public void select(int howMany, Population from, Population to) {

        double fitnessSum = 0.0;

        for (int i = 0; i < from.getPopulationSize(); i++) {
            fitnessSum += from.get(i).getFitnessValue();
        }

        double[] fitnesses = new double[from.getPopulationSize()];

        for(int i = 0; i < fitnesses.length; i++) {
            fitnesses[i] = from.get(i).getFitnessValue()/fitnessSum;
        }

        for(int i = 0; i < howMany; i++) {

            double ball = rng.nextDouble();
            double sum = 0;

            for (int j = 0; j < fitnesses.length; j++) {

                sum += fitnesses[j];
                if (sum > ball) {
                    to.add((Individual)from.get(j).clone());
                    break;
                }
            }

        }

    }

}
