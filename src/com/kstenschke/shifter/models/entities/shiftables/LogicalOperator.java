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
import com.kstenschke.shifter.models.ShiftableTypes;
import com.kstenschke.shifter.models.entities.AbstractShiftable;

import javax.annotation.Nullable;

public class LogicalOperator extends AbstractShiftable {

    public final String ACTION_TEXT = "Toggle Logical Operator";

    // Constructor
    public LogicalOperator(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    public LogicalOperator getInstance() {
        if (// @todo make shiftable also in non-selection
            null == actionContainer.selectedText
        ) return null;

        String word = actionContainer.selectedText;

        return "&&".equals(word) || "||".equals(word)
                ? this : null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.LOGICAL_OPERATOR;
    }

    // Toggle among "&&" and "||"
    public String getShifted(
            String word,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        return "&&".equals(word) ? "||" : "&&";
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        actionContainer.writeUndoable(
                actionContainer.getRunnableReplaceSelection(
                        getShifted(actionContainer.selectedText)),
                ACTION_TEXT);
        return true;
    }
}