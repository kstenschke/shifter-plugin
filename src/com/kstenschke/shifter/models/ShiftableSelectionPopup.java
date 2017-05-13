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
import com.kstenschke.shifter.models.shiftertypes.PhpConcatenation;
import com.kstenschke.shifter.models.shiftertypes.SeparatedList;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.utils.UtilsTextual;

import java.util.ArrayList;
import java.util.List;

class ShiftableSelectionPopup extends ShiftableSelection {

    /**
     * @param containsQuotes
     * @param project
     * @param document
     * @param offsetStart
     * @param offsetEnd
     * @param selectedText
     * @param phpConcatenation
     */
    static void shiftPhpConcatenationOrSwapQuotesInDocument(boolean containsQuotes, final Project project, final Document document, final int offsetStart, final int offsetEnd, final String selectedText, final PhpConcatenation phpConcatenation) {
        if (!containsQuotes) {
            document.replaceString(offsetStart, offsetEnd, phpConcatenation.getShifted());
            return;
        }

        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_CONCATENATION_ITEMS_SWAP_ORDER);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_QUOTES_SWAP);

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
                                        final int index = modes.getSelectedIndex();
                                        String shifted;

                                        switch (index) {
                                            case 0:
                                                shifted = phpConcatenation.getShifted();
                                                break;
                                            case 1:
                                            default:
                                                shifted = UtilsTextual.swapQuotes(selectedText);
                                                break;
                                        }
                                        document.replaceString(offsetStart, offsetEnd, shifted);
                                    }
                                },
                                null, null);
                    }
                });
            }
        }).setMovable(true).createPopup().showCenteredInCurrentWindow(project);
    }

    /**
     * @param containsQuotes
     * @param offsetStart
     * @param offsetEnd
     * @param selectedText
     * @param project
     * @param document
     * @param delimiterSplitPattern
     * @param delimiterGlue
     * @param isUp
     */
    static void sortListOrSwapQuotesInDocument(
            boolean containsQuotes,
            final int offsetStart, final int offsetEnd, final String selectedText,
            final Project project, final Document document,
            final String delimiterSplitPattern, final String delimiterGlue,
            final boolean isUp) {
        if (!containsQuotes) {
            document.replaceString(offsetStart, offsetEnd, SeparatedList.sortSeparatedList(selectedText, delimiterSplitPattern, delimiterGlue, isUp));
            return;
        }

        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_LIST_ITEMS_SORT);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_QUOTES_SWAP);

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
                                        final int index = modes.getSelectedIndex();
                                        String shifted;

                                        switch (index) {
                                            case 0:
                                                shifted = SeparatedList.sortSeparatedList(selectedText, delimiterSplitPattern, delimiterGlue, isUp);
                                                break;
                                            case 1:
                                            default:
                                                shifted = UtilsTextual.swapQuotes(selectedText);

                                        }
                                        document.replaceString(offsetStart, offsetEnd, shifted);
                                    }
                                },
                                null, null);
                    }
                });
            }
        }).setMovable(true).createPopup().showCenteredInCurrentWindow(project);
    }

    /**
     * @param containsQuotes
     * @param offsetStart
     * @param offsetEnd
     * @param selectedText
     * @param project
     * @param document
     * @param lineNumberSelStart
     * @param lineNumberSelEnd
     * @param isUp
     */
    static void sortLinesOrSwapQuotesInDocument(
            boolean containsQuotes,
            final int offsetStart, final int offsetEnd, final String selectedText,
            final Project project, final Document document,
            final int lineNumberSelStart, final int lineNumberSelEnd,
            final boolean isUp) {
        if (!containsQuotes) {
            ShiftableSelection.sortLinesInDocument(document, isUp, lineNumberSelStart, lineNumberSelEnd);
            return;
        }

        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_LINES_SORT);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_QUOTES_SWAP);

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
                                        final int index = modes.getSelectedIndex();
                                        String shifted;

                                        switch (index) {
                                            case 0:
                                                ShiftableSelection.sortLinesInDocument(document, isUp, lineNumberSelStart, lineNumberSelEnd);
                                                return;
                                            case 1:
                                            default:
                                                shifted = UtilsTextual.swapQuotes(selectedText);

                                        }
                                        document.replaceString(offsetStart, offsetEnd, shifted);
                                    }
                                },
                                null, null);
                    }
                });
            }
        }).setMovable(true).createPopup().showCenteredInCurrentWindow(project);
    }
}