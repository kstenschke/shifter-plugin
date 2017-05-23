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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.ui.components.JBList;
import com.kstenschke.shifter.models.shiftableTypes.MinusSeparatedPath;
import com.kstenschke.shifter.models.shiftableTypes.PhpConcatenation;
import com.kstenschke.shifter.models.shiftableTypes.SeparatedList;
import com.kstenschke.shifter.models.shiftableTypes.StringCamelCase;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class ShiftableSelectionWithPopup extends ShiftableSelection {

    private Project project;
    private Document document;

    private int offsetStart;
    private int offsetEnd;

    private int lineNumberSelStart;
    private int lineNumberSelEnd;

    private String selectedText;

    private boolean containsQuotes;

    /**
     * Constructor
     *
     * @param project
     * @param document
     * @param offsetStart
     * @param offsetEnd
     */
    public ShiftableSelectionWithPopup(Project project, Document document, int offsetStart, int offsetEnd) {
        this.project  = project;
        this.document = document;

        this.offsetStart = offsetStart;
        this.offsetEnd   = offsetEnd;

        this.lineNumberSelStart = document.getLineNumber(offsetStart);
        this.lineNumberSelEnd   = document.getLineNumber(offsetEnd);

        this.selectedText   = UtilsTextual.getSubString(document.getText(), offsetStart, offsetEnd);
        this.containsQuotes = UtilsTextual.containsQuotes(selectedText);
    }

    /**
     * @param phpConcatenation
     */
    public void shiftPhpConcatenationOrSwapQuotesInDocument(final PhpConcatenation phpConcatenation) {
        if (!containsQuotes) {
            document.replaceString(offsetStart, offsetEnd, phpConcatenation.getShifted());
            return;
        }

        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_CONCATENATION_ITEMS_SWAP_ORDER);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_QUOTES_SWAP);

        shiftSelectionByPopupInDocument(shiftOptions, false, phpConcatenation, null, null);
    }

    /**
     * @param delimiterSplitPattern
     * @param delimiterGlue
     * @param isUp
     */
    public void sortListOrSwapQuotesInDocument(
            final String delimiterSplitPattern, final String delimiterGlue,
            final boolean isUp) {
        if (!containsQuotes) {
            document.replaceString(offsetStart, offsetEnd, SeparatedList.sortSeparatedList(selectedText, delimiterSplitPattern, delimiterGlue, isUp));
            return;
        }

        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_LIST_ITEMS_SORT);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_QUOTES_SWAP);

        shiftSelectionByPopupInDocument(shiftOptions, false,null, delimiterSplitPattern, delimiterGlue);
    }

    /**
     * @param isUp
     */
    public void sortLinesOrSwapQuotesInDocument(final boolean isUp) {
        if (!containsQuotes) {
            ShiftableSelection.sortLinesInDocument(document, isUp, lineNumberSelStart, lineNumberSelEnd);
            return;
        }

        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_LINES_SORT);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_QUOTES_SWAP);

        shiftSelectionByPopupInDocument(shiftOptions, isUp,null, null, null);
    }

    public void shiftCamelCaseOrSwapWords() {
        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_CAMEL_CASE_TO_PATH);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_CAMEL_WORDS_SWAP_ORDER);

        shiftSelectionByPopupInDocument(shiftOptions, false,null, null, null);
    }

    public void shiftMinusSeparatedPathOrSwapWords() {
        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_PATH_TO_CAMEL_CASE);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_PATH_PAIR_SWAP_ORDER);

        shiftSelectionByPopupInDocument(shiftOptions, false,null, null, null);
    }

    /**
     * @param shiftOptions
     * @param isUp
     * @param phpConcatenation
     * @param delimiterSplitPattern
     * @param delimiterGlue
     */
    private void shiftSelectionByPopupInDocument(
            List<String> shiftOptions, final boolean isUp,
            @Nullable final PhpConcatenation phpConcatenation,
            @Nullable final String delimiterSplitPattern, @Nullable final String delimiterGlue
    ) {
        final Object[] options = shiftOptions.toArray(new String[shiftOptions.size()]);
        final JBList modes = new JBList(options);

        PopupChooserBuilder popup = JBPopupFactory.getInstance().createListPopupBuilder(modes);
        popup.setTitle(StaticTexts.POPUP_TITLE_SHIFT).setItemChoosenCallback(new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        // Callback when item chosen
                        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                                    public void run() {
                                        shiftSelectionByModeInDocument(modes.getSelectedValue().toString(), isUp, phpConcatenation, delimiterSplitPattern, delimiterGlue);
                                    }
                                },
                                null, null);
                    }
                });
            }
        }).setMovable(true).createPopup().showCenteredInCurrentWindow(project);
    }

    /**
     * @param mode
     * @param isUp
     * @param phpConcatenation
     * @param delimiterSplitPattern
     * @param delimiterGlue
     */
    private void shiftSelectionByModeInDocument(
            String mode, boolean isUp,
            @Nullable PhpConcatenation phpConcatenation,
            @Nullable String delimiterSplitPattern, @Nullable String delimiterGlue
    ) {
        if (mode.equals(StaticTexts.SHIFT_OPTION_CONCATENATION_ITEMS_SWAP_ORDER)) {
            document.replaceString(offsetStart, offsetEnd, phpConcatenation.getShifted());
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_CAMEL_WORDS_SWAP_ORDER)) {
            document.replaceString(offsetStart, offsetEnd, StringCamelCase.flipWordPairOrder(selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_LIST_ITEMS_SORT)) {
            document.replaceString(offsetStart, offsetEnd, SeparatedList.sortSeparatedList(selectedText, delimiterSplitPattern, delimiterGlue, isUp));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_PATH_PAIR_SWAP_ORDER)) {
            document.replaceString(offsetStart, offsetEnd, MinusSeparatedPath.flipWordsOrder(selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_LINES_SORT)) {
            ShiftableSelection.sortLinesInDocument(document, isUp, lineNumberSelStart, lineNumberSelEnd);
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_QUOTES_SWAP)) {
            document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapQuotes(selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_CAMEL_CASE_TO_PATH)) {
            document.replaceString(offsetStart, offsetEnd, StringCamelCase.getShifted(selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_PATH_TO_CAMEL_CASE)) {
            document.replaceString(offsetStart, offsetEnd, MinusSeparatedPath.getShifted(selectedText));
        }
    }
}