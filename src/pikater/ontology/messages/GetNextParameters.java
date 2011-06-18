package pikater.ontology.messages;

import jade.content.onto.basic.Action;
import jade.util.leap.List;

public class GetNextParameters extends Action{

	private static final long serialVersionUID = -4554163588726699351L;
	
	private List options;
	private Evaluation evaluation;
	private float error_rate;
	private int maximum_tries;
	
	public List getOptions() {
		return options;
	}
	public void setOptions(List options) {
		this.options = options;
	}
	public Evaluation getEvaluation() {
		return evaluation;
	}
	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}
	public float getError_rate() {
		return error_rate;
	}
	public void setError_rate(float error_rate) {
		this.error_rate = error_rate;
	}
	public int getMaximum_tries() {
		return maximum_tries;
	}
	public void setMaximum_tries(int maximum_tries) {
		this.maximum_tries = maximum_tries;
	}

}
