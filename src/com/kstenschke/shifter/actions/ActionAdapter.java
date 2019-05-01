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
package com.kstenschke.shifter.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kstenschke.shifter.models.*;
import org.jetbrains.annotations.Nullable;

// Adapter to bundle setup and delegation of different kinds of shift actions
class ActionAdapter {

    public ActionContainer actionContainer;

    // Constructor
    ActionAdapter(final AnActionEvent event, boolean isShiftUp, boolean isShiftMore) {
        actionContainer = new ActionContainer(event, isShiftUp, isShiftMore);
    }

    /**
     * Find shiftable string (selection block/lines/regular, word at caret, line at caret) and replace it by its shifted value
     *
     * @param moreCount Current "more" count, starting w/ 1. If non-more shift: null
     */
    void delegate(final @Nullable Integer moreCount) {
        if (null == actionContainer.editor) return;

        if (actionContainer.selectionModel != null &&
            actionContainer.selectionModel.hasSelection()
        ) {
            // Shift (regular or block-) selection
            shiftSelection(moreCount);
            return;
        }

        // Try shift word at caret, fallback: try shifting care lLine
        if (!ShiftableWord.shiftWordAtCaretInDocument(actionContainer, moreCount)) {
            // Word at caret wasn't identified/shifted, try shifting the whole caret line
            ShiftableLine.shiftLineInDocument(actionContainer, moreCount);
        }
    }

    private void shiftSelection(@Nullable Integer moreCount) {
        if (actionContainer.selectionModel.getBlockSelectionStarts().length > 1) {
            // Shift block selection: do word-shifting if all items are identical
            ShiftableBlockSelection.shiftBlockSelectionInDocument(this.actionContainer, moreCount);
        } else {
            // Shift regular selection: sort CSV or multi-lLine selection: sort lines alphabetically, etc.
            ShiftableSelection.shiftSelectionInDocument(actionContainer, moreCount);
        }
    }
}