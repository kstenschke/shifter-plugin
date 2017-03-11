/*
 * Copyright 2011-2017 Kay Stenschke
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
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.*;
import com.kstenschke.shifter.models.*;
import org.jetbrains.annotations.Nullable;

public class ActionsPerformer {

    public final Editor editor;
    public Document document;

    private SelectionModel selectionModel;
    private int caretOffset;

    /**
     * Constructor
     */
    ActionsPerformer(final AnActionEvent event) {
        this.editor = event.getData(PlatformDataKeys.EDITOR);

        if (this.editor != null) {
            this.document       = this.editor.getDocument();
            this.caretOffset    = this.editor.getCaretModel().getOffset();
            this.selectionModel = this.editor.getSelectionModel();
        }
    }

    /**
     * Find shiftable string (selection block/lines/regular, word at caret, line at caret) and replace it by its shifted value
     *
     * @param shiftUp   Shift up or down?
     * @param moreCount Current "more" count, starting w/ 1. If non-more shift: null
     */
    public void write(boolean shiftUp, @Nullable Integer moreCount) {
        if (this.editor != null) {
            if (selectionModel.hasSelection()) {
                // Shift (regular or block-) selection
                shiftSelection(shiftUp, moreCount);
            } else {
                // Try shift word at caret, fallback: try shifting line
                int lineNumber      = document.getLineNumber(caretOffset);
                int offsetLineStart = document.getLineStartOffset(lineNumber);
                int offsetLineEnd   = document.getLineEndOffset(lineNumber);

                CharSequence editorText = document.getCharsSequence();
                String line             = editorText.subSequence(offsetLineStart, offsetLineEnd).toString();

                boolean isWordShifted = ShiftableWord.shiftWordAtCaret(editor, caretOffset, shiftUp, line, moreCount);

                if (!isWordShifted) {
                    // Word at caret wasn't identified/shifted, try shifting the whole line
                    ShiftableLine.shiftLine(editor, caretOffset, shiftUp, offsetLineStart, line, moreCount);
                }
            }
        }
    }

    /**
     * @param shiftUp
     * @param moreCount
     */
    public void shiftSelection(boolean shiftUp, @Nullable Integer moreCount) {
        if (this.selectionModel.getBlockSelectionStarts().length > 1) {
            // Shift block selection: do word-shifting if all items are identical
            ShiftableBlockSelection.shiftBlockSelection(this, shiftUp, moreCount);
        } else {
            // Shift regular selection: sort CSV or multi-line selection: sort lines alphabetically
            ShiftableSelection.shiftSelectionInDocument(editor, caretOffset, shiftUp, moreCount);
        }
    }

}
