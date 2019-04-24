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
 * Trailing (to line of code) comment - shifting = move comment to new empty caretLine above
 */
public class TrailingComment extends AbstractShiftable {

    private ActionContainer actionContainer;

    public static final String ACTION_TEXT = "Shift trailing Comment";

    // Constructor
    public TrailingComment(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    // TODO    implement also for multi-line selections(?)
    public TrailingComment getInstance() {
        String word = actionContainer.selectedText;
        if (
            null == word ||
            !word.contains("//") ||
            (!actionContainer.isLastLineInDocument && !"\n".equals(actionContainer.postfixChar))
        ) {
            return null;
        }

        String[] parts = word.split("//");

        return
                parts.length == 2 &&
                parts[0].length() > 0 &&
                parts[1].length() > 0
                        ? this : null;
    }

    public String getShifted(
            String selection,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        String[] parts = selection.split("//");

        return leadWhitespace + "//" + parts[1] + "\n" + parts[0];
    }
}