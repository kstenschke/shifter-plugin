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
import com.kstenschke.shifter.models.ShiftableBlockSelection;
import com.kstenschke.shifter.models.ShiftableLine;
import com.kstenschke.shifter.models.ShiftableSelection;
import com.kstenschke.shifter.models.ShiftableWord;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter to bundle setup and delegation of different kinds of shift actions
 */
public class ActionAdapter {

    public final Editor editor;
    public Document document;

    private SelectionModel selectionModel;
    private int caretOffset;

    private AnActionEvent event;

    /**
     * Constructor
     */
    ActionAdapter(final AnActionEvent event) {
        this.event = event;
        editor = event.getData(PlatformDataKeys.EDITOR);

        if (null != editor) {
            document       = editor.getDocument();
            caretOffset    = editor.getCaretModel().getOffset();
            selectionModel = editor.getSelectionModel();
        }
    }

    /**
     * Find shiftable string (selection block/lines/regular, word at caret, line at caret) and replace it by its shifted value
     *
     * @param shiftUp   Shift up or down?
     * @param moreCount Current "more" count, starting w/ 1. If non-more shift: null
     */
    void delegate(boolean shiftUp, @Nullable Integer moreCount) {
        if (null == editor) {
            return;
        }

        if (selectionModel.hasSelection()) {
            // Shift (regular or block-) selection
            shiftSelection(shiftUp, moreCount);
            return;
        }

        // Try shift word at caret, fallback: try shifting line
        int lineNumber      = document.getLineNumber(caretOffset);
        int offsetLineStart = document.getLineStartOffset(lineNumber);
        int offsetLineEnd   = document.getLineEndOffset(lineNumber);

        CharSequence editorText = document.getCharsSequence();
        String line             = editorText.subSequence(offsetLineStart, offsetLineEnd).toString();

        if (!ShiftableWord.shiftWordAtCaretInDocument(editor, caretOffset, shiftUp, line, moreCount)) {
            // Word at caret wasn't identified/shifted, try shifting the whole line
            ShiftableLine.shiftLineInDocument(editor, caretOffset, shiftUp, offsetLineStart, line, moreCount);
        }
    }

    /**
     * @param shiftUp
     * @param moreCount
     */
    private void shiftSelection(boolean shiftUp, @Nullable Integer moreCount) {
        if (selectionModel.getBlockSelectionStarts().length > 1) {
            // Shift block selection: do word-shifting if all items are identical
            ShiftableBlockSelection.shiftBlockSelectionInDocument(this, shiftUp, moreCount);
            return;
        }

        // Shift regular selection: sort CSV or multi-line selection: sort lines alphabetically, etc.
        ShiftableSelection.shiftSelectionInDocument(editor, caretOffset, shiftUp, moreCount);
    }
}