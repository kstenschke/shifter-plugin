
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

public class Comment {

    /**
     * @param  str     String to be shifted currently
     * @return boolean
     */
    public static boolean isComment(String str) {
        str = str.trim();

        return str.startsWith("//")
                || (str.startsWith("/*") && str.endsWith("*/"));
    }

    /**
     * @param  str
     * @return String
     */
    public static String getShifted(String str) {
        str = str.trim();

        if (str.startsWith("//")) {
            return "/*" + str.substring(2) + "*/";
        }

        str = str.substring(2, str.length() - 2);
        if (str.contains("\n")) {
            str = str.replace("\n", " ");
        }
        return "//" + str;
    }

}
