package com.kstenschke.shifter.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsArrayTest {

    @Test
    public void testFindPositionInArray() throws Exception {
        assertEquals(0, UtilsArray.findPositionInArray(new String[]{"0", "1", "2", "3"}, "0"));
        assertEquals(1, UtilsArray.findPositionInArray(new String[]{"0", "1", "2", "3"}, "1"));
        assertEquals(-1, UtilsArray.findPositionInArray(new String[]{"0", "1", "2", "3"}, "9"));
    }

    @Test
    public void testImplode() throws Exception {
        String[] items = new String[]{"Bam", "Bam", "Hey", "What", "A", "Bam"};

        assertEquals("BamBamHeyWhatABam", UtilsArray.implode(items, ""));
        assertEquals("Bam Bam Hey What A Bam", UtilsArray.implode(items, " ");
    }

    @Test
    public void testMergeStringArrays() throws Exception {
        assertArrayEquals(new String[]{"a", "b"}, UtilsArray.mergeStringArrays(new String[]{"a"}, new String[]{"b"}));
        assertArrayEquals(new String[]{"a", "b", "c", "d"}, UtilsArray.mergeStringArrays(new String[]{"a", "b"}, new String[]{"c", "d"}));
        assertArrayEquals(new String[]{"a", "b", "c", "d"}, UtilsArray.mergeStringArrays(new String[]{"a", "b"}, new String[]{"b", "c", "d"}));
        assertArrayEquals(new String[]{"a", "b", "c", "d"}, UtilsArray.mergeStringArrays(new String[]{"a", "b", "c"}, new String[]{"b", "c", "d"});
    }
}