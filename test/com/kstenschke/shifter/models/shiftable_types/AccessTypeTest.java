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
        accessType = new AccessType();
    }

    @After
    public void tearDown() throws Exception {
        accessType = null;
    }

    @Test
    public void isAccessType() {
        assertFalse(accessType.isApplicable(null));
        assertFalse(accessType.isApplicable(""));
        assertFalse(accessType.isApplicable("foo"));

        assertTrue(accessType.isApplicable("public"));
        assertTrue(accessType.isApplicable("protected"));
        assertTrue(accessType.isApplicable("private"));

        assertFalse(accessType.isApplicable("public "));
        assertFalse(accessType.isApplicable("protected "));
        assertFalse(accessType.isApplicable("private "));
    }

    @Test
    public void getShifted() {
        ActionContainer actionContainer = new ActionContainer(null, false, false);

        accessType.isApplicable("public");
        assertEquals("protected", accessType.getShifted(
                "public", actionContainer, null, null));

        accessType.isApplicable("protected");
        assertEquals("private", accessType.getShifted(
                "protected", actionContainer, null, null));

        accessType.isApplicable("private");
        assertEquals("public", accessType.getShifted(
                "private", actionContainer,null, null));

        actionContainer.setIsShiftUp(true);
        accessType.isApplicable("public");
        assertEquals("private", accessType.getShifted(
                "public", actionContainer,null, null));

        accessType.isApplicable("protected");
        assertEquals("public", accessType.getShifted(
                "protected", actionContainer,null, null));

        accessType.isApplicable("private");
        assertEquals("protected", accessType.getShifted(
                "private", actionContainer,null, null));
    }
}