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
public class FloatSetInputVerifier extends InputVerifier{

    JComponent comp = null;

    public FloatSetInputVerifier(JComponent comp) {
        this.comp = comp;
    }

    @Override
    public boolean verify(JComponent input) {
        JTextField textField = (JTextField)input;
        String[] values = textField.getText().split(" ");

        for (String value : values) {
            value = value.trim();
            ParsePosition pos = new ParsePosition(0);
            NumberFormat nf = NumberFormat.getInstance();
            nf.parse(value, pos);
            if (pos.getIndex() != value.length()) {
                JOptionPane.showMessageDialog(comp, ResourceBundle.getBundle("pikater/gui/java/improved/Strings").getString("FLOAT_SET_ERROR"), null, JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

}
