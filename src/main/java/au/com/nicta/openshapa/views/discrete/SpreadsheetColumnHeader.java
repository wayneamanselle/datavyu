/*
 * SpreadsheetColumnHeader.java
 *
 * Created on 26/11/2008, 2:37:13 PM
 */

package au.com.nicta.openshapa.views.discrete;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import org.apache.log4j.Logger;

/**
 * SpreadsheetColumnHeader. Panel used in the JScrollPane columnHeaderView.
 * @author swhitcher
 */
public class SpreadsheetColumnHeader extends javax.swing.JPanel {

    /** Logger for this class. */
    private static Logger logger
                            = Logger.getLogger(SpreadsheetColumnHeader.class);

    /** Creates new form SpreadsheetColumnHeader. */
    public SpreadsheetColumnHeader() {
        initComponents();

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }

    /**
     * Adds a column header panel (JLabel) to the panel.
     * @param name The name of the column.
     * @param colID Column ID for reference.
     */
    public final void addColumn(final String name, final long colID) {
        JLabelWithID nameLabel = new JLabelWithID(name, colID);
        nameLabel.setOpaque(true);
       // nameLabel.setHorizontalTextPosition(JLabel.CENTER);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        // nameLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        nameLabel.setMinimumSize(new Dimension(200,14));
        nameLabel.setPreferredSize(new Dimension(200,14));
        nameLabel.setMaximumSize(new Dimension(200,14));

        this.add(nameLabel);
    }

    /**
     * Adds a column header panel (JLabel) to the panel.
     * @param colID Column ID to remove.
     */
    public final void removeColumn(final long colID) {
        JLabelWithID found = null;
        for (int i = 0; i < this.getComponentCount(); i++) {
            try {
                JLabelWithID lab = (JLabelWithID) this.getComponent(i);
                if (lab.getID() == colID) {
                    found = lab;
                    break;
                }
            } catch (ClassCastException e) {
                logger.info("Unexpected Component in mainview", e);
            }
        }
        if (found != null) {
            this.remove(found);
        } else {
            logger.warn("Did not find column to delete by id = " + colID);
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

        setName("Form"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 495, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}


/**
 * JLabelWithID utility class will need extension to a "ColumnHeader"
 * Provides JLabel that knows the colID it comes from in the db.
 * @author swhitcher
 */
class JLabelWithID extends JLabel {

    /** ID of the JLabel. */
    private long compID;

    /**
     * Creates new JLabelWithID.
     * @param text String to display
     * @param cID ID of the associated item
     */
    public JLabelWithID(final String text, final long cID) {
        super(text);

        compID = cID;
    }

    /**
     * @return ID of the component.
     */
    public long getID() {
        return compID;
    }
}