package pikater.ontology.messages;

import jade.util.leap.ArrayList;
import jade.util.leap.List;

import java.util.Random;

public class FloatSItem extends SearchItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6134477709847830513L;
	private Float min;
	private Float max;
	public Float getMin() {
		return min;
	}
	public void setMin(Float min) {
		this.min = min;
	}
	public Float getMax() {
		return max;
	}
	public void setMax(Float max) {
		this.max = max;
	}
	@Override
	public String randomValue(Random rndGen) {
		float range = max - min;
		float rFloat = min + rndGen.nextFloat()* range;
		return Float.toString(rFloat);
	}
	@Override
	public List possibleValues() {
		// TODO Auto-generated method stub
		List posVals = new ArrayList();
		int x = getNumber_of_values_to_try();
		float dv = (getMax() - getMin())/ (x - 1);
		for (int i = 0; i < x; i++) {
			float vFloat = getMin() + i * dv;
			posVals.add(Float.toString(vFloat));
		}
		return posVals;
	}
}
