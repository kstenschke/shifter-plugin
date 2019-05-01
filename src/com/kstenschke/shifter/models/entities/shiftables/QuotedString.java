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

import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.ShiftableTypes;
import com.kstenschke.shifter.models.entities.AbstractShiftable;
import com.kstenschke.shifter.utils.UtilsTextual;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class QuotedString extends AbstractShiftable {

    public final String ACTION_TEXT = "Shift Quotes";

    private String quoteChar;

    // Constructor
    public QuotedString(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable: string must be wrapped in quote characters
    public QuotedString getInstance() {
        quoteChar = actionContainer.prefixChar;

        // Must be wrapped in single-, double quotes, or backticks
        return  // Word is wrapped in single quotes
                ("'".equals(actionContainer.prefixChar) && "'".equals(actionContainer.postfixChar))
                        // Word is wrapped in double quotes
                        || ("\"".equals(actionContainer.prefixChar) && "\"".equals(actionContainer.postfixChar))
                        // Word is wrapped in backticks
                        || ("`".equals(actionContainer.prefixChar) && "`".equals(actionContainer.postfixChar))
                ? this : null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.QUOTED_STRING;
    }

    public static boolean containsShiftableQuotes(String str) {
        return (ShifterPreferences.getIsActiveConvertSingleQuotes() && str.contains("'"))
            || (ShifterPreferences.getIsActiveConvertDoubleQuotes() && str.contains("\""));
    }

    public static boolean containsEscapedQuotes(String str) {
        return str.contains("\\\"") || str.contains("\\\'");
    }

    /**
     * Shift to previous/next quoted string
     *
     * @param  word       Quoted word to be shifted
     * @return String
     */
    public String getShifted(
            String word,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        // Get array of all strings wrapped in current quoting sign
        String text = actionContainer.editorText.toString();
        List<String> allMatches = UtilsTextual.extractQuotedStrings(text, quoteChar);

        // Sort var names alphabetically
        Collections.sort(allMatches);
        int amountVars = allMatches.size();

        // Find position of given variable, return next/previous variable name
        int curIndex = allMatches.indexOf(word);
        curIndex     = NumericValue.moduloShiftInteger(curIndex, amountVars, actionContainer.isShiftUp);

        return allMatches.get(curIndex);
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        return false;
    }
}