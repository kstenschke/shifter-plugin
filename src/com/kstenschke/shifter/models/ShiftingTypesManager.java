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
import com.kstenschke.shifter.models.shiftertypes.*;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.Nullable;

/**
 * Manager of "shiftable" word types - detects word type to evoke resp. shifting
 */
public class ShiftingTypesManager {

    public static final int TYPE_UNKNOWN                        = 0;

    // Dictionary (list of strings) based types
    private static final int TYPE_ACCESSIBILITY                 = 1;
    private static final int TYPE_DICTIONARY_WORD_EXT_SPECIFIC  = 2;
    private static final int TYPE_DICTIONARY_WORD_GLOBAL        = 3;

    public static final int  TYPE_NUMERIC_VALUE                 = 10;
    private static final int TYPE_NUMERIC_POSTFIXED_STRING      = 11;
    public static final int  TYPE_ROMAN_NUMERAL                 = 12;

    // Generic types
    public static final int  TYPE_QUOTED_STRING                 = 20;
    private static final int TYPE_HTML_ENCODABLE_STRING         = 21;

    // <, >, +, -
    private static final int TYPE_OPERATOR_SIGN                 = 30;
    public static final int  TYPE_LOGICAL_OPERATOR              = 31;
    private static final int TYPE_MONO_CHARACTER_STRING         = 32;

    private static final int TYPE_RGB_COLOR                     = 40;
    // %, cm, em, in, pt, px, rem, vw, vh, vmin, vmax
    public static final int  TYPE_CSS_UNIT                      = 41;

    private static final int TYPE_DOC_COMMENT_TAG               = 50;
    private static final int TYPE_DOC_COMMENT_DATATYPE          = 51;

    public static final int  TYPE_PHP_VARIABLE                  = 60;
    public static final int  TYPE_JS_VARIABLES_DECLARATIONS     = 61;
    public static final int  TYPE_SIZZLE_SELECTOR               = 62;
    // @see trailing comment shifting is implemented in ActionsPerformer.shiftSelection()
    public static final int  TYPE_TRAILING_COMMENT              = 63;

    private static final int TYPE_TERNARY_EXPRESSION            = 70;
    private static final int TYPE_WORDS_TUPEL                   = 71;

    // Word type objects
    private com.kstenschke.shifter.models.shiftertypes.StaticWordType wordTypeAccessibilities;
    private DictionaryTerm typeDictionaryTerm;

