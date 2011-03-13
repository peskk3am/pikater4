/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pikater.gui.java.improved;

import jade.util.leap.List;
import javax.swing.table.AbstractTableModel;
import pikater.ontology.messages.SavedResult;

/**
 *
 * @author martin
 */
public class ResultsTableModel extends AbstractTableModel{

    List results;
    String [] columns = {java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("DATE"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("AGENT TYPE"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("OPTIONS"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("ERROR"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("RMSE"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("KAPPA"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("RAE"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("MAE"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("RRSE"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("TRAIN"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("TEST")};

    public ResultsTableModel(List results) {
        this.results = results;
    }

    public void add(SavedResult s) {
        results.add(s);
    }

    public int getRowCount() {
        return results.size();
    }

    public int getColumnCount() {
        return 11;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        SavedResult sr = (SavedResult)results.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return sr.getDate();
            case 1:
                return sr.getAgentType();
            case 2:
                return sr.getAgentOptions();
            case 3:
                return sr.getErrorRate();
            case 4:
                return sr.getRMSE();
            case 5:
                return sr.getKappaStatistic();
            case 6:
                return sr.getRelativeAbsoluteError();
            case 7:
                return sr.getMeanAbsError();
            case 8:
                return sr.getRootRelativeSquaredError();
            case 9:
                return sr.getTrainFile();
            case 10:
                return sr.getTestFile();
            default:
                return null;
        }
    }

    public String getColumnName(int column) {
        return columns[column];
    }
    

}
