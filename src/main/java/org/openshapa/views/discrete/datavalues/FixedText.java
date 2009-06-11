package org.openshapa.views.discrete.datavalues;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.views.discrete.EditorComponent;

/**
 * Leaf item in the Editor Component for fixed text like brackets and commas.
 * Stubs the abstract methods to nothing.
 * FixedTexts are not "editable" so EditorTracker will never set them to be
 * the current editor, so these stubs will never be called.
 */
public class FixedText extends EditorComponent {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(FixedText.class);

    /**
     * Constructor.
     */
    public FixedText(JTextComponent ta, String text) {
        super(ta, text);
    }

    /**
     * Never called (unless you want to).
     * @param e KeyEvent details.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
    }

    /**
     * Never called (unless you want to).
     * @param e KeyEvent details.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
    }

    /**
     * Never called (unless you want to).
     * @param e KeyEvent details.
     */
    @Override
    public void keyReleased(final KeyEvent e) {
    }

    /**
     * Never called (unless you want to).
     * @param fe FocusEvent details.
     */
    @Override
    public void focusLost(final FocusEvent fe) {
    }
}
