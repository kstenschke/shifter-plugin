package com.kstenschke.shifter.models.entities.shiftables;

import com.kstenschke.shifter.models.ActionContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        assertFalse(accessType.getInstance(null));
        assertFalse(accessType.getInstance(""));
        assertFalse(accessType.getInstance("foo"));

        assertTrue(accessType.getInstance("public"));
        assertTrue(accessType.getInstance("protected"));
        assertTrue(accessType.getInstance("private"));

        assertFalse(accessType.getInstance("public "));
        assertFalse(accessType.getInstance("protected "));
        assertFalse(accessType.getInstance("private "));
        */
    }

    @Test
    public void getShifted() {
        ActionContainer actionContainer = new ActionContainer(null, false, false);
/*
        accessType.getInstance("public");
        assertEquals("protected", accessType.getShifted(
                "public", actionContainer, null, null));

        accessType.getInstance("protected");
        assertEquals("private", accessType.getShifted(
                "protected", actionContainer, null, null));

        accessType.getInstance("private");
        assertEquals("public", accessType.getShifted(
                "private", actionContainer,null, null));

        actionContainer.setIsShiftUp(true);
        accessType.getInstance("public");
        assertEquals("private", accessType.getShifted(
                "public", actionContainer,null, null));

        accessType.getInstance("protected");
        assertEquals("public", accessType.getShifted(
                "protected", actionContainer,null, null));

        accessType.getInstance("private");
        assertEquals("protected", accessType.getShifted(
                "private", actionContainer,null, null));
                */
    }
}