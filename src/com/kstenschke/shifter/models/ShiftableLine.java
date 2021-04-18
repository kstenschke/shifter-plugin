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

import com.kstenschke.shifter.models.shiftable_types.JsDoc;
import com.kstenschke.shifter.models.shiftable_types.PhpDocParam;
import com.kstenschke.shifter.models.shiftable_types.HtmlEncodable;
import com.kstenschke.shifter.utils.UtilsFile;
import org.jetbrains.annotations.Nullable;

/**
 * Shiftable Line
 * Shifting strategy:
 * Either line contains one word of a clearly detectable type  => shifting will transform that word.
 * Or line context can be detected                             => resp. shifting be done.
 */
public class ShiftableLine {

    @SuppressWarnings("WeakerAccess")
    public static final String ACTION_TEXT = "Shift Line";

    private final ActionContainer actionContainer;

    /**
     * @param actionContainer
     */
    private ShiftableLine(ActionContainer actionContainer) {
        this.actionContainer = actionContainer;
    }

    /**
     * Get shifted up/down word
     *
     * @param  moreCount    Current counter while iterating multi-shift
     * @return String       Next upper/lower word
     */
    private String getShifted(@Nullable final Integer moreCount) {
        if (UtilsFile.isPhpFile(actionContainer.filename) && PhpDocParam.isPhpDocParamLine(actionContainer.caretLine) && !PhpDocParam.containsDataType(actionContainer.caretLine) && PhpDocParam.containsVariableName(actionContainer.caretLine)) {
            // Caret-line is a PHP doc @param w/o data type: guess and insert one by the variable name
            String shiftedLine = PhpDocParam.getShifted(actionContainer.caretLine);
            if (!shiftedLine.equals(actionContainer.caretLine)) {
                return shiftedLine;
            }
        }

        if (   UtilsFile.isJavaScriptFile(actionContainer.filename, true)
            && (JsDoc.isAtParamLine(actionContainer.caretLine) || JsDoc.isAtTypeLine(actionContainer.caretLine) || JsDoc.isAtReturnsLine(actionContainer.caretLine, true))
        ) {
            String shiftedLine = JsDoc.correctAtKeywordLine(actionContainer.caretLine);
            if (!shiftedLine.equals(actionContainer.caretLine)) {
                return shiftedLine;
            }
        }

        String[] words = actionContainer.caretLine.trim().split("\\s+");

        // Check all words for shiftable shiftable_types - shiftable if there's not more than one
        int amountShiftableWordsInSentence = 0;
        String wordShiftedTest;
        String wordUnshifted = "";
        String wordShifted   = "";
        String prefixChar    = "";
        String postfixChar   = "";

        for (String word : words) {
            if (word.length() > 2) {
                // Check if word is a hex RGB color including the #-prefix
                if (word.startsWith("#")) {
                    prefixChar = "#";
                    word = word.substring(1);
                }

                wordShiftedTest = new ShiftableWord(actionContainer, word, prefixChar, postfixChar, moreCount).getShifted();
                if (null != wordShiftedTest && !wordShiftedTest.equals(word)) {
                    amountShiftableWordsInSentence++;
                    wordUnshifted = word;
                    wordShifted = wordShiftedTest;
                }
            }
        }

        if (1 == amountShiftableWordsInSentence) {
            // Shift detected word in lLine
            String line = actionContainer.caretLine;
            return line.replace(wordUnshifted, wordShifted);
        }

        return HtmlEncodable.isHtmlEncodable(actionContainer.caretLine)
            // Encode or decode contained HTML special chars
            ? HtmlEncodable.getShifted(actionContainer.caretLine)
            // No shift-ability detected, return original line
            : actionContainer.caretLine;
    }

    /**
     * @param actionContainer
     * @param moreCount       Current "more" count, starting w/ 1. If non-more shift: null
     */
    public static void shiftLineInDocument(final ActionContainer actionContainer, @Nullable Integer moreCount) {
        ShiftableLine shiftableShiftableLine = new ShiftableLine(actionContainer);

        // Replace line by shifted one
        final CharSequence shiftedLine = shiftableShiftableLine.getShifted(moreCount);
        if (null != shiftedLine) {
            actionContainer.writeUndoable(actionContainer.getRunnableReplaceCaretLine(shiftedLine), ACTION_TEXT);
        }
    }
}
