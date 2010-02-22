package org.openshapa.uitests;

import java.io.File;

import javax.swing.text.JTextComponent;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for Bug 652. Bug 652: Everytime VocEditor window is reopened, the
 * predicate counter goes back to 1
 */
public final class UIBug652Test extends OpenSHAPATestClass {

    /**
     * Test for closing window and creating new predicate.
     */
    @Test
    public void testNewPredicateAfterClose() {
        System.err.println("testNewPredicateAfterClose");

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        DialogFixture vocabEditor = mainFrameFixture.dialog();

        vocabEditor.button("addPredicateButton").click();

        // will fail if can not find
        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
            @Override
            protected boolean isMatching(final JTextComponent vocEl) {
                return vocEl.getText().startsWith("predicate1");
            }
        });

        vocabEditor.button("okButton").click();

        // Create a new predicate and make sure the number has incremented
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        vocabEditor = mainFrameFixture.dialog();

        vocabEditor.button("addPredicateButton").click();

        // will fail if can not find
        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
            @Override
            protected boolean isMatching(final JTextComponent vocEl) {
                return vocEl.getText().startsWith("predicate1");
            }
        });

        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
            @Override
            protected boolean isMatching(final JTextComponent vocEl) {
                return vocEl.getText().startsWith("predicate2");
            }
        });

        vocabEditor.button("okButton").click();
    }

    /**
     * Test for running script, then creating predicates.
     */
    @Test
    public void testNewPredicateAfterScript() {
        System.err.println("testNewPredicateAfterScript");

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.clickMenuItemWithPath("Script", "Run script");

        JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
        jfcf.selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        DialogFixture vocabEditor = mainFrameFixture.dialog();

        vocabEditor.button("addPredicateButton").click();

        // will fail if can not find
        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
            @Override
            protected boolean isMatching(final JTextComponent vocEl) {
                return vocEl.getText().startsWith("predicate2");
            }
        });

        vocabEditor.button("okButton").click();

        // Create a new predicate and make sure the number has incremented
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        vocabEditor = mainFrameFixture.dialog();

        vocabEditor.button("addPredicateButton").click();

        // will fail if can not find
        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
            @Override
            protected boolean isMatching(final JTextComponent vocEl) {
                return vocEl.getText().startsWith("predicate2");
            }
        });

        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
            @Override
            protected boolean isMatching(final JTextComponent vocEl) {
                return vocEl.getText().startsWith("predicate3");
            }
        });

        vocabEditor.button("okButton").click();
    }
}
