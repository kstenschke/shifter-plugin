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
    private MonoCharacter typeMonoCharacterString;
    private Tupel wordsTupel;
    private QuotedString typeQuotedString;

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
        // Selected code line w/ trailing //-comment: moves the comment into a new caretLine before the code
        if (TrailingComment.isTrailingComment(word, postfixChar, isLastLineInDocument)) {
            return TRAILING_COMMENT;
        }

        if (PhpDocParam.isPhpDocParamLine(actionContainer.caretLine)
         && !PhpDocParam.containsDataType(actionContainer.caretLine)) {
//            return TYPE_PHP_DOC_PARAM_LINE;
            // PHP doc param line is handled in caretLine-shifting fallback
            return UNKNOWN;
        }
        // PHP variable (must be prefixed w/ "$")
        typePhpVariableOrArray = new PhpVariableOrArray();
        if (typePhpVariableOrArray.isPhpVariableOrArray(word)) {
            return PHP_VARIABLE_OR_ARRAY;
        }
        if (Parenthesis.isWrappedInParenthesis(word)) {
            return PARENTHESIS;
        }

        if (JsVariablesDeclarations.isJsVariables(word)) {
            return JS_VARIABLES_DECLARATIONS;
        }
        if (SizzleSelector.isSelector(word)) {
            return SIZZLE_SELECTOR;
        }

        // DocComment shiftable_types (must be prefixed w/ "@")
        typeDataTypeInDocComment = new DocCommentType();
        if (DocCommentType.isDocCommentTypeLineContext(actionContainer.caretLine)) {
            typeTagInDocComment = new DocCommentTag();
            if (prefixChar.matches("@")
                && typeTagInDocComment.isDocCommentTag(prefixChar, actionContainer.caretLine)
            ) {
                return DOC_COMMENT_TAG;
            }
            if (typeDataTypeInDocComment.isDocCommentType(prefixChar, actionContainer.caretLine)) {
                return DOC_COMMENT_DATA_TYPE;
            }
        }

        // Object visibility
        accessType = new AccessType();
        if (!"@".equals(prefixChar) && accessType.isAccessType(word)) {
            return ACCESS_TYPE;
        }

        // File extension specific term in dictionary
        typeDictionaryTerm = new DictionaryTerm();
        String fileExtension    = UtilsFile.extractFileExtension(actionContainer.filename);
        if (null != fileExtension) {
            if (typeDictionaryTerm.isTermInDictionary(word, fileExtension)) {
                return DICTIONARY_WORD_EXT_SPECIFIC;
            }
            if (
                UtilsFile.isJavaScriptFile(actionContainer.filename, true) &&
                JqueryObserver.isJQueryObserver(word)
            ) {
                    return JQUERY_OBSERVER;
            }
        }

        // Ternary Expression - swap IF and ELSE
        if (TernaryExpression.isTernaryExpression(word, prefixChar)) {
            return TERNARY_EXPRESSION;
        }

        // Quoted (must be wrapped in single or double quotes or backticks)
        typeQuotedString = new QuotedString();
        if (typeQuotedString.isQuotedString(prefixChar, postfixChar)) {
            return QUOTED_STRING;
        }
        // RGB (must be prefixed w/ "#")
        if (RgbColor.isRgbColorString(word, prefixChar)) {
            typeRgbColor = new RgbColor();
            return RGB_COLOR;
        }
        // Pixel value (must consist of numeric value followed by "px")
        if (CssUnit.isCssUnitValue(word)) {
            typePixelValue = new CssUnit();
            return CSS_UNIT;
        }
        if (NumericValue.isNumericValue(word)) {
            typeNumericValue = new NumericValue();
            return NUMERIC_VALUE;
        }
        // Operator sign (<, >, +, -)
        if (OperatorSign.isOperatorSign(word)) {
            typeOperatorSign = new OperatorSign();
            return OPERATOR_SIGN;
        }
        // Roman Numeral
        if (RomanNumber.isRomanNumber(word)) {
            typeRomanNumber = new RomanNumber();
            return ROMAN_NUMERAL;
        }
        if (LogicalOperator.isLogicalOperator(word)) {
            // Logical operators "&&" and "||" must be detected before MonoCharStrings to avoid confusing
            return LOGICAL_OPERATOR;
        }
        // MonoCharString (= consisting from any amount of the same character)
        if (MonoCharacter.isMonoCharacterString(word)) {
            typeMonoCharacterString = new MonoCharacter();
            return MONO_CHARACTER;
        }
        // Term in dictionary (anywhere, that is w/o limiting to the current file extension)
        if (typeDictionaryTerm.isTermInDictionary(word)) {
            return DICTIONARY_WORD_GLOBAL;
        }
        if (NumericPostfixed.hasNumericPostfix(word)) {
            return NUMERIC_POSTFIXED;
        }
        wordsTupel = new Tupel(actionContainer);
        if (wordsTupel.isWordsTupel(word)) {
            return WORDS_TUPEL;
        }
        if (SeparatedPath.isSeparatedPath(word)) {
            return SEPARATED_PATH;
        }
        if (CamelCaseString.isCamelCase(word)) {
            return CAMEL_CASED;
        }
        if (HtmlEncodable.isHtmlEncodable(word)) {
            return HTML_ENCODABLE;
        }

        return UNKNOWN;
    }

    ShiftableTypes.Type getWordType(ActionContainer actionContainer) {
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
                return accessType.getShifted(word, actionContainer.isShiftUp);
            case DICTIONARY_WORD_GLOBAL:
            case DICTIONARY_WORD_EXT_SPECIFIC:
                // The dictionary stored the matching terms-line, we don't need to differ global/ext-specific anymore
                return typeDictionaryTerm.getShifted(word, actionContainer.isShiftUp);
            // Generic shiftable_types (shifting is calculated)
            case SIZZLE_SELECTOR:
                return SizzleSelector.getShifted(word, actionContainer);
            case RGB_COLOR:
                return typeRgbColor.getShifted(word, actionContainer.isShiftUp);
            case NUMERIC_VALUE:
                // Numeric values including UNIX and millisecond timestamps
                return typeNumericValue.getShifted(word, actionContainer);
            case CSS_UNIT:
                return typePixelValue.getShifted(word, actionContainer.isShiftUp);
            case JQUERY_OBSERVER:
                return JqueryObserver.getShifted(word);
            case PHP_VARIABLE_OR_ARRAY:
                return typePhpVariableOrArray.getShifted(word, actionContainer, moreCount);
            case TERNARY_EXPRESSION:
                return TernaryExpression.getShifted(word);
            case QUOTED_STRING:
                return typeQuotedString.getShifted(word, actionContainer);
            case PARENTHESIS:
                return Parenthesis.getShifted(word);
            case OPERATOR_SIGN:
                return typeOperatorSign.getShifted(word);
            case ROMAN_NUMERAL:
                return typeRomanNumber.getShifted(word, actionContainer.isShiftUp);
            case LOGICAL_OPERATOR:
                return LogicalOperator.getShifted(word);
            case MONO_CHARACTER:
                return typeMonoCharacterString.getShifted(word, actionContainer.isShiftUp);
            case DOC_COMMENT_TAG:
                String textAfterCaret   = actionContainer.editorText.toString().substring(actionContainer.caretOffset);
                return typeTagInDocComment.getShifted(word, actionContainer, textAfterCaret);
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
        wordType = getWordType(actionContainer.selectedText, "", "", false, actionContainer);

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