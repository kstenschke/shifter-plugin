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
        assertFalse(accessType.isShiftable(null));
        assertFalse(accessType.isShiftable(""));
        assertFalse(accessType.isShiftable("foo"));

        assertTrue(accessType.isShiftable("public"));
        assertTrue(accessType.isShiftable("protected"));
        assertTrue(accessType.isShiftable("private"));

        assertFalse(accessType.isShiftable("public "));
        assertFalse(accessType.isShiftable("protected "));
        assertFalse(accessType.isShiftable("private "));
        */
    }

    @Test
    public void getShifted() {
        ActionContainer actionContainer = new ActionContainer(null, false, false);
/*
        accessType.isShiftable("public");
        assertEquals("protected", accessType.getShifted(
                "public", actionContainer, null, null));

        accessType.isShiftable("protected");
        assertEquals("private", accessType.getShifted(
                "protected", actionContainer, null, null));

        accessType.isShiftable("private");
        assertEquals("public", accessType.getShifted(
                "private", actionContainer,null, null));

        actionContainer.setIsShiftUp(true);
        accessType.isShiftable("public");
        assertEquals("private", accessType.getShifted(
                "public", actionContainer,null, null));

        accessType.isShiftable("protected");
        assertEquals("public", accessType.getShifted(
                "protected", actionContainer,null, null));

        accessType.isShiftable("private");
        assertEquals("protected", accessType.getShifted(
                "private", actionContainer,null, null));
                */
    }
}