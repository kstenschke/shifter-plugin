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

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.ShiftableTypeAbstract;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;

/**
 * Mono-Character String = String that contains only one character (no matter how often)
 */
public class MonoCharacterRepetition extends ShiftableTypeAbstract {

    private ActionContainer actionContainer;

    public static final String ACTION_TEXT = "Shift Mono-Character";

    // Constructor
    public MonoCharacterRepetition(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    public MonoCharacterRepetition getShiftableType() {
        String word = actionContainer.selectedText;

        if (word.length() == 1) {
            return null;
        }

        String wordLower = word.toLowerCase();
        String firstChar = wordLower.substring(0, 1);

        return wordLower.replace(firstChar, "").length() == 0
                ? this : null;
    }

    public String getShifted(
            String word,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        char firstChar = word.toLowerCase().charAt(0);
        firstChar      = (char) (firstChar + (actionContainer.isShiftUp ? 1 : -1));

        return StringUtils.repeat(String.valueOf(firstChar), word.length());
    }
}