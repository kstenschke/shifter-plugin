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

import com.kstenschke.shifter.models.entities.AbstractShiftable;
import com.kstenschke.shifter.models.entities.shiftables.*;
import com.kstenschke.shifter.utils.UtilsFile;
import org.jetbrains.annotations.Nullable;

import static com.kstenschke.shifter.models.ShiftableTypes.Type.*;

/**
 * Manager of "shiftable" word shiftables - detects word type to evoke resp. shifting
 */
class ShiftableTypesManager {

    private ActionContainer actionContainer;

    // Generic shiftables (calculated when shifted)
    private DocCommentTag typeTagInDocComment;
    private DocCommentType typeDataTypeInDocComment;
    private Tupel wordsTupel;

    // Constructor
    public ShiftableTypesManager(ActionContainer actionContainer) {
        this.actionContainer = actionContainer;
    }

    AbstractShiftable getShiftableType() {
        AbstractShiftable shiftableType;

        //noinspection LoopStatementThatDoesntLoop
        while (true) {
            if (null != (shiftableType = new TrailingComment(actionContainer).getInstance())) break;

            // PHP doc param line is handled in caretLine-shifting fallback
            PhpDocParam phpDocParam = new PhpDocParam(actionContainer);
            actionContainer.shiftSelectedText = false;
            actionContainer.shiftCaretLine = true;
            if (null != phpDocParam.getInstance() &&
                !phpDocParam.containsDataType(actionContainer.caretLine)) return null;
            actionContainer.shiftSelectedText = true;
            actionContainer.shiftCaretLine = false;

            if (null != (shiftableType = new PhpVariableOrArray(actionContainer).getInstance())) break;
            if (null != (shiftableType = new Parenthesis(actionContainer).getInstance())) break;
            if (null != (shiftableType = new JsVariablesDeclarations(actionContainer).getInstance())) break;
            if (null != (shiftableType = new SizzleSelector(actionContainer).getInstance())) break;
            if (null != (shiftableType = new DocCommentType(actionContainer).getInstance())) break;
            if (null != (shiftableType = new AccessType(actionContainer).getInstance())) break;

            // File extension specific term in dictionary
            if (null != (shiftableType = new DictionaryTerm(actionContainer).isInFileTypeDictionary())) break;

            if (null != (shiftableType = new JqueryObserver(actionContainer).getInstance())) break;
            if (null != (shiftableType = new TernaryExpression(actionContainer).getInstance())) break;
            if (null != (shiftableType = new QuotedString(actionContainer).getInstance())) break;
            if (null != (shiftableType = new RgbColor(actionContainer).getInstance())) break;
            if (null != (shiftableType = new CssUnit(actionContainer).getInstance())) break;
            if (null != (shiftableType = new NumericValue(actionContainer).getInstance())) break;
            if (null != (shiftableType = new OperatorSign(actionContainer).getInstance())) break;
            if (null != (shiftableType = new RomanNumber(actionContainer).getInstance())) break;

            // Logical operators "&&" and "||" must be detected before MonoCharStrings to avoid confusing
            if (null != (shiftableType = new LogicalOperator(actionContainer).getInstance())) break;

            if (null != (shiftableType = new MonoCharacterRepetition(actionContainer).getInstance())) break;

            // Term in any dictionary (w/o limiting to edited file's extension)
            if (null != (shiftableType = new DictionaryTerm(actionContainer).getInstance())) break;

            if (null != (shiftableType = new NumericPostfixed(actionContainer).getInstance())) break;
            if (null != (shiftableType = new Tupel(actionContainer).getInstance())) break;
            if (null != (shiftableType = new SeparatedPath(actionContainer).getInstance())) break;
            if (null != (shiftableType = new CamelCaseString(actionContainer).getInstance())) break;
            if (null != (shiftableType = new HtmlEncodable(actionContainer).getInstance())) break;

            // @todo 1. completely remove getWordType()

            // @todo 2. rework: remove redundant arguments (e.g. from getShifted())

            // @todo 3. make shifting context flexible: actionContainer.selectedText or textAroundCaret / etc.

            // No shiftable type detected
            return null;
        }

        return shiftableType;
    }

