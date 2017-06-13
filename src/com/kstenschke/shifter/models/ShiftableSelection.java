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
import com.kstenschke.shifter.models.shiftableTypes.*;
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

        boolean isPhpFile = UtilsFile.isPhpFile(filename);
        if (isPhpFile && PhpDocParam.shiftSelectedPhpDocInDocument(editor, document, project, offsetStart, offsetEnd, selectedText)) {
            // Detect and shift whole PHP DOC block or single line out of it, that contains @param line(s) w/o data type
            return;
        }
        if (filename.endsWith(".js") && JsDoc.isJsDocBlock(selectedText) && JsDoc.correctDocBlockInDocument(editor, document, offsetStart, offsetStart)) {
            return;
        }

        // Shift selected comment: Must be before multi-line sort to allow multi-line comment shifting
        if (com.kstenschke.shifter.models.shiftableTypes.Comment.isComment(selectedText) && shiftSelectedCommentInDocument(editor, document, filename, project, offsetStart, offsetEnd, selectedText)) {
            return;
        }

        boolean isWrappedInParenthesis = Parenthesis.isWrappedInParenthesis(selectedText);

        ShiftableTypesManager shiftingShiftableTypesManager = new ShiftableTypesManager();
        int wordType = shiftingShiftableTypesManager.getWordType(selectedText, editorText, offsetStart, filename);
        boolean isPhpVariableOrArray = wordType == ShiftableTypesManager.TYPE_PHP_VARIABLE_OR_ARRAY;

        if (isWrappedInParenthesis) {
            boolean isShiftablePhpArray = isPhpVariableOrArray && PhpVariableOrArray.isStaticShiftablePhpArray(selectedText);
            if (!isPhpVariableOrArray || !isShiftablePhpArray) {
                // Swap surrounding "(" and ")" versus "[" and "]"
                document.replaceString(offsetStart, offsetEnd, Parenthesis.getShifted(selectedText));
                return;
            }
            // Swap parenthesis or convert PHP array
            new ShiftableSelectionWithPopup(project, document, offsetStart, offsetEnd).swapParenthesisOrConvertPphpArray();
            return;
        }

        boolean isJsVarsDeclarations    = !isPhpVariableOrArray && wordType == ShiftableTypesManager.TYPE_JS_VARIABLES_DECLARATIONS;
        boolean containsShiftableQuotes = QuotedString.containsShiftableQuotes(selectedText);
        boolean isMultiLine             = UtilsTextual.isMultiLine(selectedText);

        if (UtilsFile.isCssFile(filename) && isMultiLine) {
            // CSS: Sort attributes per selector alphabetically
            String shifted = Css.getShifted(selectedText);
            if (null != shifted) {
                document.replaceString(offsetStart, offsetEnd, shifted);
                UtilsEnvironment.reformatSubString(editor, project, offsetStart, offsetStart + shifted.length());
                return;
            }
        }

        int lineNumberSelStart = document.getLineNumber(offsetStart);
        int lineNumberSelEnd = document.getLineNumber(offsetEnd);

        if (document.getLineStartOffset(lineNumberSelEnd) == offsetEnd) {
            lineNumberSelEnd--;
        }

        if (!isJsVarsDeclarations && ((lineNumberSelEnd - lineNumberSelStart) > 0 && !isPhpVariableOrArray)) {
            // Multi-line selection: sort lines or swap quotes
            new ShiftableSelectionWithPopup(project, document, offsetStart, offsetEnd).sortLinesOrSwapQuotesInDocument(isUp);
            return;
        }
        if (isJsVarsDeclarations) {
            document.replaceString(offsetStart, offsetEnd, com.kstenschke.shifter.models.shiftableTypes.JsVariablesDeclarations.getShifted(selectedText));
            return;
        }
        if (!isPhpVariableOrArray && wordType == ShiftableTypesManager.TYPE_SIZZLE_SELECTOR) {
            document.replaceString(offsetStart, offsetEnd, com.kstenschke.shifter.models.shiftableTypes.SizzleSelector.getShifted(selectedText));
            return;
        }
        if (wordType == ShiftableTypesManager.TYPE_TRAILING_COMMENT) {
            int offsetStartCaretLine = document.getLineStartOffset(lineNumberSelStart);
            int offsetEndCaretLine   = document.getLineEndOffset(lineNumberSelStart);
            String leadWhitespace    = UtilsTextual.getLeadWhitespace(editorText.subSequence(offsetStartCaretLine, offsetEndCaretLine).toString());
            String caretLine         = editorText.subSequence(offsetStartCaretLine, offsetEndCaretLine).toString();

            document.replaceString(offsetStartCaretLine, offsetEndCaretLine, com.kstenschke.shifter.models.shiftableTypes.TrailingComment.getShifted(caretLine, leadWhitespace));
            return;
        }

        if (!isPhpVariableOrArray && isPhpFile && shiftSelectionInPhpDocument(document, filename, project, offsetStart, offsetEnd, selectedText, containsShiftableQuotes, isUp)) {
            return;
        }
        if (com.kstenschke.shifter.models.shiftableTypes.TernaryExpression.isTernaryExpression(selectedText, "")) {
            document.replaceString(offsetStart, offsetEnd, com.kstenschke.shifter.models.shiftableTypes.TernaryExpression.getShifted(selectedText));
            return;
        }

        if (!isPhpVariableOrArray) {
            if (com.kstenschke.shifter.models.shiftableTypes.SeparatedList.isSeparatedList(selectedText,",")) {
                // Comma-separated list: sort / ask whether to sort or toggle quotes
                new ShiftableSelectionWithPopup(project, document, offsetStart, offsetEnd).sortListOrSwapQuotesInDocument(",(\\s)*", ", ", isUp);
                return;
            }
            if (com.kstenschke.shifter.models.shiftableTypes.SeparatedList.isSeparatedList(selectedText,"|")) {
                // Pipe-separated list
                new ShiftableSelectionWithPopup(project, document, offsetStart, offsetEnd).sortListOrSwapQuotesInDocument("\\|(\\s)*", "|", isUp);
                return;
            }
            if (containsShiftableQuotes) {
                document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapQuotes(selectedText));
                return;
            }
            if (CamelCaseString.isCamelCase(selectedText) && CamelCaseString.isWordPair(selectedText)) {
                new ShiftableSelectionWithPopup(project, document, offsetStart, offsetEnd).shiftCamelCaseOrSwapWords();
                return;
            }
            if (MinusSeparatedPath.isMinusSeparatedPath(selectedText) && MinusSeparatedPath.isWordPair(selectedText)) {
                new ShiftableSelectionWithPopup(project, document, offsetStart, offsetEnd).shiftMinusSeparatedPathOrSwapWords();
                return;
            }

            com.kstenschke.shifter.models.shiftableTypes.Tupel wordsTupel = new com.kstenschke.shifter.models.shiftableTypes.Tupel();
            if (wordsTupel.isWordsTupel(selectedText)) {
                document.replaceString(offsetStart, offsetEnd, wordsTupel.getShifted(selectedText));
                return;
            }
            if (UtilsTextual.containsSlashes(selectedText)) {
                document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapSlashes(selectedText));
                return;
            }
            if (com.kstenschke.shifter.models.shiftableTypes.LogicalOperator.isLogicalOperator(selectedText)) {
                document.replaceString(offsetStart, offsetEnd, com.kstenschke.shifter.models.shiftableTypes.LogicalOperator.getShifted(selectedText));
                return;
            }
            if (HtmlEncodable.isHtmlEncodable(selectedText)) {
                document.replaceString(offsetStart, offsetEnd, HtmlEncodable.getShifted(selectedText));
                return;
            }
        }

        String shiftedWord = shiftingShiftableTypesManager.getShiftedWord(selectedText, isUp, editorText, caretOffset, moreCount, filename, editor);
        if (isPhpVariableOrArray) {
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

    private static boolean shiftSelectionInPhpDocument(Document document, String filename, Project project, int offsetStart, int offsetEnd, String selectedText, boolean containsQuotes, boolean isUp) {
        com.kstenschke.shifter.models.shiftableTypes.PhpConcatenation phpConcatenation = new com.kstenschke.shifter.models.shiftableTypes.PhpConcatenation(selectedText);
        if (phpConcatenation.isPhpConcatenation()) {
            new ShiftableSelectionWithPopup(project, document, offsetStart, offsetEnd).shiftPhpConcatenationOrSwapQuotesInDocument(phpConcatenation, isUp);
            return true;
        }
        if (com.kstenschke.shifter.models.shiftableTypes.Comment.isHtmlComment(selectedText)) {
            document.replaceString(offsetStart, offsetEnd, com.kstenschke.shifter.models.shiftableTypes.Comment.getPhpBlockCommentFromHtmlComment(selectedText));
            return true;
        }
        if (com.kstenschke.shifter.models.shiftableTypes.Comment.isPhpBlockComment(selectedText)) {
            document.replaceString(offsetStart, offsetEnd, com.kstenschke.shifter.models.shiftableTypes.Comment.getShifted(selectedText, filename, project));
            return true;
        }
        return false;
    }

    private static boolean shiftSelectedCommentInDocument(Editor editor, Document document, String filename, Project project, int offsetStart, int offsetEnd, String selectedText) {
        if (UtilsTextual.isMultiLine(selectedText)) {
            if (filename.endsWith("js") && com.kstenschke.shifter.models.shiftableTypes.JsDoc.isJsDocBlock(selectedText) && com.kstenschke.shifter.models.shiftableTypes.JsDoc.correctDocBlockInDocument(editor, document, offsetStart, offsetEnd)) {
                return true;
            }
            if (com.kstenschke.shifter.models.shiftableTypes.Comment.isBlockComment(selectedText)) {
                com.kstenschke.shifter.models.shiftableTypes.Comment.shiftMultiLineBlockCommentInDocument(selectedText, project, document, offsetStart, offsetEnd);
                return true;
            }
            if (com.kstenschke.shifter.models.shiftableTypes.Comment.isMultipleSingleLineComments(selectedText)) {
                com.kstenschke.shifter.models.shiftableTypes.Comment.shiftMultipleSingleLineCommentsInDocument(selectedText, project, document, offsetStart, offsetEnd);
                return true;
            }
        }

        document.replaceString(offsetStart, offsetEnd, com.kstenschke.shifter.models.shiftableTypes.Comment.getShifted(selectedText, filename, project));
        return true;
    }

    /**
     * Sort lines in document alphabetically ascending / descending
     *
     * @param reverse
     * @param lineNumberSelStart
     * @param lineNumberSelEnd
     */
    protected static void sortLinesInDocument(Document document, boolean reverse, int lineNumberSelStart, int lineNumberSelEnd) {
        List<String> lines       = UtilsTextual.extractLines(document, lineNumberSelStart, lineNumberSelEnd);
        List<String> linesSorted = UtilsTextual.sortLinesNatural(lines, reverse);

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