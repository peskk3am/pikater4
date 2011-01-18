package pikater.gui.java;

import jade.util.leap.List;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import pikater.ontology.messages.Attribute;
import pikater.ontology.messages.DataInstances;
import pikater.ontology.messages.Instance;
import pikater.ontology.messages.Task;
import javax.swing.JTabbedPane;

public class ResultDetailsFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private List dataInstances = null;
	private JTabbedPane jTabbedPane = null;

	/**
	 * This is the default constructor
	 */
	public ResultDetailsFrame() {
		super();
		initialize();
	}

	public ResultDetailsFrame(List inst) {
		super();
		this.dataInstances = inst;
		initialize();
	}

	private class DataInstancesTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		
		DataInstances instance = null;
		
		public DataInstancesTableModel(DataInstances instance) {
			this.instance = instance;
		}
		
		private static final long serialVersionUID = 8207067033399270730L;

		@Override
		public int getColumnCount() {
			return instance.getAttributes().size();
		}

		@Override
		public int getRowCount() {
			return instance.getInstances().size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return instance.toString(rowIndex, columnIndex);
		}

		@Override
		public String getColumnName(int column) {
			return ((Attribute)instance.getAttributes().get(column)).getName();
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("Result details");
		
		for (int i = 0; i < dataInstances.size(); i++) {
			DataInstances di = (DataInstances)dataInstances.get(i);
			this.jTabbedPane.insertTab(di.getName(), null, new JScrollPane(new JTable(new DataInstancesTableModel(di))), null, jTabbedPane.getComponentCount());
		}
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJTabbedPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
		}
		return jTabbedPane;
	}

}
