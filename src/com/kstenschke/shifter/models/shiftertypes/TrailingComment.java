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
package com.kstenschke.shifter.models.shiftertypes;

/**
 * Trailing (to line of code) comment - shifting = move comment to new empty line above
 */
public class TrailingComment {

    /**
     * @param word String to be shifted currently
     * @return boolean.
     *
     * @todo maybe later - implement also for multi-line selections(?)
     */
    public static boolean isTrailingComment(String word, String postfixChar, boolean isLastLineInDocument) {
        if (word == null || !word.contains("//") || (!postfixChar.equals("\n") && !isLastLineInDocument)) {
            return false;
        }

        String[] parts = word.split("//");

        return parts.length == 2 && parts[0].length() > 0 && parts[1].length() > 0;
    }

    /**
     * @param   selection
     * @param   leadingWhiteSpace
     * @return
     */
    public static String getShifted(String selection, String leadingWhiteSpace) {
        String[] parts = selection.split("//");

        return leadingWhiteSpace + "//" + parts[1] + "\n" + parts[0];
    }

}