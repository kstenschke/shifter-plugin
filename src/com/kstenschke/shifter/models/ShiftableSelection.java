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
import com.intellij.openapi.project.Project;
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
                return;
            }
        }
        if (Comment.isComment(selectedText)) {
            if (UtilsTextual.isMultiLine(selectedText)) {
                if (Comment.isBlockComment(selectedText)) {
                    Comment.shiftMultiLineBlockCommentInDocument(selectedText, project, document, offsetStart, offsetEnd);
                    return;
                }
            }

            // Must be before multi-line sort to allow multi-line comment shifting
            document.replaceString(offsetStart, offsetEnd, Comment.getShifted(selectedText, filename, project));
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

        if (!isJsVarsDeclarations && ((lineNumberSelEnd - lineNumberSelStart) > 1 && !isPhpVariable)) {
            // Selection is multi-lined: sort lines alphabetical
            ShiftableSelection.sortLinesInDocument(document, isUp, lineNumberSelStart, lineNumberSelEnd);
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
            String leadingWhitespace = UtilsTextual.getLeadingWhitespace(editorText.subSequence(offsetStartCaretLine, offsetEndCaretLine).toString());
            String caretLine         = editorText.subSequence(offsetStartCaretLine, offsetEndCaretLine).toString();

            document.replaceString(offsetStartCaretLine, offsetEndCaretLine, TrailingComment.getShifted(caretLine, leadingWhitespace));
            return;
        }
        if (!isPhpVariable && UtilsFile.isPhpFile(filename)) {
            PhpConcatenation phpConcatenation = new PhpConcatenation(selectedText);
            if (phpConcatenation.isPhpConcatenation()) {
                document.replaceString(offsetStart, offsetEnd, phpConcatenation.getShifted());
                return;
            }
            if (Comment.isHtmlComment(selectedText)) {
                document.replaceString(offsetStart, offsetEnd, Comment.getPhpBlockCommentFromHtmlComment(selectedText));
                return;
            }
            if (Comment.isPhpBlockComment(selectedText)) {
                document.replaceString(offsetStart, offsetEnd, Comment.getShifted(selectedText, filename, project));
                return;
            }
        }

        if (TernaryExpression.isTernaryExpression(selectedText, "")) {
            document.replaceString(offsetStart, offsetEnd, TernaryExpression.getShifted(selectedText));
            return;
        }

        if (!isPhpVariable) {
            if (SeparatedList.isSeparatedList(selectedText,",")) {
                // Comma-separated list
                String sortedList = SeparatedList.sortSeparatedList(selectedText, ",(\\s)*", ", ", isUp);
                document.replaceString(offsetStart, offsetEnd, sortedList);
                return;
            }
            if (SeparatedList.isSeparatedList(selectedText,"|")) {
                // Pipe-separated list
                String sortedList = SeparatedList.sortSeparatedList(selectedText, "\\|(\\s)*", "|", isUp);
                document.replaceString(offsetStart, offsetEnd, sortedList);
                return;
            }
            if (UtilsTextual.containsAnyQuotes(selectedText)) {
                document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapQuotes(selectedText));
                return;
            }
            Tupel wordsTupel = new Tupel();
            if (wordsTupel.isWordsTupel(selectedText)) {
                document.replaceString(offsetStart, offsetEnd, wordsTupel.getShifted(selectedText));
                return;
            }
            if (UtilsTextual.containsAnySlashes(selectedText)) {
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
        if (UtilsTextual.isCamelCase(selectedText) || UtilsTextual.isUcFirst(selectedText)) {
            document.replaceString(offsetStart, offsetEnd, UtilsTextual.toUcFirst(shiftedWord));
            return;
        }

        document.replaceString(offsetStart, offsetEnd, shiftedWord);
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