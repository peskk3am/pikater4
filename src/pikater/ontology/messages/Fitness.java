package pikater.ontology.messages;

import jade.content.Concept;
import jade.util.leap.List;

public class Fitness implements Concept {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1513294320231705295L;
	private SearchSolution solution;//evaluated solution
	private List fitnessValues;//its evaluation - list of floats
	public SearchSolution getSolution() {
		return solution;
	}
	public void setSolution(SearchSolution solution) {
		this.solution = solution;
	}
	public List getFitnessValues() {
		return fitnessValues;
	}
	public void setFitnessValues(List fitnessValues) {
		this.fitnessValues = fitnessValues;
	}
	
}
