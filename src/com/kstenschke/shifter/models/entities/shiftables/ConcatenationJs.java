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
import org.jetbrains.annotations.Nullable;

// JavaScript concatenation in TypeScript file: shift into interpolation
public class ConcatenationJs extends AbstractShiftable {

    public final String ACTION_TEXT = "Convert to interpolation";

    private int amountVariables = 0;
    private int amountStrings = 0;

    // Constructor
    public ConcatenationJs(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    public ConcatenationJs getInstance(@Nullable Boolean checkIfShiftable) {
        if (
            null == actionContainer.selectedText
        ) return null;

        String str = actionContainer.selectedText;
        if (!str.contains("+") ||
            str.replaceAll("[\\s|\\d]", "").length() < 3) {
            return null;
        }

        return this;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.CONCATENATION_JS;
    }

    public String getShifted(
            String str,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        // @todo implement intention popup(?) for JS-concatenation in JS file: sort, swap, ...
        return str;
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        return false;
    }
}