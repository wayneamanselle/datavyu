/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.TimeStamp;
import au.com.nicta.openshapa.db.TimeStampDataValue;
import org.apache.log4j.Logger;

/**
 *
 * @author cfreeman
 */
public class OnsetView extends TimeStampDataValueView {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(OnsetView.class);

    OnsetView(final Selector cellSelection,
               final DataCell cell,
               final TimeStampDataValue timeStampDataValue,
               final boolean editable) {
        super(cellSelection, cell, timeStampDataValue, editable);
    }    

    @Override
    public void updateDatabase() {
        try {
            storeCaretPosition();
            TimeStampDataValue tsdv = (TimeStampDataValue) this.getValue();
            getParentCell().setOnset(tsdv.getItsValue());
            getParentCell().getDB().replaceCell(getParentCell());
        } catch (SystemErrorException se) {
            logger.error("Unable to update Database: ", se);
        }
    }

}
