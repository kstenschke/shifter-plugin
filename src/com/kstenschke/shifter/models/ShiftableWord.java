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
package com.kstenschke.shifter.models;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.models.shiftertypes.CssUnit;
import com.kstenschke.shifter.models.shiftertypes.NumericValue;
import com.kstenschke.shifter.utils.UtilsEnvironment;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * Shiftable word
 */
public class ShiftableWord {

    private final ShiftingTypesManager shifterTypesManager;
    private final String word;
    private final String filename;

    // "more" count, starting w/ 1. If non-more shift: null
    private final Integer moreCount;

    private final int wordType;
    private final boolean isShiftable;
    private final CharSequence editorText;

    private final int caretOffset;

    /**
     * Constructor
     *
     * @param word        Shiftable word
     * @param prefixChar  Char before the word, "#"?
     * @param postfixChar Char after the word, "#"?
     * @param line        Whole line to possibly guess the context
     * @param editorText  Whole text currently in editor
     * @param caretOffset Caret offset in document
     * @param filename    Filename of the edited file
     * @param moreCount   Current "more" count, starting w/ 1. If non-more shift: null
     */
    public ShiftableWord(
            String word, String prefixChar, String postfixChar,
            String line, CharSequence editorText,
            int caretOffset,
            String filename,
            @Nullable Integer moreCount
    ) {
        this.shifterTypesManager = new ShiftingTypesManager();

        this.editorText = editorText;
        this.caretOffset = caretOffset;
        this.filename = filename;
        this.moreCount = moreCount;

        // Detect word type
        this.wordType = shifterTypesManager.getWordType(word, prefixChar, postfixChar, false, line, filename);

        // Comprehend negative values of numeric types
        this.word = ((this.wordType == ShiftingTypesManager.TYPE_CSS_UNIT || this.wordType == ShiftingTypesManager.TYPE_NUMERIC_VALUE)
                && "-".equals(prefixChar)) ? "-" + word : word;

        // Can the word be shifted?
        this.isShiftable = this.wordType != ShiftingTypesManager.TYPE_UNKNOWN;
    }

    /**
     * Get shifted up/down word
     *
     * @param  isUp     Shift up or down?
     * @param  editor   Nullable (required to retrieve offset for positioning info-balloon which isn't shown if editor == null)
     * @return String   Next upper/lower word
     */
    public String getShifted(boolean isUp, @Nullable Editor editor) {
        if (!this.isShiftable) {
            return this.word;
        }

        // Call actual shifting
        String shiftedWord = shifterTypesManager.getShiftedWord(this.word, this.wordType, isUp, this.editorText, this.caretOffset, this.moreCount, filename, editor);

        // Keep original word casing
        if (    this.wordType != ShiftingTypesManager.TYPE_PHP_VARIABLE
             && this.wordType != ShiftingTypesManager.TYPE_QUOTED_STRING
             && ShifterPreferences.getIsActivePreserveCase()
        ) {
            if (UtilsTextual.isAllUppercase(this.word)) {
                // Convert result to upper case
                shiftedWord = shiftedWord.toUpperCase();
            } else if (UtilsTextual.isUcFirst(this.word)) {
                // Convert result to upper case first char
                shiftedWord = UtilsTextual.toUcFirst(shiftedWord);
            }

        }

        return shiftedWord;
    }

    /**
     * Post-process: do additional modifications on word after it has been shifted
     *
     * @param  word
     * @param  postfix
     * @return String   Post-processed word
     */
    private String postProcess(String word, String postfix) {
        if (UtilsFile.isCssFile(this.filename)) {
            switch (this.wordType) {
                // "0" was shifted to a different numeric value, inside a CSS file, so we can add a measure unit
                case ShiftingTypesManager.TYPE_NUMERIC_VALUE:
                    if (!CssUnit.isCssUnit(postfix)) {
                        return word + CssUnit.determineMostProminentUnit(this.editorText.toString());
                    }
                    break;
                case ShiftingTypesManager.TYPE_CSS_UNIT:
                    // Correct "0px" (or other unit) to "0"
                    if (word.startsWith("0")) {
                        return "0";
                    }
                    break;
                default:
                    return word;
            }
        }

        return word;
    }

    /**
     * Get shifted word (and replace if possible)
     *
     * @param shiftUp
     * @param line
     * @param moreCount Current "more" count, starting w/ 1. If non-more shift: null
     * @return boolean
     */
    public static boolean shiftWordAtCaret(Editor editor, Integer caretOffset, boolean shiftUp, String line, @Nullable Integer moreCount) {
        Document document       = editor.getDocument();
        CharSequence editorText = document.getCharsSequence();
        String filename         = UtilsEnvironment.getDocumentFilename(document);

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
            isWordShifted = !getShiftedWordInDocument(editor, shiftUp, filename, word, line, null, true, isOperator, moreCount).equals(word);

            if (!isWordShifted) {
                // Shifting failed, try shifting lower-cased string
                String wordLower = word.toLowerCase();
                isWordShifted = !getShiftedWordInDocument(editor, shiftUp, filename, wordLower, line, null, true, false, moreCount).equals(wordLower);
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
     * @return String           resulting shifted or original word if no shift-ability was found
     */
    public static String getShiftedWordInDocument(
            Editor editor,
            boolean shiftUp,
            String filename, String word, String line, @Nullable Integer wordOffset,
            Boolean replaceInDocument,
            boolean isOperator,
            @Nullable Integer moreCount
    ) {
        Document document       = editor.getDocument();
        CharSequence editorText = document.getCharsSequence();
        int caretOffset         = editor.getCaretModel().getOffset();

        boolean wasWordShifted = false;

        if (wordOffset == null) {
            // Extract offset of word at caret
            wordOffset = isOperator
                    ? UtilsTextual.getStartOfOperatorAtOffset(editorText, caretOffset)
                    : UtilsTextual.getStartOfWordAtOffset(editorText, caretOffset);
        }

        String prefixChar  = UtilsTextual.getCharBeforeOffset(editorText, wordOffset);
        String postfixChar = UtilsTextual.getCharAfterOffset(editorText, wordOffset + word.length() - 1);

        // Identify word type and shift it accordingly
        ShiftableWord shiftableWord = new ShiftableWord(word, prefixChar, postfixChar, line, editorText, caretOffset, filename, moreCount);

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

}