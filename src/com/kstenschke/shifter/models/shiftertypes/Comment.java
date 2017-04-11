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

import com.kstenschke.shifter.utils.UtilsFile;

/**
 * Included comment types:
 *
 * 1. Single-line comment => // ...
 * 2. Block comment       => /* ... *\/
 * 3. HTML comment        => <!-- ... -->
 */
public class Comment {

    /**
     * @param  str     String to be shifted currently
     * @return boolean
     */
    public static boolean isComment(String str) {
        str = str.trim();

        return str.startsWith("//") || isBlockComment(str);
    }

    public static boolean isBlockComment(String str) {
        str = str.trim();

        return str.startsWith("/*") && str.endsWith("*/");
    }

    public static boolean isPhpBlockComment(String str) {
        str = str.trim();

        return str.startsWith("<?php /*") && str.endsWith("*/ ?>");
    }

    public static  boolean isHtmlComment(String str) {
        str = str.trim();

        return str.startsWith("<!--") && str.endsWith("-->");
    }

    /**
     * @param  str
     * @return String
     */
    public static String getShifted(String str, String filename) {
        if (filename != null && UtilsFile.isPhpFile(filename) && isPhpBlockComment(str)) {
            // @todo    add popup to select from possible shifting types

            // PHP Block-comment inside PHP or PHTML: convert to HTML comment
            return "<!-- " + str.substring(8, str.length() - 5).trim() + " -->";
        }

        // Default comment shifting: toggle among single-line and block-comment style
        str = str.trim();

        if (str.startsWith("//")) {
            // Convert single line comment to block comment
            return "/*" + str.substring(2) + "*/";
        }

        str = str.substring(2, str.length() - 2);
        if (str.contains("\n")) {
            // Convert block- to single line comment
            // @todo    if there are multiple lines: add popup to select whether to join multiple lines
            str = str.replace("\n", " ");
        }
        return "//" + str;
    }

    /**
     * @param str
     * @return
     */
    public static String getPhpBlockCommentFromHtmlComment(String str) {
        return "<?php /* " + str.substring(4, str.length() - 3).trim() + " */ ?>";
    }
}
