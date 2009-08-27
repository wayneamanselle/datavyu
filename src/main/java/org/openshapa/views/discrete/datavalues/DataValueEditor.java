package org.openshapa.views.discrete.datavalues;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.DataCell;
import org.openshapa.db.DataValue;
import org.openshapa.db.FormalArgument;
import org.openshapa.db.Matrix;
import org.openshapa.db.MatrixVocabElement;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.Predicate;
import org.openshapa.db.PredicateVocabElement;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.discrete.EditorComponent;

/**
 * DataValueEditor - abstract class extending EditorComponent.
 * Adds functionality specific to DataValues being edited.
 */
public abstract class DataValueEditor extends EditorComponent {

    /** The parent matrix for the DataValue that this view represents.*/
    private Matrix parentMatrix;

    /** The parent predicate for the DataValue that this view represents. */
    private PredDataValue parentPredicate;

    /** The DataValue that this view represents. **/
    private DataValue model = null;

    /** The parent datacell for the DataValue that this view represents. */
    private DataCell parentCell;

    /** The index of the datavalue within its parent matrix. */
    private int mIndex;

    /** The index of the data value within its parent predicate. */
    private int pIndex;

    /** Is the data value now null? */
    private boolean argIsNull;

    /** Text when editor gained focus (became current editor). */
    private String textOnFocus;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(DataValueEditor.class);

    /**
     * Constructor.
     *
     * @param tc JTextComponent this editor works with.
     * @param cell The Parent cell that holds the matrix.
     * @param matrix The parent matrix that holds the DataValue.
     * @param matrixIndex The index of the data value within the above matrix
     * that this view is to represent.
     */
    public DataValueEditor(final JTextComponent tc,
                           final DataCell cell,
                           final Matrix matrix,
                           final int matrixIndex) {
        super(tc);
        init(cell, null, 0, matrix, matrixIndex);
    }

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent that this editor is nested within.
     * @param cell The Parent cell that holds the matrix.
     * @param predicate The parent predicate.
     * @param predicateIndex The index of the data value within the above
     * predicate that this view is to represent.
     * @param matrix The parent matrix that holds the DataValue.
     * @param matrixIndex The index of the data value within the above matrix
     * that this view is to represent.
     */
    public DataValueEditor(final JTextComponent ta,
                           final DataCell cell,
                           final PredDataValue predicate,
                           final int predicateIndex,
                           final Matrix matrix,
                           final int matrixIndex) {
        super(ta);
        init(cell, predicate, predicateIndex, matrix, matrixIndex);
    }

    /**
     * Initialise internal values.
     *
     * @param cell The Parent cell that holds the matrix.
     * @param predicate The parent predicate.
     * @param predicateIndex The index of the data value within the above
     * predicate that this view is to represent.
     * @param matrix The parent matrix that holds the DataValue.
     * @param matrixIndex The index of the data value within the above matrix
     * that this view is to represent.
     */
    private void init(final DataCell cell,
                      final PredDataValue predicate,
                      final int predicateIndex,
                      final Matrix matrix,
                      final int matrixIndex) {
        // so far all DataValueEditors are editable
        setEditable(true);
        try {
            parentCell = cell;
            parentPredicate = predicate;
            pIndex = predicateIndex;
            parentMatrix = matrix;
            mIndex = matrixIndex;

            if (predicate == null) {
                model = matrix.getArgCopy(mIndex);
            } else {
                Predicate p = predicate.getItsValue();
                model = p.getArgCopy(pIndex);
            }

        } catch (SystemErrorException ex) {
            logger.error("Unable to create DataValueEditor: ", ex);
        }

        argIsNull = (this.getModel() == null || this.getModel().isEmpty());

        updateStrings();
    }

    /**
     * Reset the values by retrieving from the database.
     * @param cell The Parent cell that holds the matrix.
     * @param matrix The parent matrix that holds the DataValue.
     */
    public final void resetValue(final DataCell cell, final Matrix matrix) {
        try {
            parentMatrix = matrix;
            parentCell = cell;
            if (parentPredicate == null) {
                model = matrix.getArgCopy(mIndex);
            } else {
                parentPredicate = (PredDataValue) matrix.getArgCopy(mIndex);
                Predicate p = parentPredicate.getItsValue();
                model = p.getArgCopy(pIndex);
            }
        } catch (SystemErrorException ex) {
            logger.error("Unable to create DataValue View: ", ex);
        }

        argIsNull = (this.getModel() == null || this.getModel().isEmpty());

        updateStrings();
    }

