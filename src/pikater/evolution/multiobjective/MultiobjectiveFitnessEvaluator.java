package pikater.evolution.multiobjective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import pikater.evolution.FitnessEvaluator;
import pikater.evolution.individuals.Individual;
import pikater.evolution.individuals.MultiobjectiveIndividual;

public abstract class MultiobjectiveFitnessEvaluator implements FitnessEvaluator {

    private static final long serialVersionUID = 8773243530620828997L;

    /**
     *
     * @param pop Population from which the non-dominated front is chosen
     * @return List of Individuals in current non-dominated front
     */
    public List<Individual> getNonDominatedFront(List<Individual> population) {
        ArrayList<Individual> front = new ArrayList<Individual>();
        front.add(population.get(0));

        for (Individual p : population) {
            if (front.contains(p)) {
                continue;
            }
            front.add(p);
            ArrayList<Individual> toRemove = new ArrayList<Individual>();
            for (Individual q : front) {
                if (q.equals(p)) {
                    continue;
                }
                if (toRemove.contains(q)) {
                    continue;
                }
                if (dominates(q, p)) {
                    toRemove.add(p);
                } else if (dominates(p, q)) {
                    toRemove.add(q);
                }
            }
            front.removeAll(toRemove);
        }
        return front;
    }

    protected List<List<Individual>> fastNonDominatedSort(List<Individual> pop) {
        List<List<Individual>> fronts = new ArrayList<List<Individual>>();
        List<Individual> population = new ArrayList<Individual>();
        population.addAll(pop);
        while (population.size() > 0) {
            List<Individual> front = getNonDominatedFront(population);
            fronts.add(front);
            population.removeAll(front);
        }
        return fronts;
    }

    protected void crowdingDistanceAssignment(List<Individual> front) {
        ArrayList<MultiobjectiveIndividual> mi = new ArrayList<MultiobjectiveIndividual>();
        for (Individual ind : front) {
            mi.add((MultiobjectiveIndividual) ind);
        }
        int l = front.size();
        for (Individual i : front) {
            i.setFitnessValue(0.0);
        }

        for (int j = 0; j < mi.get(0).getObjectives().length; j++) {
            Collections.sort(mi, new ObjectiveValueComparator(j));
            mi.get(0).setFitnessValue(Double.POSITIVE_INFINITY);
            mi.get(l - 1).setFitnessValue(Double.POSITIVE_INFINITY);
            for (int i = 1; i < l - 1; i++) {
                double dist = mi.get(i).getFitnessValue();
                dist += mi.get(i + 1).getObjectives()[j];
                dist -= mi.get(i - 1).getObjectives()[j];
                front.get(i).setFitnessValue(dist);
            }
        }
        Collections.sort(front, new FitnessValueComparator());
        double diff = 0.5 / front.size();
        double fit = 1.0;
        for (Individual chrom : front) {
            chrom.setFitnessValue(fit);
            fit -= diff;
        }
    }

    /**
     * Compares two Individuals for domination
     *
     * @param a first Individual to compare
     * @param b second Individual to compare
     * @return true if Individual a dominates Individual b
     */
    public boolean dominates(Individual a, Individual b) {
        MultiobjectiveIndividual i1 = (MultiobjectiveIndividual) a;
        MultiobjectiveIndividual i2 = (MultiobjectiveIndividual) b;
        int dom_count = 0;
        for (int i = 0; i < i1.getObjectives().length; i++) {
            if (i1.getObjectives()[i] <= i2.getObjectives()[i]) {
                dom_count++;
            }
        }
        if (dom_count == i1.getObjectives().length) {
            return true;
        }
        return false;
    }

    protected class FitnessValueComparator implements Comparator<Individual> {

        @Override
        public int compare(Individual o1, Individual o2) {
            return (int) Math.signum(o2.getFitnessValue() - o1.getFitnessValue());
        }
    }
}