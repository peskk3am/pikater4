/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FileBrowserFrame.java
 *
 * Created on Mar 6, 2011, 2:46:19 AM
 */

package pikater.gui.java.improved;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.util.leap.ArrayList;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import pikater.DataManagerService;
import pikater.ontology.messages.*;




/**
 *
 * @author martin
 */
public class FileBrowserFrame extends javax.swing.JFrame implements GuiConstants {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("pikater/gui/java/improved/Strings");


    GuiAgent myAgent;
    FilesTableModel fileListModel;

    public FileBrowserFrame(GuiAgent myAgent) {
        super();
        initComponents();
        this.myAgent = myAgent;
        this.fileListModel = new FilesTableModel();
    }

    private class FilesTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -5772409714155549244L;
        ArrayList data = new ArrayList();

        private String columnName(int index) {
            switch (index) {
                case 0:
                    return bundle.getString("FILENAME");
                case 1:
                    return bundle.getString("NUMBER_OF_ATTRIBUTES");
                case 2:
                    return bundle.getString("ATTRIBUTES_TYPE");
                case 3:
                    return bundle.getString("NUMBER_OF_INSTANCES");
                case 4:
                    return bundle.getString("MISSING_VALUES");
                case 5:
                    return bundle.getString("DEFAULT_TASK");
                default:
                    return "";
            }
        }

        private Object getColumnValue(Metadata m, int index) {
            switch (index) {
                case 0:
                    return m.getExternal_name();
                case 1:
                    return m.getNumber_of_attributes();
                case 2:
                    return m.getAttribute_type();
                case 3:
                    return m.getNumber_of_instances();
                case 4:
                    return m.getMissing_values();
                case 5:
                    return m.getDefault_task();
                default:
                    return "";
            }
        }

        private void setColumnValue(Metadata m, int index, Object value) {
            switch (index) {
                case 0:
                    m.setExternal_name((String) value);
                    break;
                case 1:
                    m.setNumber_of_attributes((Integer) value);
                    break;
                case 2:
                    m.setAttribute_type((String) value);
                    break;
                case 3:
                    m.setNumber_of_instances((Integer) value);
                    break;
                case 4:
                    m.setMissing_values((Boolean) value);
                    break;
                case 5:
                    m.setDefault_task((String) value);
                    break;
            }
        }

        public void setFiles(ArrayList data) {
            this.data = data;
        }

