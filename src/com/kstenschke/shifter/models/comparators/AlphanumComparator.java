package com.kstenschke.shifter.models.comparators;

/*
 * The Alphanum Algorithm is an improved sorting algorithm for strings
 * containing numbers.  Instead of sorting numbers in ASCII order like
 * a standard sort, this algorithm sorts numbers in numeric order.
 *
 * The Alphanum Algorithm is discussed at http://www.DaveKoelle.com
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

import org.apache.commons.lang.StringUtils;
import java.util.Comparator;

public class AlphanumComparator implements Comparator<String>
{
    private Comparator<String> comparator = new NaturalComparator();

    public AlphanumComparator(Comparator<String> comparator) {
        this.comparator = comparator;
    }

    public AlphanumComparator() {
    }

    private boolean isDigit(char ch) {
        return ch >= 48 && ch <= 57;
    }

    // Length of string is passed in for improved efficiency (only need to calculate it once)
    private String getChunk(String str, int strLength, int offset) {
        StringBuilder chunk = new StringBuilder();
        char currentChar = str.charAt(offset);
        chunk.append(currentChar);
        offset++;

        return isDigit(currentChar)
                ? extractChunkOfDigits(str, strLength, offset, chunk).toString()
                : extractChunkOfNonDigits(str, strLength, offset, chunk).toString();
    }

    public int compare(String s1, String s2) {
        s1 = prepareStringForCompare(s1);
        s2 = prepareStringForCompare(s2);

        int thisMarker = 0;
        int thatMarker = 0;
        int s1Length = s1.length();
        int s2Length = s2.length();

        while (thisMarker < s1Length && thatMarker < s2Length) {
            String thisChunk = getChunk(s1, s1Length, thisMarker);
            thisMarker += thisChunk.length();

            String thatChunk = getChunk(s2, s2Length, thatMarker);
            thatMarker += thatChunk.length();

            // If both chunks contain numeric characters, sort them numerically
            int result;
            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
                // Simple chunk comparison by length.
                int thisChunkLength = thisChunk.length();
                result = thisChunkLength - thatChunk.length();
                // If equal, the first different number counts
                if (0 == result) {
                    for (int i = 0; i < thisChunkLength; i++) {
                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
                        if (0 != result) return result;
                    }
                }
            } else {
                // Both compared characters are alphabetic
                result = comparator.compare(thisChunk, thatChunk);
            }

            if (0 != result) return result;
        }

        return s1Length - s2Length;
    }

    private StringBuilder extractChunkOfNonDigits(String str, int strLength, int offset, StringBuilder chunk) {
        char currentChar;
        while (offset < strLength) {
            currentChar = str.charAt(offset);
            if (isDigit(currentChar)) {
                break;
            }
            chunk.append(currentChar);
            offset++;
        }

        return chunk;
    }

    private StringBuilder extractChunkOfDigits(String str, int strLength, int offset, StringBuilder chunk) {
        char currentChar;
        while (offset < strLength) {
            currentChar = str.charAt(offset);
            if (!isDigit(currentChar)) {
                break;
            }
            chunk.append(currentChar);
            offset++;
        }

        return chunk;
    }

    /**
     * @param  str
     * @return Lowercase version of given string w/ all contained number-chunks converted to having 10 digits
     */
    private static String prepareStringForCompare(String str) {
        // Convert all numeric-chunks within str to having 10 digits
        String parts[] = str.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        int index = 0;
        for (String part : parts) {
            if (StringUtils.isNumeric(part)) {
                StringBuilder partBuilder = new StringBuilder(part);
                while (partBuilder.length() < 10) {
                    partBuilder.insert(0, "0");
                }
                part = partBuilder.toString();
                parts[index] = part;
            }
            index++;
        }

        return StringUtils.join(parts).toLowerCase();
    }

    private static class NaturalComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }
}