package pikater;

import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;

import java.util.Random;

import pikater.ontology.messages.Evaluation;
import pikater.ontology.messages.Option;
import pikater.ontology.messages.Options;
import pikater.ontology.messages.Task;

public class Agent_RandomSearch extends Agent_Search {

	private static final long serialVersionUID = 2777277001533605329L;

	private int number_of_tries = 0;
	private float error_rate = 1;
	
	private int maximum_tries;
	private float final_error_rate;

	@Override
	protected void loadSearchOptions(){
		List search_options = getSearch_options();
		// find maximum tries in Options
		Iterator itr = search_options.iterator();
		while (itr.hasNext()) {
			Option next = (Option) itr.next();
			if (next.getName().equals("E")){
				final_error_rate = Float.parseFloat(next.getValue());
			}
			if (next.getName().equals("M")){
				maximum_tries = Integer.parseInt(next.getValue());				
			}
		}
		System.out.println(getLocalName()+" parameters are: ");
		System.out.println("   final_error_rate: " + final_error_rate);
		System.out.println("   maximum_tries: " + maximum_tries);		
	}
	
	@Override
	protected String getAgentType() {
		return "RandomSearch";
	}

	@Override
	protected boolean finished() {
		System.out.println("finished() error_rate: "+ error_rate + " final_error_rate: "+ final_error_rate);

		if (number_of_tries >= maximum_tries) {
			return true;
		}

		if (error_rate <= final_error_rate) {
			return true;
		}
		return false;
	}

	@Override
	protected void updateFinished(List evaluations) {
		if (evaluations == null){
			error_rate = 1;
		}
		else{
			error_rate = ((Evaluation)(evaluations.get(0))).getError_rate();
			System.out.println("changing error_rate to: " + error_rate);
		}
	}
		
	@Override
	protected List generateNewOptions(List options, List evaluations) {
		// go through the Options Vector, generate random values
		Random generator = new Random();
		List new_options = new ArrayList();
		Iterator itr = getOptions().iterator();
		while (itr.hasNext()) {
			new_options.add(itr.next());
		}		
		
		itr = new_options.iterator();
		while (itr.hasNext()) {
			Option next = (Option) itr.next();

			String[] values = next.getUser_value().split(",");
			int numArgs = values.length;

			if (!next.getIs_a_set()) {
				if (next.getData_type().equals("INT") || next.getData_type().equals("MIXED")) {
					String si = "";
					for (int i = 1; i < numArgs; i++) {
						if (values[i - 1].equals("?")) {
							int rInt = (int) (next.getRange().getMin() + generator
									.nextInt((int) (next.getRange().getMax() - next
											.getRange().getMin())));
							si += Integer.toString(rInt) + ",";
						}
						else {
							si += values[i - 1] + ",";
						}							
					}
					if (values[numArgs - 1].equals("?")) {
						int rInt = (int) (next.getRange().getMin() + generator
								.nextInt((int) (next.getRange().getMax() - next
										.getRange().getMin())));
						si += Integer.toString(rInt);
					}
					else {
						si += values[numArgs - 1] + ",";
					}							
					
					next.setValue(si);
				}
				if (next.getData_type().equals("FLOAT")) {
					String sf = "";
					for (int i = 1; i < numArgs; i++) {
						if (values[i - 1].equals("?")) {							
							float rFloat = next.getRange().getMin()
									+ (float) (generator.nextDouble())
									* (next.getRange().getMax() - next
											.getRange().getMin());
							sf += Float.toString(rFloat) + ",";
						}
						else {
							sf += values[i - 1] + ",";
						}
					}
					if (values[numArgs - 1].equals("?")) {
						float rFloat = next.getRange().getMin()
								+ (float) (generator.nextDouble())
								* (next.getRange().getMax() - next.getRange()
										.getMin());
						sf += Float.toString(rFloat);
					}
					else {
						sf += values[numArgs - 1];
					}
					next.setValue(sf);
				}
				if (next.getData_type().equals("BOOLEAN")) {
					int rInt2 = generator.nextInt(2);
					if (rInt2 == 1) {
						next.setValue("True");
					} else {
						next.setValue("False");
					}
				}
			} else {
				String s = "";
				for (int i = 1; i < numArgs; i++) {
					if (values[i - 1].equals("?")) {
						int index = generator.nextInt(next.getSet().size());
						s += next.getSet().get(index) + ",";
					} else {
						s += values[i - 1] + ",";
					}

				}
				if (values[numArgs - 1].equals("?")) {
					int index = generator.nextInt(next.getSet().size());
					s += next.getSet().get(index);
				} else {
					s += values[numArgs - 1];
				}
				next.setValue(s);
			}
		}
		number_of_tries++;
		
		List options_list = new ArrayList();
		options_list.add(new Options(new_options));
		return options_list;
	}

}