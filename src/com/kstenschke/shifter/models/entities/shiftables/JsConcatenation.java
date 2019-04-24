/*
 * Copyright 2011-2019 Kay Stenschke
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
package com.kstenschke.shifter.models.entities.shiftables;

/**
 * JavaScript concatenation in TypeScript file: shift into interpolation
 */
public class JsConcatenation {

    public static final String ACTION_TEXT = "Convert to interpolation";

    private int amountVariables = 0;
    private int amountStrings = 0;

    public static Boolean isJsConcatenation(String str) {
        if (!str.contains("+") || str.replaceAll("[\\s|\\d]", "").length() < 3) {
            return false;
        }

        JsConcatenation jsConcatenation = new JsConcatenation();
        jsConcatenation.getShifted(str);

        return jsConcatenation.amountStrings > 0 && jsConcatenation.amountVariables > 0;
    }

    /**
     * @param  str      text selection to be shifted
     * @return String
     */
    public String getShifted(String str) {
        // Remove whitespace around concatenation operators
        str = str.trim();

        // Convert to interpolation
        int length               = str.length();
        boolean isWhiteSpaceInbetweenParts;
        boolean isWithinString   = false;
        // @todo implement interpolation of numbers within concatenation
        //boolean isWithinNumber   = false;
        boolean isWithinVariable = false;
        boolean doAppendVariable = false;
        boolean lastWasBackSlash = false;
        boolean isOpenCurl       = false;
        int amountPluses         = 0;
        char itemQuote           = 0;

        StringBuilder interpolation = new StringBuilder("`");

        for (int index = 0; index < length; index++) {
            char currentChar = str.charAt(index);
            isWhiteSpaceInbetweenParts = ' ' == currentChar || '\t' == currentChar || '\n' == currentChar;

            if (!isWithinString && !isWithinVariable /*&& !isWithinNumber*/) {
                if (!isWhiteSpaceInbetweenParts) {
                    if (currentChar == '"' || currentChar == '\'') {
                        // Opening quote of new string item
                        amountStrings++;
                        isWithinString = true;
                        itemQuote = currentChar;
                    } else if ('+' != currentChar) {
                        //if (Character.isDigit(currentChar)) {
                        //    isWithinNumber = true;
                        //    interpolation.append("` + ").append(currentChar);
                        //} else {
                            isWithinVariable = doAppendVariable = true;
                        //}
                    }
                }
            } else if (isWithinString) {
                if (itemQuote == currentChar && !lastWasBackSlash) {
                    // Closing quote of string item
                    isWithinString = false;
                } else {
                    // Character of string item
                    interpolation.append(currentChar);
                }
            } else if (!isWhiteSpaceInbetweenParts) {
                if ('+' == currentChar) {
                    isOpenCurl = isWithinVariable = false;
                    interpolation.append('}');
                } else {
                    doAppendVariable = true;
                }
            }
            if (doAppendVariable) {
                if (amountPluses == 0) {
                    // Concatenation overall begins w/ a variable
                    amountPluses = 1;
                    isWithinVariable = isOpenCurl = true;
                    amountVariables++;
                    interpolation.append("${").append(currentChar);
                } else {
                    // Character of variable name
                    if (!isOpenCurl) {
                        amountVariables++;
                        isOpenCurl = true;
                        interpolation.append("${");
                    }
                    interpolation.append(currentChar);
                }
                doAppendVariable = false;
            }
            lastWasBackSlash = '\\' == currentChar;
        }

        if (isWithinVariable) {
            interpolation.append('}');
        }

        return interpolation.append('`').toString();
    }
}