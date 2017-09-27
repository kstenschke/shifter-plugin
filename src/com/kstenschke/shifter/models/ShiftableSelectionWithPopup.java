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
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.ui.components.JBList;
import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.models.shiftableTypes.*;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.utils.UtilsEnvironment;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShiftableSelectionWithPopup extends ShiftableSelection {

    private final Project project;
    private final Document document;

    private final int offsetStart;
    private final int offsetEnd;

    private final int lineNumberSelStart;
    private final int lineNumberSelEnd;

    private final String selectedText;

    private final boolean containsShiftableQuotes;
    private final boolean containsEscapedQuotes;

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

        this.selectedText            = UtilsTextual.getSubString(document.getText(), offsetStart, offsetEnd);
        this.containsShiftableQuotes = QuotedString.containsShiftableQuotes(selectedText);
        this.containsEscapedQuotes   = QuotedString.containsEscapedQuotes(selectedText);
    }

    private void addQuoteShiftingOptions(List<String> shiftOptions) {
        String selectedText = document.getText().substring(offsetStart, offsetEnd);
        boolean containsSingleQuotes = selectedText.contains("'");
        boolean containsDoubleQuotes = selectedText.contains("\"");
        if (containsSingleQuotes && containsDoubleQuotes) {
            shiftOptions.add(StaticTexts.SHIFT_OPTION_QUOTES_SWAP);
        }
        if (containsDoubleQuotes && ShifterPreferences.getIsActiveConvertDoubleQuotes()) {
            shiftOptions.add(StaticTexts.SHIFT_OPTION_QUOTES_DOUBLE_TO_SINGLE);
        }
        if (containsSingleQuotes && ShifterPreferences.getIsActiveConvertSingleQuotes()) {
            shiftOptions.add(StaticTexts.SHIFT_OPTION_QUOTES_SINGLE_TO_DOUBLE);
        }
        if (containsEscapedQuotes) {
            shiftOptions.add(StaticTexts.SHIFT_OPTION_UNESCAPE_QUOTES);
        }
    }

    /**
     * @param phpConcatenation
     */
    public void shiftPhpConcatenationOrSwapQuotesInDocument(final PhpConcatenation phpConcatenation, boolean isUp) {
        if (!containsShiftableQuotes) {
            document.replaceString(offsetStart, offsetEnd, phpConcatenation.getShifted());
            return;
        }

        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_CONCATENATION_ITEMS_SWAP_ORDER);
        addQuoteShiftingOptions(shiftOptions);

        shiftSelectionByPopupInDocument(shiftOptions, isUp, phpConcatenation, null, null);
    }

    /**
     * @param delimiterSplitPattern
     * @param delimiterGlue
     * @param isUp
     */
    public void sortListOrSwapQuotesInDocument(final String delimiterSplitPattern, final String delimiterGlue, final boolean isUp) {
        if (!containsShiftableQuotes) {
            document.replaceString(offsetStart, offsetEnd, SeparatedList.sortSeparatedList(selectedText, delimiterSplitPattern, delimiterGlue, isUp));
            return;
        }

        List<String> shiftOptions = new ArrayList<String>();

        String items[] = selectedText.split(delimiterSplitPattern);
        shiftOptions.add(items.length == 2 ? StaticTexts.SHIFT_OPTION_LIST_ITEMS_SWAP : StaticTexts.SHIFT_OPTION_LIST_ITEMS_SORT);
        addQuoteShiftingOptions(shiftOptions);

        shiftSelectionByPopupInDocument(shiftOptions, isUp,null, delimiterSplitPattern, delimiterGlue);
    }

    public void swapParenthesisOrConvertPphpArray() {
        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_SWAP_PARENTHESIS);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_CONVERT_PHP_ARRAY_TO_LONG_SYNTAX);

        shiftSelectionByPopupInDocument(shiftOptions, false,null, null, null);
    }

    public void shiftQuotesInDocument() {
        List<String> shiftOptions = new ArrayList<String>();
        addQuoteShiftingOptions(shiftOptions);

        shiftSelectionByPopupInDocument(shiftOptions, false,null, null, null);
    }

    public void swapSlashesOrUnescapeQuotes() {
        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_SLASHES_SWAP);
        addQuoteShiftingOptions(shiftOptions);

        shiftSelectionByPopupInDocument(shiftOptions, false,null, null, null);
    }

    /**
     * @param isUp
     */
    public void sortLinesOrSwapQuotesInDocument(final boolean isUp) {
        if (!containsShiftableQuotes && !containsEscapedQuotes) {
            ShiftableSelection.sortLinesInDocument(document, !isUp, lineNumberSelStart, lineNumberSelEnd);
            return;
        }

        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_LINES_SORT);
        addQuoteShiftingOptions(shiftOptions);

        shiftSelectionByPopupInDocument(shiftOptions, isUp,null, null, null);
    }

    public void shiftCamelCase(boolean isTwoWords) {
        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_CAMEL_CASE_TO_PATH);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_CAMEL_CASE_TO_UNDERSCORE_SEPARATED);

        if (isTwoWords) {
            shiftOptions.add(StaticTexts.SHIFT_OPTION_CAMEL_WORDS_SWAP_ORDER);
        }

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
                                        shiftSelectionByModeInDocument(
                                                modes.getSelectedValue().toString(),
                                                isUp,
                                                phpConcatenation, delimiterSplitPattern, delimiterGlue);
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
            assert phpConcatenation != null;
            document.replaceString(offsetStart, offsetEnd, phpConcatenation.getShifted());
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_UNESCAPE_QUOTES)) {
            String text = selectedText.replace("\\\'", "'").replace("\\\"", "\"");
            document.replaceString(offsetStart, offsetEnd, text);
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_CAMEL_WORDS_SWAP_ORDER)) {
            document.replaceString(offsetStart, offsetEnd, CamelCaseString.flipWordPairOrder(selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_LIST_ITEMS_SORT) || mode.equals(StaticTexts.SHIFT_OPTION_LIST_ITEMS_SWAP)) {
            document.replaceString(offsetStart, offsetEnd, SeparatedList.sortSeparatedList(selectedText, delimiterSplitPattern, delimiterGlue, isUp));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_PATH_PAIR_SWAP_ORDER)) {
            document.replaceString(offsetStart, offsetEnd, MinusSeparatedPath.flipWordsOrder(selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_LINES_SORT)) {
            ShiftableSelection.sortLinesInDocument(document, !isUp, lineNumberSelStart, lineNumberSelEnd);
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_QUOTES_SWAP)) {
            document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapQuotes(selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_SLASHES_SWAP)) {
            document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapSlashes(selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_QUOTES_SINGLE_TO_DOUBLE)) {
            document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapQuotes(selectedText, true , false));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_QUOTES_DOUBLE_TO_SINGLE)) {
            document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapQuotes(selectedText, false, true));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_CAMEL_CASE_TO_PATH)) {
            document.replaceString(offsetStart, offsetEnd, CamelCaseString.getShifted(selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_CAMEL_CASE_TO_UNDERSCORE_SEPARATED)) {
            document.replaceString(offsetStart, offsetEnd, CamelCaseString.getShifted(selectedText, CamelCaseString.ShiftMode.CAMEL_WORDS_TO_UNDERSCORE_SEPARATED));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_PATH_TO_CAMEL_CASE)) {
            document.replaceString(offsetStart, offsetEnd, MinusSeparatedPath.getShifted(selectedText));
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_CONVERT_PHP_ARRAY_TO_LONG_SYNTAX)) {
            PhpVariableOrArray phpVariableOrArray = new PhpVariableOrArray();
            phpVariableOrArray.init(selectedText);
            document.replaceString(offsetStart, offsetEnd, phpVariableOrArray.getShiftedArray(selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_OPTION_SWAP_PARENTHESIS)) {
            document.replaceString(offsetStart, offsetEnd, Parenthesis.getShifted(selectedText));
        }
    }
}