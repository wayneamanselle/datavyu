package au.com.nicta.openshapa.views.continuous;

/**
 * Default interface for all Continuous Data Viewers.
 *
 * @author FGA
 */
public interface ContinuousDataViewer {

    /**
     * Creates a new cell in the database, and sets the start time (onset) of
     * that cell to the current time in the continuous data viewer.
     */
    void createNewCell();

    /**
     * Jogs the data stream backwards by a single unit (i.e. frame for movie)
     */
    void jogBack();

    /**
     * Stops the playback of the continous data stream.
     */
    void stop();

    /**
     * Jogs the data stream forwards by a single unit (i.e. frame for movie).
     */
    void jogForward();

    /**
     * Shuttles the video stream backwards by the current shuttle speed.
     * Repetative calls to shuttleBack increases the speed at which we reverse.
     */
    void shuttleBack();

    /**
     * Pauses the playback of the continous data stream.
     */
    void pause();

    /**
     * Shuttles the video stream forwards by the current shuttle speed.
     * Repetative calls to shuttleFoward increases the speed at which we fast
     * forward.
     */
    void shuttleForward();

    /**
     * Rewinds the continous data stream at a speed 32x normal.
     */
    void rewind();

    /**
     * Plays the continous data stream at a regular 1x normal speed.
     */
    void play();

    /**
     * Fast forwards a continous data stream at a speed 32x normal.
     */
    void forward();

    /**
     * Sets the cell starting time (onset - in the old terminology), from the
     * current time of the continous data stream.
     */
    void setCellStartTime();

    /**
     * Sets the cell stopping time (offset - in the old terminology), from the
     * current time of the continous data stream.
     */
    void setCellStopTime();

    /**
     * Find can be used to seek within a continous data stream - allowing the
     * caller to jump to a specific time in the datastream.
     *
     * @param milliseconds The time within the continous data stream, specified
     * in milliseconds from the start of the stream.
     */
    void find(final long milliseconds);

    /**
     * Go back by the specified number of milliseconds and continue playing the
     * data stream.
     *
     * @param milliseconds The number of milliseconds to jump back by.
     */
    void goBack(final long milliseconds);

    /**
     * Sets the stop time of the last cell that was created. The stop time is
     * set to the current time of the continuous data viewer.
     */
    void setNewCellStopTime();

    void syncCtrl();
    void sync();    
} //End of ContinuousDataViewer interface definition
