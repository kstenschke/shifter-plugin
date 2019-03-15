package com.kstenschke.shifter.models.shiftable_types;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class OperatorSignTest {

    @Test
    public void isOperatorSign() {
        assertFalse(OperatorSign.isOperatorSign(null));
        assertFalse(OperatorSign.isOperatorSign(""));
        assertFalse(OperatorSign.isOperatorSign(";"));
        assertFalse(OperatorSign.isOperatorSign("§"));
        assertFalse(OperatorSign.isOperatorSign("°"));
        assertFalse(OperatorSign.isOperatorSign("("));
        assertFalse(OperatorSign.isOperatorSign(")"));
        assertFalse(OperatorSign.isOperatorSign("\\"));

        assertFalse(OperatorSign.isOperatorSign("+-<>*/%"));
        assertFalse(OperatorSign.isOperatorSign("+-"));
        assertFalse(OperatorSign.isOperatorSign("+="));
        assertFalse(OperatorSign.isOperatorSign("=+"));

        assertTrue(OperatorSign.isOperatorSign("+"));
        assertTrue(OperatorSign.isOperatorSign("-"));
        assertTrue(OperatorSign.isOperatorSign("*"));
        assertTrue(OperatorSign.isOperatorSign("/"));
        assertTrue(OperatorSign.isOperatorSign(">"));
        assertTrue(OperatorSign.isOperatorSign("<"));
        assertTrue(OperatorSign.isOperatorSign("%"));
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