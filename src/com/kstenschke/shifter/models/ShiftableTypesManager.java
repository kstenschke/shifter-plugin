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
import com.kstenschke.shifter.utils.UtilsFile;
import org.jetbrains.annotations.Nullable;

import static com.kstenschke.shifter.models.ShiftableSelection.ACTION_TEXT_SHIFT_SELECTION;
import static com.kstenschke.shifter.models.ShiftableTypes.Type.*;

/**
 * Manager of "shiftable" word shiftable_types - detects word type to evoke resp. shifting
 */
class ShiftableTypesManager {

    private ShiftableTypes.Type wordType;

    // Word type objects
    private DictionaryTerm typeDictionaryTerm;
    private AccessType accessType;

    // Generic shiftable_types (calculated when shifted)
    private CssUnit typePixelValue;
    private DocCommentTag typeTagInDocComment;
    private DocCommentType typeDataTypeInDocComment;
    private NumericValue typeNumericValue;
    private OperatorSign typeOperatorSign;
    private PhpVariableOrArray typePhpVariableOrArray;
    private RgbColor typeRgbColor;
    private RomanNumber typeRomanNumber;
    private SizzleSelector typeSizzleSelector;
    private MonoCharacter typeMonoCharacterString;
    private Tupel wordsTupel;
    private QuotedString typeQuotedString;

