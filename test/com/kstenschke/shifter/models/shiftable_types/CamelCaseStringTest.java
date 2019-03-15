package com.kstenschke.shifter.models.shiftable_types;

import org.junit.Test;

import static org.junit.Assert.*;

public class CamelCaseStringTest {

    @Test
    public void isCamelCase() {
        assertFalse(CamelCaseString.isCamelCase(null));
        assertFalse(CamelCaseString.isCamelCase(""));
        assertFalse(CamelCaseString.isCamelCase("f"));
        assertFalse(CamelCaseString.isCamelCase("foobar"));
        assertFalse(CamelCaseString.isCamelCase("Foo"));
        assertFalse(CamelCaseString.isCamelCase("FooBar baz"));
        assertFalse(CamelCaseString.isCamelCase("foo1"));

        assertTrue(CamelCaseString.isCamelCase("fooBar"));
        assertTrue(CamelCaseString.isCamelCase("FooBar"));
        assertTrue(CamelCaseString.isCamelCase("foo1Bar"));
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