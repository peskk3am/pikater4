package pikater.ontology.messages;

import jade.content.Concept;
import jade.util.leap.List;

public class Results implements Concept {

	/**
	 * 
	 */
	private float maxValue = (float)Integer.MAX_VALUE;
	
	private static final long serialVersionUID = -3411423409276645995L;
	private String _problem_id;
	private Id _task_id;

	private float _avg_error_rate = -1;
	private float _avg_kappa_statistic = -1;
	private float _avg_mean_absolute_error = -1;
	private float _avg_root_mean_squared_error = -1;
	private float _avg_relative_absolute_error = -1;
	private float _avg_root_relative_squared_error = -1;

	private List _results;
	
	public void setProblem_id(String problem_id) {
		_problem_id = problem_id;
	}

	public String getProblem_id() {
		return _problem_id;
	}

	public void setTask_id(Id task_id) {
		_task_id = task_id;
	}

	public Id getTask_id() {
		return _task_id;
	}

	public void setResults(List results) {
		_results = results;
	}

	public List getResults() {
		return _results;
	}

	public void setAvg_error_rate(float avg_error_rate) {
		if (Float.isInfinite(avg_error_rate)){
			_avg_error_rate = maxValue;
		}
		else{
			_avg_error_rate = avg_error_rate;
		}
	}

	public float getAvg_error_rate() {
		return _avg_error_rate;
	}

	public void setAvg_kappa_statistic(float avg_kappa_statistic) {
		if (Float.isInfinite(avg_kappa_statistic)){
			_avg_kappa_statistic = maxValue;
		}
		else{
			_avg_kappa_statistic = avg_kappa_statistic;
		}
	}

	public float getAvg_kappa_statistic() {
		return _avg_kappa_statistic;
	}

	public void setAvg_mean_absolute_error(float avg_mean_absolute_error) {
		if (Float.isInfinite(avg_mean_absolute_error)){
			_avg_mean_absolute_error = maxValue;
		}
		else{		
			_avg_mean_absolute_error = avg_mean_absolute_error;
		}
	}

	public float getAvg_mean_absolute_error() {
		return _avg_mean_absolute_error;
	}

	public void setAvg_root_mean_squared_error(float avg_root_mean_squared_error) {
		if (Float.isInfinite(avg_root_mean_squared_error)){
			_avg_root_mean_squared_error = maxValue;
		}
		else{
			_avg_root_mean_squared_error = avg_root_mean_squared_error;
		}
	}

	public float getAvg_root_mean_squared_error() {
		return _avg_root_mean_squared_error;
	}

	public void setAvg_relative_absolute_error(float avg_relative_absolute_error) {
		if (Float.isInfinite(avg_relative_absolute_error)){
			_avg_relative_absolute_error = maxValue;
		}
		else{
			_avg_relative_absolute_error = avg_relative_absolute_error;
		}
	}

	public float getAvg_relative_absolute_error() {
		return _avg_relative_absolute_error;
	}

	public void setAvg_root_relative_squared_error(
			float avg_root_relative_squared_error) {
		if (Float.isInfinite(avg_root_relative_squared_error)){
			_avg_root_relative_squared_error = maxValue;
		}
		else{		
			_avg_root_relative_squared_error = avg_root_relative_squared_error;
		}
	}

	public float getAvg_root_relative_squared_error() {
		return _avg_root_relative_squared_error;
	}
	
}