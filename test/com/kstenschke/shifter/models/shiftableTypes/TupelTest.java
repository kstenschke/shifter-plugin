package com.kstenschke.shifter.models.shiftableTypes;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TupelTest {

    @Test
    public void isWordsTupel() {
        Tupel tupel = new Tupel(null);
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
        Tupel tupel = new Tupel(null);
        tupel.isWordsTupel("foo bar-baz");
        assertEquals("bar-baz foo", tupel.getShifted("foo bar-baz", true));
    }
}