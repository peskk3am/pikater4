/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pikater.gui.java.improved;

import javax.swing.table.AbstractTableModel;
import pikater.ontology.messages.Attribute;
import pikater.ontology.messages.DataInstances;

/**
 * Table model for data instances.
 *
 * @author Martin Pilat
 */
public class DataInstancesTableModel extends AbstractTableModel {

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
