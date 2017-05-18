package com.kstenschke.shifter.models.comparators.natorder;

/*
 NaturalOrderComparator.java -- Perform 'natural order' comparisons of strings in Java.
 Copyright (C) 2003 by Pierre-Luc Paour <natorder@paour.com>

 Based on the C version by Martin Pool, of which this is more or less a straight conversion.
 Copyright (C) 2000 by Martin Pool <mbp@humbug.org.au>

 This software is provided 'as-is', without any express or implied
 warranty.  In no event will the authors be held liable for any damages
 arising from the use of this software.

 Permission is granted to anyone to use this software for any purpose,
 including commercial applications, and to alter it and redistribute it
 freely, subject to the following restrictions:

 1. The origin of this software must not be misrepresented; you must not
 claim that you wrote the original software. If you use this software
 in a product, an acknowledgment in the product documentation would be
 appreciated but is not required.
 2. Altered source versions must be plainly marked as such, and must not be
 misrepresented as being the original software.
 3. This notice may not be removed or altered from any source distribution.
 */

import java.util.*;

public class NaturalOrderComparator implements Comparator {

    /**
     * @param  str1 The first string
     * @param  str2 The second string
     * @return -1, 0 or 1
     */
    int compareRight(String str1, String str2) {
        int bias = 0, offset1 = 0, offset2 = 0;

        // The longest run of digits wins. That aside, the greatest
        // value wins, but we can't know that it will until we've scanned
        // both numbers to know that they have the same magnitude, so we
        // remember it in BIAS.
        for (; ; offset1++, offset2++) {
            char character1 = charAt(str1, offset1);
            char character2 = charAt(str2, offset2);

            if (!Character.isDigit(character1) && !Character.isDigit(character2)) {
                return bias;
            }
            if (!Character.isDigit(character1)) {
                return -1;
            }
            if (!Character.isDigit(character2)) {
                return 1;
            }
            if (character1 == 0 && character2 == 0) {
                return bias;
            }

            if (character1 < character2) {
                if (bias == 0) {
                    bias = -1;
                }
            } else if (character1 > character2 && bias == 0) {
                bias = 1;
            }
        }
    }

    public int compare(Object object1, Object object2) {
        String str1 = object1.toString();
        String str2 = object2.toString();

        int amountLeadingZeroesInStr1, amountLeadingZeroesInStr2, result;
        int offset1 = 0, offset2 = 0;
        char character1, character2;

        while (true) {
            // Only count the number of zeroes leading the last number compared
            amountLeadingZeroesInStr1 = amountLeadingZeroesInStr2 = 0;

            character1 = charAt(str1, offset1);
            character2 = charAt(str2, offset2);

            // Skip over leading spaces or zeros
            while (Character.isSpaceChar(character1) || character1 == '0') {
                amountLeadingZeroesInStr1 = character1 == '0'
                        ? amountLeadingZeroesInStr1 + 1
                        // Only count consecutive zeroes
                        : 0;

                character1 = charAt(str1, ++offset1);
            }

            while (Character.isSpaceChar(character2) || character2 == '0') {
                amountLeadingZeroesInStr2 = character2 == '0'
                        ? amountLeadingZeroesInStr2 + 1
                        // Only count consecutive zeroes
                        : 0;

                character2 = charAt(str2, ++offset2);
            }

            // Process run of digits
            if (Character.isDigit(character1) && Character.isDigit(character2)) {
                if ((result = compareRight(str1.substring(offset1), str2.substring(offset2))) != 0) {
                    return result;
                }
            }

            if (character1 == 0 && character2 == 0) {
                // The strings compare the same. Perhaps the caller will want to call strcmp to break the tie.
                return amountLeadingZeroesInStr1 - amountLeadingZeroesInStr2;
            }
            if (character1 < character2) {
                return -1;
            }
            if (character1 > character2) {
                return 1;
            }

            ++offset1;
            ++offset2;
        }
    }

    static char charAt(String str, int offset) {
        return offset >= str.length() ? 0 : str.charAt(offset);
    }

    public static void main(String[] args) {
        String[] strings = new String[]{"1-2", "1-02", "1-20", "10-20", "fred", "jane", "pic01",
                "pic2", "pic02", "pic02a", "pic3", "pic4", "pic 4 else", "pic 5", "pic05", "pic 5",
                "pic 5 something", "pic 6", "pic   7", "pic100", "pic100a", "pic120", "pic121",
                "pic02000", "tom", "x2-g8", "x2-y7", "x2-y08", "x8-y8"};

        List orig = Arrays.asList(strings);

        System.out.println("Original: " + orig);

        List scrambled = Arrays.asList(strings);
        Collections.shuffle(scrambled);

        System.out.println("Scrambled: " + scrambled);

        Collections.sort(scrambled, new NaturalOrderComparator());

        System.out.println("Sorted: " + scrambled);
    }
}