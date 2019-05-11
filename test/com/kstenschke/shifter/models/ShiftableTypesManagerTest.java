package com.kstenschke.shifter.models;

import com.kstenschke.shifter.models.entities.AbstractShiftable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ShiftableTypesManagerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getShiftableDetectTrailingCommentSelected() {
        ActionContainer actionContainer = new ActionContainer(null, true, false);
        actionContainer.setSelectedText("echo 'x'; // output x");
        actionContainer.setIsLastLineInDocument(false);
        actionContainer.setPostfixChar("\n");
        ShiftableTypesManager shiftableTypesManager = new ShiftableTypesManager(actionContainer);

        AbstractShiftable shiftable = shiftableTypesManager.getShiftable();

        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.TRAILING_COMMENT, shiftable.getType());
    }

    @Test
    public void getShiftableDetectPhpDocContainingDataTypeSelected() {
        ActionContainer actionContainer = new ActionContainer(null, true, false);
        actionContainer.setFilename("foo.php");
        actionContainer.setSelectedText("* @param int $x");
        actionContainer.setPostfixChar("\n");
        ShiftableTypesManager shiftableTypesManager = new ShiftableTypesManager(actionContainer);

        AbstractShiftable shiftable = shiftableTypesManager.getShiftable();

        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.PHP_DOC_PARAM_CONTAINING_DATA_TYPE, shiftable.getType());
    }

    @Test
    public void getShiftableDetectPhpVariableSelected() {
        ActionContainer actionContainer = new ActionContainer(null, true, false);
        actionContainer.setFilename("foo.php");
        actionContainer.setDocumentText("<?php\n$foo=1;\n$bar=2;\n");
        actionContainer.setSelectedText("$bar");
        actionContainer.setPostfixChar(" ");
        ShiftableTypesManager shiftableTypesManager = new ShiftableTypesManager(actionContainer);

        AbstractShiftable shiftable = shiftableTypesManager.getShiftable();

        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.PHP_VARIABLE, shiftable.getType());
    }

    @Test
    public void getShiftableDetectParenthesisSelected() {
        ActionContainer actionContainer = new ActionContainer(null, true, false);
        actionContainer.setFilename("foo.php");
        actionContainer.setDocumentText("<?php\n$foo=(10+9*8)/2;\n$bar=2;\n");
        actionContainer.setSelectedText("(10+9*8)");
        actionContainer.setPostfixChar(" ");
        ShiftableTypesManager shiftableTypesManager = new ShiftableTypesManager(actionContainer);

        AbstractShiftable shiftable = shiftableTypesManager.getShiftable();

        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.PARENTHESIS, shiftable.getType());
    }

    @Test
    public void getShiftableDetectJsVariableDeclarationsSelected() {
        ActionContainer actionContainer = new ActionContainer(null, true, false);
        actionContainer.setFilename("foo.js");
        actionContainer.setDocumentText("var foo=0;\nvar bar=1;\n");
        actionContainer.setSelectedText("var foo=0;\nvar bar=1;");
        actionContainer.setPostfixChar("\n");
        ShiftableTypesManager shiftableTypesManager = new ShiftableTypesManager(actionContainer);

        AbstractShiftable shiftable = shiftableTypesManager.getShiftable();

        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.JS_VARIABLE_DECLARATIONS, shiftable.getType());
    }

    @Test
    public void getShiftableDetectSizzleSelectorSelected() {
        ActionContainer actionContainer = new ActionContainer(null, true, false);
        actionContainer.setFilename("foo.js");
        actionContainer.setDocumentText("$('div').hide();\n");
        actionContainer.setSelectedText("$('div')");
        actionContainer.setPostfixChar(".");
        ShiftableTypesManager shiftableTypesManager = new ShiftableTypesManager(actionContainer);

        AbstractShiftable shiftable = shiftableTypesManager.getShiftable();

        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.SIZZLE_SELECTOR, shiftable.getType());
    }

    @Test
    public void getShiftableDetectDocCommentTagCaretLine() {
        ActionContainer actionContainer = new ActionContainer(null, true, false);
        actionContainer.setFilename("foo.php");
        actionContainer.setDocumentText("<?php\n\n/**\n * @version 2.0\n**/\n");
        actionContainer.setCaretLine(" * @version 2.0");
        actionContainer.setPrefixChar("@");
        actionContainer.setPostfixChar("\n");
        ShiftableTypesManager shiftableTypesManager = new ShiftableTypesManager(actionContainer);

        AbstractShiftable shiftable = shiftableTypesManager.getShiftable();

        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.DOC_COMMENT_TAG, shiftable.getType());
    }

    @Test
    public void getShiftableDetectAccessTypeAtCaret() {
        ActionContainer actionContainer = new ActionContainer(null, true, false);
        actionContainer.setFilename("foo.php");
        actionContainer.setDocumentText("<?php\n\n/class Foo {\npublic function a(){}\nprotected function b(){}\nprivate function b(){}\n");
        actionContainer.setStringAtCaret("public");
        actionContainer.setPrefixChar(" ");
        actionContainer.setPostfixChar(" ");
        ShiftableTypesManager shiftableTypesManager = new ShiftableTypesManager(actionContainer);

        AbstractShiftable shiftable = shiftableTypesManager.getShiftable();
        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.ACCESS_TYPE, shiftable.getType());

        actionContainer.setStringAtCaret("private");
        shiftableTypesManager = new ShiftableTypesManager(actionContainer);
        shiftable = shiftableTypesManager.getShiftable();
        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.ACCESS_TYPE, shiftable.getType());

        actionContainer.setStringAtCaret("protected");
        shiftableTypesManager = new ShiftableTypesManager(actionContainer);
        shiftable = shiftableTypesManager.getShiftable();
        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.ACCESS_TYPE, shiftable.getType());
    }

    @Test
    public void getShiftableDetectAccessTypeSelected() {
        ActionContainer actionContainer = new ActionContainer(null, true, false);
        actionContainer.setFilename("foo.php");
        actionContainer.setDocumentText("<?php\n\n/class Foo {\npublic function a(){}\nprotected function b(){}\nprivate function b(){}\n");
        actionContainer.setSelectedText("public");
        actionContainer.setPrefixChar(" ");
        actionContainer.setPostfixChar(" ");
        ShiftableTypesManager shiftableTypesManager = new ShiftableTypesManager(actionContainer);

        AbstractShiftable shiftable = shiftableTypesManager.getShiftable();
        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.ACCESS_TYPE, shiftable.getType());

        actionContainer.setSelectedText("private");
        shiftableTypesManager = new ShiftableTypesManager(actionContainer);
        shiftable = shiftableTypesManager.getShiftable();
        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.ACCESS_TYPE, shiftable.getType());

        actionContainer.setSelectedText("protected");
        shiftableTypesManager = new ShiftableTypesManager(actionContainer);
        shiftable = shiftableTypesManager.getShiftable();
        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.ACCESS_TYPE, shiftable.getType());
    }

    /*@Test
    public void getShiftableDetectDictionaryWordOfSpecificFileTypeSelected() {
        ActionContainer actionContainer = new ActionContainer(null, true, false);
        actionContainer.setFilename("foo.txt");
        actionContainer.setDocumentText(" foo\n north\n monday\n dog\n");
        actionContainer.setSelectedText("north");
        actionContainer.setPrefixChar(" ");
        actionContainer.setPostfixChar("\n");
        ShiftableTypesManager shiftableTypesManager = new ShiftableTypesManager(actionContainer);

        AbstractShiftable shiftable = shiftableTypesManager.getShiftable();
        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.DICTIONARY_WORD, shiftable.getType());
    }*/

    @Test
    public void getShiftableDetectDictionaryWordSelected() {
        ActionContainer actionContainer = new ActionContainer(null, true, false);
        actionContainer.setFilename("foo.txt");
        actionContainer.setFileExtension("txt");
        actionContainer.setDocumentText(" foo\n north\n monday\n dog\n");
        actionContainer.setSelectedText("north");
        actionContainer.setPrefixChar(" ");
        actionContainer.setPostfixChar("\n");
        ShiftableTypesManager shiftableTypesManager = new ShiftableTypesManager(actionContainer);

        AbstractShiftable shiftable = shiftableTypesManager.getShiftable();
        assertNotNull(shiftable);
        assertEquals(ShiftableTypes.Type.DICTIONARY_WORD, shiftable.getType());
    }
}
