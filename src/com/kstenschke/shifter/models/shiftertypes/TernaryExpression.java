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
 * Ternary Expression
 */
public class TernaryExpression {

    /**
     * Check whether shifted string is a ternary expression
     *
     * @param  str
     * @param  prefixChar   Character preceding the string
     * @return boolean
     */
    public static boolean isTernaryExpression(String str, String prefixChar) {
        String expression = str.trim();

        return (expression.startsWith("?") || "?".equals(prefixChar)
                && (expression.contains(":") && !expression.endsWith(":") && !expression.startsWith(":"))
                && expression.length() >= 3
        );
    }

    /**
     * Shift: swap IF and ELSE parts
     *
     * @param  str      string to be shifted
     * @return String   The shifted string
     */
    public static String getShifted(String str) {
        Integer offsetElse = str.indexOf(":");

        if (offsetElse > -1) {
            boolean endsWithSemicolon    = str.endsWith(";");
            boolean isQuestionMarkInline = str.startsWith("?");
            if (isQuestionMarkInline) {
                str = str.substring(1);
            }

            String partThan = str.substring(0, offsetElse - 1);
            String partElse = endsWithSemicolon ? str.substring(offsetElse, str.length() - 1) : str.substring(offsetElse);

            // Detect and maintain "glue" w/ (single) whitespace
            boolean wrapWithSpace = partThan.endsWith(" ") || partElse.startsWith(" ");
            boolean wrapWithTab = partThan.endsWith("\t") || partElse.startsWith("\t");

            String glue = wrapWithSpace ? " " : (wrapWithTab ? "\t" : "");

            str = partElse.trim() + glue + ":" + glue + partThan.trim();

            if (isQuestionMarkInline) {
                str = "?" + glue + str;
            }
            if (endsWithSemicolon) {
                str += ";";
            }
        }

        return str;
    }
}