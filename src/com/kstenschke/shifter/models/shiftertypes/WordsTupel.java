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
public class WordsTupel {

    /**
     * Check whether shifted string is a ternary expression
     *
     * @param  str
     * @return boolean
     */
    public static boolean isWordsTupel(String str) {
        // @todo implement also for other delimiters than just space
        return str.split(" ").length == 2;
    }

    /**
     * Shift: tupel parts
     *
     * @param str  string to be shifted
     * @return String   The shifted string
     */
    public static String getShifted(String str) {

        String[] parts = str.split(" ");

        return parts[1] + " " + parts[0];
    }

}
