/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * OptionPanel.java
 *
 * Created on Mar 14, 2011, 3:20:02 PM
 */

package pikater.gui.java.improved;

import jade.util.leap.LinkedList;
import jade.util.leap.List;
import javax.swing.JPanel;
import pikater.ontology.messages.Interval;
import pikater.ontology.messages.Option;

/**
 *
 * @author martin
 */
public class OptionPanel extends javax.swing.JPanel {

    Option o;
    JPanel optionPanel;
    String agentType;

    public Option getOption() {

        o.setIs_a_set(false);
        if (o.getData_type().equals("BOOLEAN")) {
            BooleanOptionPanel  bop = (BooleanOptionPanel)optionPanel;
            if (bop.isDefault())
                return null;
            if (bop.isUserSpecified()) {
                o.setValue(bop.getUserSpecifiedValue());
                o.setUser_value(bop.getUserSpecifiedValue());
                o.setMutable(false);
            }
            else {
                o.setUser_value("?");
                o.setValue("?");
                o.setMutable(true);
            }
        }
        if (o.getData_type().equals("INT") || o.getData_type().equals("FLOAT")) {
            IntegerOptionPanel iop = (IntegerOptionPanel)optionPanel;
            if (iop.isDefault())
                return null;
            if (iop.isUserSpecified()) {
                o.setValue(iop.getUserSpecifiedValue());
                o.setUser_value((iop.getUserSpecifiedValue()));
                o.setMutable(false);
                //o.setRange(null);
                o.setNumber_of_values_to_try(0);
                return o;
            }
            o.setUser_value("?");
            o.setValue("?");
            o.setMutable(true);
            if (iop.isInterval()) {
                Interval range = new Interval();
                range.setMin((Float)iop.getLowerLimit().floatValue());
                range.setMax((Float)iop.getUpperLimit().floatValue());
                o.setRange(range);
            }
            if (iop.isSet()) {
                LinkedList l = new LinkedList();
                String[] elems = iop.getSet().split(" ");
                for (String elem: elems) {
                    l.add(elem);
                }
                o.setSet(l);
                o.setIs_a_set(true);
            }
            o.setNumber_of_values_to_try(iop.getNumberOfTries());
        }
        if (o.getData_type().equals("MIXED")) {
            MixedOptionPanel mop = (MixedOptionPanel)optionPanel;
            if (mop.isDefault())
                return null;
            if (mop.isUserValue()) {
                o.setValue(mop.getUserValue());
                o.setUser_value(mop.getUserValue());
                o.setMutable(false);
                o.setNumber_of_values_to_try(0);
                return o;
            }
            o.setValue(mop.getPattern());
            o.setUser_value(mop.getPattern());
            o.setMutable(true);
            if (mop.isRange()) {
                Interval range = new Interval();
                range.setMin(Float.parseFloat(mop.getLower()));
                range.setMax(Float.parseFloat(mop.getUpper()));
                o.setRange(range);
            }
            if (mop.isSet()) {
                LinkedList l = new LinkedList();
                String[] elems = mop.getSet().split(" ");

                for (String e : elems) {
                    l.add(e);
                }

                o.setSet(l);
                o.setIs_a_set(true);
            }
            o.setNumber_of_values_to_try(mop.getTries());
        }

        return o;
    }

    public void setOption(Option o) {

        System.err.println(this.o.getData_type());

        if (this.o.getData_type().equals("BOOLEAN")) {
            BooleanOptionPanel bop = (BooleanOptionPanel)optionPanel;
            bop.setOption(o);
        }
        if (this.o.getData_type().equals("INT") || this.o.getData_type().equals("FLOAT")) {
            IntegerOptionPanel iop = (IntegerOptionPanel)optionPanel;
            iop.setOption(o);
        }
        if (this.o.getData_type().equals("MIXED")) {
            MixedOptionPanel mop = (MixedOptionPanel)optionPanel;
            mop.setOption(o);
        }
    }

    public String getOptionName(){
        return o.getName();
    }

    /** Creates new form OptionPanel */
    public OptionPanel() {
        initComponents();
    }

    public OptionPanel(Option o, String agentName) {
        initComponents();
        agentType = agentName;
        this.o = o;

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pikater/gui/java/improved/AgentStrings");
        String synopsis = bundle.getString(agentType + "-" + o.getName() + "-S");
        synopsis = synopsis.replaceAll("<", "&lt;");
        synopsis = synopsis.replaceAll(">", "&gt;");

        String description = bundle.getString(agentType + "-" + o.getName() + "-D");

        optionDescription.setText("<html><b>"+synopsis+"</b><br>"+description+"</html>");

        System.err.println(o.getData_type());

        if (o.getData_type().equals("BOOLEAN")) {

            optionPanel = new BooleanOptionPanel();

            optionsPanel.add(optionPanel);
        }

        if (o.getData_type().equals("INT")) {
            Integer defaultValue = null;
            Integer lower = null;
            Integer upper = null;
            try {
                lower = o.getRange().getMin().intValue();
                upper = o.getRange().getMax().intValue();
                defaultValue = Integer.parseInt(o.getDefault_value());
            }
            catch (NumberFormatException nfe) {
                System.err.println("Error processing option:" + o.getSynopsis());
                System.err.println(nfe.getMessage());
            }

            if (defaultValue == null) {
                defaultValue = lower;
            }

            optionPanel = new IntegerOptionPanel(lower, upper, 5, defaultValue);
            optionsPanel.add(optionPanel);
        }

        if (o.getData_type().equals("FLOAT")) {
            Double defaultValue = null;
            Double lower = null;
            Double upper = null;
            try {
                lower = o.getRange().getMin().doubleValue();
                upper = o.getRange().getMax().doubleValue();
                defaultValue = Double.parseDouble(o.getDefault_value());
            }
            catch (NumberFormatException nfe) {
                System.err.println("Error processing option:" + o.getSynopsis());
                System.err.println(nfe.getMessage());
            }

            if (defaultValue == null) {
                defaultValue = lower;
            }

            optionPanel = new IntegerOptionPanel(lower, upper, 5, defaultValue);
            optionsPanel.add(optionPanel);
        }

        if (o.getData_type().equals("MIXED")) {
            optionPanel = new MixedOptionPanel(o);
            optionsPanel.add(optionPanel);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        optionDescription = new javax.swing.JTextPane();
        optionsPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        optionDescription.setContentType("text/html");
        optionDescription.setEditable(false);
        optionDescription.setMaximumSize(new java.awt.Dimension(330, 110));
        jScrollPane1.setViewportView(optionDescription);

        optionsPanel.setLayout(new javax.swing.BoxLayout(optionsPanel, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(optionsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane optionDescription;
    private javax.swing.JPanel optionsPanel;
    // End of variables declaration//GEN-END:variables

}
