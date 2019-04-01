package com.kstenschke.shifter.models.shiftable_types;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

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