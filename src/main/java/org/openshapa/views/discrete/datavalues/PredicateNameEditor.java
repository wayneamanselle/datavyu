package org.openshapa.views.discrete.datavalues;

import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.DBIndex;
import org.openshapa.db.DataCell;
import org.openshapa.db.Database;
import org.openshapa.db.Matrix;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.Predicate;
import org.openshapa.db.PredicateVocabElement;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.discrete.EditorComponent;
import org.openshapa.views.discrete.EditorTracker;

/**
 * This class is the character editor of a Predicate name.
 * DataValueEditor issues.
 */
public final class PredicateNameEditor extends DataValueEditor {

    /** The matrixRootView in which this editor belongs. */
    private MatrixRootView matrixRootView;

    /** Vector of the editors that make up the predicate args. */
    private Vector<EditorComponent> argsEditors;

    /** String holding the reserved characters. */
    static final String PREDNAME_RESERVED_CHARS = ")(<>|,;\t\n";

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(PredicateNameEditor.class);

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     * @param eds The vector of predicate argument editors.
     */
    public PredicateNameEditor(final JTextComponent ta,
                               final DataCell cell,
                               final Matrix matrix,
                               final int matrixIndex,
                               final Vector<EditorComponent> eds) {

        super(ta, cell, matrix, matrixIndex);

        matrixRootView = (MatrixRootView) ta;
        argsEditors = eds;
    }

    /**
     * Recalculate the string for this editor. Overrides because toString for a
     * Predicate includes all the args We just want the predicate name in this
     * case.
     */
    @Override
    public void updateStrings() {
        try {
            String newText = this.getText();
            PredDataValue pdv = (PredDataValue) getModel();
            if (!pdv.isEmpty()) {
                newText = pdv.getItsValue().getPredName();
            } else if (getText().length() == 0) {
                newText = getNullArg();
            }
            this.resetText(newText);
        } catch (SystemErrorException e) {
            logger.error("Problem getting predicate.", e);
        }
    }

    /**
     * The action to invoke when a key is typed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        //super.keyTyped(e);
        int pos = this.getCaretPosition();

        // The backspace key removes characters from behind the caret.
        if (!e.isConsumed()
            && e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
            && e.getKeyChar() == '\u0008') {

            this.removeBehindCaret();
            pos = this.getCaretPosition();
            this.searchForPredicate(this.getText());
            this.setCaretPosition(pos);
            e.consume();

        // The delete key removes characters ahead of the caret.
        } else if (!e.isConsumed()
                   && e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u007F') {

            this.removeAheadOfCaret();
            pos = this.getCaretPosition();
            this.searchForPredicate(this.getText());
            this.setCaretPosition(pos);
            e.consume();

        // If the character is not reserved - add it to the name of the pred
        } else if (!e.isConsumed() && !isReserved(e.getKeyChar())) {
            this.removeSelectedText();
            StringBuffer cValue = new StringBuffer(this.getText());
            cValue.insert(this.getCaretPosition(), e.getKeyChar());
            pos = this.getCaretPosition() + 1;
            this.setText(cValue.toString());
            this.searchForPredicate(this.getText());
            this.setCaretPosition(pos);
            e.consume();
        }
    }

    /**
     * @param aChar Character to test
     * @return true if the character is a reserved character.
     */
    private boolean isReserved(final char aChar) {
        return (PREDNAME_RESERVED_CHARS.indexOf(aChar) >= 0);
    }

    /**
     * Searches for a predicate in the database, whose name matches the supplied
     * argument.
     *
     * @param text The name of the predicate you are looking for in the
     * database.
     */
    public void searchForPredicate(final String text) {
        try {
            // reset the predicate argument editors
            EditorTracker edTracker = matrixRootView.getEdTracker();
            if (argsEditors.size() > 0) {
                edTracker.removeEditors(argsEditors);
                argsEditors.clear();
            }

            long newPredID = DBIndex.INVALID_ID;
            Database db = this.getModel().getDB();
            for (PredicateVocabElement pve : db.getPredVEs()) {
                if (pve.getName().equals(getText())) {
                    newPredID = pve.getID();
                    updateModelValue(newPredID);
                    break;
                }
            }

            PredDataValue pdv = (PredDataValue) getModel();
            if (!pdv.isEmpty() && newPredID == DBIndex.INVALID_ID) {
                updateModelValue(newPredID);
            }

        } catch (SystemErrorException se) {
            logger.error("Unable to search vocab.", se);
        }
    }

    /**
     * Update the model to reflect the value represented by the
     * editor's text representation.
     *
     * @param newPredID the id of the new predicate data value to use for this
     * predicate.
     */
    public void updateModelValue(final long newPredID) {
        try {
            // Make a new predicate data value
            PredDataValue pdv = new PredDataValue(getCell().getDB());
            if (newPredID == DBIndex.INVALID_ID) {
                pdv.clearValue();
            } else {
                Predicate pred = pdv.getItsValue();
                pred.setPredID(newPredID, true);
                pdv.setItsValue(pred);
            }
            setModel(pdv);

            // Update database and arguments with latest model.
            updateDatabase();
            rebuildArgEditors();
        } catch (SystemErrorException e) {
            logger.error("Unable to edit value", e);
        }
    }

    /**
     * Setting the predicate causes a reset of the predicate database. Build the
     * new arg editors and add to the editor tracker.
     */
    public void rebuildArgEditors() {
        argsEditors.addAll(buildArgEditors());

        if (argsEditors.size() > 0) {
            matrixRootView.getEdTracker().addEditors(argsEditors);
        }
        matrixRootView.rebuildText();
    }

    /**
     * Builds the argument editors for this cells predicate.
     * @return Vector of the arg editors.
     */
    private Vector<EditorComponent> buildArgEditors() {
        Vector<EditorComponent> eds = new Vector<EditorComponent>();
        try {
            eds = DataValueEditorFactory.buildPredicateArgs(matrixRootView,
                                           getCell(), getMatrix(), getmIndex());
        } catch (SystemErrorException ex) {
            logger.error("Unable to build new predicate arg editors", ex);
        }
        return eds;
    }
}