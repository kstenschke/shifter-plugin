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

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.kstenschke.shifter.models.shiftertypes.*;
import com.kstenschke.shifter.utils.UtilsEnvironment;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
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

        SelectionModel selectionModel = editor.getSelectionModel();
        int offsetStart = selectionModel.getSelectionStart();
        int offsetEnd   = selectionModel.getSelectionEnd();

        CharSequence editorText = document.getCharsSequence();
        String selectedText = UtilsTextual.getSubString(editorText, offsetStart, offsetEnd);

        if (selectedText == null || selectedText.trim().isEmpty()) {
            return;
        }

        int lineNumberSelStart = document.getLineNumber(offsetStart);
        int lineNumberSelEnd = document.getLineNumber(offsetEnd);

        if (document.getLineStartOffset(lineNumberSelEnd) == offsetEnd) {
            lineNumberSelEnd--;
        }

        ShiftingTypesManager shiftingTypesManager = new ShiftingTypesManager();

        String filename   = UtilsEnvironment.getDocumentFilename(document);
        int wordType = shiftingTypesManager.getWordType(selectedText, editorText, offsetStart, filename);

        boolean isPhpVariable        = wordType == ShiftingTypesManager.TYPE_PHP_VARIABLE;
        boolean isJsVarsDeclarations = !isPhpVariable && wordType == ShiftingTypesManager.TYPE_JS_VARIABLES_DECLARATIONS;
        boolean isSizzleSelector     = !isPhpVariable && !isJsVarsDeclarations && wordType == ShiftingTypesManager.TYPE_SIZZLE_SELECTOR;

        // @todo refactor logical branches (cleanup flow, extract sub sections into private methods)
        if (!isJsVarsDeclarations && ((lineNumberSelEnd - lineNumberSelStart) > 1 && !isPhpVariable)) {
            // Selection is multi-lined: sort lines alphabetical
            ShiftableSelection.sortLinesInDocument(document, isUp, lineNumberSelStart, lineNumberSelEnd);
        } else {
            if (isJsVarsDeclarations) {
                document.replaceString(offsetStart, offsetEnd, JsVariablesDeclarations.getShifted(selectedText));
            } else {
                if (isSizzleSelector) {
                    document.replaceString(offsetStart, offsetEnd, SizzleSelector.getShifted(selectedText));
                } else {
                    // Selection within one line, or PHP array definition over 1 or more lines
                    if (!isPhpVariable && UtilsTextual.isCommaSeparatedList(selectedText)) {
                        String sortedList = UtilsTextual.sortCommaSeparatedList(selectedText, isUp);
                        document.replaceString(offsetStart, offsetEnd, sortedList);
                    } else {
                        boolean isDone = false;
                        if (!isPhpVariable && UtilsFile.isPhpFile(filename)) {
                            PhpConcatenation phpConcatenation = new PhpConcatenation(selectedText);
                            if (phpConcatenation.isPhpConcatenation()) {
                                isDone = true;
                                document.replaceString(offsetStart, offsetEnd, phpConcatenation.getShifted());
                            }
                        }

                        if (!isDone) {
                            if (TernaryExpression.isTernaryExpression(selectedText, "")) {
                                document.replaceString(offsetStart, offsetEnd, TernaryExpression.getShifted(selectedText));
                                isDone = true;
                            } else if (wordType == ShiftingTypesManager.TYPE_TRAILING_COMMENT) {
                                int offsetStartCaretLine = document.getLineStartOffset(lineNumberSelStart);
                                int offsetEndCaretLine   = document.getLineEndOffset(lineNumberSelStart);
                                String leadingWhitespace = UtilsTextual.getLeadingWhitespace(editorText.subSequence(offsetStartCaretLine, offsetEndCaretLine).toString());
                                String caretLine         = editorText.subSequence(offsetStartCaretLine, offsetEndCaretLine).toString();

                                document.replaceString(offsetStartCaretLine, offsetEndCaretLine, TrailingComment.getShifted(caretLine, leadingWhitespace));
                                isDone = true;
                            } else if (!isPhpVariable) {
                                if (UtilsTextual.containsAnyQuotes(selectedText)) {
                                    document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapQuotes(selectedText));
                                    isDone = true;
                                } else if (UtilsTextual.containsAnySlashes(selectedText)) {
                                    document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapSlashes(selectedText));
                                    isDone = true;
                                } else if (LogicalOperator.isLogicalOperator(selectedText)) {
                                    document.replaceString(offsetStart, offsetEnd, LogicalOperator.getShifted(selectedText));
                                    isDone = true;
                                } else {
                                    Tupel wordsTupel = new Tupel();
                                    if (wordsTupel.isWordsTupel(selectedText)) {
                                        document.replaceString(offsetStart, offsetEnd, wordsTupel.getShifted(selectedText));
                                        isDone = true;
                                    } else if (StringHtmlEncodable.isHtmlEncodable(selectedText)) {
                                        document.replaceString(offsetStart, offsetEnd, StringHtmlEncodable.getShifted(selectedText));
                                        isDone = true;
                                    }
                                }
                            }

                            if (!isDone) {
                                // Detect and shift various types
                                String shiftedWord = shiftingTypesManager.getShiftedWord(selectedText, isUp, editorText, caretOffset, moreCount, filename, editor);

                                if (!isPhpVariable) {
                                    // @todo extract this and its redundancy in ShiftableWord
                                    if (UtilsTextual.isAllUppercase(selectedText)) {
                                        shiftedWord = shiftedWord.toUpperCase();

                                    } else if (UtilsTextual.isCamelCase(selectedText) || UtilsTextual.isUcFirst(selectedText)) {
                                        // @todo    check is there a way to implement a toCamelCase conversion?
                                        shiftedWord = UtilsTextual.toUcFirst(shiftedWord);
                                    }
                                }

                                document.replaceString(offsetStart, offsetEnd, shiftedWord);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Sort lines in document alphabetically ascending / descending
     *
     * @param shiftUp
     * @param lineNumberSelStart
     * @param lineNumberSelEnd
     */
    private static void sortLinesInDocument(Document document, boolean shiftUp, int lineNumberSelStart, int lineNumberSelEnd) {
        List<String> lines = UtilsTextual.extractLines(document, lineNumberSelStart, lineNumberSelEnd);
        List<String> linesSorted = UtilsTextual.sortLines(lines, shiftUp);

        String linesString = UtilsTextual.joinLines(linesSorted).toString();

        if (UtilsTextual.hasDuplicateLines(linesString) && JOptionPane.showConfirmDialog(
                    null,
                    "Duplicated lines detected. Reduce to single occurrences?",
                    "Reduce duplicate lines?",
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
