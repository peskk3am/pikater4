/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BooleanOptionPanel.java
 *
 * Created on Mar 14, 2011, 4:08:12 PM
 */

package pikater.gui.java.improved;

import pikater.ontology.messages.Option;

/**
 *
 * @author martin
 */
public class BooleanOptionPanel extends javax.swing.JPanel {

    public boolean isSetByOptionManager() {
        return setByOptManRadio.isSelected();
    }

    public boolean isUserSpecified() {
        return manualValueRadio.isSelected();
    }

    public String getUserSpecifiedValue() {
        return userSpecifiedValue.isSelected() ? "True" : "False";
    }

    public boolean isDefault() {
        return keepDefaultRadio.isSelected();
    }

    public void setOption(Option o) {

        if (o.getMutable()) {
            setByOptManRadio.setSelected(true);
        }
        else {
            manualValueRadio.setSelected(true);
            userSpecifiedValue.setSelected(o.getValue().equals("True"));
        }

    }

    /** Creates new form BooleanOptionPanel */
    public BooleanOptionPanel() {
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        keepDefaultRadio = new javax.swing.JRadioButton();
        setByOptManRadio = new javax.swing.JRadioButton();
        manualValueRadio = new javax.swing.JRadioButton();
        userSpecifiedValue = new javax.swing.JCheckBox();

        buttonGroup1.add(keepDefaultRadio);
        keepDefaultRadio.setSelected(true);
        keepDefaultRadio.setText("Keep default value");

        buttonGroup1.add(setByOptManRadio);
        setByOptManRadio.setText("Option is set by option manager");

        buttonGroup1.add(manualValueRadio);
        manualValueRadio.setText("Specify manual value");
        manualValueRadio.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                manualValueRadioStateChanged(evt);
            }
        });

        userSpecifiedValue.setText("Option is set");
        userSpecifiedValue.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(userSpecifiedValue))
            .addComponent(keepDefaultRadio)
            .addComponent(setByOptManRadio)
            .addComponent(manualValueRadio)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(keepDefaultRadio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setByOptManRadio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(manualValueRadio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userSpecifiedValue))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void manualValueRadioStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_manualValueRadioStateChanged
        userSpecifiedValue.setEnabled(manualValueRadio.isSelected());
    }//GEN-LAST:event_manualValueRadioStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton keepDefaultRadio;
    private javax.swing.JRadioButton manualValueRadio;
    private javax.swing.JRadioButton setByOptManRadio;
    private javax.swing.JCheckBox userSpecifiedValue;
    // End of variables declaration//GEN-END:variables

}