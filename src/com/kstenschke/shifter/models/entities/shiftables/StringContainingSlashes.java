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
import com.kstenschke.shifter.utils.UtilsTextual;

import org.jetbrains.annotations.Nullable;

public class StringContainingSlashes extends AbstractShiftable {

    public final String ACTION_TEXT = "Shift Slashes";

    // Constructor
    public StringContainingSlashes(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable: string must be wrapped in quote characters
    public StringContainingSlashes getInstance(@Nullable Boolean checkIfShiftable) {
        String str = actionContainer.getStringToBeShifted();

        return UtilsTextual.containsSlashes(str) &&
               (UtilsTextual.subStringCount(str, "/") != 2 ||
                !str.contains("//")
               )
                ? this : null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.STRING_CONTAINING_SLASHES;
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
        return null;
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        if (QuotedString.containsEscapedQuotes(actionContainer.selectedText)) {
            new ShiftableSelectionWithPopup(actionContainer).swapSlashesOrUnescapeQuotes();
            return true;
        }
        actionContainer.writeUndoable(
                actionContainer.getRunnableReplaceSelection(UtilsTextual.swapSlashes(actionContainer.selectedText)),
                ACTION_TEXT);
        return true;
    }
}