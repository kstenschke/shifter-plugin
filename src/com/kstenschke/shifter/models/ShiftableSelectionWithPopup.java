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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.ui.components.JBList;
import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.models.entities.AbstractShiftable;
import com.kstenschke.shifter.models.entities.shiftables.*;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShiftableSelectionWithPopup extends ShiftableSelection {

    private final ActionContainer actionContainer;

    private final boolean containsShiftableQuotes;
    private final boolean containsEscapedQuotes;

    // Constructor
    public ShiftableSelectionWithPopup(ActionContainer actionContainer) {
        this.actionContainer = actionContainer;

        containsShiftableQuotes = QuotedString.containsShiftableQuotes(actionContainer.selectedText);
        containsEscapedQuotes   = QuotedString.containsEscapedQuotes(actionContainer.selectedText);
    }

    private void addQuoteShiftingOptions(List<String> shiftOptions) {
        boolean containsSingleQuotes = actionContainer.selectedText.contains("'");
        boolean containsDoubleQuotes = actionContainer.selectedText.contains("\"");

        if (containsSingleQuotes && containsDoubleQuotes) shiftOptions.add(StaticTexts.SHIFT_QUOTES_SWAP);

        if (containsDoubleQuotes && ShifterPreferences.getIsActiveConvertDoubleQuotes())
            shiftOptions.add(StaticTexts.SHIFT_QUOTES_DOUBLE_TO_SINGLE);

        if (containsSingleQuotes && ShifterPreferences.getIsActiveConvertSingleQuotes())
            shiftOptions.add(StaticTexts.SHIFT_QUOTES_SINGLE_TO_DOUBLE);

        if (containsEscapedQuotes) shiftOptions.add(StaticTexts.SHIFT_UNESCAPE_QUOTES);
    }

    public void shiftPhpConcatenationOrSwapQuotesInDocument(final ConcatenationPhp concatenationPhp) {
        if (!containsShiftableQuotes) {
            actionContainer.document.replaceString(
                    actionContainer.offsetSelectionStart,
                    actionContainer.offsetSelectionEnd,
                    concatenationPhp.getShifted(null));
            return;
        }

        List<String> shiftOptions = new ArrayList<>();
        shiftOptions.add(StaticTexts.SHIFT_CONCATENATION_ITEMS_SWAP_ORDER);
        addQuoteShiftingOptions(shiftOptions);

        shiftSelectionByPopupInDocument(shiftOptions, actionContainer.isShiftUp, concatenationPhp, null, null);
    }

    public void sortListOrSwapQuotesOrInterpolateTypeScriptInDocument(
            final String delimiterSplitPattern,
            final String delimiterGlue,
            final boolean isJsConcatenation,
            final boolean isUp
    ) {
        if (!containsShiftableQuotes && !isJsConcatenation) {
            // Sort
            actionContainer.delimiterSplitPattern = delimiterSplitPattern;
            actionContainer.delimiterGlue = delimiterGlue;
            actionContainer.isShiftUp = isUp;
            AbstractShiftable shiftableTypeAbstract = new SeparatedList(actionContainer);

            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceSelection(
                            shiftableTypeAbstract.getShifted(
                                    actionContainer.selectedText,
                                    null,
                                    null,
                                    false,
                                    false)),
                    ACTION_TEXT_SHIFT_SELECTION);
            return;
        }

        if (isJsConcatenation) {
            actionContainer.delimiter = ",";
        }

        List<String> shiftOptions = new ArrayList<>();
        if (isJsConcatenation) shiftOptions.add(StaticTexts.SHIFT_CONVERT_TO_TYPESCRIPT_STRING_INTERPOLATION);

        String items[] = actionContainer.selectedText.split(delimiterSplitPattern);
        shiftOptions.add(items.length == 2 ? StaticTexts.SHIFT_LIST_ITEMS_SWAP : StaticTexts.SHIFT_LIST_ITEMS_SORT);

        addQuoteShiftingOptions(shiftOptions);

        shiftSelectionByPopupInDocument(shiftOptions, isUp,null, delimiterSplitPattern, delimiterGlue);
    }

    public void interpolateConcatenationOrSwapQuotesInDocument(final boolean isUp) {
        actionContainer.delimiter = ",";

        String delimiterSplitPattern = "\\|(\\s)*";
        List<String> shiftOptions = new ArrayList<>();

        String items[] = actionContainer.selectedText.split(delimiterSplitPattern);
        shiftOptions.add(StaticTexts.SHIFT_CONVERT_TO_TYPESCRIPT_STRING_INTERPOLATION);

        if (items.length == 2) shiftOptions.add(StaticTexts.SHIFT_LIST_ITEMS_SWAP);

        addQuoteShiftingOptions(shiftOptions);

        shiftSelectionByPopupInDocument(shiftOptions, isUp,null, delimiterSplitPattern, "|");
    }

    public void swapParenthesisOrConvertPphpArray() {
        List<String> shiftOptions = new ArrayList<>();
        shiftOptions.add(StaticTexts.SHIFT_SWAP_PARENTHESIS);
        shiftOptions.add(StaticTexts.SHIFT_CONVERT_PHP_ARRAY_TO_LONG_SYNTAX);

        shiftSelectionByPopupInDocument(shiftOptions, false,null, null, null);
    }

    public void shiftQuotesInDocument() {
        List<String> shiftOptions = new ArrayList<>();
        addQuoteShiftingOptions(shiftOptions);

        shiftSelectionByPopupInDocument(shiftOptions, false,null, null, null);
    }

    public void swapSlashesOrUnescapeQuotes() {
        List<String> shiftOptions = new ArrayList<>();
        shiftOptions.add(StaticTexts.SHIFT_SLASHES_SWAP);
        addQuoteShiftingOptions(shiftOptions);

        shiftSelectionByPopupInDocument(shiftOptions, false,null, null, null);
    }

    public void sortLinesOrSwapQuotesInDocument() {
        if (!containsShiftableQuotes && !containsEscapedQuotes) {
            ShiftableSelection.sortLinesInDocument(actionContainer, !actionContainer.isShiftUp);
            return;
        }

        List<String> shiftOptions = new ArrayList<>();
        shiftOptions.add(StaticTexts.SHIFT_LINES_SORT);
        addQuoteShiftingOptions(shiftOptions);

        shiftSelectionByPopupInDocument(shiftOptions, actionContainer.isShiftUp,null, null, null);
    }

    public void shiftCamelCase(boolean isTwoWords) {
        List<String> shiftOptions = new ArrayList<>();
        shiftOptions.add(StaticTexts.SHIFT_CAMEL_CASE_TO_PATH);
        shiftOptions.add(StaticTexts.SHIFT_CAMEL_CASE_TO_UNDERSCORE_SEPARATED);

        if (isTwoWords) shiftOptions.add(StaticTexts.SHIFT_CAMEL_WORDS_SWAP_ORDER);

        shiftSelectionByPopupInDocument(shiftOptions, false,null, null, null);
    }

    public void shiftDictionaryTermOrToggleTupelOrder() {
        List<String> shiftOptions = new ArrayList<>();
        shiftOptions.add(StaticTexts.SHIFT_SHIFT_DICTIONARY_TERM);
        shiftOptions.add(StaticTexts.SHIFT_SWAP_TUPEL_WORDS_ORDER);

        shiftSelectionByPopupInDocument(shiftOptions, false,null, null, null);
    }

    public void shiftSeparatedPathOrSwapWords() {
        List<String> shiftOptions = new ArrayList<>();
        shiftOptions.add(StaticTexts.SHIFT_PATH_TO_CAMEL_CASE);
        shiftOptions.add(StaticTexts.SHIFT_PATH_PAIR_SWAP_ORDER);

        shiftSelectionByPopupInDocument(shiftOptions, false,null, null, null);
    }

    private void shiftSelectionByPopupInDocument(
            List<String> shiftOptions,
            final boolean isUp,
            @Nullable final ConcatenationPhp concatenationPhp,
            @Nullable final String delimiterSplitPattern,
            @Nullable final String delimiterGlue
    ) {
        final Object[] options = shiftOptions.toArray(new String[0]);
        final JBList modes = new JBList(options);

        PopupChooserBuilder popup = new PopupChooserBuilder(modes);
        popup.setTitle(StaticTexts.POPUP_TITLE_SHIFT).setItemChoosenCallback(() -> ApplicationManager.getApplication().runWriteAction(() -> {
            // Callback when item chosen
            CommandProcessor.getInstance().executeCommand(actionContainer.project, () -> shiftSelectionByModeInDocument(
                    modes.getSelectedValue().toString(),
                    isUp,
                    concatenationPhp, delimiterSplitPattern, delimiterGlue),
                    null, null);
        })).setMovable(true).createPopup().showCenteredInCurrentWindow(actionContainer.project);
    }

    private void shiftSelectionByModeInDocument(
            String mode,
            boolean isUp,
            @Nullable ConcatenationPhp concatenationPhp,
            @Nullable String delimiterSplitPattern,
            @Nullable String delimiterGlue
    ) {
        AbstractShiftable shiftableType;

        if (mode.equals(StaticTexts.SHIFT_CONCATENATION_ITEMS_SWAP_ORDER)) {
            assert null != concatenationPhp;
            actionContainer.document.replaceString(
                    actionContainer.offsetSelectionStart,
                    actionContainer.offsetSelectionEnd,
                    concatenationPhp.getShifted(null));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_CONVERT_TO_TYPESCRIPT_STRING_INTERPOLATION)) {
            actionContainer.document.replaceString(
                    actionContainer.offsetSelectionStart,
                    actionContainer.offsetSelectionEnd,
                    new ConcatenationJs(actionContainer).getShifted(actionContainer.selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_UNESCAPE_QUOTES)) {
            String text = actionContainer.selectedText
                    .replace("\\\'", "'")
                    .replace("\\\"", "\"");
            actionContainer.document.replaceString(
                    actionContainer.offsetSelectionStart,
                    actionContainer.offsetSelectionEnd,
                    text);
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_CAMEL_WORDS_SWAP_ORDER)) {
            actionContainer.document.replaceString(
                    actionContainer.offsetSelectionStart,
                    actionContainer.offsetSelectionEnd,
                    CamelCaseString.flipWordPairOrder(actionContainer.selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_SWAP_TUPEL_WORDS_ORDER)) {
            Tupel tupel = new Tupel(actionContainer).getInstance(false);
            actionContainer.disableIntentionPopup = true;
            actionContainer.document.replaceString(
                    actionContainer.offsetSelectionStart,
                    actionContainer.offsetSelectionEnd,
                    tupel.getShifted(actionContainer.selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_SHIFT_DICTIONARY_TERM)) {
            DictionaryWord dictionaryWord = new DictionaryWord(actionContainer);
            if (null != dictionaryWord.getInstance()) {
                actionContainer.document.replaceString(
                        actionContainer.offsetSelectionStart,
                        actionContainer.offsetSelectionEnd,
                        dictionaryWord.getShifted(actionContainer.selectedText));
                return;
            }
        }
        if (mode.equals(StaticTexts.SHIFT_LIST_ITEMS_SORT) ||
            mode.equals(StaticTexts.SHIFT_LIST_ITEMS_SWAP)
        ) {
            actionContainer.delimiterSplitPattern = delimiterSplitPattern;
            actionContainer.delimiterGlue = delimiterGlue;
            actionContainer.isShiftUp = isUp;
            shiftableType = new SeparatedList(actionContainer);

            actionContainer.document.replaceString(
                    actionContainer.offsetSelectionStart,
                    actionContainer.offsetSelectionEnd,
                    shiftableType.getShifted(
                            actionContainer.selectedText,
                            null,
                            null));
            return;
        }
        SeparatedPath separatedPath = new SeparatedPath(actionContainer);
        if (mode.equals(StaticTexts.SHIFT_PATH_PAIR_SWAP_ORDER)) {
            actionContainer.document.replaceString(
                    actionContainer.offsetSelectionStart,
                    actionContainer.offsetSelectionEnd,
                    separatedPath.flipWordsOrder(actionContainer.selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_LINES_SORT)) {
            ShiftableSelection.sortLinesInDocument(actionContainer, !isUp);
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_QUOTES_SWAP)) {
            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, UtilsTextual.swapQuotes(actionContainer.selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_SLASHES_SWAP)) {
            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, UtilsTextual.swapSlashes(actionContainer.selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_QUOTES_SINGLE_TO_DOUBLE)) {
            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, UtilsTextual.swapQuotes(actionContainer.selectedText, true , false));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_QUOTES_DOUBLE_TO_SINGLE)) {
            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, UtilsTextual.swapQuotes(actionContainer.selectedText, false, true));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_CAMEL_CASE_TO_PATH)) {
            CamelCaseString camelCaseString = new CamelCaseString(actionContainer);
            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, camelCaseString.getShifted(actionContainer.selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_CAMEL_CASE_TO_UNDERSCORE_SEPARATED)) {
            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, CamelCaseString.getShifted(actionContainer.selectedText, CamelCaseString.ShiftMode.CAMEL_WORDS_TO_UNDERSCORE_SEPARATED));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_PATH_TO_CAMEL_CASE)) {
            SeparatedPath separatedPath1 = new SeparatedPath(actionContainer);
            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, separatedPath1.getShifted(actionContainer.selectedText));
        }
        if (mode.equals(StaticTexts.SHIFT_CONVERT_PHP_ARRAY_TO_LONG_SYNTAX)) {
            PhpVariableOrArray phpVariableOrArray = new PhpVariableOrArray(actionContainer);
            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, phpVariableOrArray.getShiftedArray(actionContainer.selectedText));
            return;
        }
        if (mode.equals(StaticTexts.SHIFT_SWAP_PARENTHESIS)) {
            Parenthesis parenthesis = new Parenthesis(actionContainer);
            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, parenthesis.getShifted(actionContainer.selectedText));
        }
    }
}