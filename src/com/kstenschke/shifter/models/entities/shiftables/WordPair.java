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
import com.kstenschke.shifter.models.ShiftableSelectionWithPopup;
import com.kstenschke.shifter.models.ShiftableTypes;
import com.kstenschke.shifter.utils.UtilsTextual;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WordPair extends SeparatedPath {

    public final String ACTION_TEXT = "Shift Path";

    // Constructor
    public WordPair(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    public WordPair getInstance(@Nullable Boolean checkIfShiftable) {
        return null != super.getInstance(checkIfShiftable) &&
               isWordPair(actionContainer.selectedText)
                    ? this : null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.WORD_PAIR;
    }

    public boolean isWordPair(String str) {
        return str.split("-").length == 2;
    }

    public String flipWordsOrder(String str) {
        String words[] = str.split(getWordsGlue(str));

        return words[1] + "-" + words[0];
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        new ShiftableSelectionWithPopup(actionContainer).shiftSeparatedPathOrSwapWords();
        return true;
    }

    private WordPair getShiftableType(String str, CharSequence glue) {
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