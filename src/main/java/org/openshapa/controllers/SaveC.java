package org.openshapa.controllers;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;
import org.openshapa.OpenSHAPA;
import org.openshapa.project.Project;
import org.openshapa.util.FileFilters.SHAPAFilter;
import org.openshapa.util.HashUtils;

/**
 * Master controller for handling project and database file saving logic.
 */
public class SaveC {

    private static SaveC INSTANCE = new SaveC();
    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SaveC.class);
    /** Last option used for saving */
    private FileFilter lastSaveOption;

    /**
     * Singleton.
     */
    private SaveC() {}

    /**
     * @return only instance of this class.
     */
    public static SaveC getInstance() {
        return INSTANCE;
    }

    /**
     * Set the last save option used. This affects the "Save" functionality.
     * @param saveOption
     */
    public void setLastSaveOption(FileFilter saveOption) {
        this.lastSaveOption = saveOption;
    }

    public FileFilter getLastSaveOption() {
        if (lastSaveOption == null) {
            return new SHAPAFilter();
        }
        return lastSaveOption;
    }

    /**
     * Saves what is being worked on using the last save option.
     */
    public void save() {
        if (lastSaveOption instanceof SHAPAFilter) {
            saveProject();
        } else {
            saveDatabase();
        }
    }

    /**
     * Save the currently opened project and database. Enforce database naming
     * rule: [project name]-[SHA-1(project name).substring(0, 10)].csv
     * 
     */
    public void saveProject() {
        Project project = OpenSHAPA.getProject();
        final String projectName = project.getProjectName();

        // Compute the database file name
        String databaseFileName = projectName;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(projectName.getBytes());
            byte[] digest = md.digest();
            String stringDigest = HashUtils.convertToHex(digest);
            databaseFileName += "-" + stringDigest.substring(0, 10);
            databaseFileName = databaseFileName.concat(".csv");
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Could not get SHA-1 implementation", ex);
            /* Stop here, this should not happen, but in case it does
             * don't risk overwriting.\
             */
            return;
        }

        // Update the project data
        project.setDatabaseFile(databaseFileName);

        // Save the database
        File dbFile = new File(project.getDatabaseDir() + "/" + databaseFileName);
        new SaveDatabaseC(dbFile);

        // Now save the project
        new SaveProjectC().save(project.getDatabaseDir() + "/" + projectName);

        // Update the application title
        OpenSHAPA.getApplication().updateTitle();
    }

    /**
     * Save what is being worked on as a new project.
     * @param directory
     * @param file
     */
    public void saveAsProject(final String directory, final String file) {
        /* First, check if the destination PROJECT file exists. We do not care
         * if the target database exists or not.
         */
        String newProjectName = file;
        /* Find out the new name of the project, build the output project file
         * name.
         */
        if (newProjectName.endsWith(".shapa")) {
            int extensionIndex = newProjectName.lastIndexOf(".shapa");
            newProjectName = newProjectName.substring(0, extensionIndex);
        }
        if (newProjectName.length() == 0) {
            logger.error("Invalid file name supplied.");
            return;
        }
        final String projectFileName = newProjectName.concat(".shapa");
        File projectFile = new File(directory, projectFileName);
        /* Do the save if the project file does not exists or if the user 
         * confirms a file overwrite in the case that the file exists.
         */
        boolean doSave = (!projectFile.exists() ||
                            (projectFile.exists() &&
                            OpenSHAPA.getApplication().overwriteExisting()));
        // Stop the save process if user does not want to save.
        if (!doSave) {
            return;
        }

        // We have the new project name, calculate new database file name
        String databaseFileName = newProjectName;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(newProjectName.getBytes());
            byte[] digest = md.digest();
            String stringDigest = HashUtils.convertToHex(digest);
            databaseFileName += "-" + stringDigest.substring(0, 10);
            databaseFileName = databaseFileName.concat(".csv");
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Could not get SHA-1 implementation", ex);
            /* Stop here, this should not happen, but in case it does
             * don't risk overwriting.\
             */
            return;
        }

        // Update project information first.
        Project project = OpenSHAPA.getProject();
        project.setProjectName(newProjectName);
        project.setDatabaseDir(directory);
        project.setDatabaseFile(databaseFileName);

        this.setLastSaveOption(new SHAPAFilter());

        // Save the database
        new SaveDatabaseC(new File(directory, databaseFileName));

        // Save the project
        new SaveProjectC().save(directory + "/" + newProjectName);
    }

    /**
     * Just save the database.
     */
    public void saveDatabase() {
        Project project = OpenSHAPA.getProject();
        project.saveProject();
        new SaveDatabaseC(new File(project.getDatabaseDir(),
                                   project.getDatabaseFile()));
    }

    /**
     * Save what is worked on as a new database
     * @param directory
     * @param file
     * @param saveFormat
     */
    public void saveAsDatabase(final String directory, final String file, 
            final FileFilter saveFormat) {
        /* Even though the user explicitly chooses to save as a database, we
         * will still need to update the project information, just in case the
         * user decides to save as a project. We will only update the project
         * name. The project name will be the file name minus any extension, if
         * applicable.
         */
        String newProjectName = file;
        int extensionIndex = file.lastIndexOf(".");
        if (extensionIndex != -1) {
            newProjectName = newProjectName.substring(0, extensionIndex);
        }

        if (newProjectName.length() == 0) {
            logger.error("Invalid file name supplied.");
            return;
        }

        Project project = OpenSHAPA.getProject();
        project.setProjectName(newProjectName);
        project.setDatabaseDir(directory);
        project.setDatabaseFile(file);
        project.saveProject();

        this.setLastSaveOption(saveFormat);

        new SaveDatabaseC(directory + "/" + file, saveFormat);
    }

}