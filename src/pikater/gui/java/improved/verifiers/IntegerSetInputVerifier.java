/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pikater.gui.java.improved.verifiers;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ResourceBundle;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author martin
 */
public class IntegerSetInputVerifier extends InputVerifier{

    JComponent comp = null;
    String additionalChars = "";

    public IntegerSetInputVerifier(JComponent comp, String additionalChars) {
        this(comp);
        this.additionalChars = additionalChars;
    }

    public IntegerSetInputVerifier(JComponent comp) {
        this.comp = comp;
    }

    @Override
    public boolean verify(JComponent input) {
        JTextField textField = (JTextField)input;
        String[] values = textField.getText().split(" ");

        for (String value : values) {
            value = value.trim();
            if (value.length() == 1 && additionalChars.indexOf(value) != -1)
                continue;
            ParsePosition pos = new ParsePosition(0);
            NumberFormat nf = NumberFormat.getIntegerInstance();
            nf.parse(value, pos);
            if (pos.getIndex() != value.length()) {
                JOptionPane.showMessageDialog(comp, ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("INTEGER_SET_ERROR"), null, JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

}
