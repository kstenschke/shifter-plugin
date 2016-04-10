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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Quoted String.
 */
public class QuotedString {

    private String quoteChar;

    /**
     * Check whether shifted word is wrapped in quote characters
     *
     * @param prefixChar  Character preceding the string
     * @param postfixChar Character after the string
     * @return boolean.
     */
    public boolean isQuotedString(String prefixChar, String postfixChar) {
        this.quoteChar = prefixChar;

        // Must begin be wrapped in single-, double quotes, or backticks
        return ("'".equals(prefixChar) && "'".equals(postfixChar))     // word is wrapped in single quotes
                || ("\"".equals(prefixChar) && "\"".equals(postfixChar))    // word is wrapped in double quotes
                || ("`".equals(prefixChar) && "`".equals(postfixChar));    // word is wrapped in backticks
    }

    /**
     * Shift to prev/next quoted string
     *
     * @param word       Quoted word to be shifted
     * @param editorText Full text of editor
     * @param isUp       Shifting up or down?
     * @return String
     */
    public String getShifted(String word, CharSequence editorText, boolean isUp) {
        // Get full text of currently edited document
        String text = editorText.toString();

        // Use regEx matcher to extract array of all string wrapped in current quoting sign
        List<String> allMatches = new ArrayList<String>();

        String pattern = "(?<=" + this.quoteChar + ")[a-zA-Z0-9_]+(?=" + this.quoteChar + ")";
        Matcher m = Pattern.compile(pattern).matcher(text);

        while (m.find()) {
            if (!allMatches.contains(m.group())) {
                allMatches.add(m.group());
            }
        }

        // Sort var names alphabetically
        Collections.sort(allMatches);
        int amountVars = allMatches.size();

        // Find position of given variable
        int curIndex = allMatches.indexOf(word);

        // Return next/previous variable name
        if (isUp) {
            curIndex++;
            if (curIndex == amountVars) {
                curIndex = 0;
            }
        } else {
            curIndex--;
            if (curIndex == -1) {
                curIndex = amountVars - 1;
            }
        }

        return allMatches.get(curIndex);
    }

}
