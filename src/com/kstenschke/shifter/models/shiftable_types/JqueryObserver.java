/*
 * Copyright Kay Stenschke
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
package com.kstenschke.shifter.models.shiftable_types;

/**
 * JavaScript Variables (multi-lined declarations of multiple vars)
 */
public class JqueryObserver {

    public static final String ACTION_TEXT = "Shift jQuery Observer";

    /**
     * Check whether given string represents a declaration of (multiple) JS variables:
     * -selection has multiple lines
     * -each trimmed line starts w/ "var" (at least 2 occurrences)
     * -each trimmed line ends w/ ";"
     * -there can be empty lines
     * -there can be commented lines, beginning w/ "//"
     *
     * @param  str     String to be checked
     * @return boolean
     */
    public static Boolean isJQueryObserver(String str) {
        if (str.startsWith(".")) {
            str = str.substring(1);
        }

        return "blur(".equals(str)
            || "change(".equals(str)
            || "click(".equals(str)
            || "dblclick(".equals(str)
            || "error(".equals(str)
            || "focus(".equals(str)
            || "keypress(".equals(str)
            || "keydown(".equals(str)
            || "keyup(".equals(str)
            || "load(".equals(str)
            || "mouseenter(".equals(str)
            || "mouseleave(".equals(str)
            || "resize(".equals(str)
            || "submit(".equals(str)
            || "scroll(".equals(str)
            || "unload(".equals(str);
    }

    /**
     * @param  str      text selection to be shifted
     * @return String
     */
    public static String getShifted(String str) {
        boolean startsWithDot = str.startsWith(".");
        if (startsWithDot) {
            str = str.substring(1);
        }
        String eventName = str.replace("(", "");

        return (startsWithDot ? "." : "") + "on('" + eventName + "', ";
    }
}
