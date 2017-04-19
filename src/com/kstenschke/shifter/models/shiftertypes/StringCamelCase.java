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

import com.kstenschke.shifter.utils.UtilsTextual;

/**
 * "camelCase" and "TitleCase" strings
 */
public class StringCamelCase {

    /**
     * @return boolean
     */
    public static boolean isCamelCase(String str) {
        return str.length() > 2 && (UtilsTextual.isUpperCamelCase(str) || UtilsTextual.isLowerCamelCase(str));
    }

    public static boolean isWordPair(String str) {
        return UtilsTextual.splitCamelCaseIntoWords(str).length == 2;
    }

    /**
     * @param str
     * @return string   Converts: "camelCase" to "caseCamel" and: "TitleCase" to "CaseTitle"
     */
    public static String flipWordPairOrder(String str) {
        boolean isLcFirst = UtilsTextual.isLcFirst(str);

        String words[] = UtilsTextual.splitCamelCaseIntoWords(str);

        return isLcFirst
                ? UtilsTextual.toLcFirst(words[1]) + UtilsTextual.toUcFirst(words[0])
                : words[1] + words[0];
    }

    /**
     * @return String
     */
    public static String getShifted(String word) {
        return word;
    }
}