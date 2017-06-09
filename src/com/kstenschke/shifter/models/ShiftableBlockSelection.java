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

import com.intellij.openapi.editor.*;
import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.actions.ActionsPerformer;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.resources.ui.DialogNumericBlockOptions;
import com.kstenschke.shifter.utils.UtilsEnvironment;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// Shiftable block selection
public class ShiftableBlockSelection {

    /**
     * @param  blockSelectionStarts
     * @param  blockSelectionEnds
     * @param  editorText
     * @return boolean
     */
    private static boolean areNumericValues(int[] blockSelectionStarts, int[] blockSelectionEnds, CharSequence editorText) {
        String currentItem;

        for (int i = 1; i < blockSelectionStarts.length; i++) {
            currentItem = editorText.subSequence(blockSelectionStarts[i], blockSelectionEnds[i]).toString();
            if (!StringUtils.isNumeric(currentItem)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param  blockSelectionStarts
     * @param  blockSelectionEnds
     * @param  editorText
     * @return boolean
     */
    private static boolean areBlockItemsIdentical(int[] blockSelectionStarts, int[] blockSelectionEnds, CharSequence editorText) {
        String firstItem = editorText.subSequence(blockSelectionStarts[0], blockSelectionEnds[0]).toString();
        String currentItem;

        for (int i = 1; i < blockSelectionStarts.length; i++) {
            currentItem = editorText.subSequence(blockSelectionStarts[i], blockSelectionEnds[i]).toString();
            if (!currentItem.equals(firstItem)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param actionsPerformer
     * @param shiftUp
     * @param moreCount Current "more" count, starting w/ 1. If non-more shift: null
     */
    public static void shiftBlockSelectionInDocument(ActionsPerformer actionsPerformer, boolean shiftUp, @Nullable Integer moreCount) {
        Editor editor                 = actionsPerformer.editor;
        if (null == editor) {
            return;
        }
        SelectionModel selectionModel = editor.getSelectionModel();
        Document document             = actionsPerformer.document;

        int[] blockSelectionStarts = selectionModel.getBlockSelectionStarts();
        int[]blockSelectionEnds   = selectionModel.getBlockSelectionEnds();

        String documentText = document.getText();

        if (ShiftableBlockSelection.areNumericValues(blockSelectionStarts, blockSelectionEnds, documentText)) {
            shiftNumericalBlockSelectionInDocument(shiftUp, editor, document, Integer.valueOf(documentText.subSequence(blockSelectionStarts[0], blockSelectionEnds[0]).toString()), documentText);
            return;
        }
        if (ShiftableBlockSelection.areBlockItemsIdentical(blockSelectionStarts, blockSelectionEnds, documentText)) {
            shiftIdenticalBlockItemsInDocument(shiftUp, moreCount, editor, document, blockSelectionStarts, blockSelectionEnds, documentText);
        }
    }

    private static void shiftIdenticalBlockItemsInDocument(boolean shiftUp, @Nullable Integer moreCount, Editor editor, Document document, int[] blockSelectionStarts, int[] blockSelectionEnds, String editorText) {
        String filename = UtilsEnvironment.getDocumentFilename(document);

        String word = editorText.subSequence(blockSelectionStarts[0], blockSelectionEnds[0]).toString();
        String line = UtilsTextual.getLine(document, document.getLineNumber(blockSelectionStarts[0])).trim();

        Integer wordOffset = UtilsTextual.getStartOfWordAtOffset(editorText, blockSelectionStarts[0]);
        String newWord     = ShiftableWord.getShiftedWordInDocument(editor, shiftUp, filename, word, line, wordOffset, false, false, moreCount);

        if (newWord != null && !newWord.equals(word)) {
            for (int i = blockSelectionEnds.length - 1; i >= 0; i--) {
                document.replaceString(blockSelectionStarts[i], blockSelectionEnds[i], newWord);
            }
        }
    }

    /**
     * Ask whether to 1. replace by enumeration or 2. in/decrement each
     *
     * @param shiftUp
     * @param editor
     * @param document
     * @param integer
     * @param editorText
     */
    private static void shiftNumericalBlockSelectionInDocument(boolean shiftUp, Editor editor, Document document, Integer integer, String editorText) {
        Integer firstNumber = integer;
        if (null == firstNumber) {
            firstNumber = 0;
        }
        DialogNumericBlockOptions optionsDialog = new DialogNumericBlockOptions(firstNumber);
        UtilsEnvironment.setDialogVisible(editor, ShifterPreferences.ID_DIALOG_NUMERIC_BLOCK_OPTIONS, optionsDialog, StaticTexts.TITLE_NUMERIC_BLOCK_OPTIONS);
        if (!optionsDialog.wasCancelled()) {
            if (optionsDialog.isShiftModeEnumerate()) {
                insertBlockEnumerationInDocument(editor, document, optionsDialog.getFirstNumber());
                return;
            }
            inOrDecrementNumericBlockInDocument(editor, document, editorText, shiftUp);
        }
    }

    /**
     * Replace given block selection w/ enumeration starting w/ given value
     *
     * @param editor
     * @param document
     * @param firstNumber
     */
    private static void insertBlockEnumerationInDocument(Editor editor, Document document, String firstNumber) {
        Integer currentValue = Integer.valueOf(firstNumber);
        if (null == currentValue) {
            currentValue = 0;
        }

        List<CaretState> caretsAndSelections = editor.getCaretModel().getCaretsAndSelections();
        CaretState caretsAndSelection;
        LogicalPosition selectionStart;
        LogicalPosition selectionEnd;

        int offsetSelectionStart;
        int offsetSelectionEnd;

        for (CaretState caretsAndSelectionCurrent : caretsAndSelections) {
            caretsAndSelection = caretsAndSelectionCurrent;
            selectionStart = caretsAndSelection.getSelectionStart();
            selectionEnd = caretsAndSelection.getSelectionEnd();
            if (selectionStart != null && selectionEnd != null) {
                offsetSelectionStart = editor.logicalPositionToOffset(selectionStart);
                offsetSelectionEnd = editor.logicalPositionToOffset(selectionEnd);

                document.replaceString(offsetSelectionStart, offsetSelectionEnd, currentValue.toString());

                currentValue++;
            }
        }
    }

    /**
     * Increment or decrement each item in given numeric block selection
     *
     * @param editor
     * @param document
     * @param editorText
     * @param shiftUp
     */
    private static void inOrDecrementNumericBlockInDocument(Editor editor, Document document, String editorText, boolean shiftUp) {
        int addend = shiftUp ? 1 : -1;
        Integer value;

        List<CaretState> caretsAndSelections = editor.getCaretModel().getCaretsAndSelections();
        CaretState caretsAndSelection;
        LogicalPosition selectionStart;
        LogicalPosition selectionEnd;

        int offsetSelectionStart;
        int offsetSelectionEnd;

        for (CaretState caretsAndSelectionCurrent : caretsAndSelections) {
            value = null;
            caretsAndSelection = caretsAndSelectionCurrent;
            selectionStart = caretsAndSelection.getSelectionStart();
            selectionEnd = caretsAndSelection.getSelectionEnd();
            if (selectionStart != null && selectionEnd != null) {
                offsetSelectionStart = editor.logicalPositionToOffset(selectionStart);
                offsetSelectionEnd = editor.logicalPositionToOffset(selectionEnd);

                try {
                    value = Integer.valueOf(editorText.subSequence(offsetSelectionStart, offsetSelectionEnd).toString());
                } catch (NumberFormatException e) {
                    // Silently continue
                }
                if (null == value) {
                    value = 0;
                }

                document.replaceString(offsetSelectionStart, offsetSelectionEnd, String.valueOf(value + addend));
            }
        }
    }
}