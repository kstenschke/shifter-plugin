package com.kstenschke.shifter.models.shiftable_types;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

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
        assertFalse(tupel.isWordsTupel(null));
        assertFalse(tupel.isWordsTupel(""));
        assertFalse(tupel.isWordsTupel("foo"));
        assertFalse(tupel.isWordsTupel("fooBar"));

        assertTrue(tupel.isWordsTupel("foo,bar"));
        assertTrue(tupel.isWordsTupel("foo.bar"));
        assertTrue(tupel.isWordsTupel("foo=bar"));
        assertTrue(tupel.isWordsTupel("foo bar"));
        assertTrue(tupel.isWordsTupel("foo bar-baz"));
    }

    @Test
    @Ignore
    public void getShifted() {
        tupel.isWordsTupel("foo bar-baz");
        assertEquals("bar-baz foo", tupel.getShifted("foo bar-baz", true));
    }
}