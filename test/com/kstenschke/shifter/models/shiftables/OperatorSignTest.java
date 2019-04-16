package com.kstenschke.shifter.models.shiftables;

import org.junit.Ignore;
import org.junit.Test;

public class OperatorSignTest {

    @Test
    public void isOperatorSign() {
        /*
        assertFalse(OperatorSign.getShiftableType(null));
        assertFalse(OperatorSign.getShiftableType(""));
        assertFalse(OperatorSign.getShiftableType(";"));
        assertFalse(OperatorSign.getShiftableType("§"));
        assertFalse(OperatorSign.getShiftableType("°"));
        assertFalse(OperatorSign.getShiftableType("("));
        assertFalse(OperatorSign.getShiftableType(")"));
        assertFalse(OperatorSign.getShiftableType("\\"));
        */
        //assertFalse(OperatorSign.getShiftableType("+-<>*/%"));
        /*
        assertFalse(OperatorSign.getShiftableType("+-"));
        assertFalse(OperatorSign.getShiftableType("+="));
        assertFalse(OperatorSign.getShiftableType("=+"));

        assertTrue(OperatorSign.getShiftableType("+"));
        assertTrue(OperatorSign.getShiftableType("-"));
        assertTrue(OperatorSign.getShiftableType("*"));
        assertTrue(OperatorSign.getShiftableType("/"));
        assertTrue(OperatorSign.getShiftableType(">"));
        assertTrue(OperatorSign.getShiftableType("<"));
        assertTrue(OperatorSign.getShiftableType("%"));
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