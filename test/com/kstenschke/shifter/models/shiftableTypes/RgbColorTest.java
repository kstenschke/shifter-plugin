package com.kstenschke.shifter.models.shiftableTypes;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class RgbColorTest {

    @Test
    public void isRgbColorString() {
        assertFalse(RgbColor.isRgbColorString(null, null));
        assertFalse(RgbColor.isRgbColorString(null, ""));
        assertFalse(RgbColor.isRgbColorString("", null));
        assertFalse(RgbColor.isRgbColorString("", ""));
        assertFalse(RgbColor.isRgbColorString("", "#"));
        assertFalse(RgbColor.isRgbColorString("1", "#"));
        assertFalse(RgbColor.isRgbColorString("11", "#"));

        assertTrue(RgbColor.isRgbColorString("111", "#"));

        assertFalse(RgbColor.isRgbColorString("1111", "#"));
        assertFalse(RgbColor.isRgbColorString("11111", "#"));

        assertTrue(RgbColor.isRgbColorString("111111", "#"));
        assertTrue(RgbColor.isRgbColorString("11aaff", "#"));
        assertTrue(RgbColor.isRgbColorString("ffffff", "#"));

        assertFalse(RgbColor.isRgbColorString("gfffff", "#"));
        assertFalse(RgbColor.isRgbColorString("ffgfff", "#"));
        assertFalse(RgbColor.isRgbColorString("ffffgf", "#"));

        assertFalse(RgbColor.isRgbColorString("fffffff", "#"));
    }

    @Test
    @Ignore
    public void getShifted() {
    }
}