/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ResultsBrowserFrame.java
 *
 * Created on Mar 6, 2011, 10:12:36 PM
 */
package pikater.gui.java.improved;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.util.leap.List;
import jade.util.leap.LinkedList;
import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import pikater.DataManagerService;
import pikater.ontology.messages.Agent;
import pikater.ontology.messages.Data;
import pikater.ontology.messages.DataInstances;
import pikater.ontology.messages.Execute;
import pikater.ontology.messages.LoadResults;
import pikater.ontology.messages.Task;

/**
 *
 * @author martin
 */
public class ResultsBrowserFrame extends javax.swing.JFrame implements GuiConstants {

    GuiAgent myAgent;
    LoadResults filter;
    ResultsFilterDialog rfd = new ResultsFilterDialog(this, true);
    CurrentResultsTableModel currentResults = new CurrentResultsTableModel();
    DataInputFrame did = null;

    /** Creates new form ResultsBrowserFrame */
    public ResultsBrowserFrame() {
        initComponents();
    }

    public ResultsBrowserFrame(GuiAgent myAgent) {
        System.err.println(this.getClass() + " started");
        initComponents();
        filter = new LoadResults();
        filter.setUserID(1);
        filterText.setText(filter.asText());
        this.myAgent = myAgent;
        System.err.println(this.getClass() + " created");

        if (new File("studentMode").exists()) {
            System.err.println("Student Mode");
            jTabbedPane1.remove(jPanel3);
        }
        else {
            System.err.println("Normal Mode");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        loadResultsButton = new javax.swing.JButton();
        editFilterButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        filterText = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        savedResultsTable = new javax.swing.JTable();
        savedResultsExportButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        currentResultsExportButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        currentResultsTable = new javax.swing.JTable();
        saveResultsButton = new javax.swing.JButton();
        loadCurrentResultsButton = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings"); // NOI18N
        jMenuItem1.setText(bundle.getString("DETAILS")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setText(bundle.getString("LABEL_DATA")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);

        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Bundle"); // NOI18N
        setTitle(bundle1.getString("PIKATER 1.0 - RESULTS BROWSER")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Results Filter"));

        loadResultsButton.setText(bundle.getString("LOAD_RESULTS")); // NOI18N
        loadResultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadResultsButtonActionPerformed(evt);
            }
        });

        editFilterButton.setText(bundle.getString("EDIT_FILTER")); // NOI18N
        editFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editFilterButtonActionPerformed(evt);
            }
        });

        filterText.setColumns(20);
        filterText.setLineWrap(true);
        filterText.setRows(5);
        filterText.setEnabled(false);
        jScrollPane2.setViewportView(filterText);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(editFilterButton)
                    .addComponent(loadResultsButton))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {editFilterButton, loadResultsButton});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(editFilterButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loadResultsButton))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Results"));

        savedResultsTable.setModel(new pikater.gui.java.improved.SavedResultsTableModel(new LinkedList()));
        savedResultsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(savedResultsTable);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                .addContainerGap())
        );

        savedResultsExportButton.setText(bundle1.getString("EXPORT CSV")); // NOI18N
        savedResultsExportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savedResultsExportButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 711, Short.MAX_VALUE)
                                .addComponent(savedResultsExportButton)))
                        .addGap(2, 2, 2)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(savedResultsExportButton)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("SAVED_RESULTS"), jPanel3); // NOI18N

        currentResultsExportButton.setText(bundle1.getString("EXPORT CSV")); // NOI18N
        currentResultsExportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentResultsExportButtonActionPerformed(evt);
            }
        });

        currentResultsTable.setModel(currentResults);
        currentResultsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        currentResultsTable.setComponentPopupMenu(jPopupMenu1);
        jScrollPane3.setViewportView(currentResultsTable);

        saveResultsButton.setText(bundle.getString("SAVE_RESULTS")); // NOI18N
        saveResultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveResultsButtonActionPerformed(evt);
            }
        });

        loadCurrentResultsButton.setText(bundle.getString("LOAD_RESULTS")); // NOI18N
        loadCurrentResultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadCurrentResultsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(loadCurrentResultsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveResultsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(currentResultsExportButton))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 815, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadCurrentResultsButton)
                    .addComponent(saveResultsButton)
                    .addComponent(currentResultsExportButton))
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("CURRENT_RESULTS"), jPanel4); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public DataInputFrame getDataInputDialog() {
        return did;
    }

    public void showResults(List results) {
        savedResultsTable.setModel(new SavedResultsTableModel(results));
        TableColumnAdjuster tca = new TableColumnAdjuster(savedResultsTable);
        tca.adjustColumns();
    }

    public void addResult(Task t) {

        currentResults.add(t);
        currentResultsTable.setModel(currentResults);
        TableColumnAdjuster tca = new TableColumnAdjuster(currentResultsTable);
        tca.adjustColumns();

        if (currentResults.getTrainingFile(t.getData().getExternal_train_file_name()) == null) {
            GuiEvent ge = new GuiEvent(this, GuiConstants.GET_DATA);
            ge.addParameter(t.getData().getExternal_train_file_name());
            myAgent.postGuiEvent(ge);
        }

        jTabbedPane1.setSelectedComponent(jPanel4);
        this.setVisible(true);
    }

    public void addTrainingFile(String name, DataInstances data) {
        currentResults.addTrainingFile(name, data);
    }

    private void loadResultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadResultsButtonActionPerformed

        GuiEvent ge = new GuiEvent(this, LOAD_RESULTS);
        ge.addParameter(filter);

        myAgent.postGuiEvent(ge);

    }//GEN-LAST:event_loadResultsButtonActionPerformed

    private void editFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editFilterButtonActionPerformed
        rfd.setVisible(true);

        LoadResults lr = rfd.getFilter();

        if (lr == null) {
            return;
        }

        filter = lr;
        filterText.setText(filter.asText());

    }//GEN-LAST:event_editFilterButtonActionPerformed

    private void currentResultsExportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentResultsExportButtonActionPerformed

        JFileChooser fChooser = new JFileChooser();
        fChooser.showSaveDialog(this);

        File output = fChooser.getSelectedFile();

        if (output == null) {
            return;
        }

        try {
            FileWriter out = new FileWriter(output);

            for (String s : currentResults.columns) {
                out.write("\"" + s + "\"");
                if (!s.equals(currentResults.columns[currentResults.columns.length - 1])) {
                    out.write(",");
                }
            }

            out.write("\n");

            for (int i = 0; i < currentResultsTable.getModel().getRowCount(); i++) {
                for (int j = 0; j < currentResultsTable.getModel().getColumnCount(); j++) {
                    if (j < 3) {
                        out.write("\"" + currentResultsTable.getModel().getValueAt(i, j).toString() + "\"");
                    } else {
                        out.write(currentResultsTable.getModel().getValueAt(i, j).toString());
                    }
                    if (j < currentResultsTable.getModel().getColumnCount() - 1) {
                        out.write(",");
                    }
                }
                out.write("\n");
            }

            out.close();

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }//GEN-LAST:event_currentResultsExportButtonActionPerformed

    private void savedResultsExportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savedResultsExportButtonActionPerformed
        int columns = savedResultsTable.getModel().getColumnCount();
        ArrayList<String> columnNames = new ArrayList<String>();

        for (int i = 0; i < columns; i++) {
            columnNames.add(savedResultsTable.getModel().getColumnName(i));
        }

        JFileChooser fChooser = new JFileChooser();
        fChooser.showSaveDialog(this);

        File output = fChooser.getSelectedFile();

        if (output == null) {
            return;
        }

        try {
            FileWriter out = new FileWriter(output);

            for (String s : columnNames) {
                out.write("\"" + s + "\"");
                if (!s.equals(columnNames.get(columnNames.size() - 1))) {
                    out.write(",");
                }
            }

            out.write("\n");

            for (int i = 0; i < savedResultsTable.getModel().getRowCount(); i++) {
                for (int j = 0; j < savedResultsTable.getModel().getColumnCount(); j++) {
                    if (j < 3) {
                        out.write("\"" + savedResultsTable.getModel().getValueAt(i, j).toString() + "\"");
                    } else {
                        out.write(savedResultsTable.getModel().getValueAt(i, j).toString());
                    }
                    if (j < savedResultsTable.getModel().getColumnCount() - 1) {
                        out.write(",");
                    }
                }
                out.write("\n");
            }

            out.close();

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        

    }//GEN-LAST:event_savedResultsExportButtonActionPerformed

    private void saveResultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveResultsButtonActionPerformed
        JFileChooser choose = new JFileChooser() {

            @Override
            public void approveSelection() {
                File f = getSelectedFile();

                if (!f.getAbsolutePath().endsWith(".bres")) {
                    f = new File(f.getAbsolutePath() + ".bres");
                }

                setSelectedFile(f);

                if (f.exists() && getDialogType() == SAVE_DIALOG) {
                    int result = JOptionPane.showConfirmDialog(this, ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("FILE_EXISTS"), null, JOptionPane.YES_NO_OPTION);
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                            super.setSelectedFile(null);
                            return;
                    }
                }
                super.approveSelection();
            }
        };

        FileNameExtensionFilter fnf = new FileNameExtensionFilter("BANG results file", "bres");
        choose.setFileFilter(fnf);
        choose.setAcceptAllFileFilterUsed(false);

        choose.showSaveDialog(this);

        File f = choose.getSelectedFile();

        if (f == null) {
            System.err.println("Selection canceled");
            return;
        }
        
        currentResults.writeFile(f.getAbsolutePath());

    }//GEN-LAST:event_saveResultsButtonActionPerformed

    private void loadCurrentResultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadCurrentResultsButtonActionPerformed
        JFileChooser choose = new JFileChooser();
        FileNameExtensionFilter fnf = new FileNameExtensionFilter("BANG results file", "bres");
        choose.setFileFilter(fnf);
        choose.setAcceptAllFileFilterUsed(false);

        choose.showOpenDialog(this);

        File f = choose.getSelectedFile();

        if (f == null)
            return;

        currentResults.loadFile(f.getAbsolutePath());

        TableColumnAdjuster tca = new TableColumnAdjuster(currentResultsTable);
        tca.adjustColumns();
    }//GEN-LAST:event_loadCurrentResultsButtonActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        Point p = jMenuItem1.getLocation();
        int row = currentResultsTable.rowAtPoint(p);

        currentResultsTable.getSelectionModel().setSelectionInterval(row, row);

        Task t = currentResults.getResult(row);

        List data = t.getResult().getLabeled_data();
        if (data == null || data.size() == 0) {
            System.err.println("No data");
        }

        ResultDetailsFrame rdf = new ResultDetailsFrame(data, t.getData().getExternal_train_file_name(), myAgent, currentResults);
        rdf.setVisible(true);
        
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed

        if (did != null) {
            JOptionPane.showMessageDialog(this, "Cannot open two label windows", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Point p = jMenuItem1.getLocation();
        int row = currentResultsTable.getSelectedRow();

        did = new DataInputFrame(this, currentResults.getTrainingFile(currentResults.getResult(row).getData().getExternal_train_file_name()), myAgent, currentResults.getResult(row).getResult().getObject());
        did.setVisible(true);
       
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    public void dataInputDialogClosed() {
        did = null;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new ResultsBrowserFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton currentResultsExportButton;
    private javax.swing.JTable currentResultsTable;
    private javax.swing.JButton editFilterButton;
    private javax.swing.JTextArea filterText;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton loadCurrentResultsButton;
    private javax.swing.JButton loadResultsButton;
    private javax.swing.JButton saveResultsButton;
    private javax.swing.JButton savedResultsExportButton;
    private javax.swing.JTable savedResultsTable;
    // End of variables declaration//GEN-END:variables
}
