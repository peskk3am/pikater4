package pikater.ontology.messages;

import jade.util.leap.ArrayList;
import jade.util.leap.List;

import java.util.Random;

public class IntSItem extends SearchItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8936246832263259542L;
	private Integer min;
	private Integer max;
	public Integer getMin() {
		return min;
	}
	public void setMin(Integer min) {
		this.min = min;
	}
	public Integer getMax() {
		return max;
	}
	public void setMax(Integer max) {
		this.max = max;
	}
	@Override
	public String randomValue(Random rndGen) {
		// TODO Auto-generated method stub
		int range = max - min;//+1?
		int rInt = min + rndGen.nextInt(range);
		return Integer.toString(rInt);
	}
	@Override
	public List possibleValues() {
		// TODO Auto-generated method stub
		List posVals = new ArrayList();
		int x = getNumber_of_values_to_try();
		int range = (int) (getMax()	- getMin() + 1);
		// if there is less possibilities than x -> change x
		if (range < x) {
			x = range;
		}
		for (int i = 0; i < x; i++) {
			int vInt = (int) (getMin() + i	* (range / x));
			posVals.add(Integer.toString(vInt));
		}
		return posVals;
	}
}
