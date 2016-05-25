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
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;

/**
 * JavaScript Variables (multi-lined declarations of multiple vars)
 */
public class JsVariablesDeclarations {

    /**
     * Check whether given string represents a declaration of JS variables:
     * -selection has multiple lines
     * -each trimmed line starts w/ "var" (at least 2 occurrences)
     * -each trimmed line ends w/ ";"
     * -there can be empty lines
     * -there can commented lines, beginning w/ "//"
     *
     * @param  str     String to be checked
     * @return boolean
     */
    public static Boolean isJsVariables(String str) {
        str = str.trim();
        if ( !str.startsWith("var") || !str.endsWith(";") || !str.contains("\n")
             || StringUtils.countMatches(str, "var") < 2 || StringUtils.countMatches(str, ";") < 2
        ) {
            return false;
        }

        return true;
    }

    /**
     * @param  str      text selection to be shifted
     * @return String
     */
    public static String getShifted(String str) {
        String[] lines = str.split("\n");
        String shiftedLines = "";

        int lineNumber = 0;
        String shiftedLine;
        for(String line : lines) {
            line = line.trim();

            if (line.isEmpty() || line.startsWith("//")) {
                // do not change empty or comment-lines
                shiftedLine = line;
            } else {
                // remove "var " from beginning
                line = line.substring(4);
                // replace ";" from ending by ",\n"
                if (StringUtils.countMatches(line, "//") == 1) {
                    // handle line ending with comment intact
                    String[] parts = line.split("//");
                    parts[0] = parts[0].trim();
                    shiftedLine = parts[0].substring(0, parts[0].length() - 1) + ", //" + parts[1];
                } else {
                    shiftedLine = line.substring(0, line.length() - 1) + ",";
                }
            }

            shiftedLines += (lineNumber == 0 ? "" : "\t") + shiftedLine + "\n";
            lineNumber++;
        }

        return "var " + shiftedLines.substring(0, shiftedLines.length()-2) + ";";
    }

}
