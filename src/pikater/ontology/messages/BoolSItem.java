package pikater.ontology.messages;

import jade.util.leap.ArrayList;
import jade.util.leap.List;

import java.util.Random;

public class BoolSItem extends SearchItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2149606613979259287L;

	@Override
	public String randomValue(Random rndGen) {
		int rInt2 = rndGen.nextInt(2);
		if (rInt2 == 1) {
			return "True";
		} else {
			return "False";
		}
	}

	@Override
	public List possibleValues() {
		// TODO Auto-generated method stub
		List posVals = new ArrayList();
		posVals.add("True");
		posVals.add("False");
		return posVals;
	}

}
