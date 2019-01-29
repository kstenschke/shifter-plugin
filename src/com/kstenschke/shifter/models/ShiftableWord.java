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
package com.kstenschke.shifter.models;

import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.models.shiftableTypes.CssUnit;
import com.kstenschke.shifter.models.shiftableTypes.JsDoc;
import com.kstenschke.shifter.models.shiftableTypes.NumericValue;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

import static com.kstenschke.shifter.models.ShiftableTypes.Type.*;

/**
 * Shiftable word
 */
public class ShiftableWord {

    private final ShiftableTypesManager shiftingShiftableTypesManager;
    private final String word;

    // "more" count, starting w/ 1. If non-more shift: null
    private final Integer moreCount;

    private final ShiftableTypes.Type wordType;
    private final boolean isShiftable;

    final private ActionContainer actionContainer;

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
            ActionContainer actionContainer,
            String word,
            String prefixChar,
            String postfixChar,
            @Nullable Integer moreCount
    ) {
        this.actionContainer = actionContainer;
        this.moreCount       = moreCount;

        shiftingShiftableTypesManager = new ShiftableTypesManager();

        // Detect word type
        wordType = shiftingShiftableTypesManager.getWordType(word, prefixChar, postfixChar, false, actionContainer);

        // Comprehend negative values of numeric shiftableTypes
        this.word = (
                (CSS_UNIT == wordType || NUMERIC_VALUE == wordType)
                && "-".equals(prefixChar)
        )
            ? "-" + word
            : word;

        // Can the word be shifted?
        isShiftable = UNKNOWN != wordType;
    }

    /**
     * Get shifted up/down word
     *
     * @uses  actionContainer.isUp     Shift up or down?
     * @uses  actionContainer.editor   Nullable (required to retrieve offset for positioning info-balloon which isn't shown if editor == null)
     * @return String   Next upper/lower word
     */
    public String getShifted() {
        if (isShiftable) {
            String shiftedWord = shiftingShiftableTypesManager.getShiftedWord(actionContainer, word, wordType, moreCount);

            return word.equals(shiftedWord) ? word : maintainCasingOnShiftedWord(shiftedWord);
        }

        return word;
    }

    private String maintainCasingOnShiftedWord(String shiftedWord) {
        if (    PHP_VARIABLE_OR_ARRAY != wordType
             && QUOTED_STRING != wordType
             && CAMEL_CASED != wordType
             && ShifterPreferences.getIsActivePreserveCase()
        ) {
            if (UtilsTextual.isAllUppercase(word)) {
                return shiftedWord.toUpperCase();
            }
            if (UtilsTextual.isUcFirst(word)) {
                return UtilsTextual.toUcFirstRestLower(shiftedWord);
            }
            if (UtilsTextual.isLcFirst(word)) {
                return UtilsTextual.toLcFirst(shiftedWord);
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
        if (!UtilsFile.isCssFile(actionContainer.filename)) {
            return word;
        }

        switch (wordType) {
            // "0" was shifted to a different numeric value, inside a CSS file, so we can add a measure unit
            case NUMERIC_VALUE:
                return CssUnit.isCssUnit(postfix)
                        ? word
                        : word + CssUnit.determineMostProminentUnit(actionContainer.editorText.toString());
            case CSS_UNIT:
                // Correct "0px" (or other unit) to "0"
                return word.startsWith("0") ? "0" : word;
            default:
                return word;
        }
    }

    /**
     * Get shifted word (and replace if possible)
     *
     * @param shiftUp
     * @param line
     * @param moreCount Current "more" count, starting w/ 1. If non-more shift: null
     * @return boolean
     */
    public static boolean shiftWordAtCaretInDocument(ActionContainer actionContainer, @Nullable Integer moreCount) {
        boolean isOperator = false;
        String word        = UtilsTextual.getOperatorAtOffset(actionContainer.editorText, actionContainer.caretOffset);
        if (null == word) {
            boolean isCSS = actionContainer.fileExtension.endsWith("css");
            word = UtilsTextual.getWordAtOffset(actionContainer.editorText, actionContainer.caretOffset, isCSS);
        } else {
            isOperator = true;
        }

        if (null == word || word.isEmpty()) {
            return false;
        }
        if (actionContainer.fileExtension.endsWith("js") && shiftWordAtCaretInJsDocument(actionContainer, word)) {
            return true;
        }

        boolean isWordShifted = !getShiftedWordInDocument(actionContainer, word, null, true, isOperator, moreCount).equals(word);
        if (!isWordShifted) {
            // Shifting failed, try shifting lower-cased string
            String wordLower = word.toLowerCase();
            isWordShifted = !getShiftedWordInDocument(actionContainer, wordLower, null, true, false, moreCount).equals(wordLower);
        }

        return isWordShifted;
    }

    @Nullable
    private static Boolean shiftWordAtCaretInJsDocument(final ActionContainer actionContainer, final String word) {
        if (   (JsDoc.isAtParamLine(actionContainer.caretLine) || JsDoc.isAtTypeLine(actionContainer.caretLine))
            && JsDoc.containsNoCompounds(actionContainer.caretLine) && JsDoc.isWordRightOfAtKeyword(word, actionContainer.caretLine) && JsDoc.isDataType(word)) {
            // Add missing curly brackets around data type at caret in jsDoc @param line
            actionContainer.writeUndoable(
                    new Runnable() {
                @Override
                public void run() {
                    JsDoc.addCompoundsAroundDataTypeAtCaretInDocument(actionContainer, word);
                }
            },
                    "Shift JsDoc Data Type");

            return true;
        }
        if (JsDoc.isInvalidAtReturnsLine(actionContainer.caretLine)) {
            actionContainer.writeUndoable(
                    new Runnable() {
                        @Override
                        public void run() {
                            JsDoc.correctInvalidReturnsCommentInDocument(actionContainer);
                        }
                    },
                    "Shift JsDoc");

            return true;
        }

        return false;
    }

    /**
     * @param shiftUp           isShiftUp   ...or down?
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
            final ActionContainer actionContainer,
            String word,
            @Nullable Integer wordOffset,
            Boolean replaceInDocument,
            boolean isOperator,
            @Nullable Integer moreCount
    ) {
        if (null == wordOffset) {
            // Extract offset of word at caret
            wordOffset = isOperator
                    ? UtilsTextual.getStartOfOperatorAtOffset(actionContainer.editorText, actionContainer.caretOffset)
                    : UtilsTextual.getStartOfWordAtOffset(actionContainer.editorText, actionContainer.caretOffset);
        }

        String prefixChar  = UtilsTextual.getCharBeforeOffset(actionContainer.editorText, wordOffset);
        String postfixChar = UtilsTextual.getCharAfterOffset(actionContainer.editorText, wordOffset + word.length() - 1);

        // Identify word type and shift it accordingly
        ShiftableWord shiftableShiftableWord = new ShiftableWord(actionContainer, word, prefixChar, postfixChar, moreCount);

        if (!isOperator && (NumericValue.isNumericValue(word) || CssUnit.isCssUnitValue(word)) && "-".equals(prefixChar)) {
            word = "-" + word;
            wordOffset--;
        }

        String newWord = shiftableShiftableWord.getShifted();
        if (null == newWord || newWord.length() == 0 || newWord.matches(Pattern.quote(word)) || null == wordOffset) {
            return word;
        }

        newWord = shiftableShiftableWord.postProcess(newWord, postfixChar);
        final int wordOffsetFin = wordOffset;
        final int wordOffsetEndFin = wordOffset + word.length();
        final String newWordFin = newWord;

        if (replaceInDocument) {
            // Replace word at caret by shifted one (if any)
            actionContainer.writeUndoable(
                    new Runnable() {
                        @Override
                        public void run() {
                            actionContainer.document.replaceString(wordOffsetFin, wordOffsetEndFin, newWordFin);
                            if (actionContainer.selectedText.isEmpty() && newWordFin.contains(" ")) {
                                // There's no selection and shifted word newly contains a space: select it
                                actionContainer.selectionModel.setSelection(wordOffsetFin, wordOffsetFin + newWordFin.length());
                            }
                        }
                    });

        }
        return newWord;
    }
}