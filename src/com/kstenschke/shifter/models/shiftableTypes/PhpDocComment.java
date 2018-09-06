/*
 * Copyright 2011-2018 Kay Stenschke
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

import static org.apache.commons.lang.StringUtils.trim;

class PhpDocComment {

    /**
     * Check whether given String is a PHP doc comment block
     *
     * @param  str
     * @return boolean
     */
    public static boolean isPhpDocComment(String str) {
        str = trim(str);
        String lines[] = str.split("\n");

        return lines.length > 2 && str.startsWith("/**") && str.endsWith("*/") && str.contains(" * ");
    }

    /**
     * @param  str
     * @return boolean
     */
    public static boolean containsAtParam(String str) {
        return str.contains("@param ");
    }

    /**
     * @param  str
     * @return String
     */
    public static String getShifted(String str) {
        String lines[] = str.split("\n");
        String shifted = "";

        int indexLine = 1;
        for (String line : lines) {
            if (containsAtParam(line) && !PhpDocParam.containsDataType(line) && PhpDocParam.containsVariableName(line)) {
                // PHP doc @param comment that contains variable name but no data type: guess data type by variable name
                line = PhpDocParam.getShifted(line);
            }
            shifted += line + (indexLine < lines.length ? "\n" : "");
            indexLine++;
        }

        return shifted;
    }
}