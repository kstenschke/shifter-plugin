package com.kstenschke.shifter.utils.natorder;

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
    int compareRight(String a, String b) {
        int bias = 0, offsetA = 0, offsetB = 0;

        // The longest run of digits wins. That aside, the greatest
        // value wins, but we can't know that it will until we've scanned
        // both numbers to know that they have the same magnitude, so we
        // remember it in BIAS.
        for (; ; offsetA++, offsetB++) {
            char characterA = charAt(a, offsetA);
            char characterB = charAt(b, offsetB);

            if (!Character.isDigit(characterA) && !Character.isDigit(characterB)) {
                return bias;
            }
            if (!Character.isDigit(characterA)) {
                return -1;
            }
            if (!Character.isDigit(characterB)) {
                return +1;
            }
            if (characterA == 0 && characterB == 0) {
                return bias;
            }

            if (characterA < characterB) {
                if (bias == 0) {
                    bias = -1;
                }
            } else if (characterA > characterB) {
                if (bias == 0)
                    bias = +1;
            }
        }
    }

    public int compare(Object object1, Object object2) {
        String stringA = object1.toString();
        String stringB = object2.toString();

        int nza, nzb, result;
        int offsetA = 0, offsetB = 0;
        char characterA, characterB;

        while (true) {
            // Only count the number of zeroes leading the last number compared
            nza = nzb = 0;

            characterA = charAt(stringA, offsetA);
            characterB = charAt(stringB, offsetB);

            // Skip over leading spaces or zeros
            while (Character.isSpaceChar(characterA) || characterA == '0') {
                nza = characterA == '0'
                        ? nza + 1
                        // Only count consecutive zeroes
                        : 0;

                characterA = charAt(stringA, ++offsetA);
            }

            while (Character.isSpaceChar(characterB) || characterB == '0') {
                nzb = characterB == '0'
                        ? nzb + 1
                        // Only count consecutive zeroes
                        : 0;

                characterB = charAt(stringB, ++offsetB);
            }

            // Process run of digits
            if (Character.isDigit(characterA) && Character.isDigit(characterB)) {
                if ((result = compareRight(stringA.substring(offsetA), stringB.substring(offsetB))) != 0) {
                    return result;
                }
            }

            if (characterA == 0 && characterB == 0) {
                // The strings compare the same. Perhaps the caller will want to call strcmp to break the tie.
                return nza - nzb;
            }

            if (characterA < characterB) {
                return -1;
            }
            if (characterA > characterB) {
                return +1;
            }

            ++offsetA;
            ++offsetB;
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