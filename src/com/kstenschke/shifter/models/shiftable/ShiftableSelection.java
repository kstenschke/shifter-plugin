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
package com.kstenschke.shifter.models.shiftable;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.kstenschke.shifter.models.shiftable.types.*;
import com.kstenschke.shifter.resources.StaticTexts;
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

        ShiftableTypesManager shiftingShiftableTypesManager = new ShiftableTypesManager();
        int wordType = shiftingShiftableTypesManager.getWordType(selectedText, editorText, offsetStart, filename);

        boolean isPhpVariable        = wordType == ShiftableTypesManager.TYPE_PHP_VARIABLE;
        boolean isJsVarsDeclarations = !isPhpVariable && wordType == ShiftableTypesManager.TYPE_JS_VARIABLES_DECLARATIONS;
        boolean containsQuotes       = UtilsTextual.containsQuotes(selectedText);

        if (!isJsVarsDeclarations && ((lineNumberSelEnd - lineNumberSelStart) > 0 && !isPhpVariable)) {
            // Selection is multi-lined: sort lines or swap quotes
            new ShiftableSelectionWithPopup(project, document, offsetStart, offsetEnd).sortLinesOrSwapQuotesInDocument(isUp);
            return;
        }
        if (isJsVarsDeclarations) {
            document.replaceString(offsetStart, offsetEnd, JsVariablesDeclarations.getShifted(selectedText));
            return;
        }
        if (!isPhpVariable && wordType == ShiftableTypesManager.TYPE_SIZZLE_SELECTOR) {
            document.replaceString(offsetStart, offsetEnd, SizzleSelector.getShifted(selectedText));
            return;
        }
        if (wordType == ShiftableTypesManager.TYPE_TRAILING_COMMENT) {
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
                new ShiftableSelectionWithPopup(project, document, offsetStart, offsetEnd).sortListOrSwapQuotesInDocument(",(\\s)*", ", ", isUp);
                return;
            }
            if (SeparatedList.isSeparatedList(selectedText,"|")) {
                // Pipe-separated list
                new ShiftableSelectionWithPopup(project, document, offsetStart, offsetEnd).sortListOrSwapQuotesInDocument("\\|(\\s)*", "|", isUp);
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

        String shiftedWord = shiftingShiftableTypesManager.getShiftedWord(selectedText, isUp, editorText, caretOffset, moreCount, filename, editor);
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
            new ShiftableSelectionWithPopup(project, document, offsetStart, offsetEnd).shiftPhpConcatenationOrSwapQuotesInDocument(phpConcatenation);
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

    /**
     * Sort lines in document alphabetically ascending / descending
     *
     * @param shiftUp
     * @param lineNumberSelStart
     * @param lineNumberSelEnd
     */
    protected static void sortLinesInDocument(Document document, boolean shiftUp, int lineNumberSelStart, int lineNumberSelEnd) {
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