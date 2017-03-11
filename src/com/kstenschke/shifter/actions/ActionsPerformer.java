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
import com.kstenschke.shifter.models.shiftertypes.*;
import com.kstenschke.shifter.utils.*;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class ActionsPerformer {

    public final Editor editor;
    public Document document;

    private SelectionModel selectionModel;
    private CharSequence editorText;
    private int caretOffset;
    private boolean hasSelection;
    private String filename = null;

    /**
     * Constructor
     */
    ActionsPerformer(final AnActionEvent event) {
        this.editor = event.getData(PlatformDataKeys.EDITOR);

        if (this.editor != null) {
            this.document       = this.editor.getDocument();
            this.filename       = this.getFilename();
            this.editorText     = this.document.getCharsSequence();
            this.caretOffset    = this.editor.getCaretModel().getOffset();
            this.selectionModel = this.editor.getSelectionModel();
            this.hasSelection   = this.selectionModel.hasSelection();
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
            if (this.hasSelection) {
                if (this.selectionModel.getBlockSelectionStarts().length > 1) {
                    // Shift block selection: do word-shifting if all items are identical
                    ShiftableBlockSelection.shiftBlockSelection(this, shiftUp, moreCount);
                } else {
                    // Shift regular selection: sort CSV or multi-line selection: sort lines alphabetically
                    ShiftableSelection.shiftSelectionInDocument(editor, filename, caretOffset, shiftUp, moreCount);
                }
            } else {
                // Shift word at caret
                int lineNumber      = document.getLineNumber(caretOffset);
                int offsetLineStart = document.getLineStartOffset(lineNumber);
                int offsetLineEnd   = document.getLineEndOffset(lineNumber);

                String line = editorText.subSequence(offsetLineStart, offsetLineEnd).toString();

                boolean isWordShifted = shiftWordAtCaret(shiftUp, this.filename, line, moreCount);

                // Word at caret wasn't identified/shifted, try shifting the whole line
                if (!isWordShifted) {
                    ShiftableLine.shiftLine(editor, caretOffset, shiftUp, this.filename, offsetLineStart, line, moreCount);
                }
            }
        }
    }

    /**
     * Get shifted word (and replace if possible)
     *
     * @param shiftUp
     * @param filename
     * @param line
     * @param moreCount Current "more" count, starting w/ 1. If non-more shift: null
     * @return boolean
     */
    private boolean shiftWordAtCaret(boolean shiftUp, String filename, String line, @Nullable Integer moreCount) {
        String word;
        boolean isOperator = false;

        String fileExtension = UtilsFile.extractFileExtension(filename, true);
        // @todo make hyphen-inclusion configurable in dictionary per file type
        boolean isCSS = fileExtension != null && fileExtension.endsWith("css");

        word = UtilsTextual.getOperatorAtOffset(editorText, caretOffset);
        if (word == null) {
            word = UtilsTextual.getWordAtOffset(editorText, caretOffset, isCSS);
        } else {
            isOperator = true;
        }

        boolean isWordShifted = false;
        if (word != null && !word.isEmpty()) {
            isWordShifted = !this.getShiftedWordInDocument(shiftUp, filename, word, line, null, true, isOperator, moreCount).equals(word);

            if (!isWordShifted) {
                // Shifting failed, try shifting lower-cased string
                String wordLower = word.toLowerCase();
                isWordShifted = !this.getShiftedWordInDocument(shiftUp, filename, wordLower, line, null, true, false, moreCount).equals(wordLower);
            }
        }

        return isWordShifted;
    }

    /**
     * @param shiftUp           shiftUp   ...or down?
     * @param filename
     * @param word
     * @param line
     * @param wordOffset        null = calculate from word at offset
     * @param replaceInDocument
     * @param isOperator
     * @param moreCount         current "more" count, starting w/ 1. If non-more shift: null
     * @return String           resulting shifted or original word if no shiftability was found
     */
    public String getShiftedWordInDocument(
            boolean shiftUp,
            String filename, String word, String line, @Nullable Integer wordOffset,
            Boolean replaceInDocument,
            boolean isOperator,
            @Nullable Integer moreCount
    ) {
        boolean wasWordShifted = false;

        if (wordOffset == null) {
            // Extract offset of word at caret
            wordOffset = isOperator
                    ? UtilsTextual.getStartOfOperatorAtOffset(this.editorText, this.caretOffset)
                    : UtilsTextual.getStartOfWordAtOffset(this.editorText, this.caretOffset);
        }

        String prefixChar  = UtilsTextual.getCharBeforeOffset(this.editorText, wordOffset);
        String postfixChar = UtilsTextual.getCharAfterOffset(this.editorText, wordOffset + word.length() - 1);

        // Identify word type and shift it accordingly
        ShiftableWord shiftableWord = new ShiftableWord(word, prefixChar, postfixChar, line, this.editorText, this.caretOffset, filename, moreCount);

        if (!isOperator && (NumericValue.isNumericValue(word) || CssUnit.isCssUnitValue(word)) && "-".equals(prefixChar)) {
            word = "-" + word;
            wordOffset--;
        }

        String newWord = shiftableWord.getShifted(shiftUp, editor);

        if (newWord != null && newWord.length() > 0 && !newWord.matches(Pattern.quote(word)) && wordOffset != null) {
            newWord = shiftableWord.postProcess(newWord, postfixChar);

            if (replaceInDocument) {
                // Replace word at caret by shifted one (if any)
                document.replaceString(wordOffset, wordOffset + word.length(), newWord);
            }
            wasWordShifted = true;
        }

        return wasWordShifted ? newWord : word;
    }

    /**
     * @return String  filename of currently edited document
     */
    private String getFilename() {
        if (this.filename == null && this.document != null) {
            this.filename = UtilsEnvironment.getDocumentFilename(this.document);
        }

        return this.filename;
    }

}