    ShiftableTypeAbstract getShiftableType(ActionContainer actionContainer) {
        ShiftableTypeAbstract shiftableType;

        //noinspection LoopStatementThatDoesntLoop
        while (true) {
            if (null != (shiftableType = new TrailingComment(actionContainer).getShiftableType())) break;

            // PHP doc param line is handled in caretLine-shifting fallback
            if (PhpDocParam.isPhpDocParamLine(actionContainer.caretLine) &&
                !PhpDocParam.containsDataType(actionContainer.caretLine)) return null;

            if (null != (shiftableType = new PhpVariableOrArray(actionContainer).getShiftableType())) break;
            if (null != (shiftableType = new Parenthesis(actionContainer).getShiftableType())) break;
            if (null != (shiftableType = new JsVariablesDeclarations(actionContainer).getShiftableType())) break;
            if (null != (shiftableType = new SizzleSelector(actionContainer).getShiftableType())) break;
            if (null != (shiftableType = new DocCommentType(actionContainer).getShiftableType())) break;
            if (null != (shiftableType = new AccessType(actionContainer).getShiftableType())) break;
            if (null != (shiftableType = new DictionaryTerm(actionContainer).getShiftableType())) break;
            if (null != (shiftableType = new JqueryObserver(actionContainer).getShiftableType())) break;
            if (null != (shiftableType = new TernaryExpression(actionContainer).getShiftableType())) break;
            if (null != (shiftableType = new QuotedString(actionContainer).getShiftableType())) break;
            if (null != (shiftableType = new RgbColor(actionContainer).getShiftableType())) break;
            if (null != (shiftableType = new CssUnit(actionContainer).getShiftableType())) break;
            if (null != (shiftableType = new NumericValue(actionContainer).getShiftableType())) break;
            if (null != (shiftableType = new OperatorSign(actionContainer).getShiftableType())) break;

            // @todo 1. convert all shiftable types and add them here

            // @todo 2. completely remove getWordType() when 1. is done

            // @todo 3. rework: remove redundant arguments (e.g. from getShifted())

            // @todo 4. make shifting context flexible: actionContainer.selectedText or textAroundCaret / etc.

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
        ShiftableTypeAbstract shiftableType;
        // Selected code line w/ trailing //-comment: moves the comment into a new caretLine before the code
        if (null != new TrailingComment(actionContainer).getShiftableType()) return TRAILING_COMMENT;

        if (PhpDocParam.isPhpDocParamLine(actionContainer.caretLine) &&
            !PhpDocParam.containsDataType(actionContainer.caretLine)) {
//            return TYPE_PHP_DOC_PARAM_LINE;
            // PHP doc param line is handled in caretLine-shifting fallback
            return UNKNOWN;
        }

        if (null != new PhpVariableOrArray(actionContainer).getShiftableType()) return PHP_VARIABLE_OR_ARRAY;
        if (null != new Parenthesis(actionContainer).getShiftableType()) return PARENTHESIS;
        if (null != new JsVariablesDeclarations(actionContainer).getShiftableType()) return JS_VARIABLES_DECLARATIONS;
        if (null != new SizzleSelector(actionContainer).getShiftableType()) return SIZZLE_SELECTOR;

        // DocComment shiftable_types (must be prefixed w/ "@")
        typeDataTypeInDocComment = new DocCommentType(actionContainer);
        if (typeDataTypeInDocComment.isDocCommentTypeLineContext(actionContainer.caretLine)) {
            typeTagInDocComment = new DocCommentTag(actionContainer);
            if (prefixChar.matches("@") && null != typeTagInDocComment.getShiftableType()) return DOC_COMMENT_TAG;
            if (null != typeDataTypeInDocComment.getShiftableType()) return DOC_COMMENT_DATA_TYPE;
        }

        // Object visibility
        if (!"@".equals(prefixChar) && null != new AccessType(actionContainer).getShiftableType()) return ACCESS_TYPE;

        // File extension specific term in dictionary
        typeDictionaryTerm = new DictionaryTerm(actionContainer);
        String fileExtension    = UtilsFile.extractFileExtension(actionContainer.filename);
        if (null != fileExtension) {
            if (typeDictionaryTerm.isInFileTypeDictionary(word, fileExtension)) return DICTIONARY_WORD_EXT_SPECIFIC;
            if (null != new JqueryObserver(actionContainer).getShiftableType()) return JQUERY_OBSERVER;
        }

        if (null != new TernaryExpression(actionContainer).getShiftableType()) return TERNARY_EXPRESSION;
        if (null != new QuotedString(actionContainer).getShiftableType()) return QUOTED_STRING;
        if (null != new RgbColor(actionContainer).getShiftableType()) return RGB_COLOR;
        if (null != new CssUnit(actionContainer).getShiftableType()) return CSS_UNIT;
        if (null != new NumericValue(actionContainer).getShiftableType()) return NUMERIC_VALUE;
        if (null != new OperatorSign(actionContainer).getShiftableType()) return OPERATOR_SIGN;

        if (RomanNumber.isRomanNumber(word)) {
            typeRomanNumber = new RomanNumber();
            return ROMAN_NUMERAL;
        }
        if (LogicalOperator.isLogicalOperator(word)) {
            // Logical operators "&&" and "||" must be detected before MonoCharStrings to avoid confusing
            return LOGICAL_OPERATOR;
        }

        if (MonoCharacter.isMonoCharacterString(word)) {
            typeMonoCharacterString = new MonoCharacter();
            return MONO_CHARACTER;
        }

        // Term in dictionary (anywhere, that is w/o limiting to the current file extension)
        if (null != typeDictionaryTerm.getShiftableType()) return DICTIONARY_WORD_GLOBAL;

        if (NumericPostfixed.hasNumericPostfix(word)) return NUMERIC_POSTFIXED;

        wordsTupel = new Tupel(actionContainer);
        if (wordsTupel.isWordsTupel(word)) return WORDS_TUPEL;

        if (SeparatedPath.isSeparatedPath(word)) return SEPARATED_PATH;

        if (CamelCaseString.isCamelCase(word)) return CAMEL_CASED;

        if (HtmlEncodable.isHtmlEncodable(word)) return HTML_ENCODABLE;

        return UNKNOWN;
    }

    ShiftableTypes.Type getWordType(ActionContainer actionContainer) {
        if (null == actionContainer.editorText) return UNKNOWN;

        int editorTextLength = actionContainer.editorText.length();
        int offsetPostfixChar = actionContainer.caretOffset + actionContainer.selectedText.length();
        String postfixChar = editorTextLength > offsetPostfixChar
                ? String.valueOf(actionContainer.editorText.charAt(offsetPostfixChar))
                : "";
        boolean isLastLineInDocument = offsetPostfixChar == editorTextLength;

        return getWordType(actionContainer.selectedText, "", postfixChar, isLastLineInDocument, actionContainer);
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
            // String based word shiftable_types
            case ACCESS_TYPE:
                return accessType.getShifted(word, actionContainer);
            case DICTIONARY_WORD_GLOBAL:
            case DICTIONARY_WORD_EXT_SPECIFIC:
                // The dictionary stored the matching terms-line, we don't need to differ global/ext-specific anymore
                return typeDictionaryTerm.getShifted(word, actionContainer);
            // Generic shiftable_types (shifting is calculated)
            case SIZZLE_SELECTOR:
                return typeSizzleSelector.getShifted(word, actionContainer);
            case RGB_COLOR:
                return typeRgbColor.getShifted(word, actionContainer);
            case NUMERIC_VALUE:
                // Numeric values including UNIX and millisecond timestamps
                return typeNumericValue.getShifted(word, actionContainer);
            case CSS_UNIT:
                return typePixelValue.getShifted(word, actionContainer);
            case JQUERY_OBSERVER:
                JqueryObserver jqueryObserver = new JqueryObserver(actionContainer);
                return jqueryObserver.getShifted(word, actionContainer, null, null);
            case PHP_VARIABLE_OR_ARRAY:
                return typePhpVariableOrArray.getShifted(word, actionContainer, moreCount);
            case TERNARY_EXPRESSION:
                TernaryExpression ternaryExpression = new TernaryExpression(actionContainer);
                return ternaryExpression.getShifted(word);
            case QUOTED_STRING:
                return typeQuotedString.getShifted(word, actionContainer);
            case PARENTHESIS:
                Parenthesis parenthesis = new Parenthesis(actionContainer);
                return parenthesis.getShifted(word);
            case OPERATOR_SIGN:
                return typeOperatorSign.getShifted(word);
            case ROMAN_NUMERAL:
                return typeRomanNumber.getShifted(word, actionContainer.isShiftUp);
            case LOGICAL_OPERATOR:
                return LogicalOperator.getShifted(word);
            case MONO_CHARACTER:
                return typeMonoCharacterString.getShifted(word, actionContainer.isShiftUp);
            case DOC_COMMENT_TAG:
                actionContainer.textAfterCaret   = actionContainer.editorText.toString().substring(actionContainer.caretOffset);
                return typeTagInDocComment.getShifted(word, actionContainer, null);
            case DOC_COMMENT_DATA_TYPE:
                return typeDataTypeInDocComment.getShifted(word, actionContainer);
            case SEPARATED_PATH:
                return SeparatedPath.getShifted(word);
            case CAMEL_CASED:
                return CamelCaseString.getShifted(word);
            case HTML_ENCODABLE:
                return HtmlEncodable.getShifted(word);
            case NUMERIC_POSTFIXED:
                return NumericPostfixed.getShifted(word, actionContainer.isShiftUp);
            case WORDS_TUPEL:
                return wordsTupel.getShifted(word, true);
            default:
                return word;
        }
    }

    String getShiftedWord(ActionContainer actionContainer, @Nullable Integer moreCount) {
        actionContainer.shiftSelectedText = true;

        ShiftableTypeAbstract shiftableType = getShiftableType(actionContainer);
        if (null != shiftableType) {
            return shiftableType.getShifted(
                    actionContainer.selectedText,
                    actionContainer,
                    moreCount,
                    null);
        }

        wordType = getWordType(
                actionContainer.selectedText,
                "",
                "",
                false,
                actionContainer);

        return getShiftedWord(actionContainer, actionContainer.selectedText, wordType, moreCount);
    }

    String getActionText() {
        switch (wordType) {
            case CSS_UNIT:
                return CssUnit.ACTION_TEXT;
            case HTML_ENCODABLE:
                return HtmlEncodable.ACTION_TEXT;
            case JS_VARIABLES_DECLARATIONS:
                return JsVariablesDeclarations.ACTION_TEXT;
            case LOGICAL_OPERATOR:
                return LogicalOperator.ACTION_TEXT;
            case MONO_CHARACTER:
                return MonoCharacter.ACTION_TEXT;
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
    }
}