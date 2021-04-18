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

import com.kstenschke.shifter.utils.UtilsTextual;
import org.apache.commons.lang.StringUtils;

/**
 * "camelCase" and "TitleCase" strings
 */
public class CamelCaseString {

    public enum ShiftMode {
        CAMEL_WORDS_TO_MINUS_SEPARATED,
        CAMEL_WORDS_TO_UNDERSCORE_SEPARATED,
    }

    public static boolean isCamelCase(String str) {
        return !UtilsTextual.startsNumeric(str) && UtilsTextual.isCamelCase(str);
    }

    public static boolean isWordPair(String str) {
        return getAmountWords(str) == 2;
    }

    /**
     * @param  str
     * @return string   Converts: "camelCase" to "caseCamel" and: "TitleCase" to "CaseTitle"
     */
    public static String flipWordPairOrder(String str) {
        String[] words = UtilsTextual.splitCamelCaseIntoWords(str);
        if (words.length <= 1 || words.length > 2) {
            return str;
        }

        return UtilsTextual.isLcFirst(str)
                ? UtilsTextual.toLcFirst(words[1]) + UtilsTextual.toUcFirstRestLower(words[0])
                : words[1] + words[0];
    }

    private static int getAmountWords(String word) {
        return UtilsTextual.splitCamelCaseIntoWords(word, true).length;
    }

    /**
     * Convert into minus-separated path
     *
     * @param  word
     * @return String
     */
    public static String getShifted(String word) {
        return getShifted(word, ShiftMode.CAMEL_WORDS_TO_MINUS_SEPARATED);
    }

    public static String getShifted(String word, ShiftMode mode) {
        String[] parts = UtilsTextual.splitCamelCaseIntoWords(word, true);

        switch (mode) {
            case CAMEL_WORDS_TO_UNDERSCORE_SEPARATED:
                return StringUtils.join(parts, "_");
            case CAMEL_WORDS_TO_MINUS_SEPARATED:
            default:
                return StringUtils.join(parts, "-");
        }
    }
}
