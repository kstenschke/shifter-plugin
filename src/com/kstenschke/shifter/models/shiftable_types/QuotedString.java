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

import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.ShiftableTypeAbstract;
import com.kstenschke.shifter.utils.UtilsTextual;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Quoted String.
 */
public class QuotedString extends ShiftableTypeAbstract {

    private ActionContainer actionContainer;

    private String quoteChar;

    public QuotedString(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    /**
     * @return boolean  Is word to be shifted wrapped in quote characters?
     */
    public boolean isApplicable() {
        quoteChar = actionContainer.prefixChar;

        // Must be wrapped in single-, double quotes, or backticks

        return  // Word is wrapped in single quotes
                ("'".equals(actionContainer.prefixChar) && "'".equals(actionContainer.postfixChar))
                        // Word is wrapped in double quotes
                        || ("\"".equals(actionContainer.prefixChar) && "\"".equals(actionContainer.postfixChar))
                        // Word is wrapped in backticks
                        || ("`".equals(actionContainer.prefixChar) && "`".equals(actionContainer.postfixChar));
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
     * @param  actionContainer
     * @return String
     */
    public String getShifted(
            String word,
            ActionContainer actionContainer,
            Integer moreCount,
            String leadingWhiteSpace
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
}