    /**
     * Recalculate the string for this editor.  In particular check if it
     * is "null" and display the appropriate FormalArg.
     */
    public void updateStrings() {
        String t = "";
        if (!isNullArg()) {
            t = this.getModel().toString();
        } else {
            t = getNullArg();
        }

        this.resetText(t);
    }

    /**
     * @return The displayable version of the null argument.
     */
    public final String getNullArg() {
        String t = "";
        try {
            if (parentMatrix != null && parentPredicate == null) {
                long mveid = parentMatrix.getMveID();
                MatrixVocabElement mve = parentMatrix.getDB()
                                                     .getMatrixVE(mveid);
                FormalArgument fa = mve.getFormalArg(mIndex);
                t = fa.toString();
            } else if (parentMatrix != null && parentPredicate != null) {
                Predicate p = parentPredicate.getItsValue();
                PredicateVocabElement pve = parentMatrix.getDB()
                                                    .getPredVE(p.getPveID());
                FormalArgument fa = pve.getFormalArg(pIndex);
                t = fa.toString();
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to get NULL arg", e);
        }
        return t;
    }

    /**
     * focusSet is the signal that this editor has become "current".
     * @param fe Focus Event
     */
    @Override
    public void focusGained(final FocusEvent fe) {
        textOnFocus = getText();
    }

    /**
     * Action to take when focus is lost for this editor.
     * @param fe Focus Event
     */
    @Override
    public void focusLost(final FocusEvent fe) {
        if (!getText().equals(textOnFocus)) {
            updateDatabase();
        }
    }

    /**
     * Action to take by this editor when a key is pressed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
        checkNullArgKeyTyped(e);

        switch (e.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                // Ignore - handled when the key is typed.
                e.consume();
                break;

            case KeyEvent.VK_LEFT:
                // Move caret to the left.
                int c = Math.max(0, this.getCaretPosition() - 1);
                this.setCaretPosition(c);

                // If after the move, we have a character to the left is
                // preserved character we need to skip one before passing
                // the key event down to skip again (effectively skipping
                // the preserved character).
                int b = Math.max(0, getCaretPosition());
                c = Math.max(0, this.getCaretPosition() - 1);
                if (this.isPreserved(getText().charAt(b))
                    || this.isPreserved(getText().charAt(c))) {
                    setCaretPosition(Math.max(0, getCaretPosition() - 1));
                }
                e.consume();
                break;

            case KeyEvent.VK_RIGHT:
                // Move caret to the right.
                c = Math.min(this.getText().length(),
                             this.getCaretPosition() + 1);
                this.setCaretPosition(c);

                // If after the move, we have a character to the right that
                // is a preserved character, we need to skip one before
                // passing the key event down to skip again (effectively
                // skipping the preserved character)
                b = Math.min(getText().length() - 1, getCaretPosition());
                c = Math.min(getText().length() - 1, getCaretPosition() + 1);
                if (c < this.getText().length()
                    && (this.isPreserved(getText().charAt(c))
                        || this.isPreserved(getText().charAt(b)))) {
                    setCaretPosition(Math.min(getText().length() - 1,
                                              getCaretPosition() + 1));
                }
                e.consume();
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_UP:
                // Key stroke gets passed up a parent element to navigate
                // cells up and down.
                break;
            default:
                break;
        }
    }

    /**
     * Action to take by this editor when a key is typed.
     * @param e KeyEvent
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        checkNullArgKeyTyped(e);

        // The backspace key removes digits from behind the caret.
        if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
            && e.getKeyChar() == '\u0008') {

            // Can't delete an empty nominal data value.
            if (!this.getModel().isEmpty()) {
                this.removeBehindCaret();
                e.consume();
            }

        // The delete key removes digits ahead of the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u007F') {

            // Can't delete an empty nominal data value.
            if (!this.getModel().isEmpty()) {
                this.removeAheadOfCaret();
                e.consume();
            }
        }
    }

    /**
     * Action to take by this editor when a key is released.
     * @param e KeyEvent
     */
    @Override
    public void keyReleased(final KeyEvent e) {
        if (getText().length() == 0) {
            argIsNull = true;
            setText(getNullArg());
        } else {
            argIsNull = (getText().equals(getNullArg()));
        }
        if (argIsNull) {
            selectAll();
        } else {
            /*
            if (!sanityCheck()) {
                setText(prevText);
                setCaretPosition(prevCaret);
            }*/
        }
    }

    /**
     * Determine if the next key action is going to create a null arg or
     * if the editor is already effectively a null arg.
     * @param e KeyEvent
     */
    public final void checkNullArgKeyTyped(final KeyEvent e) {
        // if we are a null arg already
        // consume chars that do not make sense
        // backspace, enter, tab and delete
        if (isNullArg()) {
            boolean consumeIfNull = false;
            int type = e.getID();
            int loc = e.getKeyLocation();
            char ch = e.getKeyChar();
            int code = e.getKeyCode();
            if (type == KeyEvent.KEY_TYPED) {
                consumeIfNull = ((ch == '\b')
                                || (ch == '\t')
                                || (ch == '\n')
                                || (ch == '\u007f'));
            } else {
                consumeIfNull = ((code == KeyEvent.VK_BACK_SPACE)
                                || (code == KeyEvent.VK_TAB)
                                || ((code == KeyEvent.VK_ENTER
                                        && loc != KeyEvent.KEY_LOCATION_NUMPAD))
                                || (code == KeyEvent.VK_DELETE));
            }
            if (consumeIfNull) {
                e.consume();
            }
        }
    }

    /**
     * @return true if the editor is currently displaying a "null" arg.
     */
    public final boolean isNullArg() {
        return argIsNull;
    }

    /**
     * Update the database with the model value.
     */
    public void updateDatabase() {
        /*
        try {            
        } catch (SystemErrorException e) {
            logger.error("Unable to reget the cell data: ", e);
        }
        
        // update the model.
        if (isNullArg()) {
            updateModelNull();
        } else {
            // call the subclass (template pattern)
            updateModelValue();
        }*/

        try {
            // reget the parentCell in case onset or offset have been changed.
            parentCell = (DataCell) parentCell.getDB()
                                              .getCell(parentCell.getID());

            // Update the OpenSHAPA database with the latest values.
            if (parentMatrix != null && parentPredicate == null) {
                parentMatrix.replaceArg(mIndex, model);
            } else if (parentMatrix != null && parentPredicate != null) {

                Predicate p = parentPredicate.getItsValue();
                p.replaceArg(pIndex, model);
                parentPredicate.setItsValue(p);
                parentMatrix.replaceArg(mIndex, parentPredicate);
            }
            parentCell.setVal(parentMatrix);
            parentCell.getDB().replaceCell(parentCell);
        } catch (SystemErrorException ex) {
            logger.error("Unable to update Database: ", ex);
        }
    }

    /**
     * Override selection to catch if the value is null.
     * @param startClick Start character of the selection.
     * @param endClick End character of the selection.
     */
    @Override
    public final void select(final int startClick, final int endClick) {
        if (!isNullArg()) {
            super.select(startClick, endClick);
        } else {
            super.select(0, Integer.MAX_VALUE);
        }
    }

    /**
     * Set the model data value.
     * @param dv The datavalue to set.
     */
    public final void setModel(final DataValue dv) {
        model = dv;
    }

    /**
     * @return The model that this data value view represents.
     */
    public final DataValue getModel() {
        return this.model;
    }

    /**
     * @return The datacell this datavlue is in.
     */
    public final DataCell getCell() {
        return this.parentCell;
    }

    /**
     * @return The matrix this datavalue is in.
     */
    public final Matrix getMatrix() {
        return this.parentMatrix;
    }

    /**
     * @return The index within the matrix where this datavalue exists.
     */
    public final int getmIndex() {
        return this.mIndex;
    }

}