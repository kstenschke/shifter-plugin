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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tupel (two items w/ delimiter in between)
 */
public class Tupel {

    private String delimiter;

    /**
     * Check whether shifted string is a ternary expression
     *
     * @param  str
     * @return boolean
     */
    public boolean isWordsTupel(String str) {
        String glues[] = new String[]{
                ",",

                // Multi-character delimiters containing singe-character delimiters must precede those
                "!==", "!=",
                "===", "==",
                "<=", ">=",
                "&&", "||",

                ".", ":",
                "+", "-", "*", "/", "%", "=",
                "&", "|",
                "<", ">",

                // Space must be last to not be prematurely detected around other delimiter
                " ",
        };

        for (String glue : glues) {
            String parts[] = str.split("\\s*" + Pattern.quote(glue) + "\\s*");
            if (parts.length == 2 && !parts[0].isEmpty() && !parts[1].isEmpty()) {
                this.delimiter = glue;
                return true;
            }
        }

        return false;
    }

    /**
     * Shift: swap tupel parts
     *
     * @param  str      string to be shifted
     * @return String   The shifted string
     */
    public String getShifted(String str) {
        // Split into tupel
        String splitPattern = "\\s*" + Pattern.quote(this.delimiter) + "\\s*";
        String[] parts      = str.split(splitPattern);

        // Retain variable whitespace around delimiters
        Pattern partsPattern = Pattern.compile(splitPattern);
        Matcher matcher      = partsPattern.matcher(str);
        if (matcher.find()) {
            String glueWithWhitespace = matcher.group(0);
            // Swap parts
            return parts[1].trim() + glueWithWhitespace + parts[0].trim();
        }

        return parts[1].trim() + delimiter + parts[0].trim();
    }
}