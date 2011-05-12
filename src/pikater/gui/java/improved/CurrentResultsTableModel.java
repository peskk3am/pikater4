/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pikater.gui.java.improved;

import jade.util.leap.LinkedList;
import jade.util.leap.List;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import pikater.ontology.messages.SavedResult;
import pikater.ontology.messages.Task;

/**
 *
 * @author martin
 */
public class CurrentResultsTableModel extends AbstractTableModel{

    List results;
    String [] columns = {java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("DATE"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("AGENT TYPE"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("OPTIONS"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("ERROR"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("RMSE"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("KAPPA"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("RAE"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("MAE"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("RRSE"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("TRAIN"), java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("TEST")};

    public Task getResult(int index) {
        return (Task)results.get(index);
    }

    public CurrentResultsTableModel() {
        this.results = new LinkedList();
    }

    public void add(Task t) {
        results.add(t);
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return results.size();
    }

    @Override
    public int getColumnCount() {
        return 11;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task t = (Task)results.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return t.getStart();
            case 1:
                return t.getAgent().getType();
            case 2:
                return t.getAgent().optionsToString();
            case 3:
                return t.getResult().getError_rate();
            case 4:
                return t.getResult().getRoot_mean_squared_error();
            case 5:
                return t.getResult().getKappa_statistic();
            case 6:
                return t.getResult().getRelative_absolute_error();
            case 7:
                return t.getResult().getMean_absolute_error();
            case 8:
                return t.getResult().getRoot_mean_squared_error();
            case 9:
                return t.getData().getExternal_train_file_name();
            case 10:
                return t.getData().getExternal_test_file_name();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    public void writeFile(String filename) {

        try {
            ObjectOutputStream encoder = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));

            encoder.writeObject(results);

            encoder.close();
        }
        catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
        }
        catch (IOException io) {
            io.printStackTrace();
        }
        
    }

    public void loadFile(String filename) {
        try {
            ObjectInputStream encoder = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));

            results = (List)encoder.readObject();

            encoder.close();

            super.fireTableDataChanged();
        }
        catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
        }
        catch (IOException io) {
            io.printStackTrace();
        }
        catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        }
    }


}