    // Generic types (calculated when shifted)
    private com.kstenschke.shifter.models.shiftertypes.CssUnit typePixelValue;
    private com.kstenschke.shifter.models.shiftertypes.DocCommentTag typeTagInDocComment;
    private com.kstenschke.shifter.models.shiftertypes.DocCommentType typeDataTypeInDocComment;
    private com.kstenschke.shifter.models.shiftertypes.NumericValue typeNumericValue;
    private OperatorSign typeOperatorSign;
    private com.kstenschke.shifter.models.shiftertypes.PhpVariable typePhpVariable;
    private com.kstenschke.shifter.models.shiftertypes.RbgColor typeRgbColor;
    private RomanNumber typeRomanNumber;
    private StringMonoCharacter typeMonoCharacterString;
    private Tupel wordsTupel;
    private com.kstenschke.shifter.models.shiftertypes.QuotedString typeQuotedString;

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
    public int getWordType(String word, String prefixChar, String postfixChar, boolean isLastLineInDocument, String line, String filename) {
        // PHP variable (must be prefixed w/ "$")
        this.typePhpVariable = new com.kstenschke.shifter.models.shiftertypes.PhpVariable();
        if (this.typePhpVariable.isPhpVariable(word)) {
            return TYPE_PHP_VARIABLE;
        }

        if (JsVariablesDeclarations.isJsVariables(word)) {
            return TYPE_JS_VARIABLES_DECLARATIONS;
        }
        if (SizzleSelector.isSelector(word)) {
            return TYPE_SIZZLE_SELECTOR;
        }

        // DocComment types (must be prefixed w/ "@")
        this.typeDataTypeInDocComment   = new com.kstenschke.shifter.models.shiftertypes.DocCommentType();
        boolean isDocCommentLineContext = this.typeDataTypeInDocComment.isDocCommentTypeLineContext(line);
        if (isDocCommentLineContext) {
            this.typeTagInDocComment = new com.kstenschke.shifter.models.shiftertypes.DocCommentTag();
            if (prefixChar.matches("@") && this.typeTagInDocComment.isDocCommentTag(prefixChar, line)) {
                return TYPE_DOC_COMMENT_TAG;
            }
            if (this.typeDataTypeInDocComment.isDocCommentType(prefixChar, line)) {
                return TYPE_DOC_COMMENT_DATATYPE;
            }
        }

        // Object visibility
        if (!"@".equals(prefixChar) && this.isKeywordAccessType(word)) {
            return TYPE_ACCESSIBILITY;
        }

        // File extension specific term in dictionary
        this.typeDictionaryTerm = new DictionaryTerm();
        String fileExtension    = UtilsFile.extractFileExtension(filename);
        if (fileExtension != null && this.typeDictionaryTerm.isTermInDictionary(word, fileExtension)) {
            return TYPE_DICTIONARY_WORD_EXT_SPECIFIC;
        }

        // Ternary Expression - swap IF and ELSE
        if (TernaryExpression.isTernaryExpression(word, prefixChar)) {
            return TYPE_TERNARY_EXPRESSION;
        }

        // Selected code line w/ trailing //-comment: moves the comment into a new line before the code
        if (TrailingComment.isTrailingComment(word, postfixChar, isLastLineInDocument)) {
            return TYPE_TRAILING_COMMENT;
        }

        // Quoted (must be wrapped in single or double quotes or backticks)
        this.typeQuotedString = new com.kstenschke.shifter.models.shiftertypes.QuotedString();
        if (this.typeQuotedString.isQuotedString(prefixChar, postfixChar)) {
            return TYPE_QUOTED_STRING;
        }
        // RGB (must be prefixed w/ "#")
        if (com.kstenschke.shifter.models.shiftertypes.RbgColor.isRgbColorString(word, prefixChar)) {
            this.typeRgbColor = new com.kstenschke.shifter.models.shiftertypes.RbgColor();
            return TYPE_RGB_COLOR;
        }
        // Pixel value (must consist of numeric value followed by "px")
        if (com.kstenschke.shifter.models.shiftertypes.CssUnit.isCssUnitValue(word)) {
            this.typePixelValue = new com.kstenschke.shifter.models.shiftertypes.CssUnit();
            return TYPE_CSS_UNIT;
        }
        if (com.kstenschke.shifter.models.shiftertypes.NumericValue.isNumericValue(word)) {
            this.typeNumericValue = new com.kstenschke.shifter.models.shiftertypes.NumericValue();
            return TYPE_NUMERIC_VALUE;
        }
        // Operator sign (<, >, +, -)
        if (OperatorSign.isOperatorSign(word)) {
            this.typeOperatorSign    = new OperatorSign();
            return TYPE_OPERATOR_SIGN;
        }
        // Roman Numeral
        if (RomanNumber.isRomanNumber(word)) {
            this.typeRomanNumber    = new RomanNumber();
            return TYPE_ROMAN_NUMERAL;
        }
        if (LogicalOperator.isLogicalOperator(word)) {
            // Logical operators "&&" and "||" must be detected before MonoCharStrings to avoid confusing
            return TYPE_LOGICAL_OPERATOR;
        }
        // MonoCharString (= consisting from any amount of the same character)
        if (StringMonoCharacter.isMonoCharacterString(word)) {
            this.typeMonoCharacterString    = new StringMonoCharacter();
            return TYPE_MONO_CHARACTER_STRING;
        }
        // Term in dictionary (anywhere, that is w/o limiting to the current file extension)
        if (this.typeDictionaryTerm.isTermInDictionary(word, false)) {
            return TYPE_DICTIONARY_WORD_GLOBAL;
        }
        if (com.kstenschke.shifter.models.shiftertypes.StringNumericPostfix.isNumericPostfix(word)) {
            return TYPE_NUMERIC_POSTFIXED_STRING;
        }
        wordsTupel = new Tupel();
        if (wordsTupel.isWordsTupel(word)) {
            return TYPE_WORDS_TUPEL;
        }
        if (com.kstenschke.shifter.models.shiftertypes.StringHtmlEncodable.isHtmlEncodable(word)) {
            return TYPE_HTML_ENCODABLE_STRING;
        }

        return TYPE_UNKNOWN;
    }

