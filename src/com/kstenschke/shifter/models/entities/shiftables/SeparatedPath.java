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
import com.kstenschke.shifter.models.ShiftablesEnum;
import com.kstenschke.shifter.models.entities.AbstractShiftable;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SeparatedPath extends AbstractShiftable {

    public final String ACTION_TEXT = "Shift Path";

    // Constructor
    public SeparatedPath(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    public SeparatedPath getInstance(@Nullable Boolean checkIfShiftable) {
        if (// @todo make shiftable also in non-selection
            null == actionContainer.selectedText
        ) return null;

        String str = actionContainer.selectedText;

        return null != getShiftableType(str, "-") ||
               null != getShiftableType(str, "_")
                ? this : null;
    }

    public ShiftablesEnum.Type getType() {
        return ShiftablesEnum.Type.SEPARATED_PATH;
    }

    public boolean isWordPair(String str) {
        return str.split("-").length == 2;
    }

    public String flipWordsOrder(String str) {
        String words[] = str.split(getWordsGlue(str));

        return words[1] + "-" + words[0];
    }

    public String getShifted(
            String word,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        String parts[] = word.split(getWordsGlue(word));
        StringBuilder shifted = new StringBuilder();
        int index = 0;
        for (String part : parts) {
            shifted.append(index == 0 ? part : UtilsTextual.toUcFirstRestLower(part));
            index++;
        }

        return shifted.toString();
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        return false;
    }

    private SeparatedPath getShiftableType(String str, CharSequence glue) {
        return
                str.length() > 3 &&
                        UtilsTextual.startsAlphabetic(str) &&
                        str.contains(glue) &&
                        UtilsTextual.isAlphaNumericAndMinus(str.toLowerCase())
                        ? this : null;
    }

    @NotNull
    private String getWordsGlue(String word) {
        return null != getShiftableType(word, "-")
                ? "-"
                : "_";
    }
}