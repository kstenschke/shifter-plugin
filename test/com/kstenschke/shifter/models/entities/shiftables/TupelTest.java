package com.kstenschke.shifter.models.entities.shiftables;

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
        assertFalse(tupel.getInstance(null));
        assertFalse(tupel.getInstance(""));
        assertFalse(tupel.getInstance("foo"));
        assertFalse(tupel.getInstance("fooBar"));

        assertTrue(tupel.getInstance("foo,bar"));
        assertTrue(tupel.getInstance("foo.bar"));
        assertTrue(tupel.getInstance("foo=bar"));
        assertTrue(tupel.getInstance("foo bar"));
        assertTrue(tupel.getInstance("foo bar-baz"));
        */
    }

    @Test
    @Ignore
    public void getShifted() {
        /*
        tupel.getInstance("foo bar-baz");
        assertEquals("bar-baz foo", tupel.getShifted("foo bar-baz", true));
        */
    }
}