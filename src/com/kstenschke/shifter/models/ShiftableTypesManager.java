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
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.Nullable;

import static com.kstenschke.shifter.models.ShiftableTypes.Type.*;

/**
 * Manager of "shiftable" word shiftableTypes - detects word type to evoke resp. shifting
 */
class ShiftableTypesManager {

    // Word type objects
    private com.kstenschke.shifter.models.shiftableTypes.StaticWordType wordTypeAccessibilities;
    private com.kstenschke.shifter.models.shiftableTypes.DictionaryTerm typeDictionaryTerm;

    // Generic shiftableTypes (calculated when shifted)
    private com.kstenschke.shifter.models.shiftableTypes.CssUnit typePixelValue;
    private com.kstenschke.shifter.models.shiftableTypes.DocCommentTag typeTagInDocComment;
    private com.kstenschke.shifter.models.shiftableTypes.DocCommentType typeDataTypeInDocComment;
    private com.kstenschke.shifter.models.shiftableTypes.NumericValue typeNumericValue;
    private com.kstenschke.shifter.models.shiftableTypes.OperatorSign typeOperatorSign;
    private PhpVariableOrArray typePhpVariableOrArray;
    private com.kstenschke.shifter.models.shiftableTypes.RbgColor typeRgbColor;
    private com.kstenschke.shifter.models.shiftableTypes.RomanNumber typeRomanNumber;
    private MonoCharacter typeMonoCharacterString;
    private com.kstenschke.shifter.models.shiftableTypes.Tupel wordsTupel;
    private com.kstenschke.shifter.models.shiftableTypes.QuotedString typeQuotedString;

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
    public ShiftableTypes.Type getWordType(String word, String prefixChar, String postfixChar, boolean isLastLineInDocument, ActionContainer actionContainer) {
        // Selected code line w/ trailing //-comment: moves the comment into a new caretLine before the code
        if (com.kstenschke.shifter.models.shiftableTypes.TrailingComment.isTrailingComment(word, postfixChar, isLastLineInDocument)) {
            return TRAILING_COMMENT;
        }

        if (com.kstenschke.shifter.models.shiftableTypes.PhpDocParam.isPhpDocParamLine(actionContainer.caretLine)
         && !com.kstenschke.shifter.models.shiftableTypes.PhpDocParam.containsDataType(actionContainer.caretLine)) {
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

        if (com.kstenschke.shifter.models.shiftableTypes.JsVariablesDeclarations.isJsVariables(word)) {
            return JS_VARIABLES_DECLARATIONS;
        }
        if (com.kstenschke.shifter.models.shiftableTypes.SizzleSelector.isSelector(word)) {
            return SIZZLE_SELECTOR;
        }

        // DocComment shiftableTypes (must be prefixed w/ "@")
        typeDataTypeInDocComment = new com.kstenschke.shifter.models.shiftableTypes.DocCommentType();
        if (DocCommentType.isDocCommentTypeLineContext(actionContainer.caretLine)) {
            typeTagInDocComment = new com.kstenschke.shifter.models.shiftableTypes.DocCommentTag();
            if (prefixChar.matches("@") && typeTagInDocComment.isDocCommentTag(prefixChar, actionContainer.caretLine)) {
                return DOC_COMMENT_TAG;
            }
            if (typeDataTypeInDocComment.isDocCommentType(prefixChar, actionContainer.caretLine)) {
                return DOC_COMMENT_DATA_TYPE;
            }
        }

        // Object visibility
        if (!"@".equals(prefixChar) && isKeywordAccessType(word)) {
            return ACCESSIBILITY;
        }

        // File extension specific term in dictionary
        typeDictionaryTerm = new com.kstenschke.shifter.models.shiftableTypes.DictionaryTerm();
        String fileExtension    = UtilsFile.extractFileExtension(actionContainer.filename);
        if (null != fileExtension && typeDictionaryTerm.isTermInDictionary(word, fileExtension)) {
            return DICTIONARY_WORD_EXT_SPECIFIC;
        }

        // Ternary Expression - swap IF and ELSE
        if (com.kstenschke.shifter.models.shiftableTypes.TernaryExpression.isTernaryExpression(word, prefixChar)) {
            return TERNARY_EXPRESSION;
        }

        // Quoted (must be wrapped in single or double quotes or backticks)
        typeQuotedString = new com.kstenschke.shifter.models.shiftableTypes.QuotedString();
        if (typeQuotedString.isQuotedString(prefixChar, postfixChar)) {
            return QUOTED_STRING;
        }
        // RGB (must be prefixed w/ "#")
        if (com.kstenschke.shifter.models.shiftableTypes.RbgColor.isRgbColorString(word, prefixChar)) {
            typeRgbColor = new com.kstenschke.shifter.models.shiftableTypes.RbgColor();
            return RGB_COLOR;
        }
        // Pixel value (must consist of numeric value followed by "px")
        if (com.kstenschke.shifter.models.shiftableTypes.CssUnit.isCssUnitValue(word)) {
            typePixelValue = new com.kstenschke.shifter.models.shiftableTypes.CssUnit();
            return CSS_UNIT;
        }
        if (com.kstenschke.shifter.models.shiftableTypes.NumericValue.isNumericValue(word)) {
            typeNumericValue = new com.kstenschke.shifter.models.shiftableTypes.NumericValue();
            return NUMERIC_VALUE;
        }
        // Operator sign (<, >, +, -)
        if (com.kstenschke.shifter.models.shiftableTypes.OperatorSign.isOperatorSign(word)) {
            typeOperatorSign = new com.kstenschke.shifter.models.shiftableTypes.OperatorSign();
            return OPERATOR_SIGN;
        }
        // Roman Numeral
        if (com.kstenschke.shifter.models.shiftableTypes.RomanNumber.isRomanNumber(word)) {
            typeRomanNumber = new com.kstenschke.shifter.models.shiftableTypes.RomanNumber();
            return ROMAN_NUMERAL;
        }
        if (com.kstenschke.shifter.models.shiftableTypes.LogicalOperator.isLogicalOperator(word)) {
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
        wordsTupel = new com.kstenschke.shifter.models.shiftableTypes.Tupel(actionContainer);
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

    public ShiftableTypes.Type getWordType(ActionContainer actionContainer) {
        String line = UtilsTextual.getLineAtOffset(actionContainer.editorText.toString(), actionContainer.caretOffset);

        int editorTextLength = actionContainer.editorText.length();
        int offsetPostfixChar = actionContainer.caretOffset + actionContainer.selectedText.length();
        String postfixChar = editorTextLength > offsetPostfixChar
                ? String.valueOf(actionContainer.editorText.charAt(offsetPostfixChar))
                : "";
        boolean isLastLineInDocument = offsetPostfixChar == editorTextLength;

        return getWordType(actionContainer.selectedText, "", postfixChar, isLastLineInDocument, actionContainer);
    }

    private boolean isKeywordAccessType(String word) {
        String[] keywordsAccessType = {"public", "private", "protected"};
        wordTypeAccessibilities = new com.kstenschke.shifter.models.shiftableTypes.StaticWordType(keywordsAccessType);

        return wordTypeAccessibilities.hasWord(word);
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
    String getShiftedWord(ActionContainer actionContainer, String word, ShiftableTypes.Type wordType, Integer moreCount) {
        switch (wordType) {
            // String based word shiftableTypes
            case ACCESSIBILITY:
                return wordTypeAccessibilities.getShifted(word, actionContainer.isShiftUp);
            case DICTIONARY_WORD_GLOBAL:
            case DICTIONARY_WORD_EXT_SPECIFIC:
                // The dictionary stored the matching terms-line, we don't need to differ global/ext-specific anymore
                return typeDictionaryTerm.getShifted(word, actionContainer.isShiftUp);

            // Generic shiftableTypes (shifting is calculated)
            case SIZZLE_SELECTOR:
                return com.kstenschke.shifter.models.shiftableTypes.SizzleSelector.getShifted(word);
            case RGB_COLOR:
                return typeRgbColor.getShifted(word, actionContainer.isShiftUp);
            case NUMERIC_VALUE:
                // Numeric values including UNIX and millisecond timestamps
                return typeNumericValue.getShifted(word, actionContainer);
            case CSS_UNIT:
                return typePixelValue.getShifted(word, actionContainer.isShiftUp);
            case PHP_VARIABLE_OR_ARRAY:
                return typePhpVariableOrArray.getShifted(word, actionContainer, moreCount);
            case TERNARY_EXPRESSION:
                return com.kstenschke.shifter.models.shiftableTypes.TernaryExpression.getShifted(word);
            case QUOTED_STRING:
                return typeQuotedString.getShifted(word, actionContainer);
            case PARENTHESIS:
                return Parenthesis.getShifted(word);
            case OPERATOR_SIGN:
                return typeOperatorSign.getShifted(word);
            case ROMAN_NUMERAL:
                return typeRomanNumber.getShifted(word, actionContainer.isShiftUp);
            case LOGICAL_OPERATOR:
                return com.kstenschke.shifter.models.shiftableTypes.LogicalOperator.getShifted(word);
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

    public String getShiftedWord(ActionContainer actionContainer, @Nullable Integer moreCount) {
        //String line                  = UtilsTextual.getLineAtOffset(actionContainer.editorText.toString(), actionContainer.caretOffset);
        ShiftableTypes.Type wordType = getWordType(actionContainer.selectedText, "", "", false, actionContainer);

        return getShiftedWord(actionContainer, actionContainer.selectedText, wordType, moreCount);
    }
}