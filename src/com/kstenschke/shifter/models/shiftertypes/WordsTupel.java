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

    private String delimiter;

    /**
     * Check whether shifted string is a ternary expression
     *
     * @param  str
     * @return boolean
     */
    public boolean isWordsTupel(String str) {
        if (str.split(" ").length == 2) {
            this.delimiter = " ";
            return true;
        }
        if (str.split("\\|").length == 2) {
            this.delimiter = "|";
            return true;
        }
        if (str.split(" : ").length == 2) {
            this.delimiter = " : ";
            return true;
        }

        return false;
    }

    /**
     * Shift: tupel parts
     *
     * @param str  string to be shifted
     * @return String   The shifted string
     */
    public String getShifted(String str) {

        String[] parts = str.split(this.delimiter == "|" ? "\\|" : this.delimiter);

        return parts[1] + this.delimiter + parts[0];
    }

}
