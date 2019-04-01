package com.kstenschke.shifter.models.shiftable_types;

import com.kstenschke.shifter.models.ActionContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccessTypeTest {

    private AccessType accessType;

    @Before
    public void setUp() throws Exception {
        accessType = new AccessType(null);
    }

    @After
    public void tearDown() throws Exception {
        accessType = null;
    }

    @Test
    public void isAccessType() {
        /*
        assertFalse(accessType.getShiftableType(null));
        assertFalse(accessType.getShiftableType(""));
        assertFalse(accessType.getShiftableType("foo"));

        assertTrue(accessType.getShiftableType("public"));
        assertTrue(accessType.getShiftableType("protected"));
        assertTrue(accessType.getShiftableType("private"));

        assertFalse(accessType.getShiftableType("public "));
        assertFalse(accessType.getShiftableType("protected "));
        assertFalse(accessType.getShiftableType("private "));
        */
    }

    @Test
    public void getShifted() {
        ActionContainer actionContainer = new ActionContainer(null, false, false);
/*
        accessType.getShiftableType("public");
        assertEquals("protected", accessType.getShifted(
                "public", actionContainer, null, null));

        accessType.getShiftableType("protected");
        assertEquals("private", accessType.getShifted(
                "protected", actionContainer, null, null));

        accessType.getShiftableType("private");
        assertEquals("public", accessType.getShifted(
                "private", actionContainer,null, null));

        actionContainer.setIsShiftUp(true);
        accessType.getShiftableType("public");
        assertEquals("private", accessType.getShifted(
                "public", actionContainer,null, null));

        accessType.getShiftableType("protected");
        assertEquals("public", accessType.getShifted(
                "protected", actionContainer,null, null));

        accessType.getShiftableType("private");
        assertEquals("protected", accessType.getShifted(
                "private", actionContainer,null, null));
                */
    }
}