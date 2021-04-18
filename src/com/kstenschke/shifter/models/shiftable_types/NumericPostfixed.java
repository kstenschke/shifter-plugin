/*
 * Copyright Kay Stenschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kstenschke.shifter.models.shiftable_types;

/**
 * String w/ numeric postfix
 */
public class NumericPostfixed {

    /**
     * @param  word     String to be analyzed
     * @return boolean
     */
    public static boolean hasNumericPostfix(String word) {
        return word.matches("^.+?\\d$");
    }

    /**
     * Shift numeric postfix of string
     *
     * @param  word     Quoted word to be shifted
     * @param  isUp     Shifting up or down?
     * @return String
     */
    public static String getShifted(String word, boolean isUp) {
        int indexFirstNumericChar,
            indexLastNumericChar;

        for (indexLastNumericChar = word.length() - 1; indexLastNumericChar >= 0; indexLastNumericChar--) {
            String curChar = word.substring(indexLastNumericChar, indexLastNumericChar + 1);
            if (curChar.matches("\\d")) {
                // Found last numeric character
                break;
            }
        }
        for (indexFirstNumericChar = indexLastNumericChar - 1; indexFirstNumericChar >= 0; indexFirstNumericChar--) {
            String curChar = word.substring(indexFirstNumericChar, indexFirstNumericChar + 1);
            if (!curChar.matches("\\d")) {
                // Found non-numeric character
                indexFirstNumericChar += 1;
                break;
            }
        }

        String leadPart    = word.substring(0, indexFirstNumericChar);
        String numericPart = word.substring(indexFirstNumericChar, indexLastNumericChar + 1);

        int shiftedNumber = Integer.parseInt(numericPart) + (isUp ? 1 : -1);

        return leadPart + shiftedNumber;
    }
}
