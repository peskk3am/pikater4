package pikater.gui.java;

import jade.util.leap.List;

import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import pikater.ontology.messages.Attribute;
import pikater.ontology.messages.DataInstances;
import javax.swing.JTabbedPane;
import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;

public class ResultDetailsFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;  //  @jve:decl-index=0:visual-constraint="49,10"
	private List dataInstances = null;
	private JTabbedPane jTabbedPane = null;
	private JButton jButton = null;
	private Vector<JTable> tables = null;
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
		this.tables = new Vector<JTable>();
		
		for (int i = 0; i < dataInstances.size(); i++) {
			DataInstances di = (DataInstances)dataInstances.get(i);
			JTable table = new JTable(new DataInstancesTableModel(di));
			this.jTabbedPane.insertTab(di.getName(), null, new JScrollPane(table), null, jTabbedPane.getComponentCount());
			tables.add(table);
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
			jContentPane.setSize(new Dimension(223, 212));
			jContentPane.add(getJTabbedPane(), BorderLayout.CENTER);
			jContentPane.add(getJButton(), BorderLayout.SOUTH);
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

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Export as CSV...");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					JFileChooser fChooser = new JFileChooser();
					fChooser.showSaveDialog(jButton);
					
					File output = fChooser.getSelectedFile();
					
					if (output == null)
						return;
					
					try {
						FileWriter out = new FileWriter(output);
						
						JScrollPane jScroll = (JScrollPane)jTabbedPane.getSelectedComponent();
						JTable jTable = tables.get(jTabbedPane.getSelectedIndex());
						
						for (int i = 0; i < jTable.getColumnCount(); i++) {
							out.write("\"" + jTable.getColumnName(i) + "\"");
							if (i < jTable.getColumnCount() - 1)
									out.write(",");
						}
						
						out.write("\n");
						
						for (int i = 0; i < jTable.getModel().getRowCount(); i++) {
							for (int j = 0; j < jTable.getModel().getColumnCount(); j++) {
								if (j < 4) 
									out.write("\"" + jTable.getModel().getValueAt(i, j).toString() +  "\"");
								else
									out.write(jTable.getModel().getValueAt(i, j).toString());
								if (j < jTable.getModel().getColumnCount() - 1) {
									out.write(",");
								}
							}
							out.write("\n");
						}
						
						out.close();
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					
				}
			});
		}
		return jButton;
	}

}
