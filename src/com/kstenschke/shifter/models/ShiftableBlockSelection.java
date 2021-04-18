/*
 * Copyright Kay Stenschke
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
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.resources.ui.DialogNumericBlockOptions;
import com.kstenschke.shifter.utils.UtilsEnvironment;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// Shiftable block selection
public class ShiftableBlockSelection {

    private static final String ACTION_TEXT_SHIFT_COLUMN_SELECTION = "Shift Column Selection";

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
     * @param actionContainer
     * @param moreCount Current "more" count, starting w/ 1. If non-more shift: null
     */
    public static void shiftBlockSelectionInDocument(final ActionContainer actionContainer, @Nullable Integer moreCount) {
        if (null == actionContainer.editor) {
            return;
        }

        final int stepSize = null == moreCount ? 1 : moreCount;

        final int[] blockSelectionStarts = actionContainer.selectionModel.getBlockSelectionStarts();
        final int[]blockSelectionEnds   = actionContainer.selectionModel.getBlockSelectionEnds();

        if (ShiftableBlockSelection.areNumericValues(blockSelectionStarts, blockSelectionEnds, actionContainer.documentText)) {
            shiftNumericalBlockSelectionInDocument(
                    actionContainer,
                    Integer.valueOf(actionContainer.documentText.subSequence(blockSelectionStarts[0], blockSelectionEnds[0]).toString()),
                    stepSize);
            return;
        }
        if (ShiftableBlockSelection.areBlockItemsIdentical(blockSelectionStarts, blockSelectionEnds, actionContainer.documentText)) {
            actionContainer.writeUndoable(
                    () -> shiftIdenticalBlockItemsInDocument(actionContainer, stepSize, blockSelectionStarts, blockSelectionEnds),
                    ACTION_TEXT_SHIFT_COLUMN_SELECTION
            );
        }
    }

    private static void shiftIdenticalBlockItemsInDocument(ActionContainer actionContainer, @Nullable Integer moreCount, int[] blockSelectionStarts, int[] blockSelectionEnds) {
        String word        = actionContainer.editorText.subSequence(blockSelectionStarts[0], blockSelectionEnds[0]).toString();
        Integer wordOffset = UtilsTextual.getStartOfWordAtOffset(actionContainer.editorText, blockSelectionStarts[0]);
        String newWord     = ShiftableWord.getShiftedWordInDocument(actionContainer, word, wordOffset, false, false, moreCount);

        if (null != newWord && !newWord.equals(word)) {
            for (int i = blockSelectionEnds.length - 1; i >= 0; i--) {
                actionContainer.document.replaceString(blockSelectionStarts[i], blockSelectionEnds[i], newWord);
            }
        }
    }

    /**
     * Ask whether to 1. replace by enumeration or 2. in/decrement each
     *
     * @param actionContainer
     * @param startWith
     * @param stepSize
     */
    private static void shiftNumericalBlockSelectionInDocument(final ActionContainer actionContainer, Integer startWith, final int stepSize) {
        Integer firstNumber = null == startWith ? 0 : startWith;
        final DialogNumericBlockOptions optionsDialog = new DialogNumericBlockOptions(firstNumber);
        UtilsEnvironment.setDialogVisible(actionContainer.editor, ShifterPreferences.ID_DIALOG_NUMERIC_BLOCK_OPTIONS, optionsDialog, StaticTexts.TITLE_NUMERIC_BLOCK_OPTIONS);
        if (optionsDialog.wasCancelled()) {
            return;
        }

        if (optionsDialog.isShiftModeEnumerate()) {
            actionContainer.writeUndoable(
                    () -> insertBlockEnumerationInDocument(actionContainer, optionsDialog.getFirstNumber()),
                    ACTION_TEXT_SHIFT_COLUMN_SELECTION
            );

            return;
        }

        actionContainer.writeUndoable(
                () -> inOrDecrementNumericBlockInDocument(actionContainer, stepSize),
                ACTION_TEXT_SHIFT_COLUMN_SELECTION
        );
    }

    /**
     * Replace given block selection w/ enumeration starting w/ given value
     *
     * @param actionContainer
     * @param firstNumber
     */
    private static void insertBlockEnumerationInDocument(ActionContainer actionContainer, String firstNumber) {
        Integer currentValue = Integer.valueOf(firstNumber);

        List<CaretState> caretsAndSelections = actionContainer.editor.getCaretModel().getCaretsAndSelections();
        CaretState caretsAndSelection;
        LogicalPosition selectionStart;
        LogicalPosition selectionEnd;

        int offsetSelectionStart;
        int offsetSelectionEnd;

        for (CaretState caretsAndSelectionCurrent : caretsAndSelections) {
            caretsAndSelection = caretsAndSelectionCurrent;
            selectionStart = caretsAndSelection.getSelectionStart();
            selectionEnd = caretsAndSelection.getSelectionEnd();
            if (null != selectionStart && null != selectionEnd) {
                offsetSelectionStart = actionContainer.editor.logicalPositionToOffset(selectionStart);
                offsetSelectionEnd = actionContainer.editor.logicalPositionToOffset(selectionEnd);

                actionContainer.document.replaceString(offsetSelectionStart, offsetSelectionEnd, currentValue.toString());

                currentValue++;
            }
        }
    }

    /**
     * Increment or decrement each item in given numeric block selection
     *
     * @param actionContainer
     * @param stepSize
     */
    private static void inOrDecrementNumericBlockInDocument(ActionContainer actionContainer, int stepSize) {
        int addend = actionContainer.isShiftUp ? stepSize : -stepSize;
        Integer value;

        List<CaretState> caretsAndSelections = actionContainer.editor.getCaretModel().getCaretsAndSelections();
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
            if (null != selectionStart && null != selectionEnd) {
                offsetSelectionStart = actionContainer.editor.logicalPositionToOffset(selectionStart);
                offsetSelectionEnd = actionContainer.editor.logicalPositionToOffset(selectionEnd);

                try {
                    value = Integer.valueOf(actionContainer.editorText.subSequence(offsetSelectionStart, offsetSelectionEnd).toString());
                } catch (NumberFormatException e) {
                    // Silently continue
                }
                if (null == value) {
                    value = 0;
                }

                actionContainer.document.replaceString(offsetSelectionStart, offsetSelectionEnd, String.valueOf(value + addend));
            }
        }
    }
}
