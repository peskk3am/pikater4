/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pikater.gui.java.improved.verifiers;

import java.text.NumberFormat;
import java.text.ParseException;
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
public class MLPLayersVerifier extends InputVerifier {

    JComponent comp = null;
    String additionalChars = null;

    public MLPLayersVerifier(JComponent comp, String additionalChars) {
        this.comp = comp;
        this.additionalChars = additionalChars;
    }

    @Override
    public boolean verify(JComponent input) {
        JTextField textField = (JTextField)input;
        String[] values = textField.getText().split(",");
        
        for (String value : values) {
            value = value.trim();
            if (value.length() == 1 && additionalChars.indexOf(value) != -1) {
                continue;
            }
            ParsePosition pos = new ParsePosition(0);
            NumberFormat nf = NumberFormat.getIntegerInstance();
            nf.parse(value, pos);
            if (pos.getIndex() != value.length()) {
                if (additionalChars.indexOf("?") != -1)
                    JOptionPane.showMessageDialog(comp, ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("MLP_LAYERS_ERROR"), null, JOptionPane.ERROR_MESSAGE);
                else
                    JOptionPane.showMessageDialog(comp, ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("MLP_LAYERS_ERROR_NO_?"), null, JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    

}
