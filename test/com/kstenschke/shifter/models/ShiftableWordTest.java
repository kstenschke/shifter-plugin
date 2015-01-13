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
        // ------------------------------------------------- test: shift numbers
        String filename     = "style.css";

        String editorText   =
                "body {" + "\n" +
                        "   margin: 0 1 2px 3%;" + "\n" +
                        "}" + "\n";
        String line         = "   margin: 0 1 2 3;";
        int caretOffset     = 18;

        ShiftableWord shiftableWord = new ShiftableWord("0", " ", " ", line, editorText, caretOffset, filename, null);
        assertEquals("1", shiftableWord.getShifted(true, null));
        assertEquals("-1", shiftableWord.getShifted(false, null));

        caretOffset = 20;
        shiftableWord = new ShiftableWord("1", " ", " ", line, editorText, caretOffset, filename, null);
        assertEquals("2", shiftableWord.getShifted(true, null));
        assertEquals("0", shiftableWord.getShifted(false, null));

        caretOffset = 22;
        shiftableWord = new ShiftableWord("2", " ", "px", line, editorText, caretOffset, filename, null);
        assertEquals("3", shiftableWord.getShifted(true, null));
        assertEquals("1", shiftableWord.getShifted(false, null));

        caretOffset = 24;
        shiftableWord = new ShiftableWord("3", " ", "%", line, editorText, caretOffset, filename, null);
        assertEquals("4", shiftableWord.getShifted(true, null));
        assertEquals("2", shiftableWord.getShifted(false, null));

        // ------------------------------------------------- test: convert 3 digits RGB to 6 digits
        editorText   =
            "body {" + "\n" +
            "   color: #111;" + "\n" +
            "}" + "\n";
        line         = "   color: #111;";
        caretOffset     = 18;

        shiftableWord = new ShiftableWord("111", "#", ";", line, editorText, caretOffset, filename, null);
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

        // ------------------------------------------------- test: shift UNIX timestamp by 1 day
        filename = "index.php";
        editorText   =
                "<?php" + "\n" +
                "   $timestamp = 1262304000;" + "\n";  // 1.1.2010
        line         = "   $timestamp = 1262304000;";
        caretOffset  = 23;

        shiftableWord = new ShiftableWord("1262304000", " ", ";", line, editorText, caretOffset, filename, null);
        assertEquals("1262390400", shiftableWord.getShifted(true, null));
        assertEquals("1262217600", shiftableWord.getShifted(false, null));
    }
}