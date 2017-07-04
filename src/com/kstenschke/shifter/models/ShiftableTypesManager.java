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

import com.intellij.openapi.editor.Editor;
import com.kstenschke.shifter.models.shiftableTypes.*;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.Nullable;

import static com.kstenschke.shifter.models.ShiftableTypes.Type.*;

/**
 * Manager of "shiftable" word shiftableTypes - detects word type to evoke resp. shifting
 */
public class ShiftableTypesManager {

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
     * @param  line                     Whole line the caret is in
     * @param  filename                 Name of edited file
     * @return int
     */
    public ShiftableTypes.Type getWordType(String word, String prefixChar, String postfixChar, boolean isLastLineInDocument, String line, String filename) {
        // Selected code line w/ trailing //-comment: moves the comment into a new line before the code
        if (com.kstenschke.shifter.models.shiftableTypes.TrailingComment.isTrailingComment(word, postfixChar, isLastLineInDocument)) {
            return TRAILING_COMMENT;
        }

        if (com.kstenschke.shifter.models.shiftableTypes.PhpDocParam.isPhpDocParamLine(line)
         && !com.kstenschke.shifter.models.shiftableTypes.PhpDocParam.containsDataType(line)) {
//            return TYPE_PHP_DOC_PARAM_LINE;
            // PHP doc param line is handled in line-shifting fallback
            return UNKNOWN;
        }
        // PHP variable (must be prefixed w/ "$")
        this.typePhpVariableOrArray = new PhpVariableOrArray();
        if (this.typePhpVariableOrArray.isPhpVariableOrArray(word)) {
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
        this.typeDataTypeInDocComment = new com.kstenschke.shifter.models.shiftableTypes.DocCommentType();
        if (DocCommentType.isDocCommentTypeLineContext(line)) {
            this.typeTagInDocComment = new com.kstenschke.shifter.models.shiftableTypes.DocCommentTag();
            if (prefixChar.matches("@") && this.typeTagInDocComment.isDocCommentTag(prefixChar, line)) {
                return DOC_COMMENT_TAG;
            }
            if (this.typeDataTypeInDocComment.isDocCommentType(prefixChar, line)) {
                return DOC_COMMENT_DATA_TYPE;
            }
        }

        // Object visibility
        if (!"@".equals(prefixChar) && this.isKeywordAccessType(word)) {
            return ACCESSIBILITY;
        }

        // File extension specific term in dictionary
        this.typeDictionaryTerm = new com.kstenschke.shifter.models.shiftableTypes.DictionaryTerm();
        String fileExtension    = UtilsFile.extractFileExtension(filename);
        if (fileExtension != null && this.typeDictionaryTerm.isTermInDictionary(word, fileExtension)) {
            return DICTIONARY_WORD_EXT_SPECIFIC;
        }

        // Ternary Expression - swap IF and ELSE
        if (com.kstenschke.shifter.models.shiftableTypes.TernaryExpression.isTernaryExpression(word, prefixChar)) {
            return TERNARY_EXPRESSION;
        }

        // Quoted (must be wrapped in single or double quotes or backticks)
        this.typeQuotedString = new com.kstenschke.shifter.models.shiftableTypes.QuotedString();
        if (this.typeQuotedString.isQuotedString(prefixChar, postfixChar)) {
            return QUOTED_STRING;
        }
        // RGB (must be prefixed w/ "#")
        if (com.kstenschke.shifter.models.shiftableTypes.RbgColor.isRgbColorString(word, prefixChar)) {
            this.typeRgbColor = new com.kstenschke.shifter.models.shiftableTypes.RbgColor();
            return RGB_COLOR;
        }
        // Pixel value (must consist of numeric value followed by "px")
        if (com.kstenschke.shifter.models.shiftableTypes.CssUnit.isCssUnitValue(word)) {
            this.typePixelValue = new com.kstenschke.shifter.models.shiftableTypes.CssUnit();
            return CSS_UNIT;
        }
        if (com.kstenschke.shifter.models.shiftableTypes.NumericValue.isNumericValue(word)) {
            this.typeNumericValue = new com.kstenschke.shifter.models.shiftableTypes.NumericValue();
            return NUMERIC_VALUE;
        }
        // Operator sign (<, >, +, -)
        if (com.kstenschke.shifter.models.shiftableTypes.OperatorSign.isOperatorSign(word)) {
            this.typeOperatorSign    = new com.kstenschke.shifter.models.shiftableTypes.OperatorSign();
            return OPERATOR_SIGN;
        }
        // Roman Numeral
        if (com.kstenschke.shifter.models.shiftableTypes.RomanNumber.isRomanNumber(word)) {
            this.typeRomanNumber    = new com.kstenschke.shifter.models.shiftableTypes.RomanNumber();
            return ROMAN_NUMERAL;
        }
        if (com.kstenschke.shifter.models.shiftableTypes.LogicalOperator.isLogicalOperator(word)) {
            // Logical operators "&&" and "||" must be detected before MonoCharStrings to avoid confusing
            return LOGICAL_OPERATOR;
        }
        // MonoCharString (= consisting from any amount of the same character)
        if (MonoCharacter.isMonoCharacterString(word)) {
            this.typeMonoCharacterString    = new MonoCharacter();
            return MONO_CHARACTER_STRING;
        }
        // Term in dictionary (anywhere, that is w/o limiting to the current file extension)
        if (this.typeDictionaryTerm.isTermInDictionary(word, false)) {
            return DICTIONARY_WORD_GLOBAL;
        }
        if (NumericPostfixed.hasNumericPostfix(word)) {
            return NUMERIC_POSTFIXED_STRING;
        }
        wordsTupel = new com.kstenschke.shifter.models.shiftableTypes.Tupel();
        if (wordsTupel.isWordsTupel(word)) {
            return WORDS_TUPEL;
        }
        if (MinusSeparatedPath.isMinusSeparatedPath(word)) {
            return MINUS_SEPARATED_PATH;
        }
        if (CamelCaseString.isCamelCase(word)) {
            return CAMEL_CASE_STRING;
        }
        if (HtmlEncodable.isHtmlEncodable(word)) {
            return HTML_ENCODABLE_STRING;
        }

        return UNKNOWN;
    }

    public ShiftableTypes.Type getWordType(String word, CharSequence editorText, int caretOffset, String filename) {
        String line = UtilsTextual.getLineAtOffset(editorText.toString(), caretOffset);

        int editorTextLength = editorText.length();
        int offsetPostfixChar = caretOffset + word.length();
        String postfixChar = editorTextLength > offsetPostfixChar
                ? String.valueOf(editorText.charAt(offsetPostfixChar))
                : "";
        boolean isLastLineInDocument = offsetPostfixChar == editorTextLength;

        return this.getWordType(word, "", postfixChar, isLastLineInDocument, line, filename);
    }

    /**
     * @param  word
     * @return boolean
     */
    private boolean isKeywordAccessType(String word) {
        String[] keywordsAccessType = {"public", "private", "protected"};
        this.wordTypeAccessibilities = new com.kstenschke.shifter.models.shiftableTypes.StaticWordType(keywordsAccessType);

        return this.wordTypeAccessibilities.hasWord(word);
    }

    /**
     * Shift given word
     * ShifterTypesManager: get next/previous keyword of given word group
     * Generic: calculate shifted value
     *
     * @param  word         Word to be shifted
     * @param  wordType     Shiftable word type
     * @param  isUp         Shift up or down?
     * @param  editorText   Full text of currently edited document
     * @param  caretOffset  Caret offset in document
     * @param  filename     Filename of currently edited file
     * @param  editor       Editor instance
     * @param  moreCount    Current "more" count, starting w/ 1. If non-more shift: null
     * @return              The shifted word
     */
    public String getShiftedWord(String word, ShiftableTypes.Type wordType, boolean isUp, CharSequence editorText, int caretOffset, Integer moreCount, String filename, @Nullable Editor editor) {
        switch (wordType) {
            // String based word shiftableTypes
            case ACCESSIBILITY:
                return this.wordTypeAccessibilities.getShifted(word, isUp);
            case DICTIONARY_WORD_GLOBAL:
            case DICTIONARY_WORD_EXT_SPECIFIC:
                // The dictionary stored the matching terms-line, we don't need to differ global/ext-specific anymore
                return this.typeDictionaryTerm.getShifted(word, isUp);

            // Generic shiftableTypes (shifting is calculated)
            case SIZZLE_SELECTOR:
                return com.kstenschke.shifter.models.shiftableTypes.SizzleSelector.getShifted(word);
            case RGB_COLOR:
                return this.typeRgbColor.getShifted(word, isUp);
            case NUMERIC_VALUE:
                // Numeric values including UNIX and millisecond timestamps
                return this.typeNumericValue.getShifted(word, isUp, editor, filename);
            case CSS_UNIT:
                return this.typePixelValue.getShifted(word, isUp);
            case PHP_VARIABLE_OR_ARRAY:
                return this.typePhpVariableOrArray.getShifted(word, editorText, isUp, moreCount);
            case TERNARY_EXPRESSION:
                return com.kstenschke.shifter.models.shiftableTypes.TernaryExpression.getShifted(word);
            case QUOTED_STRING:
                return this.typeQuotedString.getShifted(word, editorText, isUp);
            case PARENTHESIS:
                return Parenthesis.getShifted(word);
            case OPERATOR_SIGN:
                return this.typeOperatorSign.getShifted(word);
            case ROMAN_NUMERAL:
                return this.typeRomanNumber.getShifted(word, isUp);
            case LOGICAL_OPERATOR:
                return com.kstenschke.shifter.models.shiftableTypes.LogicalOperator.getShifted(word);
            case MONO_CHARACTER_STRING:
                return this.typeMonoCharacterString.getShifted(word, isUp);
            case DOC_COMMENT_TAG:
                String textAfterCaret   = editorText.toString().substring(caretOffset);
                return this.typeTagInDocComment.getShifted(word, isUp, filename, textAfterCaret);
            case DOC_COMMENT_DATA_TYPE:
                return this.typeDataTypeInDocComment.getShifted(word, isUp, filename);
            case MINUS_SEPARATED_PATH:
                return MinusSeparatedPath.getShifted(word);
            case CAMEL_CASE_STRING:
                return CamelCaseString.getShifted(word);
            case HTML_ENCODABLE_STRING:
                return HtmlEncodable.getShifted(word);
            case NUMERIC_POSTFIXED_STRING:
                return NumericPostfixed.getShifted(word, isUp);
            case WORDS_TUPEL:
                return wordsTupel.getShifted(word);
            default:
                return word;
        }
    }

    /**
     * @param  word
     * @param  isUp
     * @param  editorText
     * @param  caretOffset
     * @param  moreCount
     * @param  filename
     * @param  editor
     * @return String
     */
    public String getShiftedWord(String word, boolean isUp, CharSequence editorText, int caretOffset, @Nullable Integer moreCount, String filename, Editor editor) {
        String line    = UtilsTextual.getLineAtOffset(editorText.toString(), caretOffset);
        ShiftableTypes.Type wordType = this.getWordType(word, "", "", false, line, filename);

        return this.getShiftedWord(word, wordType, isUp, editorText, caretOffset, moreCount, filename, editor);
    }
}