        public FilesTableModel() {
            reloadFileInfo();
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            // super.addTableModelListener(l);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (data == null) {
                return String.class;
            }
            Object value = getColumnValue((Metadata) data.get(0), columnIndex);
            if (value == null) {
                return String.class;
            }
            return getColumnValue((Metadata) data.get(0), columnIndex).getClass();
        }

        @Override
        public int getColumnCount() {
            if (data == null || data.size() == 0) {
                return 0;
            }
            return 6;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnName(columnIndex);
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object value = getColumnValue((Metadata) data.get(rowIndex),
                    columnIndex);

            return value;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex > 0) {
                return true;
            }
            return false;
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            // super.removeTableModelListener(l);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Metadata update = (Metadata) data.get(rowIndex);
            setColumnValue(update, columnIndex, aValue);
            GuiEvent ge = new GuiEvent(this, UPDATE_METADATA);
            ge.addParameter(update);
            myAgent.postGuiEvent(ge);
        }
    }

    public void reloadFileInfo() {
        GuiEvent ge = new GuiEvent(this, GET_FILES_INFO);
        GetFileInfo gfi = new GetFileInfo();
        gfi.setUserID(1);
        ge.addParameter(gfi);
        myAgent.postGuiEvent(ge);
    }

    public void setFiles(ArrayList data) {
        fileListModel.setFiles(data);
        jTable1.setModel(fileListModel);
        jTable1.createDefaultColumnsFromModel();
        TableColumnAdjuster tca = new TableColumnAdjuster(jTable1);
        tca.adjustColumns();
    }


    /** Creates new form FileBrowserFrame */
    public FileBrowserFrame() {
        initComponents();
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        missAttr = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        attType = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        defaultTask = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        filenameText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        nInstActive = new javax.swing.JCheckBox();
        nInstUpper = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        nInstLower = new javax.swing.JSpinner();
        nAttrActive = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        nAttrUpper = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        nAttrLower = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        showDetailsButton = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Strings"); // NOI18N
        jMenuItem1.setLabel(bundle.getString("VIEW...")); // NOI18N
        jMenuItem1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuItem1MouseClicked(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        setTitle(bundle.getString("PIKATER_FILE_BROWSER")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("FILTER"))); // NOI18N

        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("pikater/gui/java/improved/Bundle"); // NOI18N
        jLabel1.setText(bundle1.getString("MISSING VALUES")); // NOI18N

        missAttr.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Yes", "No" }));

        jLabel8.setText(bundle1.getString("ATTRIUTES TYPE")); // NOI18N

        attType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Numeric", "Categorical", "Multivariate" }));

        jLabel9.setText(bundle1.getString("DEFAULT TASK")); // NOI18N

        defaultTask.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Classification", "Regression" }));

        jLabel10.setText(bundle1.getString("FILENAME")); // NOI18N

        jLabel2.setText(bundle1.getString("NUMBER OF INSTANCES")); // NOI18N

        jLabel5.setText("<=");

        nInstActive.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                nInstActiveStateChanged(evt);
            }
        });

        nInstUpper.setEnabled(false);

        jLabel6.setText(bundle1.getString("NUMBER OF ATTRIBUTES")); // NOI18N

        nInstLower.setEnabled(false);

        nAttrActive.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                nAttrActiveStateChanged(evt);
            }
        });

        jLabel3.setText("<=");

        nAttrUpper.setEnabled(false);

        jLabel7.setText("<=");

        nAttrLower.setEnabled(false);

        jLabel4.setText("<=");

        okButton.setText(bundle1.getString("OK")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(defaultTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(missAttr, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(filenameText)
                            .addComponent(attType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nAttrLower)
                            .addComponent(nInstLower, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nInstUpper, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nAttrUpper)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nAttrActive)
                            .addComponent(nInstActive))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(okButton)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {nInstLower, nInstUpper});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, jLabel6});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(missAttr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(attType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(defaultTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(filenameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nInstLower, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(nInstUpper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nInstActive))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(nAttrLower, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nAttrUpper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nAttrActive)))
                    .addComponent(okButton))
                .addContainerGap())
        );

        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        showDetailsButton.setText(bundle.getString("SHOW_FILES_DETAILS")); // NOI18N
        showDetailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showDetailsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(showDetailsButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showDetailsButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3 && jTable1.getSelectedRowCount() > 0) {
            jPopupMenu1.setVisible(true);
            jPopupMenu1.setLocation(evt.getLocationOnScreen());
        }
        else
            jPopupMenu1.setVisible(false);
    }//GEN-LAST:event_jTable1MouseClicked

    private void jMenuItem1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem1MouseClicked
        jPopupMenu1.setVisible(false);
        String filename = jTable1.getModel().getValueAt(jTable1.getSelectedRow(), 0).toString();
        FileDetailsFrame fdf = new FileDetailsFrame(filename, myAgent);
        fdf.setVisible(true);
    }//GEN-LAST:event_jMenuItem1MouseClicked

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        GetFileInfo gfi = new GetFileInfo();

        gfi.setUserID(1);

        if (nAttrActive.isSelected()) {
            gfi.setAttrUpper((Integer)nAttrUpper.getValue());
            gfi.setAttrLower((Integer)nAttrLower.getValue());
        }

        if (nInstActive.isSelected()) {
            gfi.setInstLower((Integer)nInstLower.getValue());
            gfi.setInstUpper((Integer)nInstUpper.getValue());
        }

        if (!filenameText.getText().isEmpty()) {
            gfi.setFilename(filenameText.getText());
        }

        if (missAttr.getSelectedIndex() > 0) {
            gfi.setMissingValues(missAttr.getSelectedIndex() == 1);
        }

        if (attType.getSelectedIndex() > 0) {
            gfi.setAttributeType(attType.getSelectedItem().toString());
        }

        if (defaultTask.getSelectedIndex() > 0) {
            gfi.setDefaultTask(defaultTask.getSelectedItem().toString());
        }

        GuiEvent ge = new GuiEvent(this, GuiConstants.GET_FILES_INFO);
        ge.addParameter(gfi);
        myAgent.postGuiEvent(ge);
    }//GEN-LAST:event_okButtonActionPerformed

    private void nInstActiveStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_nInstActiveStateChanged
        nInstLower.setEnabled(nInstActive.isSelected());
        nInstUpper.setEnabled(nInstActive.isSelected());
    }//GEN-LAST:event_nInstActiveStateChanged

    private void nAttrActiveStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_nAttrActiveStateChanged
        nAttrLower.setEnabled(nAttrActive.isSelected());
        nAttrUpper.setEnabled(nAttrActive.isSelected());
    }//GEN-LAST:event_nAttrActiveStateChanged

    private void showDetailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showDetailsButtonActionPerformed
        if (jTable1.getSelectedRow() >= 0)  {
            jMenuItem1MouseClicked(null);
        }
    }//GEN-LAST:event_showDetailsButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox attType;
    private javax.swing.JComboBox defaultTask;
    private javax.swing.JTextField filenameText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JComboBox missAttr;
    private javax.swing.JCheckBox nAttrActive;
    private javax.swing.JSpinner nAttrLower;
    private javax.swing.JSpinner nAttrUpper;
    private javax.swing.JCheckBox nInstActive;
    private javax.swing.JSpinner nInstLower;
    private javax.swing.JSpinner nInstUpper;
    private javax.swing.JButton okButton;
    private javax.swing.JButton showDetailsButton;
    // End of variables declaration//GEN-END:variables

}
