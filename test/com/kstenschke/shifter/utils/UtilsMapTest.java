package com.kstenschke.shifter.utils;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class UtilsMapTest {

    @Test
    public void testGetSumOfValues() throws Exception {
        HashMap<String, Integer> map = new HashMap<String, Integer>() {{
            put("a", 1);
            put("b", 2);
            put("c", 3);
            put("d", 4);
        }};

        assertEquals( 10, UtilsMap.getSumOfValues(map) );

        assertEquals( 0, UtilsMap.getSumOfValues(new HashMap<String, Integer>()) );
    }

    @Test
    public void testGetKeyOfHighestValue() throws Exception {
        HashMap<String, Integer> map = new HashMap<String, Integer>() {{
            put("a", 1);
            put("b", 2);
            put("c", 3);
            put("d", 4);
        }};

        assertEquals( "d", UtilsMap.getKeyOfHighestValue(map) );
    }
}