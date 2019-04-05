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

import com.kstenschke.shifter.models.shiftable_types.*;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

import static com.kstenschke.shifter.models.ShiftableTypes.Type.*;

// Shiftable (non-block) selection
public class ShiftableSelection {

    static final String ACTION_TEXT_SHIFT_SELECTION = "Shift Selection";

    private static final String ACTION_TEXT_SWAP_SLASHES     = "Swap Slashes";
    private static final String ACTION_TEXT_SWAP_WORDS_ORDER = "Swap Words Order";
    private static final String ACTION_TEXT_SWAP_QUOTES      = "Swap Quotes";

    /**
     * @param actionContainer
     * @param moreCount     Current "more" count, starting w/ 1. If non-more shift: null
     */
    public static void shiftSelectionInDocument(final ActionContainer actionContainer, @Nullable Integer moreCount) {
        if (null == actionContainer.selectedText || actionContainer.selectedText.trim().isEmpty()) {
            return;
        }

        // @todo convert all shiftable type to extend ShiftableTypeAbstract

        boolean isPhpFile = UtilsFile.isPhpFile(actionContainer.filename);
        if (isPhpFile &&
            null != new PhpDocParam(actionContainer).getShifted(actionContainer.selectedText)
        ) {
            // Detect and shift whole PHPDoc block or single line out of it, that contains @param caretLine(s) w/o data type
            return;
        }
        if (UtilsFile.isJavaScriptFile(actionContainer.filename, true) &&
            JsDoc.isJsDocBlock(actionContainer.selectedText) &&
            JsDoc.correctDocBlockInDocument(actionContainer)) {
            return;
        }

        // Shift selected comment: Must be before multi-line sort to allow multi-caretLine comment shifting
        if (Comment.isComment(actionContainer.selectedText)) {
            shiftSelectedCommentInDocument(actionContainer);
            return;
        }

        final XmlAttributes xmlAttributes = new XmlAttributes(actionContainer);
        if (xmlAttributes.isXmlAttributes(actionContainer.selectedText)) {
            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceSelection(
                            xmlAttributes.getShifted(
                                    actionContainer.selectedText, true)),
                    XmlAttributes.ACTION_TEXT);
            return;
        }

        Parenthesis parenthesis = new Parenthesis(actionContainer);
        boolean isWrappedInParenthesis = parenthesis.getShiftableType() != null;

        ShiftableTypesManager shiftableTypesManager = new ShiftableTypesManager(actionContainer);

        //ShiftableTypeAbstract shiftableType = shiftableTypesManager.getShiftableType(actionContainer);


        ShiftableTypes.Type wordType;
        if (null == actionContainer.editorText) {
            wordType = UNKNOWN;
        } else {
            int editorTextLength = actionContainer.editorText.length();
            int offsetPostfixChar = actionContainer.caretOffset + actionContainer.selectedText.length();
            String postfixChar = editorTextLength > offsetPostfixChar
                    ? String.valueOf(actionContainer.editorText.charAt(offsetPostfixChar))
                    : "";
            boolean isLastLineInDocument = offsetPostfixChar == editorTextLength;

            wordType = shiftableTypesManager.getWordType(actionContainer.selectedText, "", postfixChar, isLastLineInDocument, actionContainer);
        }


        boolean isPhpVariableOrArray = PHP_VARIABLE_OR_ARRAY == wordType;

        if (isWrappedInParenthesis) {
            boolean isShiftablePhpArray = isPhpVariableOrArray &&
                                          PhpVariableOrArray.isStaticShiftablePhpArray(actionContainer.selectedText);
            if (!isPhpVariableOrArray || !isShiftablePhpArray) {
                // Swap surrounding "(" and ")" versus "[" and "]"
                actionContainer.writeUndoable(
                        actionContainer.getRunnableReplaceSelection(
                                parenthesis.getShifted(actionContainer.selectedText)),
                        Parenthesis.ACTION_TEXT);
                return;
            }
            // Swap parenthesis or convert PHP array
            new ShiftableSelectionWithPopup(actionContainer).swapParenthesisOrConvertPphpArray();
            return;
        }

        boolean isJsVarsDeclarations    = !isPhpVariableOrArray && JS_VARIABLES_DECLARATIONS == wordType;
        boolean containsShiftableQuotes = QuotedString.containsShiftableQuotes(actionContainer.selectedText);
        boolean isMultiLine             = UtilsTextual.isMultiLine(actionContainer.selectedText);

