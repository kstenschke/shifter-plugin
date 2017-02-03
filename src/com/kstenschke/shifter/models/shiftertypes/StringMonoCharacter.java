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

import org.apache.commons.lang.StringUtils;

/**
 * Mono-Character String = String that contains only one character (no matter how often)
 */
public class StringMonoCharacter {

    /**
     * @param word String to be shifted currently
     * @return boolean.
     */
    public static boolean isMonoCharacterString(String word) {
        if (word.length() == 1) {
            return true;
        }

        String wordLower = word.toLowerCase();
        String firstChar = wordLower.substring(0, 1);

        return wordLower.replace(firstChar, "").length() == 0;
    }

    /**
     * Shift mono-character string
     *
     * @param word Quoted word to be shifted
     * @param isUp Shifting up or down?
     * @return String
     */
    public String getShifted(String word, boolean isUp) {
        char firstChar = word.toLowerCase().charAt(0);
        firstChar      = (char) (firstChar + (isUp ? 1 : -1));

        return StringUtils.repeat(String.valueOf(firstChar), word.length());
    }

}