    /**
     * Detect word type (get the one w/ highest priority to be shifted) of given string
     *
     * @param  word                     Word whose type shall be identified
     * @param  prefixChar               Prefix character
     * @param  postfixChar              Postfix character
     * @param  isLastLineInDocument
     * @param  actionContainer
     * @return int
     */
    ShiftableTypes.Type getWordType(
            String word,
            String prefixChar,
            String postfixChar,
            boolean isLastLineInDocument,
            ActionContainer actionContainer
    ) {
        AbstractShiftable shiftableType;
        // Selected code line w/ trailing //-comment: moves the comment into a new caretLine before the code
        if (null != new TrailingComment(actionContainer).getInstance()) return TRAILING_COMMENT;

        PhpDocParam phpDocParam = new PhpDocParam(actionContainer);
        actionContainer.shiftSelectedText = false;
        actionContainer.shiftCaretLine = true;
        if (null != phpDocParam.getInstance() &&
            !phpDocParam.containsDataType(actionContainer.caretLine)) {
//            return TYPE_PHP_DOC_PARAM_LINE;
            // PHP doc param line is handled in caretLine-shifting fallback
            return UNKNOWN;
        }

        if (null != new PhpVariableOrArray(actionContainer).getInstance()) return PHP_VARIABLE_OR_ARRAY;
        if (null != new Parenthesis(actionContainer).getInstance()) return PARENTHESIS;
        if (null != new JsVariablesDeclarations(actionContainer).getInstance()) return JS_VARIABLES_DECLARATIONS;
        if (null != new SizzleSelector(actionContainer).getInstance()) return SIZZLE_SELECTOR;

        // DocComment shiftables (must be prefixed w/ "@")
        typeDataTypeInDocComment = new DocCommentType(actionContainer);
        if (typeDataTypeInDocComment.isDocCommentTypeLineContext(actionContainer.caretLine)) {
            typeTagInDocComment = new DocCommentTag(actionContainer);
            if (prefixChar.matches("@") && null != typeTagInDocComment.getInstance()) return DOC_COMMENT_TAG;
            if (null != typeDataTypeInDocComment.getInstance()) return DOC_COMMENT_DATA_TYPE;
        }

        // Object visibility
        if (!"@".equals(prefixChar) && null != new AccessType(actionContainer).getInstance()) return ACCESS_TYPE;

        // File extension specific term in dictionary
        String fileExtension    = UtilsFile.extractFileExtension(actionContainer.filename);
        if (null != fileExtension) {
            if (null != new DictionaryTerm(actionContainer).isInFileTypeDictionary()) return DICTIONARY_WORD_EXT_SPECIFIC;
            if (null != new JqueryObserver(actionContainer).getInstance()) return JQUERY_OBSERVER;
        }

        if (null != new TernaryExpression(actionContainer).getInstance()) return TERNARY_EXPRESSION;
        if (null != new QuotedString(actionContainer).getInstance()) return QUOTED_STRING;
        if (null != new RgbColor(actionContainer).getInstance()) return RGB_COLOR;
        if (null != new CssUnit(actionContainer).getInstance()) return CSS_UNIT;
        if (null != new NumericValue(actionContainer).getInstance()) return NUMERIC_VALUE;
        if (null != new OperatorSign(actionContainer).getInstance()) return OPERATOR_SIGN;
        if (null != new RomanNumber(actionContainer).getInstance()) return ROMAN_NUMERAL;

        // Logical operators "&&" and "||" must be detected before MonoCharStrings to avoid confusing
        if (null != new LogicalOperator(actionContainer).getInstance()) return LOGICAL_OPERATOR;

        if (null != new MonoCharacterRepetition(actionContainer).getInstance()) return MONO_CHARACTER_REPETITION;

        // Term in dictionary (anywhere, that is w/o limiting to the current file extension)
        if (null != new DictionaryTerm(actionContainer).getInstance()) return DICTIONARY_WORD_GLOBAL;

        if (null != new NumericPostfixed(actionContainer).getInstance()) return NUMERIC_POSTFIXED;
        if (null != new Tupel(actionContainer).getInstance()) return WORDS_TUPEL;
        if (null != new SeparatedPath(actionContainer).getInstance()) return SEPARATED_PATH;
        if (null != new CamelCaseString(actionContainer).getInstance()) return CAMEL_CASED;
        if (null != new HtmlEncodable(actionContainer).getInstance()) return HTML_ENCODABLE;

        return UNKNOWN;
    }

