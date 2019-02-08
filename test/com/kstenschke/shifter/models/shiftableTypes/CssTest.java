package com.kstenschke.shifter.models.shiftableTypes;

import org.junit.Test;

import static org.junit.Assert.*;

public class CssTest {

    @Test
    public void getShifted() {
        assertEquals(
                "color: #fff;font-size: 10px;",
                Css.getShifted(
                        "color: #fff;\n" +
                              "font-size: 10px;"
                )
        );
        assertEquals(
                "color: #fff;font-size: 10px;",
                Css.getShifted(
                              "font-size: 10px;\n" +
                                      "color: #fff;"
                )
        );
    }
}