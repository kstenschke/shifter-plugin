package com.kstenschke.shifter.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShiftableWordTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetShifted() throws Exception {
        // ------------------------------------------------- CSS properties
        // ------------------------------------------------- test: convert 3 digits RGB to 6 digits
        String filename     = "style.css";

        String editorText   =
            "body {" + "\n" +
            "   color: #111;" + "\n" +
            "}" + "\n";
        String line         = "   color: #111;";
        int caretOffset     = 18;

        ShiftableWord shiftableWord = new ShiftableWord("111", "#", ";", line, editorText, caretOffset, filename, null);
        assertEquals("121212", shiftableWord.getShifted(true, null));
        assertEquals("101010", shiftableWord.getShifted(false, null));

        // ------------------------------------------------- test: shift RGB up/down
        editorText   =
                "body {" + "\n" +
                        "   color: #111111;" + "\n" +
                        "}" + "\n";
        line         = "   color: #111111;";
        caretOffset  = 18;

        shiftableWord = new ShiftableWord("111111", "#", ";", line, editorText, caretOffset, filename, null);
        assertEquals("121212", shiftableWord.getShifted(true, null));
        assertEquals("101010", shiftableWord.getShifted(false, null));
    }

    @Test
    public void testPostProcess() throws Exception {

    }
}