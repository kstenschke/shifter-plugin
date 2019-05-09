package com.kstenschke.shifter.models.entities.shiftables;

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.entities.AbstractShiftable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CamelCaseStringTest {

    private CamelCaseString camelCaseString;
    private AbstractShiftable shiftable;
    private ActionContainer actionContainer;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        camelCaseString = null;
        shiftable = null;
        actionContainer = null;

    }

    @Test
    public void getInstanceNoActionContainer() {
        camelCaseString = new CamelCaseString(null);
        assertNull(camelCaseString.getInstance());
    }

    @Test
    public void getInstanceEmpty() {
        actionContainer = new ActionContainer(null, true, false);
        actionContainer.setSelectedText("");
        camelCaseString = new CamelCaseString(actionContainer);
        assertNull(camelCaseString.getInstance());
    }

    @Test
    public void getInstanceStartsNumeric() {
        actionContainer = new ActionContainer(null, true, false);
        actionContainer.setSelectedText("10foo");
        camelCaseString = new CamelCaseString(actionContainer);
        assertNull(camelCaseString.getInstance());
    }

    @Test
    public void getInstanceNotCamelCase() {
        actionContainer = new ActionContainer(null, true, false);
        actionContainer.setSelectedText("foo");
        camelCaseString = new CamelCaseString(actionContainer);
        assertNull(camelCaseString.getInstance());
    }

    @Test
    public void getInstanceCamelCaseButContainsSpace() {
        actionContainer = new ActionContainer(null, true, false);
        actionContainer.setSelectedText("fooBar baz");
        camelCaseString = new CamelCaseString(actionContainer);
        assertNull(camelCaseString.getInstance());
    }

    @Test
    public void getInstanceCamelCase() {
        actionContainer = new ActionContainer(null, true, false);
        actionContainer.setSelectedText("fooBar");
        camelCaseString = new CamelCaseString(actionContainer);
        assertNotNull(camelCaseString.getInstance());
    }

    /*@Test
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
    }*/
}