        if (UtilsFile.isCssFile(actionContainer.filename) && isMultiLine) {
            // CSS: Sort attributes per selector alphabetically
            final String shifted = Css.getShifted(actionContainer.selectedText);
            if (null != shifted) {
                actionContainer.writeUndoable(
                        actionContainer.getRunnableReplaceSelection(shifted, true),
                        Css.ACTION_TEXT);
                return;
            }
        }

        int lineNumberSelStart = actionContainer.document.getLineNumber(actionContainer.offsetSelectionStart);
        int lineNumberSelEnd   = actionContainer.document.getLineNumber(actionContainer.offsetSelectionEnd);

        if (actionContainer.document.getLineStartOffset(lineNumberSelEnd) == actionContainer.offsetSelectionEnd) {
            lineNumberSelEnd--;
        }

        TernaryExpression ternaryExpression = new TernaryExpression(actionContainer);
        if (null != ternaryExpression.getShiftableType()) {
            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceSelection(
                            ternaryExpression.getShifted(actionContainer.selectedText, actionContainer, null, null),
                            true),
                    TernaryExpression.ACTION_TEXT);
            return;
        }
        if (!isJsVarsDeclarations && ((lineNumberSelEnd - lineNumberSelStart) > 0 && !isPhpVariableOrArray)) {
            // Multi-line selection: sort lines or swap quotes
            new ShiftableSelectionWithPopup(actionContainer).sortLinesOrSwapQuotesInDocument();
            return;
        }
        if (isJsVarsDeclarations) {
            JsVariablesDeclarations jsVariablesDeclarations = new JsVariablesDeclarations(actionContainer);
            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceSelection(
                            jsVariablesDeclarations.getShifted(actionContainer.selectedText)),
                            JsVariablesDeclarations.ACTION_TEXT);
            return;
        }
        JqueryObserver jqueryObserver = new JqueryObserver(actionContainer);
        if (null != jqueryObserver.getShiftableType()) {
            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceSelection(
                            jqueryObserver.getShifted(actionContainer.selectedText, actionContainer, null, null)),
                    JqueryObserver.ACTION_TEXT);
            return;
        }
        if (!isPhpVariableOrArray && SIZZLE_SELECTOR == wordType) {
            SizzleSelector sizzleSelector = new SizzleSelector(actionContainer);
            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceSelection(sizzleSelector.getShifted(actionContainer.selectedText, actionContainer)),
                    SizzleSelector.ACTION_TEXT);
            return;
        }
        if (TRAILING_COMMENT == wordType) {
            final int offsetStartCaretLine = actionContainer.document.getLineStartOffset(lineNumberSelStart);
            final int offsetEndCaretLine   = actionContainer.document.getLineEndOffset(lineNumberSelStart);
            final String leadWhitespace    = UtilsTextual.getLeadWhitespace(
                actionContainer.editorText.subSequence(offsetStartCaretLine, offsetEndCaretLine).toString());
            final String caretLine         = actionContainer.editorText.subSequence(offsetStartCaretLine, offsetEndCaretLine).toString();

            TrailingComment trailingComment = new TrailingComment(actionContainer);
            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceCaretLine(
                            trailingComment.getShifted(caretLine, actionContainer, moreCount, leadWhitespace)),
                            TrailingComment.ACTION_TEXT);
            return;
        }

        if (!isPhpVariableOrArray) {
            if (isPhpFile && shiftSelectionInPhpDocument(actionContainer)) {
                return;
            }
            boolean isJsConcatenationInTypeScript = "ts".equals(actionContainer.fileExtension) &&
                    JsConcatenation.isJsConcatenation(actionContainer.selectedText);
            if (SeparatedList.isSeparatedList(actionContainer.selectedText,",")) {
                // Comma-separated list: sort / ask whether to sort or toggle quotes
                new ShiftableSelectionWithPopup(actionContainer).sortListOrSwapQuotesOrInterpolateTypeScriptInDocument(
                        ",(\\s)*",
                        ", ",
                        true,
                        actionContainer.isShiftUp);
                return;
            }
            final LogicalConjunction logicalConjunction = new LogicalConjunction();
            boolean isLogicalConjunction = logicalConjunction.isLogicalConjunction(actionContainer.selectedText);
            if ( (!isLogicalConjunction || !logicalConjunction.isOrLogic)
                 && SeparatedList.isSeparatedList(actionContainer.selectedText,"|")
            ) {
                // Pipe-separated list (not confused w/ || of logical conjunctions)
                new ShiftableSelectionWithPopup(actionContainer).sortListOrSwapQuotesOrInterpolateTypeScriptInDocument(
                    "\\|(\\s)*",
                    "|",
                    isJsConcatenationInTypeScript,
                    actionContainer.isShiftUp);
                return;
            }
            if (isJsConcatenationInTypeScript) {
                if (containsShiftableQuotes) {
                    // Can toggle quotes or convert to interpolation
                    new ShiftableSelectionWithPopup(actionContainer).interpolateConcatenationOrSwapQuotesInDocument(actionContainer.isShiftUp);
                    return;
                } else {
                    // @todo add popup: toggle order or convert to interpolation
                    actionContainer.writeUndoable(
                            actionContainer.getRunnableReplaceSelection(new JsConcatenation().getShifted(actionContainer.selectedText)),
                            JsConcatenation.ACTION_TEXT);
                    return;
                }
            }
            if (containsShiftableQuotes) {
                if (!QuotedString.containsEscapedQuotes(actionContainer.selectedText)) {
                    actionContainer.writeUndoable(
                            actionContainer.getRunnableReplaceSelection(UtilsTextual.swapQuotes(actionContainer.selectedText)),
                            ACTION_TEXT_SWAP_QUOTES);
                    return;
                }
                new ShiftableSelectionWithPopup(actionContainer).shiftQuotesInDocument();
                return;
            }

            CamelCaseString camelCaseString = new CamelCaseString(actionContainer);
            if (null != camelCaseString.getShiftableType()) {
                new ShiftableSelectionWithPopup(actionContainer).shiftCamelCase(
                        CamelCaseString.isWordPair(actionContainer.selectedText));
                return;
            }
            SeparatedPath separatedPath = new SeparatedPath(actionContainer);
            if (null != separatedPath.getShiftableType() &&
                separatedPath.isWordPair(actionContainer.selectedText)
            ) {
                new ShiftableSelectionWithPopup(actionContainer).shiftSeparatedPathOrSwapWords();
                return;
            }

            final Tupel wordsTupel = new Tupel(actionContainer);
            if (null != wordsTupel.getShiftableType()) {
                actionContainer.disableIntentionPopup = false;
                final String replacement = wordsTupel.getShifted(actionContainer.selectedText, actionContainer);
                if (!replacement.isEmpty()) {
                    /* If there is a selection, and it is a words tupel and at the same time a dictionary term,
                     * an intention popup is opened to chose whether to 1. Swap words order or 2. Shift dictionaric
                     * The manipulation of 2. is done already, 1. returns the replacement string (if it is not a dictionary term also)
                     */
                    actionContainer.writeUndoable(actionContainer.getRunnableReplaceSelection(replacement), ACTION_TEXT_SWAP_WORDS_ORDER);
                }

                return;
            }
            if (UtilsTextual.containsSlashes(actionContainer.selectedText)) {
                if (QuotedString.containsEscapedQuotes(actionContainer.selectedText)) {
                    new ShiftableSelectionWithPopup(actionContainer).swapSlashesOrUnescapeQuotes();
                    return;
                }
                actionContainer.writeUndoable(
                        actionContainer.getRunnableReplaceSelection(UtilsTextual.swapSlashes(actionContainer.selectedText)),
                        ACTION_TEXT_SWAP_SLASHES);
                return;
            }
            LogicalOperator logicalOperator = new LogicalOperator(actionContainer);
            if (null != logicalOperator.getShiftableType()) {
                actionContainer.writeUndoable(
                        actionContainer.getRunnableReplaceSelection(
                                logicalOperator.getShifted(actionContainer.selectedText)),
                        LogicalOperator.ACTION_TEXT);
                return;
            }
            if (isLogicalConjunction) {
                actionContainer.writeUndoable(
                        actionContainer.getRunnableReplaceSelection(logicalConjunction.getShifted(actionContainer.selectedText)),
                        LogicalConjunction.ACTION_TEXT);
                return;
            }
            HtmlEncodable htmlEncodable = new HtmlEncodable(actionContainer);
            if (null != htmlEncodable.getShiftableType()) {
                actionContainer.writeUndoable(
                        actionContainer.getRunnableReplaceSelection(
                                htmlEncodable.getShifted(actionContainer.selectedText, actionContainer)),
                        HtmlEncodable.ACTION_TEXT);
                return;
            }
        }

        actionContainer.trimSelectedText();
        final String shiftedWord = shiftableTypesManager.getShiftedWord(actionContainer, moreCount);
        if (isPhpVariableOrArray) {
            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceSelection(
                        actionContainer.whiteSpaceLHSinSelection + shiftedWord + actionContainer.whiteSpaceRHSinSelection),
                    shiftableTypesManager.getActionText());
            return;
        }
        if (UtilsTextual.isAllUppercase(actionContainer.selectedText)) {
            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceSelection(
                        actionContainer.whiteSpaceLHSinSelection + shiftedWord.toUpperCase() + actionContainer.whiteSpaceRHSinSelection),
                    ACTION_TEXT_SHIFT_SELECTION);
            return;
        }
        if (UtilsTextual.isUpperCamelCase(actionContainer.selectedText) ||
            UtilsTextual.isUcFirstRestLower(actionContainer.selectedText)) {
            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceSelection(
                        actionContainer.whiteSpaceLHSinSelection + UtilsTextual.toUcFirstRestLower(shiftedWord) + actionContainer.whiteSpaceRHSinSelection),
                    ACTION_TEXT_SHIFT_SELECTION);
            return;
        }

        actionContainer.writeUndoable(
                actionContainer.getRunnableReplaceSelection(
                    actionContainer.whiteSpaceLHSinSelection + shiftedWord + actionContainer.whiteSpaceRHSinSelection),
                ACTION_TEXT_SHIFT_SELECTION);
    }

    private static boolean shiftSelectionInPhpDocument(final ActionContainer actionContainer) {
        final PhpConcatenation phpConcatenation = new PhpConcatenation(actionContainer.selectedText);
        if (phpConcatenation.isPhpConcatenation()) {
            actionContainer.writeUndoable(
                    () -> new ShiftableSelectionWithPopup(actionContainer).shiftPhpConcatenationOrSwapQuotesInDocument(phpConcatenation),
                    ACTION_TEXT_SHIFT_SELECTION);

            return true;
        }
        if (Comment.isHtmlComment(actionContainer.selectedText)) {
            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceSelection(Comment.getPhpBlockCommentFromHtmlComment(actionContainer.selectedText)),
                    ACTION_TEXT_SHIFT_SELECTION);
            return true;
        }
        if (Comment.isPhpBlockComment(actionContainer.selectedText)) {
            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceSelection(Comment.getShifted(actionContainer)),
                    ACTION_TEXT_SHIFT_SELECTION);
            return true;
        }
        return false;
    }

    private static void shiftSelectedCommentInDocument(ActionContainer actionContainer) {
        if (UtilsTextual.isMultiLine(actionContainer.selectedText)) {
            if (actionContainer.filename.endsWith("js") && JsDoc.isJsDocBlock(actionContainer.selectedText)) {
                JsDoc.correctDocBlockInDocument(actionContainer);
                return;
            }
            if (Comment.isBlockComment(actionContainer.selectedText)) {
                Comment.shiftMultiLineBlockCommentInDocument(actionContainer);
                return;
            }
            if (Comment.isMultipleSingleLineComments(actionContainer.selectedText)) {
                Comment.shiftMultipleSingleLineCommentsInDocument(actionContainer);
                return;
            }
        }

        actionContainer.writeUndoable(
                actionContainer.getRunnableReplaceSelection(
                        Comment.getShifted(actionContainer)),
                Comment.ACTION_TEXT);
    }

    /**
     * Sort lines in document alphabetically ascending / descending
     *
     * @param actionContainer
     * @param reverse
     */
    static void sortLinesInDocument(final ActionContainer actionContainer, boolean reverse) {
        List<String> lines = UtilsTextual.extractLines(
            actionContainer.document, actionContainer.lineNumberSelStart, actionContainer.lineNumberSelEnd);
        UtilsTextual.sortLinesNatural(lines, reverse);
        String linesString = UtilsTextual.joinLines(lines).toString().trim();

        if (UtilsTextual.hasDuplicateLines(linesString) && JOptionPane.showConfirmDialog(
                null,
                StaticTexts.MESSAGE_REDUCE_DUPLICATE_LINES,
                StaticTexts.TITLE_REDUCE_DUPLICATE_LINES,
                JOptionPane.OK_CANCEL_OPTION
        ) == JOptionPane.OK_OPTION)
        {
            linesString = UtilsTextual.reduceDuplicateLines(linesString);
        }

        actionContainer.writeUndoable(actionContainer.getRunnableReplaceSelection(linesString, true), ACTION_TEXT_SHIFT_SELECTION);
    }
}