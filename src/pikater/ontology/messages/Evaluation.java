package pikater.ontology.messages;

import jade.content.Concept;
import jade.util.leap.List;

public class Evaluation implements Concept {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1319671908304254420L;
	private float _error_rate = -1;
	private float _kappa_statistic = -1;
	private float _mean_absolute_error = -1;
	private float _root_mean_squared_error = -1;
	private float _relative_absolute_error = -1; // percent
	private float _root_relative_squared_error = -1; // percent
	private String _status;

	// private DataInstances data_table;
	private List _labeled_data; // List of DataInstances

	public String getStatus() {
	return _status;
	}
	public void setStatus(String status) {
	_status = status;
	}

	public void setError_rate(float error_rate) {
		_error_rate = error_rate;
	}

	public float getError_rate() {
		return _error_rate;
	}

	public void setKappa_statistic(float kappa_statistic) {
		_kappa_statistic = kappa_statistic;
	}

	public float getKappa_statistic() {
		return _kappa_statistic;
	}

	public void setMean_absolute_error(float mean_absolute_error) {
		_mean_absolute_error = mean_absolute_error;
	}

	public float getMean_absolute_error() {
		return _mean_absolute_error;
	}

	public void setRoot_mean_squared_error(float root_mean_squared_error) {
		_root_mean_squared_error = root_mean_squared_error;
	}

	public float getRoot_mean_squared_error() {
		return _root_mean_squared_error;
	}

	public void setRelative_absolute_error(float relative_absolute_error) {
		_relative_absolute_error = relative_absolute_error;
	}

	public float getRelative_absolute_error() {
		return _relative_absolute_error;
	}

	public void setRoot_relative_squared_error(float root_relative_squared_error) {
		_root_relative_squared_error = root_relative_squared_error;
	}

	public float getRoot_relative_squared_error() {
		return _root_relative_squared_error;
	}

	public List getLabeled_data() {
		return _labeled_data;
	}

	public void setLabeled_data(List labeled_data) {
		_labeled_data = labeled_data;
	}

}
