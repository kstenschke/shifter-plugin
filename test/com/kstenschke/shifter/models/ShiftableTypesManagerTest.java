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
    public void getShiftableDetectSelectedTrailingCommentTest() {
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
    public void getShiftableDetectSelectedPhpDocContainingDataType() {
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
    public void getShiftableDetectSelectedPhpVariable() {
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
}
