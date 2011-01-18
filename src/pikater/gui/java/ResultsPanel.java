package pikater.gui.java;

import java.awt.GridBagConstraints;

import java.awt.GridBagLayout;
import java.awt.Point;
import java.util.ArrayList;
import jade.util.leap.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import pikater.ontology.messages.DataInstances;
import pikater.ontology.messages.Task;
import javax.swing.JButton;
import java.awt.Dimension;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ResultsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private String[] columns = { "Agent type", "Options", "Train file",
			"Test file", "Error rate", "Mean absolute error", "MSE", "Kappa" };
	private DefaultTableModel tableModel = new DefaultTableModel(columns, 0); // @jve:decl-index=0:visual-constraint="12,223"
	private ArrayList<Task> tasks = new ArrayList<Task>();
	private JButton jButton = null;
	/**
	 * This is the default constructor
	 */
	public ResultsPanel() {
		super();
		initialize();
	}

	public void addResult(Task t) {

		tasks.add(t);

		Vector<String> data = new Vector<String>();

		data.add(t.getAgent().getType());
		data.add(t.getAgent().optionsToString());
		data.add(t.getData().getTrain_file_name());
		data.add(t.getData().getTest_file_name());
		data.add(String.valueOf(t.getResult().getError_rate()));
		data.add(String.valueOf(t.getResult().getMean_absolute_error()));
		data.add(String.valueOf(t.getResult().getRoot_mean_squared_error()));
		data.add(String.valueOf(t.getResult().getKappa_statistic()));

		tableModel.addRow(data);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridx = 0;
		this.setSize(320, 251);
		this.setLayout(new GridBagLayout());
		this.add(getJScrollPane(), gridBagConstraints);
		this.add(getJButton(), gridBagConstraints1);
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable(tableModel);
			jTable.addMouseListener(new java.awt.event.MouseListener() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					Point p = e.getPoint();
					int row = jTable.rowAtPoint(p);
					Task current = tasks.get(row);
					List dataInstances = current.getResult().getLabeled_data();
					if (dataInstances == null) {
						return;
					}

					ResultDetailsFrame rdf = new ResultDetailsFrame(dataInstances);
					rdf.setVisible(true);

				}

				public void mousePressed(java.awt.event.MouseEvent e) {
				}

				public void mouseReleased(java.awt.event.MouseEvent e) {
				}

				public void mouseEntered(java.awt.event.MouseEvent e) {
				}

				public void mouseExited(java.awt.event.MouseEvent e) {
				}
			});
		}
		return jTable;
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
						
						for (String s: columns)  {
							out.write("\"" + s + "\"");
							if (!s.equals(columns[columns.length - 1]))
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

}  //  @jve:decl-index=0:visual-constraint="10,10"
