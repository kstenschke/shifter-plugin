package com.kstenschke.shifter.models.shiftableTypes;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccessTypeTest {

    @Test
    public void isAccessType() {
        AccessType accessType = new AccessType();

        assertFalse(accessType.isAccessType(null));
        assertFalse(accessType.isAccessType(""));
        assertFalse(accessType.isAccessType("foo"));

        assertTrue(accessType.isAccessType("public"));
        assertTrue(accessType.isAccessType("protected"));
        assertTrue(accessType.isAccessType("private"));

        assertFalse(accessType.isAccessType("public "));
        assertFalse(accessType.isAccessType("protected "));
        assertFalse(accessType.isAccessType("private "));
    }

    @Test
    public void getShifted() {
        AccessType accessType = new AccessType();

        accessType.isAccessType("public");
        assertEquals("protected", accessType.getShifted("public", false));

        accessType.isAccessType("protected");
        assertEquals("private", accessType.getShifted("protected", false));

        accessType.isAccessType("private");
        assertEquals("public", accessType.getShifted("private", false));

        accessType.isAccessType("public");
        assertEquals("private", accessType.getShifted("public", true));

        accessType.isAccessType("protected");
        assertEquals("public", accessType.getShifted("protected", true));

        accessType.isAccessType("private");
        assertEquals("protected", accessType.getShifted("private", true));
    }
}