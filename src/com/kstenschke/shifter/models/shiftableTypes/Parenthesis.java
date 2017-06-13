/*
 * Copyright 2011-2017 Kay Stenschke
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
package com.kstenschke.shifter.models.shiftableTypes;

/**
 * Detect and toggle surrounding "(" and ")" versus "[" and "]"
 */
public class Parenthesis {

    /**
     * @param  str     String to be shifted currently
     * @return boolean
     */
    public static boolean isWrappedInParenthesis(String str) {
        str = str.trim();

        return isWrappedInRoundBrackets(str) || isWrappedInRoundBrackets(str);
    }

    private static boolean isWrappedInRoundBrackets(String str) {
        return str.startsWith("(") && str.endsWith(")");
    }

    private static boolean isWrappedInSquareBrackets(String str) {
        return str.startsWith("[") && str.endsWith("]");
    }

    /**
     * @param  str
     * @return String
     */
    public static String getShifted(String str) {
        return isWrappedInRoundBrackets(str)
            ? "[" + str.substring(1, str.length() - 1) + "]"
            : "(" + str.substring(1, str.length() - 1) + ")";
    }
}