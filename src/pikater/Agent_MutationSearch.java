package pikater;

import java.util.Random;

import pikater.ontology.messages.Option;

public abstract class Agent_MutationSearch extends Agent_Search {
	/**
	 * 
	 */
	private static final long serialVersionUID = -401141068045485111L;
	Random rnd_gen = new Random(1);

	//Create random option value
	protected String randomOptValue(Option next_opt){
		
		String[] values = next_opt.getUser_value().split(",");
		float range = next_opt.getRange().getMax() - next_opt.getRange().getMin();
		int numArgs = values.length;
		if (!next_opt.getIs_a_set()) {
			if (next_opt.getData_type().equals("INT") || next_opt.getData_type().equals("MIXED")) {
				//INT, MIXED
				String si = "";
				for (int i = 0; i < numArgs; i++) {
					String sg;
					if (values[i].equals("?")) {
						int rInt = (int) (next_opt.getRange().getMin() + rnd_gen.nextInt((int) range));
						sg = Integer.toString(rInt);
					}
					else {
						sg = values[i];
					}
					si+=sg;
					if(i<numArgs-1){
						si+=",";
					}
				}							
				return si;
			}else
			if (next_opt.getData_type().equals("FLOAT")) {
				//FLOAT
				String sf = "";
				for (int i = 0; i < numArgs; i++) {
					String sg;
					if (values[i ].equals("?")) {							
						float rFloat = next_opt.getRange().getMin() + rnd_gen.nextFloat()* range;
						sg = Float.toString(rFloat);
					}
					else {
						sg = values[i];
					}
					sf+=sg;
					if(i<numArgs-1){
						sf+=",";
					}
				}
				return sf;
			}else
			if (next_opt.getData_type().equals("BOOLEAN")) {
				//BOOLEAN
				int rInt2 = rnd_gen.nextInt(2);
				if (rInt2 == 1) {
					return "True";
				} else {
					return "False";
				}
			}
			else
				return "";//?
		} else {
			//SET
			String s = "";
			for (int i = 0; i < numArgs; i++) {
				String sg;
				if (values[i].equals("?")) {
					int index = rnd_gen.nextInt(next_opt.getSet().size());
					sg = next_opt.getSet().get(index).toString();
				} else {
					sg = values[i];
				}
				s+=sg;
				if(i<numArgs-1){
					s+=",";
				}
			}
			return s;
		}
	}
	
	
	//Mutate option value ("?" values generated with probability of mutation)
	protected String mutateOptValue(Option next_opt, double mutation){
		String[] values = next_opt.getUser_value().split(",");
		String[] old_values = next_opt.getValue().split(",");
		float range = next_opt.getRange().getMax() - next_opt.getRange().getMin();
		int numArgs = values.length;
		if (!next_opt.getIs_a_set()) {
			if (next_opt.getData_type().equals("INT") || next_opt.getData_type().equals("MIXED")) {
				//INT, MIXED
				String si = "";
				for (int i = 0; i < numArgs; i++) {
					String sg;
					if (values[i].equals("?") && (rnd_gen.nextDouble() < mutation)) {
							//Generate new value
							int rInt = (int) (next_opt.getRange().getMin() + rnd_gen.nextInt((int) range));
							sg = Integer.toString(rInt);
					}
					else {
						sg = old_values[i];
					}
					si+=sg;
					if(i<numArgs-1){
						si+=",";
					}
				}							
				return si;
			}else
			if (next_opt.getData_type().equals("FLOAT")) {
				//FLOAT
				String sf = "";
				for (int i = 0; i < numArgs; i++) {
					String sg;
					if (values[i ].equals("?")&& (rnd_gen.nextDouble() < mutation)) {
						//Generate new value
						float rFloat = next_opt.getRange().getMin()	+ rnd_gen.nextFloat()* range;
						sg = Float.toString(rFloat);
					}
					else {
						sg = old_values[i];
					}
					sf+=sg;
					if(i<numArgs-1){
						sf+=",";
					}
				}
				return sf;
			}else
			if (next_opt.getData_type().equals("BOOLEAN")) {
				//BOOLEAN
				// what if this has been already set???
				if(rnd_gen.nextDouble() < mutation){
					//Generate new value
					int rInt2 = rnd_gen.nextInt(2);
					if (rInt2 == 1) {
						return "True";
					} else {
						return "False";
					}
				}else return old_values[0];
			}
			else
				return "";//?
		} else {
			//SET
			String s = "";
			for (int i = 0; i < numArgs; i++) {
				String sg;
				if (values[i].equals("?")&& (rnd_gen.nextDouble() < mutation)) {
					//Generate new value
					int index = rnd_gen.nextInt(next_opt.getSet().size());
					sg = next_opt.getSet().get(index).toString();
				} else {
					sg = old_values[i];
				}
				s+=sg;
				if(i<numArgs-1){
					s+=",";
				}
			}
			return s;
		}
	}
}
