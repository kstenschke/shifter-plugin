package com.kstenschke.shifter.models.shiftables;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TupelTest {

    private Tupel tupel;

    @Before
    public void setUp() throws Exception {
        tupel = new Tupel(null);
    }

    @After
    public void tearDown() throws Exception {
        tupel = null;
    }

    @Test
    public void isWordsTupel() {
        /*
        assertFalse(tupel.getShiftableType(null));
        assertFalse(tupel.getShiftableType(""));
        assertFalse(tupel.getShiftableType("foo"));
        assertFalse(tupel.getShiftableType("fooBar"));

        assertTrue(tupel.getShiftableType("foo,bar"));
        assertTrue(tupel.getShiftableType("foo.bar"));
        assertTrue(tupel.getShiftableType("foo=bar"));
        assertTrue(tupel.getShiftableType("foo bar"));
        assertTrue(tupel.getShiftableType("foo bar-baz"));
        */
    }

    @Test
    @Ignore
    public void getShifted() {
        /*
        tupel.getShiftableType("foo bar-baz");
        assertEquals("bar-baz foo", tupel.getShifted("foo bar-baz", true));
        */
    }
}