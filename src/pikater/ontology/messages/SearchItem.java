package pikater.ontology.messages;

import java.util.Random;

import jade.content.Concept;
import jade.util.leap.List;

public abstract class SearchItem implements Concept {
	/**
	 * Item in solution-schema
	 */
	private static final long serialVersionUID = 3249399049389780447L;
	private Integer number_of_values_to_try;
	//Create random solution item
	public abstract String randomValue(Random rnd_gen);
	//Returns all possible values from this schema
	public abstract List possibleValues();
	public Integer getNumber_of_values_to_try() {
		return number_of_values_to_try;
	}
	public void setNumber_of_values_to_try(Integer numberOfValuesToTry) {
		number_of_values_to_try = numberOfValuesToTry;
	}
}
