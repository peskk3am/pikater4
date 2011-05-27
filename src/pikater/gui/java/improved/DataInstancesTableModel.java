/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pikater.gui.java.improved;

import jade.util.leap.ArrayList;
import jade.util.leap.List;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;
import pikater.ontology.messages.Attribute;
import pikater.ontology.messages.DataInstances;
import pikater.ontology.messages.Instance;

/**
 * Table model for data instances.
 *
 * @author Martin Pilat
 */
public class DataInstancesTableModel extends AbstractTableModel {

		DataInstances instance = null;
                String[] classes;

                public int getClassIndex() {
                    if (instance.getClass_index() >= 0)
                        return instance.getClass_index();
                    return ((Instance)instance.getInstances().get(0)).getValues().size() - 1;
                }

                public Instance getInstance(int rowIndex) {
                    return (Instance)instance.getInstances().get(rowIndex);
                }

                public String[] getClasses() {
                    HashMap<String, Integer> classCounts = new HashMap<String, Integer>();

                    int classIndex = getClassIndex();
                    for (int i = 0; i < instance.getInstances().size(); i++) {
                        Instance inst = (Instance)instance.getInstances().get(i);

                        String iClass = getValueAt(i, classIndex).toString();
                        if (classCounts.get(iClass) != null) {
                            classCounts.put(iClass, classCounts.get(iClass) + 1);
                        }
                        else
                            classCounts.put(iClass, 1);
                    }

                    Object[] oClasses = classCounts.keySet().toArray();
                    String[] classes = new String[oClasses.length];
                    for (int i = 0; i < classes.length; i++) {
                        classes[i] = (String)oClasses[i];
                    }

                    return classes;
                }

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

                @Override
                public boolean isCellEditable(int row, int column) {
                    if (column < getColumnCount() - 1)
                        return true;
                    return false;
                }

                @Override
                public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

                    Instance inst = (Instance)instance.getInstances().get(rowIndex);
                    List values = inst.getValues();
                    List missing = inst.getMissing();

                    try {
                        List newValues = new ArrayList();
                        List newMissing = new ArrayList();
                        for (int i = 0; i < values.size(); i++) {
                            if (i == columnIndex) {
                                newValues.add(aValue.toString().equals("?") ? -1.0 : NumberFormat.getInstance().parse(aValue.toString()).doubleValue());
                                newMissing.add((aValue.toString().equals("?")) ? true : false);
                            }
                            else {
                                newValues.add(values.get(i));
                                newMissing.add(missing.get(i));
                            }
                        }
                        inst.setValues(newValues);
                        inst.setMissing(newMissing);

                        super.fireTableCellUpdated(rowIndex, columnIndex);
                    }
                    catch (ParseException pe) {
                        pe.printStackTrace();
                    }

                    
                }

                public String getCSVString() {

                    String output = "";

                    for (int j = 0; j < getRowCount(); j++) {
                        boolean allMissing = true;
                        String line = "";
                        for (int i = 0; i < getColumnCount(); i++) {
                            if (!getValueAt(j, i).equals("?")) {
                                allMissing = false;
                            }
                            if (i < getColumnCount() - 1) {
                                line += getValueAt(j, i).toString() + ",";
                            }
                            else {
                                line += getValueAt(j, i).toString() + "\n";
                            }
                        }
                        if (!allMissing)
                            output += line;
                    }

                    return output;

                }

                public void addNewInstance() {

                    List instances = instance.getInstances();

                    Instance inst = new Instance();

                    List values = new ArrayList();
                    List missing = new ArrayList();
                    for (int i = 0; i < getColumnCount(); i++) {
                        values.add(-1.0);
                        missing.add(true);
                    }

                    inst.setMissing(missing);
                    inst.setValues(values);

                    instance.getInstances().add(inst);

                    fireTableDataChanged();
                }
	}
