/*
 * Copyright 2011-2018 Kay Stenschke
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

import com.kstenschke.shifter.models.shiftableTypes.*;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.utils.UtilsEnvironment;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

import static com.kstenschke.shifter.models.ShiftableTypes.Type.*;

// Shiftable (non-block) selection
public class ShiftableSelection {

    protected static final String ACTION_TEXT_SHIFT_SELECTION = "Shift Selection";

    /**
     * @param actionContainer
     * @param moreCount     Current "more" count, starting w/ 1. If non-more shift: null
     */
    public static void shiftSelectionInDocument(final ActionContainer actionContainer, @Nullable Integer moreCount) {
        if (null == actionContainer.selectedText || actionContainer.selectedText.trim().isEmpty()) {
            return;
        }

        boolean isPhpFile = UtilsFile.isPhpFile(actionContainer.filename);
        if (isPhpFile && PhpDocParam.shiftSelectedPhpDocInDocument(actionContainer)) {
            // Detect and shift whole PHPDoc block or single caretLine out of it, that contains @param caretLine(s) w/o data type
            return;
        }
        if (actionContainer.filename.endsWith(".js") && JsDoc.isJsDocBlock(actionContainer.selectedText) && JsDoc.correctDocBlockInDocument(actionContainer)) {
            return;
        }

        // Shift selected comment: Must be before multi-caretLine sort to allow multi-caretLine comment shifting
        if (com.kstenschke.shifter.models.shiftableTypes.Comment.isComment(actionContainer.selectedText)) {
            shiftSelectedCommentInDocument(actionContainer);
            return;
        }

        boolean isWrappedInParenthesis = Parenthesis.isWrappedInParenthesis(actionContainer.selectedText);

        ShiftableTypesManager shiftingShiftableTypesManager = new ShiftableTypesManager();
        ShiftableTypes.Type wordType = shiftingShiftableTypesManager.getWordType(actionContainer);
        boolean isPhpVariableOrArray = wordType == PHP_VARIABLE_OR_ARRAY;

        if (isWrappedInParenthesis) {
            boolean isShiftablePhpArray = isPhpVariableOrArray && PhpVariableOrArray.isStaticShiftablePhpArray(actionContainer.selectedText);
            if (!isPhpVariableOrArray || !isShiftablePhpArray) {
                // Swap surrounding "(" and ")" versus "[" and "]"
                actionContainer.writeUndoable(
                        new Runnable() {
                            @Override
                            public void run() {
                                actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, Parenthesis.getShifted(actionContainer.selectedText));
                            }
                        },
                        ACTION_TEXT_SHIFT_SELECTION);
                return;
            }
            // Swap parenthesis or convert PHP array
            new ShiftableSelectionWithPopup(actionContainer).swapParenthesisOrConvertPphpArray();
            return;
        }

        boolean isJsVarsDeclarations    = !isPhpVariableOrArray && wordType == JS_VARIABLES_DECLARATIONS;
        boolean containsShiftableQuotes = QuotedString.containsShiftableQuotes(actionContainer.selectedText);
        boolean isMultiLine             = UtilsTextual.isMultiLine(actionContainer.selectedText);

        if (UtilsFile.isCssFile(actionContainer.filename) && isMultiLine) {
            // CSS: Sort attributes per selector alphabetically
            final String shifted = Css.getShifted(actionContainer.selectedText);
            if (null != shifted) {
                actionContainer.writeUndoable(
                        new Runnable() {
                            @Override
                            public void run() {
                                actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, shifted);
                                UtilsEnvironment.reformatSubString(actionContainer.editor, actionContainer.project, actionContainer.offsetSelectionStart, actionContainer.offsetSelectionStart + shifted.length());
                            }
                        },
                        ACTION_TEXT_SHIFT_SELECTION);

                return;
            }
        }

        int lineNumberSelStart = actionContainer.document.getLineNumber(actionContainer.offsetSelectionStart);
        int lineNumberSelEnd   = actionContainer.document.getLineNumber(actionContainer.offsetSelectionEnd);

        if (actionContainer.document.getLineStartOffset(lineNumberSelEnd) == actionContainer.offsetSelectionEnd) {
            lineNumberSelEnd--;
        }

        if (com.kstenschke.shifter.models.shiftableTypes.TernaryExpression.isTernaryExpression(actionContainer.selectedText, "")) {
            actionContainer.writeUndoable(
                    new Runnable() {
                        @Override
                        public void run() {
                            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, com.kstenschke.shifter.models.shiftableTypes.TernaryExpression.getShifted(actionContainer.selectedText));
                            UtilsEnvironment.reformatSelection(actionContainer.editor, actionContainer.project);
                        }
                    },
                    ACTION_TEXT_SHIFT_SELECTION);

            return;
        }
        if (!isJsVarsDeclarations && ((lineNumberSelEnd - lineNumberSelStart) > 0 && !isPhpVariableOrArray)) {
            // Multi-caretLine selection: sort lines or swap quotes
            new ShiftableSelectionWithPopup(actionContainer).sortLinesOrSwapQuotesInDocument();
            return;
        }
        if (isJsVarsDeclarations) {
            actionContainer.writeUndoable(
                    new Runnable() {
                        @Override
                        public void run() {
                            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, com.kstenschke.shifter.models.shiftableTypes.JsVariablesDeclarations.getShifted(actionContainer.selectedText));
                        }
                    },
                    ACTION_TEXT_SHIFT_SELECTION);

            return;
        }
        if (!isPhpVariableOrArray && wordType == SIZZLE_SELECTOR) {
            actionContainer.writeUndoable(
                    new Runnable() {
                        @Override
                        public void run() {
                            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, com.kstenschke.shifter.models.shiftableTypes.SizzleSelector.getShifted(actionContainer.selectedText));
                        }
                    },
                    ACTION_TEXT_SHIFT_SELECTION);

            return;
        }
        if (wordType == TRAILING_COMMENT) {
            final int offsetStartCaretLine = actionContainer.document.getLineStartOffset(lineNumberSelStart);
            final int offsetEndCaretLine   = actionContainer.document.getLineEndOffset(lineNumberSelStart);
            final String leadWhitespace    = UtilsTextual.getLeadWhitespace(actionContainer.editorText.subSequence(offsetStartCaretLine, offsetEndCaretLine).toString());
            final String caretLine         = actionContainer.editorText.subSequence(offsetStartCaretLine, offsetEndCaretLine).toString();

            actionContainer.writeUndoable(
                    new Runnable() {
                        @Override
                        public void run() {
                            actionContainer.document.replaceString(offsetStartCaretLine, offsetEndCaretLine, com.kstenschke.shifter.models.shiftableTypes.TrailingComment.getShifted(caretLine, leadWhitespace));
                        }
                    },
                    ACTION_TEXT_SHIFT_SELECTION);

            return;
        }

        if (!isPhpVariableOrArray && isPhpFile && shiftSelectionInPhpDocument(actionContainer, containsShiftableQuotes)) {
            return;
        }

        if (!isPhpVariableOrArray) {
            if (com.kstenschke.shifter.models.shiftableTypes.SeparatedList.isSeparatedList(actionContainer.selectedText,",")) {
                // Comma-separated list: sort / ask whether to sort or toggle quotes
                new ShiftableSelectionWithPopup(actionContainer).sortListOrSwapQuotesInDocument(",(\\s)*", ", ", actionContainer.isShiftUp);
                return;
            }
            if (com.kstenschke.shifter.models.shiftableTypes.SeparatedList.isSeparatedList(actionContainer.selectedText,"|")) {
                // Pipe-separated list
                new ShiftableSelectionWithPopup(actionContainer).sortListOrSwapQuotesInDocument("\\|(\\s)*", "|", actionContainer.isShiftUp);
                return;
            }
            if (containsShiftableQuotes) {
                if (!QuotedString.containsEscapedQuotes(actionContainer.selectedText)) {
                    actionContainer.writeUndoable(
                            new Runnable() {
                                @Override
                                public void run() {
                                    actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, UtilsTextual.swapQuotes(actionContainer.selectedText));
                                }
                            },
                            ACTION_TEXT_SHIFT_SELECTION);

                    return;
                }
                new ShiftableSelectionWithPopup(actionContainer).shiftQuotesInDocument();
                return;
            }
            if (CamelCaseString.isCamelCase(actionContainer.selectedText)) {
                new ShiftableSelectionWithPopup(actionContainer).shiftCamelCase(
                        CamelCaseString.isWordPair(actionContainer.selectedText));
                return;
            }
            if (SeparatedPath.isSeparatedPath(actionContainer.selectedText) && SeparatedPath.isWordPair(actionContainer.selectedText)) {
                new ShiftableSelectionWithPopup(actionContainer).shiftSeparatedPathOrSwapWords();
                return;
            }

            final com.kstenschke.shifter.models.shiftableTypes.Tupel wordsTupel = new com.kstenschke.shifter.models.shiftableTypes.Tupel(actionContainer);
            if (wordsTupel.isWordsTupel(actionContainer.selectedText)) {
                final String replacement = wordsTupel.getShifted(actionContainer.selectedText, false);
                if (!replacement.isEmpty()) {
                    /* If there is a selection, and it is a words tupel and at the same time a dictionary term,
                     * an intention popup is opened to chose whether to 1. Swap words order or 2. Shift dictionaric
                     * The manipulation of 2. is done already, 1. returns the replacement string (if it is not a dictionary term also)
                     */
                    actionContainer.writeUndoable(
                            new Runnable() {
                                @Override
                                public void run() {
                                    actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, replacement);
                                }
                            },
                            "Swap Words Order");
                }

                return;
            }
            if (UtilsTextual.containsSlashes(actionContainer.selectedText)) {
                if (QuotedString.containsEscapedQuotes(actionContainer.selectedText)) {
                    new ShiftableSelectionWithPopup(actionContainer).swapSlashesOrUnescapeQuotes();
                    return;
                }
                actionContainer.writeUndoable(
                        new Runnable() {
                            @Override
                            public void run() {
                                actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, UtilsTextual.swapSlashes(actionContainer.selectedText));
                            }
                        },
                        ACTION_TEXT_SHIFT_SELECTION);

                return;
            }
            if (com.kstenschke.shifter.models.shiftableTypes.LogicalOperator.isLogicalOperator(actionContainer.selectedText)) {
                actionContainer.writeUndoable(
                        new Runnable() {
                            @Override
                            public void run() {
                                actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, com.kstenschke.shifter.models.shiftableTypes.LogicalOperator.getShifted(actionContainer.selectedText));
                            }
                        },
                        ACTION_TEXT_SHIFT_SELECTION);

                return;
            }
            if (HtmlEncodable.isHtmlEncodable(actionContainer.selectedText)) {
                actionContainer.writeUndoable(
                        new Runnable() {
                            @Override
                            public void run() {
                                actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, HtmlEncodable.getShifted(actionContainer.selectedText));
                            }
                        },
                        ACTION_TEXT_SHIFT_SELECTION);

                return;
            }
        }

        final String shiftedWord = shiftingShiftableTypesManager.getShiftedWord(actionContainer, moreCount);
        if (isPhpVariableOrArray) {
            actionContainer.writeUndoable(
                    new Runnable() {
                        @Override
                        public void run() {
                            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, shiftedWord);
                        }
                    },
                    ACTION_TEXT_SHIFT_SELECTION);

            return;
        }
        if (UtilsTextual.isAllUppercase(actionContainer.selectedText)) {
            actionContainer.writeUndoable(
                    new Runnable() {
                        @Override
                        public void run() {
                            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, shiftedWord.toUpperCase());
                        }
                    },
                    ACTION_TEXT_SHIFT_SELECTION);

            return;
        }
        if (UtilsTextual.isUpperCamelCase(actionContainer.selectedText) || UtilsTextual.isUcFirst(actionContainer.selectedText)) {
            actionContainer.writeUndoable(
                    new Runnable() {
                        @Override
                        public void run() {
                            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, UtilsTextual.toUcFirstRestLower(shiftedWord));
                        }
                    },
                    ACTION_TEXT_SHIFT_SELECTION);

            return;
        }

        actionContainer.writeUndoable(
                new Runnable() {
                    @Override
                    public void run() {
                        actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, shiftedWord);
                    }
                },
                ACTION_TEXT_SHIFT_SELECTION);
    }

    private static boolean shiftSelectionInPhpDocument(final ActionContainer actionContainer, boolean containsQuotes) {
        final com.kstenschke.shifter.models.shiftableTypes.PhpConcatenation phpConcatenation = new com.kstenschke.shifter.models.shiftableTypes.PhpConcatenation(actionContainer.selectedText);
        if (phpConcatenation.isPhpConcatenation()) {
            actionContainer.writeUndoable(
                    new Runnable() {
                        @Override
                        public void run() {
                            new ShiftableSelectionWithPopup(actionContainer).shiftPhpConcatenationOrSwapQuotesInDocument(phpConcatenation);
                        }
                    },
                    ACTION_TEXT_SHIFT_SELECTION);

            return true;
        }
        if (com.kstenschke.shifter.models.shiftableTypes.Comment.isHtmlComment(actionContainer.selectedText)) {
            actionContainer.writeUndoable(
                    new Runnable() {
                        @Override
                        public void run() {
                            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, com.kstenschke.shifter.models.shiftableTypes.Comment.getPhpBlockCommentFromHtmlComment(actionContainer.selectedText));
                        }
                    },
                    ACTION_TEXT_SHIFT_SELECTION);

            return true;
        }
        if (com.kstenschke.shifter.models.shiftableTypes.Comment.isPhpBlockComment(actionContainer.selectedText)) {
            actionContainer.writeUndoable(
                    new Runnable() {
                        @Override
                        public void run() {
                            actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, com.kstenschke.shifter.models.shiftableTypes.Comment.getShifted(actionContainer));
                        }
                    },
                    ACTION_TEXT_SHIFT_SELECTION);

            return true;
        }
        return false;
    }

    private static void shiftSelectedCommentInDocument(ActionContainer actionContainer) {
        if (UtilsTextual.isMultiLine(actionContainer.selectedText)) {
            if (actionContainer.filename.endsWith("js") && com.kstenschke.shifter.models.shiftableTypes.JsDoc.isJsDocBlock(actionContainer.selectedText)) {
                com.kstenschke.shifter.models.shiftableTypes.JsDoc.correctDocBlockInDocument(actionContainer);
                return;
            }
            if (com.kstenschke.shifter.models.shiftableTypes.Comment.isBlockComment(actionContainer.selectedText)) {
                com.kstenschke.shifter.models.shiftableTypes.Comment.shiftMultiLineBlockCommentInDocument(actionContainer);
                return;
            }
            if (com.kstenschke.shifter.models.shiftableTypes.Comment.isMultipleSingleLineComments(actionContainer.selectedText)) {
                com.kstenschke.shifter.models.shiftableTypes.Comment.shiftMultipleSingleLineCommentsInDocument(actionContainer);
                return;
            }
        }

        actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, com.kstenschke.shifter.models.shiftableTypes.Comment.getShifted(actionContainer));
    }

    /**
     * Sort lines in document alphabetically ascending / descending
     *
     * @param actionContainer
     * @param reverse
     */
    static void sortLinesInDocument(final ActionContainer actionContainer, boolean reverse) {
        List<String> lines       = UtilsTextual.extractLines(actionContainer.document, actionContainer.lineNumberSelStart, actionContainer.lineNumberSelEnd);
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

        final String linesStringFin = linesString;
        actionContainer.writeUndoable(
                new Runnable() {
                    @Override
                    public void run() {
                        actionContainer.document.replaceString(actionContainer.offsetSelectionLineStart, actionContainer.offsetSelectionLineEnd, linesStringFin);
                    }
                },
                ACTION_TEXT_SHIFT_SELECTION);
    }
}