package pikater.evolution.multiobjective;

import java.util.Comparator;
import pikater.evolution.individuals.Individual;
import pikater.evolution.individuals.MultiobjectiveIndividual;

public class ObjectiveValueComparator implements Comparator<Individual> {

    private int mult = 1;
    int idx;

    public ObjectiveValueComparator(int idx) {
        this(idx, false);
    }

    public ObjectiveValueComparator(int idx, boolean reverse) {
        this.idx = idx;
        if (reverse) {
            mult = -1;
        }
    }

    @Override
    public int compare(Individual o1, Individual o2) {
        MultiobjectiveIndividual i1 = (MultiobjectiveIndividual) o1;
        MultiobjectiveIndividual i2 = (MultiobjectiveIndividual) o2;

        return mult * (int) Math.signum(i1.getObjectives()[idx] - i2.getObjectives()[idx]);
    }
}