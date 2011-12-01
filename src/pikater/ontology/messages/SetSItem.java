package pikater.ontology.messages;

import java.util.Random;

import jade.util.leap.ArrayList;
import jade.util.leap.List;

public class SetSItem extends SearchItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7123951122403010638L;

	private List set;//List of strings - all possible values
	
	public List getSet() {
		return set;
	}

	public void setSet(List set) {
		this.set = set;
	}

	@Override
	public String randomValue(Random rnd_gen) {
		// TODO Auto-generated method stub
		int index = rnd_gen.nextInt(set.size());
		return set.get(index).toString();//?toString?
	}
	
	@Override
	public  List possibleValues(){
		if (set.size() > getNumber_of_values_to_try()){
			List posVals = new ArrayList();
			for(int i = 0; i < getNumber_of_values_to_try(); i++)
				posVals.add(set.get(i));
			return posVals;
		}else
			return set;		
	}
}
