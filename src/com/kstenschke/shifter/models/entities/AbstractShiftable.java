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
package com.kstenschke.shifter.models.entities;

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.ShiftableTypes;

import javax.annotation.Nullable;

public abstract class AbstractShiftable {

    protected ActionContainer actionContainer;

    // Java doesn't know abstract properties, ensure all shiftables having this
    public String ACTION_TEXT = "Shift abstract";

    // Constructor
    public AbstractShiftable(@Nullable ActionContainer actionContainer) {
        this.actionContainer = actionContainer;
    }

    // Get shiftable instance or null if not applicable
    abstract public AbstractShiftable getInstance();

    abstract public ShiftableTypes.Type getType();

    abstract public String getShifted(
            String word,
            @Nullable Integer moreCount,
            @Nullable String leadWhiteSpace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    );

    public String getShifted(String word) {
        return getShifted(word, null, null, false, false);
    }

    public String getShifted(String word, Integer moreCount) {
        return getShifted(word, moreCount, null, false, false);
    }

    public String getShifted(String word, Integer moreCount, String leadWhitespace) {
        return getShifted(word, moreCount, leadWhitespace, false, false);
    }

    public void replaceSelectionShifted(boolean reformat) {
        actionContainer.writeUndoable(
                actionContainer.getRunnableReplaceSelection(
                        getShifted(actionContainer.selectedText, null, null),
                        reformat),
                ACTION_TEXT);
    }

    public void replaceSelectionShifted() {
        replaceSelectionShifted(true);
    }

    public String getActionText() {
        return ACTION_TEXT;
    }
}
