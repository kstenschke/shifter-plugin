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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.ui.components.JBList;
import com.kstenschke.shifter.models.shiftertypes.*;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.utils.UtilsEnvironment;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

// Shiftable (non-block) selection
public class ShiftableSelection {

    /**
     * @param editor
     * @param caretOffset
     * @param isUp          Are we shifting up or down?
     * @param moreCount     Current "more" count, starting w/ 1. If non-more shift: null
     */
    public static void shiftSelectionInDocument(Editor editor, Integer caretOffset, boolean isUp, @Nullable Integer moreCount) {
        Document document = editor.getDocument();
        String filename   = UtilsEnvironment.getDocumentFilename(document);
        Project project   = editor.getProject();

        SelectionModel selectionModel = editor.getSelectionModel();
        int offsetStart = selectionModel.getSelectionStart();
        int offsetEnd   = selectionModel.getSelectionEnd();

        CharSequence editorText = document.getCharsSequence();
        String selectedText = UtilsTextual.getSubString(editorText, offsetStart, offsetEnd);

        if (selectedText == null || selectedText.trim().isEmpty()) {
            return;
        }
        if (PhpDocComment.isPhpDocComment(selectedText) && PhpDocComment.containsAtParam(selectedText)) {
            String shifted = PhpDocComment.getShifted(selectedText);
            if (!shifted.equals(selectedText)) {
                // PHP doc comment block: guess missing data types by resp. variable names
                document.replaceString(offsetStart, offsetEnd, shifted);
                UtilsEnvironment.reformatSubString(editor, editor.getProject(), offsetStart, offsetEnd);
                return;
            }
        }
        // Shift selected comment: Must be before multi-line sort to allow multi-line comment shifting
        if (Comment.isComment(selectedText) && shiftSelectedCommentInDocument(editor, document, filename, project, offsetStart, offsetEnd, selectedText)) {
            return;
        }

        int lineNumberSelStart = document.getLineNumber(offsetStart);
        int lineNumberSelEnd = document.getLineNumber(offsetEnd);

        if (document.getLineStartOffset(lineNumberSelEnd) == offsetEnd) {
            lineNumberSelEnd--;
        }

        ShiftingTypesManager shiftingTypesManager = new ShiftingTypesManager();
        int wordType = shiftingTypesManager.getWordType(selectedText, editorText, offsetStart, filename);

        boolean isPhpVariable        = wordType == ShiftingTypesManager.TYPE_PHP_VARIABLE;
        boolean isJsVarsDeclarations = !isPhpVariable && wordType == ShiftingTypesManager.TYPE_JS_VARIABLES_DECLARATIONS;
        boolean containsQuotes       = UtilsTextual.containsQuotes(selectedText);

        if (!isJsVarsDeclarations && ((lineNumberSelEnd - lineNumberSelStart) > 0 && !isPhpVariable)) {
            // Selection is multi-lined: sort lines or swap quotes
            sortLinesOrSwapQuotesInDocument(containsQuotes, offsetStart, offsetEnd, selectedText, project, document, lineNumberSelStart, lineNumberSelEnd, isUp);
            return;
        }
        if (isJsVarsDeclarations) {
            document.replaceString(offsetStart, offsetEnd, JsVariablesDeclarations.getShifted(selectedText));
            return;
        }
        if (!isPhpVariable && wordType == ShiftingTypesManager.TYPE_SIZZLE_SELECTOR) {
            document.replaceString(offsetStart, offsetEnd, SizzleSelector.getShifted(selectedText));
            return;
        }
        if (wordType == ShiftingTypesManager.TYPE_TRAILING_COMMENT) {
            int offsetStartCaretLine = document.getLineStartOffset(lineNumberSelStart);
            int offsetEndCaretLine   = document.getLineEndOffset(lineNumberSelStart);
            String leadWhitespace    = UtilsTextual.getLeadWhitespace(editorText.subSequence(offsetStartCaretLine, offsetEndCaretLine).toString());
            String caretLine         = editorText.subSequence(offsetStartCaretLine, offsetEndCaretLine).toString();

            document.replaceString(offsetStartCaretLine, offsetEndCaretLine, TrailingComment.getShifted(caretLine, leadWhitespace));
            return;
        }

        if (!isPhpVariable && UtilsFile.isPhpFile(filename) && shiftSelectionInPhpDocument(document, filename, project, offsetStart, offsetEnd, selectedText, containsQuotes)) {
            return;
        }
        if (TernaryExpression.isTernaryExpression(selectedText, "")) {
            document.replaceString(offsetStart, offsetEnd, TernaryExpression.getShifted(selectedText));
            return;
        }

        if (!isPhpVariable) {
            if (SeparatedList.isSeparatedList(selectedText,",")) {
                // Comma-separated list
                sortListOrSwapQuotesInDocument(containsQuotes, offsetStart, offsetEnd, selectedText, project, document, ",(\\s)*", ", ", isUp);
                return;
            }
            if (SeparatedList.isSeparatedList(selectedText,"|")) {
                // Pipe-separated list
                sortListOrSwapQuotesInDocument(containsQuotes, offsetStart, offsetEnd, selectedText, project, document, "\\|(\\s)*", "|", isUp);
                return;
            }
            if (containsQuotes) {
                document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapQuotes(selectedText));
                return;
            }

            if (StringCamelCase.isCamelCase(selectedText) && StringCamelCase.isWordPair(selectedText)) {
                document.replaceString(offsetStart, offsetEnd, StringCamelCase.flipWordPairOrder(selectedText));
                return;
            }

            Tupel wordsTupel = new Tupel();
            if (wordsTupel.isWordsTupel(selectedText)) {
                document.replaceString(offsetStart, offsetEnd, wordsTupel.getShifted(selectedText));
                return;
            }
            if (UtilsTextual.containsSlashes(selectedText)) {
                document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapSlashes(selectedText));
                return;
            }
            if (LogicalOperator.isLogicalOperator(selectedText)) {
                document.replaceString(offsetStart, offsetEnd, LogicalOperator.getShifted(selectedText));
                return;
            }
            if (StringHtmlEncodable.isHtmlEncodable(selectedText)) {
                document.replaceString(offsetStart, offsetEnd, StringHtmlEncodable.getShifted(selectedText));
                return;
            }
        }

        String shiftedWord = shiftingTypesManager.getShiftedWord(selectedText, isUp, editorText, caretOffset, moreCount, filename, editor);
        if (isPhpVariable) {
            document.replaceString(offsetStart, offsetEnd, shiftedWord);
            return;
        }
        if (UtilsTextual.isAllUppercase(selectedText)) {
            document.replaceString(offsetStart, offsetEnd, shiftedWord.toUpperCase());
            return;
        }
        if (UtilsTextual.isUpperCamelCase(selectedText) || UtilsTextual.isUcFirst(selectedText)) {
            document.replaceString(offsetStart, offsetEnd, UtilsTextual.toUcFirst(shiftedWord));
            return;
        }

        document.replaceString(offsetStart, offsetEnd, shiftedWord);
    }

    private static boolean shiftSelectionInPhpDocument(Document document, String filename, Project project, int offsetStart, int offsetEnd, String selectedText, boolean containsQuotes) {
        PhpConcatenation phpConcatenation = new PhpConcatenation(selectedText);
        if (phpConcatenation.isPhpConcatenation()) {
            shiftPhpConcatenationOrSwapQuotesInDocument(containsQuotes, project, document, offsetStart, offsetEnd, selectedText, phpConcatenation);
            return true;
        }
        if (Comment.isHtmlComment(selectedText)) {
            document.replaceString(offsetStart, offsetEnd, Comment.getPhpBlockCommentFromHtmlComment(selectedText));
            return true;
        }
        if (Comment.isPhpBlockComment(selectedText)) {
            document.replaceString(offsetStart, offsetEnd, Comment.getShifted(selectedText, filename, project));
            return true;
        }
        return false;
    }

    private static boolean shiftSelectedCommentInDocument(Editor editor, Document document, String filename, Project project, int offsetStart, int offsetEnd, String selectedText) {
        if (UtilsTextual.isMultiLine(selectedText)) {
            if (filename.endsWith("js") && JsDoc.isJsDocBlock(selectedText) && JsDoc.correctDocBlockInDocument(editor, document, offsetStart, offsetEnd)) {
                boolean x = true;
                return true;
            }

            if (Comment.isBlockComment(selectedText)) {
                Comment.shiftMultiLineBlockCommentInDocument(selectedText, project, document, offsetStart, offsetEnd);
                return true;
            }
            if (Comment.isMultipleSingleLineComments(selectedText)) {
                Comment.shiftMultipleSingleLineCommentsInDocument(selectedText, project, document, offsetStart, offsetEnd);
                return true;
            }
        }

        document.replaceString(offsetStart, offsetEnd, Comment.getShifted(selectedText, filename, project));
        return true;
    }

    public static void shiftPhpConcatenationOrSwapQuotesInDocument(boolean containsQuotes, final Project project, final Document document, final int offsetStart, final int offsetEnd, final String selectedText, final PhpConcatenation phpConcatenation) {
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

    public static void sortListOrSwapQuotesInDocument(
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

    public static void sortLinesOrSwapQuotesInDocument(
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

    /**
     * Sort lines in document alphabetically ascending / descending
     *
     * @param shiftUp
     * @param lineNumberSelStart
     * @param lineNumberSelEnd
     */
    private static void sortLinesInDocument(Document document, boolean shiftUp, int lineNumberSelStart, int lineNumberSelEnd) {
        List<String> lines       = UtilsTextual.extractLines(document, lineNumberSelStart, lineNumberSelEnd);
        List<String> linesSorted = UtilsTextual.sortLines(lines, shiftUp);

        String linesString = UtilsTextual.joinLines(linesSorted).toString();

        if (UtilsTextual.hasDuplicateLines(linesString) && JOptionPane.showConfirmDialog(
                    null,
                    StaticTexts.MESSAGE_REDUCE_DUPLICATE_LINES,
                StaticTexts.TITLE_REDUCE_DUPLICATE_LINES,
                    JOptionPane.OK_CANCEL_OPTION
            ) == JOptionPane.OK_OPTION)
        {
            linesString = UtilsTextual.reduceDuplicateLines(linesString);
        }

        int offsetLineStart = document.getLineStartOffset(lineNumberSelStart);
        int offsetLineEnd   = document.getLineEndOffset(lineNumberSelEnd) + document.getLineSeparatorLength(lineNumberSelEnd);

        document.replaceString(offsetLineStart, offsetLineEnd, linesString);
    }
}