package com.kstenschke.shifter.models.shiftables;

import org.junit.Test;

import static org.junit.Assert.*;

public class CamelCaseStringTest {

    @Test
    public void isCamelCase() {
        /*
        assertFalse(CamelCaseString.getShiftableType(null));
        assertFalse(CamelCaseString.getShiftableType(""));
        assertFalse(CamelCaseString.getShiftableType("f"));
        assertFalse(CamelCaseString.getShiftableType("foobar"));
        assertFalse(CamelCaseString.getShiftableType("Foo"));
        assertFalse(CamelCaseString.getShiftableType("FooBar baz"));
        assertFalse(CamelCaseString.getShiftableType("foo1"));

        assertTrue(CamelCaseString.getShiftableType("fooBar"));
        assertTrue(CamelCaseString.getShiftableType("FooBar"));
        assertTrue(CamelCaseString.getShiftableType("foo1Bar"));
        */
    }

    @Test
    public void isWordPair() {
        assertFalse(CamelCaseString.isWordPair(null));
        assertFalse(CamelCaseString.isWordPair(""));
        assertFalse(CamelCaseString.isWordPair("f"));
        assertFalse(CamelCaseString.isWordPair("foo"));
        assertFalse(CamelCaseString.isWordPair("foo bar"));
        assertFalse(CamelCaseString.isWordPair("fooBarBaz"));
        assertFalse(CamelCaseString.isWordPair(" bar"));

        assertTrue(CamelCaseString.isWordPair("fooBar"));
        assertTrue(CamelCaseString.isWordPair("FooBar"));
        assertTrue(CamelCaseString.isWordPair("foo1Bar"));
    }

    @Test
    public void flipWordPairOrder() {
        assertNull(CamelCaseString.flipWordPairOrder(null));

        assertEquals("", CamelCaseString.flipWordPairOrder(""));
        assertEquals("foo", CamelCaseString.flipWordPairOrder("foo"));
        assertEquals("fooBarBaz", CamelCaseString.flipWordPairOrder("fooBarBaz"));
        assertEquals("foo bar", CamelCaseString.flipWordPairOrder("foo bar"));

        assertEquals("barFoo", CamelCaseString.flipWordPairOrder("fooBar"));
        assertEquals("BarFoo", CamelCaseString.flipWordPairOrder("FooBar"));
        assertEquals("Bar2Foo1", CamelCaseString.flipWordPairOrder("Foo1Bar2"));
    }

    @Test
    public void getShifted() {
        assertEquals("barFoo", CamelCaseString.flipWordPairOrder("fooBar"));
        assertEquals("BarFoo", CamelCaseString.flipWordPairOrder("FooBar"));
    }
}