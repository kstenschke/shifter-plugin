/*
 * Copyright 2011-2019 Kay Stenschke
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
import org.jetbrains.annotations.NotNull;

public class SeparatedPath {

    public static boolean isSeparatedPath(String str) {
        return isSeparatedPath(str, "-") || isSeparatedPath(str, "_");
    }

    private static boolean isSeparatedPath(String str, CharSequence glue) {
        return str.length() > 3 && UtilsTextual.startsAlphabetic(str) && str.contains(glue)
            && UtilsTextual.isAlphaNumericAndMinus(str.toLowerCase());
    }

    public static boolean isWordPair(String str) {
        return str.split("-").length == 2;
    }

    public static String flipWordsOrder(String str) {
        String words[] = str.split(getWordsGlue(str));

        return words[1] + "-" + words[0];
    }

    /**
     * @param  word
     * @return String   Given string converted to camelCase
     */
    public static String getShifted(String word) {
        String parts[] = word.split(getWordsGlue(word));
        StringBuilder shifted = new StringBuilder();
        int index = 0;
        for (String part : parts) {
            shifted.append(index == 0 ? part : UtilsTextual.toUcFirstRestLower(part));
            index++;
        }

        return shifted.toString();
    }

    @NotNull
    private static String getWordsGlue(String word) {
        return isSeparatedPath(word, "-") ? "-" : "_";
    }
}