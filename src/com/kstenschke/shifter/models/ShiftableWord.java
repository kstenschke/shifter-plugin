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
import com.kstenschke.shifter.models.entities.AbstractShiftable;
import com.kstenschke.shifter.models.entities.shiftables.CssUnit;
import com.kstenschke.shifter.models.entities.shiftables.JsDoc;
import com.kstenschke.shifter.models.entities.shiftables.NumericValue;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;

import static com.kstenschke.shifter.models.ShiftableTypes.Type.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class ShiftableWord {

    private ShiftableTypesManager shiftingShiftableTypesManager;
    private String word;

    // "more" count, starting w/ 1. If non-more shift: null
    private final Integer moreCount;

    private AbstractShiftable shiftable;
    // @todo eliminate shiftableType, use shiftable
    private ShiftableTypes.Type shiftableType;

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
            @NotNull ActionContainer actionContainer,
            String word,
            String prefixChar,
            String postfixChar,
            @Nullable Integer moreCount
    ) {
        this.actionContainer = actionContainer;
        this.moreCount       = moreCount;

        shiftingShiftableTypesManager = new ShiftableTypesManager(actionContainer);

        // Detect word type
        shiftingShiftableTypesManager.setPrefixChar(prefixChar);
        // @todo eliminate shiftableType, use shiftable
        shiftable = shiftingShiftableTypesManager.getShiftable();
        shiftableType = shiftingShiftableTypesManager.getShiftableType();

        // Comprehend negative values of numeric shiftables
        this.word = (
                (CSS_UNIT == shiftableType ||
                 NUMERIC_VALUE == shiftableType)
                && "-".equals(prefixChar)
        )
            ? "-" + word
            : word;

        // Can the word be shifted?
        isShiftable = UNKNOWN != shiftableType;
    }

    public ShiftableTypes.Type getShiftableType() {
        return shiftableType;
    }

    /**
     * @uses  actionContainer.isUp     Shift up or down?
     * @uses  actionContainer.editor   Nullable (required to retrieve offset for positioning info-balloon which isn't shown if editor == null)
     * @return String   Next upper/lower word
     */
    public String getShifted(boolean checkIfIsShiftable) {
        if (!checkIfIsShiftable && !isShiftable) {
            return word;
        }

        //String shiftedWord = shiftable.getShifted(word, moreCount);
        String shiftedWord = shiftingShiftableTypesManager.getShiftedWord(actionContainer, word, shiftableType, moreCount);

        return word.equals(shiftedWord) ? word : maintainCasingOnShiftedWord(shiftedWord);
    }

    private String maintainCasingOnShiftedWord(String shiftedWord) {
        if (    PHP_VARIABLE != shiftableType
             && QUOTED_STRING != shiftableType
             && CAMEL_CASE_STRING != shiftableType
             && ShifterPreferences.getIsActivePreserveCase()
        ) {
            if (UtilsTextual.isAllUppercase(word)) return shiftedWord.toUpperCase();
            if (UtilsTextual.isUcFirstRestLower(word)) return UtilsTextual.toUcFirstRestLower(shiftedWord);
            if (UtilsTextual.isLcFirst(word)) return UtilsTextual.toLcFirst(shiftedWord);
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
        if (!UtilsFile.isCssFile(actionContainer.filename)) return word;

        switch (shiftableType) {
            // "0" was shifted to a different numeric value, inside a CSS file, so we can add a measure unit
            case NUMERIC_VALUE:
                return CssUnit.isCssUnit(postfix)
                        ? word
                        : word + CssUnit.determineMostProminentUnit(actionContainer.editorText.toString());
            // Correct "0px" (or other unit) to "0"
            case CSS_UNIT: return word.startsWith("0") ? "0" : word;
            default: return word;
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
        String word = UtilsTextual.getOperatorAtOffset(actionContainer.editorText, actionContainer.caretOffset);
        if (null == word) {
            word = UtilsTextual.getWordAtOffset(
                    actionContainer.editorText,
                    actionContainer.caretOffset,
                    actionContainer.fileExtension.endsWith("css"));
        } else isOperator = true;

        if (null == word || word.isEmpty()) return false;

        if (actionContainer.fileExtension.endsWith("js") &&
            shiftWordAtCaretInJsDocument(actionContainer, word))
                return true;

        if (!word.equals(getShiftedWordInDocument(
                actionContainer,
                word,
                null,
                true,
                isOperator,
                moreCount)))
            return true;

            // Shifting failed, try shifting lower-cased string
            String wordLower = word.toLowerCase();
            return !getShiftedWordInDocument(
                    actionContainer,
                    wordLower,
                    null,
                    true,
                    false,
                    moreCount).equals(wordLower);
    }

    @Nullable
    private static Boolean shiftWordAtCaretInJsDocument(final ActionContainer actionContainer, final String word) {
        if (
            (JsDoc.isAtParamLine(actionContainer.caretLine) || JsDoc.isAtTypeLine(actionContainer.caretLine)) &&
            JsDoc.containsNoCompounds(actionContainer.caretLine) &&
            JsDoc.isWordRightOfAtKeyword(word, actionContainer.caretLine) &&
            JsDoc.isDataType(word)
        ) {
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

        if (!JsDoc.isInvalidAtReturnsLine(actionContainer.caretLine)) return false;

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
            @NotNull ActionContainer actionContainer,
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
        ShiftableWord shiftableWord = new ShiftableWord(actionContainer, word, prefixChar, postfixChar, moreCount);
        // @todo reuse shiftableTypesManager here, eliminate hard-code knowledge about types' internals

        if (!isOperator &&
            "-".equals(prefixChar) &&
            (null != new NumericValue(actionContainer).getInstance() ||
             null != new CssUnit(actionContainer).getInstance()
            )
        ) {
            word = "-" + word;
            wordOffset--;
        }

        String newWord = shiftableWord.getShifted(true);
        if (null == newWord ||
            newWord.length() == 0 ||
            newWord.matches(Pattern.quote(word)) ||
            null == wordOffset
        ) {
            return word;
        }

        newWord = shiftableWord.postProcess(newWord, postfixChar);
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
                            if ((null == actionContainer.selectedText || actionContainer.selectedText.isEmpty()) &&
                                newWordFin.contains(" ")
                            ) {
                                // There's no selection and shifted word newly contains a space: select it
                                actionContainer.selectionModel.setSelection(
                                        wordOffsetFin,
                                        wordOffsetFin + newWordFin.length());
                            }
                        }
                    });

        }

        return newWord;
    }
}