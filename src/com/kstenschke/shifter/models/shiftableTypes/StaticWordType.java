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
package com.kstenschke.shifter.models.shiftableTypes;

import com.kstenschke.shifter.utils.UtilsArray;

/**
 * Shifter general word type class
 */
public class StaticWordType {

    private final String[] keywords;
    private final int amountKeywords;
    private final String regExPattern;

    /**
     * Constructor
     */
    public StaticWordType(String[] keywords) {
        this.keywords  = keywords;
        amountKeywords = keywords.length;
        regExPattern   = UtilsArray.implode(keywords, "|");
    }

    /**
     * Check whether the given string is a known keyword of the word type
     *
     * @param  word        Word to be compared against keywords
     * @return boolean
     */
    public boolean hasWord(String word) {
        return (word).matches(regExPattern);
    }

    /**
     * @param  word     Word to be shifted
     * @param  isUp     Shifting up or down?
     * @return String   Shifting result
     */
    public String getShifted(String word, boolean isUp) {
        int wordOffset = UtilsArray.getOffset(keywords, word);
        if (-1 == wordOffset) {
            return word;
        }

        return isUp ? getShiftedUp(wordOffset) : getShiftedDown(wordOffset);
    }

    private String getShiftedUp(int wordOffset) {
        wordOffset++;
        if (wordOffset >= amountKeywords) {
            wordOffset = 0;
        }

        return keywords[wordOffset];
    }

    private String getShiftedDown(int wordOffset) {
        wordOffset -= 1;
        if (wordOffset < 0) {
            wordOffset = amountKeywords - 1;
        }

        return keywords[wordOffset];
    }
}