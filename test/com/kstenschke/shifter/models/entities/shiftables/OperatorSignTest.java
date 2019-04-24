package com.kstenschke.shifter.models.entities.shiftables;

import org.junit.Ignore;
import org.junit.Test;

public class OperatorSignTest {

    @Test
    public void isOperatorSign() {
        /*
        assertFalse(OperatorSign.getInstance(null));
        assertFalse(OperatorSign.getInstance(""));
        assertFalse(OperatorSign.getInstance(";"));
        assertFalse(OperatorSign.getInstance("§"));
        assertFalse(OperatorSign.getInstance("°"));
        assertFalse(OperatorSign.getInstance("("));
        assertFalse(OperatorSign.getInstance(")"));
        assertFalse(OperatorSign.getInstance("\\"));
        */
        //assertFalse(OperatorSign.getInstance("+-<>*/%"));
        /*
        assertFalse(OperatorSign.getInstance("+-"));
        assertFalse(OperatorSign.getInstance("+="));
        assertFalse(OperatorSign.getInstance("=+"));

        assertTrue(OperatorSign.getInstance("+"));
        assertTrue(OperatorSign.getInstance("-"));
        assertTrue(OperatorSign.getInstance("*"));
        assertTrue(OperatorSign.getInstance("/"));
        assertTrue(OperatorSign.getInstance(">"));
        assertTrue(OperatorSign.getInstance("<"));
        assertTrue(OperatorSign.getInstance("%"));
        */
    }

    @Test
    @Ignore
    public void isWhitespaceWrappedOperator() {
    }

    @Test
    @Ignore
    public void getShifted() {
    }
}