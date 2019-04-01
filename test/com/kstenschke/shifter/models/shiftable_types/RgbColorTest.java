package com.kstenschke.shifter.models.shiftable_types;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class RgbColorTest {

    @Test
    public void isRgbColorString() {
        assertFalse(RgbColor.getShiftableType(null, null));
        assertFalse(RgbColor.getShiftableType(null, ""));
        assertFalse(RgbColor.getShiftableType("", null));
        assertFalse(RgbColor.getShiftableType("", ""));
        assertFalse(RgbColor.getShiftableType("", "#"));
        assertFalse(RgbColor.getShiftableType("1", "#"));
        assertFalse(RgbColor.getShiftableType("11", "#"));

        assertTrue(RgbColor.getShiftableType("111", "#"));

        assertFalse(RgbColor.getShiftableType("1111", "#"));
        assertFalse(RgbColor.getShiftableType("11111", "#"));

        assertTrue(RgbColor.getShiftableType("111111", "#"));
        assertTrue(RgbColor.getShiftableType("11aaff", "#"));
        assertTrue(RgbColor.getShiftableType("ffffff", "#"));

        assertFalse(RgbColor.getShiftableType("gfffff", "#"));
        assertFalse(RgbColor.getShiftableType("ffgfff", "#"));
        assertFalse(RgbColor.getShiftableType("ffffgf", "#"));

        assertFalse(RgbColor.getShiftableType("fffffff", "#"));
    }

    @Test
    @Ignore
    public void getShifted() {
    }
}