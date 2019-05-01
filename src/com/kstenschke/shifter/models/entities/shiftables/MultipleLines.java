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
import com.kstenschke.shifter.models.entities.AbstractShiftable;
import org.jetbrains.annotations.Nullable;

public class MultipleLines extends AbstractShiftable {

    public final String ACTION_TEXT = "Shift HTML Entities";

    // Constructor
    public MultipleLines(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    public MultipleLines getInstance(@Nullable Boolean checkIfShiftable) {
        int lineNumberSelStart = actionContainer.document.getLineNumber(actionContainer.offsetSelectionStart);
        int lineNumberSelEnd   = actionContainer.document.getLineNumber(actionContainer.offsetSelectionEnd);
        if (actionContainer.document.getLineStartOffset(lineNumberSelEnd) == actionContainer.offsetSelectionEnd) {
            lineNumberSelEnd--;
        }

        return (lineNumberSelEnd - lineNumberSelStart) > 0 &&
               // @todo make the following check unnecessary via shiftable detection order
               null == new PhpVariableOrArray(actionContainer).getInstance()
                ? this : null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.MULTIPLE_LINES;
    }

    public String getShifted(
            String word,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        return null;
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        // Multi-line selection: sort lines or swap quotes
        new ShiftableSelectionWithPopup(actionContainer).sortLinesOrSwapQuotesInDocument();
        return true;
    }
}