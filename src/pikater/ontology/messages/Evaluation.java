package pikater.ontology.messages;

import java.util.Date;

import jade.content.Concept;
import jade.util.leap.LinkedList;
import jade.util.leap.List;

public class Evaluation implements Concept {
	/**
	 * 
	 */
	private float maxValue = (float)Integer.MAX_VALUE;
	
	private static final long serialVersionUID = 1319671908304254420L;
	private List evaluations;
	private Date start;
	private String _status;

	private String object_filename;
	
	//private DataInstances data_table;
	private List _labeled_data = new LinkedList(); // List of DataInstances

    private DataInstances data_table;

	private byte [] object;  // saved agent

    public void setStatus(String status) {
            _status = status;
        }

    public String getStatus() {
            return _status;
        }

    public DataInstances getData_table() {
		return data_table;
	}

	public void setData_table(DataInstances dataTable) {
		data_table = dataTable;
	}

	public List getLabeled_data() {
		return _labeled_data;
	}

	public void setLabeled_data(List labeled_data) {
		_labeled_data = labeled_data;
	}
	
	public void setObject_filename(String object_filename) {
		this.object_filename = object_filename;
	}

	public String getObject_filename() {
		return object_filename;
	}
	
	public void setObject(byte [] object) {
		this.object = object;
	}
	public byte [] getObject() {
		return object;
	}

	public List getEvaluations() {
		return evaluations;
	}
	public void setEvaluations(List evaluations) {
		this.evaluations = evaluations;
	}
	
    public void setStart(Date start) {
        this.start = start;
    }

    public Date getStart() {
        return start;
    }

}
