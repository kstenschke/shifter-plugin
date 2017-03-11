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
 * Ternary Expression
 */
public class WordsTupel {

    private String delimiter;

    /**
     * Check whether shifted string is a ternary expression
     *
     * @param  str
     * @return boolean
     */
    public boolean isWordsTupel(String str) {
        String glues[] = new String[]{
                // Multi-character delimiters containing singe-character delimiters must precede those
                "!==", "!=",
                "===", "==",
                "<=", ">=",
                "&&", "||",

                ".", ",", ":",
                "+", "-", "/", "*", "%",
                "<", ">", "=",
                "&", "|",

                // Space must be last to not be prematurely detected around other delimiter
                " ",
        };

        for (String glue : glues) {
            String delimiterPattern = Pattern.quote(glue);

            if (str.split("\\s*" + delimiterPattern + "\\s*").length == 2) {
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
        String splitPattern = "^(\\s*" + Pattern.quote(this.delimiter) + "\\s*)";
        // Split into tupel
        String[] parts      = str.split(splitPattern);

        // Retain variable whitespace around delimiter
        Pattern partsPattern      = Pattern.compile(splitPattern);
        Matcher matcher           = partsPattern.matcher(str);
        String glueWithWhitespace = matcher.group(1);

        // Swap parts
        return parts[1] + glueWithWhitespace + parts[0];
    }

}
