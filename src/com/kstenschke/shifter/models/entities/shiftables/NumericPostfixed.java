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
package com.kstenschke.shifter.models.entities.shiftables;

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.entities.AbstractShiftable;

import javax.annotation.Nullable;

/**
 * String w/ numeric postfix
 */
public class NumericPostfixed extends AbstractShiftable {

    private ActionContainer actionContainer;

    // Constructor
    public NumericPostfixed(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    public NumericPostfixed getInstance() {
        String word = actionContainer.selectedText;

        return word.matches("^.+?\\d$")
            ? this : null;
    }

    public String getShifted(
            String word,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        int indexFirstNumericChar,
            indexLastNumericChar;

        for (indexLastNumericChar = word.length() - 1; indexLastNumericChar >= 0; indexLastNumericChar--) {
            String curChar = word.substring(indexLastNumericChar, indexLastNumericChar + 1);
            if (curChar.matches("\\d")) {
                // Found last numeric character
                break;
            }
        }
        for (indexFirstNumericChar = indexLastNumericChar - 1; indexFirstNumericChar >= 0; indexFirstNumericChar--) {
            String curChar = word.substring(indexFirstNumericChar, indexFirstNumericChar + 1);
            if (!curChar.matches("\\d")) {
                // Found non-numeric character
                indexFirstNumericChar += 1;
                break;
            }
        }

        String leadPart    = word.substring(0, indexFirstNumericChar);
        String numericPart = word.substring(indexFirstNumericChar, indexLastNumericChar + 1);

        int shiftedNumber = Integer.parseInt(numericPart) + (actionContainer.isShiftUp ? 1 : -1);

        return leadPart + shiftedNumber;
    }
}