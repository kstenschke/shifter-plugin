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
import com.kstenschke.shifter.utils.UtilsTextual;

import javax.annotation.Nullable;

// Logical conjunction (selected AND && / OR ||, w/ two operands)
public class LogicalConjunction extends AbstractShiftable {

    public final String ACTION_TEXT = "Toggle logical conjunction operands";

    public boolean isOrLogic = false;

    // Constructor
    public LogicalConjunction(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    public LogicalConjunction getInstance() {
        if (// @todo make shiftable also in non-selection
            null == actionContainer.selectedText
        ) return null;

        String str = actionContainer.selectedText;

        if (null == str || str.length() < 4) return null;

        if (str.indexOf("||") > 0 &&
            UtilsTextual.subStringCount(str, "||") == 1
        ) {
            isOrLogic = true;
            actionContainer.delimiter = "|";
            return this;
        }
        if (str.indexOf("&&") > 0 &&
            UtilsTextual.subStringCount(str, "&&") == 1
        ) {
            isOrLogic = false;
            actionContainer.delimiter = "|";
            return this;
        }

        return null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.LOGICAL_CONJUNCTION;
    }

    public String getShifted(
            String str,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        String[] parts = actionContainer.selectedText.split(isOrLogic ? "\\|\\|" : "&&");

        return parts[1].trim() + (isOrLogic ? " || " : " && ") + parts[0];
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        return false;
    }
}