    /**
     * Shift given word
     * ShifterTypesManager: get next/previous keyword of given word group
     * Generic: calculate shifted value
     *
     * @param  actionContainer
     * @param  word         Word to be shifted
     * @param  wordType     Shiftable word type
     * @param  moreCount    Current "more" count, starting w/ 1. If non-more shift: null
     * @return              The shifted word
     */
    String getShiftedWord(
            ActionContainer actionContainer,
            String word,
            ShiftableTypes.Type wordType,
            Integer moreCount
    ) {
        switch (wordType) {
            // String based word shiftables
            case ACCESS_TYPE:
                return new AccessType(actionContainer).getShifted(word);
            case DICTIONARY_WORD_GLOBAL:
            case DICTIONARY_WORD_EXT_SPECIFIC:
                // The dictionary stored the matching terms-line, we don't need to differ global/ext-specific anymore
                return new DictionaryTerm(actionContainer).getShifted(word);
            // Generic shiftables (shifting is calculated)
            case SIZZLE_SELECTOR:
                return new SizzleSelector(actionContainer).getShifted(word);
            case RGB_COLOR:
                return new RgbColor(actionContainer).getShifted(word);
            case NUMERIC_VALUE:
                // Numeric values including UNIX and millisecond timestamps
                return new NumericValue(actionContainer).getShifted(word);
            case CSS_UNIT:
                return new CssUnit(actionContainer).getShifted(word);
            case JQUERY_OBSERVER:
                JqueryObserver jqueryObserver = new JqueryObserver(actionContainer);
                return jqueryObserver.getShifted(word, null, null);
            case PHP_VARIABLE_OR_ARRAY:
                return new PhpVariableOrArray(actionContainer).getShifted(word, moreCount);
            case TERNARY_EXPRESSION:
                TernaryExpression ternaryExpression = new TernaryExpression(actionContainer);
                return ternaryExpression.getShifted(word);
            case QUOTED_STRING:
                return new QuotedString(actionContainer).getShifted(word);
            case PARENTHESIS:
                Parenthesis parenthesis = new Parenthesis(actionContainer);
                return parenthesis.getShifted(word);
            case OPERATOR_SIGN:
                return new OperatorSign(actionContainer).getShifted(word);
            case ROMAN_NUMERAL:
                return new RomanNumber(actionContainer).getShifted(word);
            case LOGICAL_OPERATOR:
                return new LogicalOperator(actionContainer).getShifted(word);
            case MONO_CHARACTER_REPETITION:
                return new MonoCharacterRepetition(actionContainer).getShifted(word);
            case DOC_COMMENT_TAG:
                actionContainer.textAfterCaret = actionContainer.editorText.toString().substring(actionContainer.caretOffset);
                return typeTagInDocComment.getShifted(word, null);
            case DOC_COMMENT_DATA_TYPE:
                return typeDataTypeInDocComment.getShifted(word);
            case SEPARATED_PATH:
                return new SeparatedPath(actionContainer).getShifted(word);
            case CAMEL_CASED:
                return new CamelCaseString(actionContainer).getShifted(word);
            case HTML_ENCODABLE:
                return new HtmlEncodable(actionContainer).getShifted(word);
            case NUMERIC_POSTFIXED:
                return new NumericPostfixed(actionContainer).getShifted(word);
            case WORDS_TUPEL:
                actionContainer.disableIntentionPopup = true;
                return wordsTupel.getShifted(word);
            default:
                return word;
        }
    }

    String getShiftedWord(ActionContainer actionContainer, @Nullable Integer moreCount) {
        actionContainer.shiftSelectedText = true;

        AbstractShiftable shiftableType = getShiftableType();
        return null == shiftableType
                ? actionContainer.selectedText
                : shiftableType.getShifted(actionContainer.selectedText, moreCount,null);
    }

    String getActionText() {
        // @todo return ShiftableTypeAbstract.ACTION_TXT
        return "@todo return ShiftableTypeAbstract.ACTION_TXT";
        /*switch (wordType) {
            case CSS_UNIT:
                return CssUnit.ACTION_TEXT;
            case HTML_ENCODABLE:
                return HtmlEncodable.ACTION_TEXT;
            case JS_VARIABLES_DECLARATIONS:
                return JsVariablesDeclarations.ACTION_TEXT;
            case LOGICAL_OPERATOR:
                return LogicalOperator.ACTION_TEXT;
            case MONO_CHARACTER_REPETITION:
                return MonoCharacterRepetition.ACTION_TEXT;
            case NUMERIC_VALUE:
                return NumericValue.ACTION_TEXT;
            case RGB_COLOR:
                return RgbColor.ACTION_TEXT;
            case TERNARY_EXPRESSION:
                return TernaryExpression.ACTION_TEXT;
            case TRAILING_COMMENT:
                return TrailingComment.ACTION_TEXT;
            case WORDS_TUPEL:
                return Tupel.ACTION_TEXT;
            default:
                return ACTION_TEXT_SHIFT_SELECTION;
        }
        */
    }
}