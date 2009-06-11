package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.openshapa.db.FloatDataValue;
import org.openshapa.db.PredDataValue;

/**
 * This class is the character editor of a FloatDataValue.
 */
public final class FloatDataValueEditor extends DataValueEditor {

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public FloatDataValueEditor(final JTextComponent ta,
                            final DataCell cell,
                            final Matrix matrix,
                            final int matrixIndex) {
        super(ta, cell, matrix, matrixIndex);
    }

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param p The predicate holding the datavalue this editor will represent.
     * @param pi The index of the datavalue within the predicate.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public FloatDataValueEditor(final JTextComponent ta,
                            final DataCell cell,
                            final PredDataValue p,
                            final int pi,
                            final Matrix matrix,
                            final int matrixIndex) {
        super(ta, cell, p, pi, matrix, matrixIndex);
    }

    /**
     * The action to invoke when a key is typed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        super.keyTyped(e);

        if (!e.isConsumed()) {

            // '-' key toggles the state of a negative / positive number.
            if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD
                || e.getKeyCode() == KeyEvent.KEY_LOCATION_UNKNOWN)
                && e.getKeyChar() == '-') {

                int pos = getCaretPositionLocal();
                String t = getText();
                if (t.startsWith("-")) {
                    // take off the '-'
                    setText(t.substring(1));
                    pos = 0;
                    // alternate handling pos--;
                } else {
                    // add the '-'
                    setText("-" + t);
                    pos = 1;
                    // alternate handling pos++;
                }
                setCaretPosition(pos);

                e.consume();

            } else if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD
                || e.getKeyCode() == KeyEvent.KEY_LOCATION_UNKNOWN)
                && e.getKeyChar() == '.') {

                int pos = getCaretPositionLocal();
                String t = getText();
                int dotPos = t.indexOf('.');
                if (dotPos >= 0) {
                    // take out the '.'
                    if (dotPos == 0) {
                        t = t.substring(1);
                    } else if (dotPos > 0) {
                        t = t.substring(0, dotPos) + t.substring(dotPos + 1);
                    }
                    setText(t);
                    if (dotPos < pos) {
                        pos--;
                    }
                    setCaretPosition(pos);
                }
            } else if (!Character.isDigit(e.getKeyChar())) {
                // Every other key stroke is ignored by the int editor.
                e.consume();
            }
        }
    }

    /**
     * Update the model to reflect the value represented by the
     * editor's text representation.
     */
    @Override
    public void updateModelValue() {
        FloatDataValue dv = (FloatDataValue) getModel();
        dv.setItsValue(buildValue(getText()));
    }

    /**
     * Sanity check the current text of the editor and return a boolean.
     * @return true if the text is an okay representation for this DataValue.
     */
    @Override
    public boolean sanityCheck() {
        boolean res = true;
        // could call a subRange test for this dataval
        try {
            Double test = buildValue(getText());
            String ans = test.toString();
        } catch (NumberFormatException e) {
            res = false;
        }
        return res;
    }

    /**
     * Builds a new value from a string.
     * @param textField The string that you want to create the value from.
     * @return A value that can be set into the database.
     */
    public Double buildValue(final String textField) {
        if (textField == null || textField.equals("")
                || textField.equals(getNullArg())) {
            return null;
        } else {
            return new Double(textField);
        }
    }
}