    public int getWordType(String word, CharSequence editorText, int caretOffset, String filename) {
        String line = UtilsTextual.extractLineAroundOffset(editorText.toString(), caretOffset);

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
        this.wordTypeAccessibilities = new com.kstenschke.shifter.models.shiftertypes.StaticWordType(keywordsAccessType);

        return this.wordTypeAccessibilities.hasWord(word);
    }

    /**
     * Shift given word
     * ShifterTypesManager: get next/previous keyword of given word group
     * Generic: calculate shifted value
     *
     * @param  word         Word to be shifted
     * @param  idWordType   Word type ID
     * @param  isUp         Shift up or down?
     * @param  editorText   Full text of currently edited document
     * @param  caretOffset  Caret offset in document
     * @param  filename     Filename of currently edited file
     * @param  editor       Editor instance
     * @param  moreCount    Current "more" count, starting w/ 1. If non-more shift: null
     * @return              The shifted word
     */
        public String getShiftedWord(String word, int idWordType, boolean isUp, CharSequence editorText, int caretOffset, Integer moreCount, String filename, @Nullable Editor editor) {
            switch (idWordType) {
                // String based word types
                case TYPE_ACCESSIBILITY:
                    return this.wordTypeAccessibilities.getShifted(word, isUp);
                case TYPE_DICTIONARY_WORD_GLOBAL:
                case TYPE_DICTIONARY_WORD_EXT_SPECIFIC:
                    // The dictionary stored the matching terms-line, we don't need to differ global/ext-specific anymore
                    return this.typeDictionaryTerm.getShifted(word, isUp);

                // Generic types (shifting is calculated)
                case TYPE_SIZZLE_SELECTOR:
                    return SizzleSelector.getShifted(word);
                case TYPE_RGB_COLOR:
                    return this.typeRgbColor.getShifted(word, isUp);
                case TYPE_NUMERIC_VALUE:
                    // numeric values including UNIX and millisecond timestamps
                    return this.typeNumericValue.getShifted(word, isUp, editor);
                case TYPE_CSS_UNIT:
                    return this.typePixelValue.getShifted(word, isUp);
                case TYPE_PHP_VARIABLE:
                    return this.typePhpVariable.getShifted(word, editorText, isUp, moreCount);
                case TYPE_TERNARY_EXPRESSION:
                    return TernaryExpression.getShifted(word);
                case TYPE_QUOTED_STRING:
                    return this.typeQuotedString.getShifted(word, editorText, isUp);
                case TYPE_OPERATOR_SIGN:
                    return this.typeOperatorSign.getShifted(word);
                case TYPE_ROMAN_NUMERAL:
                    return this.typeRomanNumber.getShifted(word, isUp);
                case TYPE_LOGICAL_OPERATOR:
                    return LogicalOperator.getShifted(word);
                case TYPE_MONO_CHARACTER_STRING:
                    return this.typeMonoCharacterString.getShifted(word, isUp);
                case TYPE_DOC_COMMENT_TAG:
                    String textAfterCaret   = editorText.toString().substring(caretOffset);
                    return this.typeTagInDocComment.getShifted(word, isUp, filename, textAfterCaret);
                case TYPE_DOC_COMMENT_DATATYPE:
                    return this.typeDataTypeInDocComment.getShifted(word, isUp, filename);
                case TYPE_HTML_ENCODABLE_STRING:
                    return com.kstenschke.shifter.models.shiftertypes.StringHtmlEncodable.getShifted(word);
                case TYPE_NUMERIC_POSTFIXED_STRING:
                    return com.kstenschke.shifter.models.shiftertypes.StringNumericPostfix.getShifted(word, isUp);
                case TYPE_WORDS_TUPEL:
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
        String line    = UtilsTextual.extractLineAroundOffset(editorText.toString(), caretOffset);
        int idWordType = this.getWordType(word, "", "", false, line, filename);

        return this.getShiftedWord(word, idWordType, isUp, editorText, caretOffset, moreCount, filename, editor);
    }
}