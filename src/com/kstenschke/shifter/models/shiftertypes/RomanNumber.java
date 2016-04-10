/*
 * Copyright 2011-2016 Kay Stenschke
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
package com.kstenschke.shifter.models.shiftertypes;

import com.kstenschke.shifter.utils.UtilsTextual;

/**
 * Roman number class
 */
public class RomanNumber {

    /**
     * @param  str      String to be checked
     * @return boolean  Does the given string represent a CSS length value?
     */
    public static boolean isRomanNumber(String str) {
        return UtilsTextual.containsOnly(str, new String[]{"I", "V", "X", "L", "C", "D", "M"});
    }

    /**
     * @param  value    String representing a roman numeral
     * @param  isUp     Shifting up or down?
     * @return String   Value shifted up or down by one
     */
    public String getShifted(String value, boolean isUp) {
        int intVal = new RomanNumeral(value).toInt();

        if( intVal == 1 && ! isUp ) {
            return value;
        }

        return isUp
                ? new RomanNumeral(intVal + 1).toString()
                : new RomanNumeral(intVal - 1).toString();
    }

    /**
     * An object of type RomanNumeral is an integer between 1 and 3999.  It can
     * be constructed either from an integer or from a string that represents
     * a Roman numeral in this range.  The function toString() will return a
     * standardized Roman numeral representation of the number.  The function
     * toInt() will return the number as a value of type int.
     */
    public class RomanNumeral {

        private final int num;   // The number represented by this Roman numeral.

         /* The following arrays are used by the toString() function to construct
            the standard Roman numeral representation of the number.  For each i,
            the number numbers[i] is represented by the corresponding string, letters[i].
         */

        private int[]    numbers = { 1000,  900,  500,  400,  100,   90,
                50,   40,   10,    9,    5,    4,    1 };

        private String[] letters = { "M",  "CM",  "D",  "CD", "C",  "XC",
                "L",  "XL",  "X",  "IX", "V",  "IV", "I" };

        /**
         * Constructor.  Creates the Roman number with the int value specified
         * by the parameter.  Throws a NumberFormatException if arabic is
         * not in the range 1 to 3999 inclusive.
         */
        public RomanNumeral(int arabic) {
            if (arabic < 1)
                throw new NumberFormatException("Value of RomanNumeral must be positive.");
            if (arabic > 3999)
                throw new NumberFormatException("Value of RomanNumeral must be 3999 or less.");
            num = arabic;
        }

        /*
         * Constructor.  Creates the Roman number with the given representation.
         * For example, RomanNumeral("xvii") is 17.  If the parameter is not a
         * legal Roman numeral, a NumberFormatException is thrown.  Both upper and
         * lower case letters are allowed.
         */
        public RomanNumeral(String roman) {
            if (roman.length() == 0)
                throw new NumberFormatException("An empty string does not define a Roman numeral.");

            String romanUpper = roman.toUpperCase();

            int i = 0;       // A position in the string, roman
            int arabic = 0;  // Arabic numeral equivalent of the part of the string that has
            //    been converted so far.

            while (i < romanUpper.length()) {

                char letter = romanUpper.charAt(i);        // Letter at current position in string.
                int number = letterToNumber(letter);  // Numerical equivalent of letter.

                i++;  // Move on to next position in the string

                if (i == romanUpper.length()) {
                    // There is no letter in the string following the one we have just processed.
                    // So just add the number corresponding to the single letter to arabic.
                    arabic += number;
                }
                else {
                    // Look at the next letter in the string.  If it has a larger Roman numeral
                    // equivalent than number, then the two letters are counted together as
                    // a Roman numeral with value (nextNumber - number).
                    int nextNumber = letterToNumber(romanUpper.charAt(i));
                    if (nextNumber > number) {
                        // Combine the two letters to get one value, and move on to next position in string.
                        arabic += (nextNumber - number);
                        i++;
                    }
                    else {
                        // Don't combine the letters.  Just add the value of the one letter onto the number.
                        arabic += number;
                    }
                }

            }  // end while

            if (arabic > 3999)
                throw new NumberFormatException("Roman numeral must have value 3999 or less.");

            num = arabic;

        } // end constructor

        /**
         * Find the integer value of letter considered as a Roman numeral.  Throws
         * NumberFormatException if letter is not a legal Roman numeral.  The letter
         * must be upper case.
         */
        private int letterToNumber(char letter) {
            switch (letter) {
                case 'I':  return 1;
                case 'V':  return 5;
                case 'X':  return 10;
                case 'L':  return 50;
                case 'C':  return 100;
                case 'D':  return 500;
                case 'M':  return 1000;
                default:   throw new NumberFormatException(
                        "Illegal character \"" + letter + "\" in Roman numeral");
            }
        }

        /**
         * Return the standard representation of this Roman numeral.
         */
        public String toString() {
            String roman = "";
            // N represents the part of num that still has to be converted to Roman numeral representation.
            int nonRoman = num;
            for (int i = 0; i < numbers.length; i++) {
                while (nonRoman >= numbers[i]) {
                    roman += letters[i];
                    nonRoman -= numbers[i];
                }
            }

            return roman;
        }

        /**
         * Return the value of this Roman numeral as an int.
         */
        public int toInt() {
            return num;
        }

    }

}