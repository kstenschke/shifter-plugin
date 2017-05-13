package com.kstenschke.shifter.models;

import org.junit.Before;
import org.junit.Test;

public class ShiftableLineTest {

    @Before
    public void setup() {

    }

    @Test
    public void testGetShifted() throws Exception {
        String[] lines = {
                "Eight (8) is a number that comes after 7 and before 9.",
                "The cardinal number between seven and nine is eight.",
                "the fourth tone of an ascending diatonic scale, or a tone three degrees above.",
                "Monday is my favorite day of the week.",
                "There are 24 hours in a day.",
        };

        String line1 =lines[0];

        CharSequence editorText =
            lines[0] + "\n" ;

        int caretOffset = 0;
        String filename = "test.txt";

//        ShiftableLine shiftableLine  = new ShiftableLine(document, line1, caretOffset);
//        String result = shiftableLine.getShifted(true, null, null);
    }
}