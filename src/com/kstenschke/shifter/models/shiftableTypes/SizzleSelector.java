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

import com.kstenschke.shifter.utils.UtilsTextual;

/**
 * Sizzle DOM selector
 */
public class SizzleSelector {

    /**
     * Check whether given string represents a declaration of JS variables
     *
     * @param  str     String to be checked
     * @return boolean
     */
    public static Boolean isSelector(String str) {
        str = str.trim();
        return !(!str.startsWith("$(") || !str.endsWith(")"));
    }

    /**
     * @param  selector      text selection to be shifted
     * @return String
     * @todo   extend: duplicate caretLine around selection, from 1st of the 2 resulting lines: strip all non-selector strings (making it a declaration as is already when shifting just the selector)
     */
    public static String getShifted(String selector) {
        String varName = selector.replaceAll("\\$|\\.|'|\"|\\)|#|\\[|\\(|>|<|]|=|_|\\s", "-");
        varName = varName.replaceAll("--", "-");

        String[] words = varName.split("-");
        varName = "";
        int index = 0;
        for (String word : words) {
            varName += index > 0 ? UtilsTextual.toUcFirstRestLower(word) : word;
            index++;
        }

        return "var $" + UtilsTextual.toLcFirst(varName) + " = " + selector + ";";
    }
}