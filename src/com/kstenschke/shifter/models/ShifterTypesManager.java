/*
 * Copyright 2011-2014 Kay Stenschke
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
import com.kstenschke.shifter.utils.UtilsFile;

/**
 * Manager of "shiftable" word types - detects word type to evoke resp. shifting
 */
public class ShifterTypesManager {

    public static final int TYPE_UNKNOWN = 0;

        // Dictionary (list of strings) based types
    private static final int TYPE_ACCESSIBILITY                 = 1;
    private static final int TYPE_DICTIONARY_WORD_EXT_SPECIFIC  = 2;
    private static final int TYPE_DICTIONARY_WORD_GLOBAL        = 3;

        // Generic types
    public static final int     TYPE_QUOTED_STRING            = 50;
    private static final int    TYPE_HTML_ENCODABLE_STRING    = 51;
    private static final int    TYPE_MONO_CHARACTER_STRING    = 52;
    private static final int    TYPE_RGB_COLOR                = 53;
    public static final int     TYPE_CSS_UNIT                 = 54; // em, px, pt, cm, in
    private static final int    TYPE_DOCCOMMENT_TAG           = 55;
    private static final int    TYPE_DOCCOMMENT_DATATYPE      = 56;
    public static final int     TYPE_PHP_VARIABLE             = 57;
    public static final int     TYPE_NUMERIC_VALUE            = 58;
    private static final int    TYPE_NUMERIC_POSTFIXED_STRING = 59;

        // Word type objects
    private com.kstenschke.shifter.models.shiftertypes.StaticWordType wordTypeAccessibilities;
    private com.kstenschke.shifter.models.shiftertypes.Dictionary typeDictionaryTerm;

        // Generic types (calculated when shifted)
    private com.kstenschke.shifter.models.shiftertypes.QuotedString typeQuotedString;
    private com.kstenschke.shifter.models.shiftertypes.StringMonoCharacter typeMonoCharacterString;
    private com.kstenschke.shifter.models.shiftertypes.RbgColor typeRgbColor;
    private com.kstenschke.shifter.models.shiftertypes.CssUnit typePixelValue;
    private com.kstenschke.shifter.models.shiftertypes.NumericValue typeNumericValue;
    private com.kstenschke.shifter.models.shiftertypes.PhpVariable typePhpVariable;
    private com.kstenschke.shifter.models.shiftertypes.DocCommentTag typeTagInDocComment;
    private com.kstenschke.shifter.models.shiftertypes.DocCommentType typeDataTypeInDocComment;

    /**
     * Constructor
     */
    public ShifterTypesManager() {

    }

    /**
     * Detect word type (get the one with highest priority to be shifted) of given string
     *
     * @param	word			Word whose type shall be identified
     * @param	prefixChar		Prefix character
     * @param	postfixChar		Postfix character
     * @param	line			Whole line the caret is in
     * @param	filename		Name of edited file
     * @return	int
     */
    public int getWordType(String word, String prefixChar, String postfixChar, String line, String filename) {
            // PHP variable (must be prefixed with "$")
        this.typePhpVariable = new com.kstenschke.shifter.models.shiftertypes.PhpVariable();
        if (this.typePhpVariable.isPhpVariable(word)) return TYPE_PHP_VARIABLE;

            // DocComment types (must be prefixed with "@")
        this.typeDataTypeInDocComment = new com.kstenschke.shifter.models.shiftertypes.DocCommentType();
        boolean isDocCommentLineContext   = this.typeDataTypeInDocComment.isDocCommentTypeLineContext(line);

        if( isDocCommentLineContext ) {
            this.typeTagInDocComment = new com.kstenschke.shifter.models.shiftertypes.DocCommentTag();

            if ( prefixChar.matches("@") && this.typeTagInDocComment.isDocCommentTag(prefixChar, line) ) {
                return TYPE_DOCCOMMENT_TAG;
            }

            if (this.typeDataTypeInDocComment.isDocCommentType(prefixChar, line)) return TYPE_DOCCOMMENT_DATATYPE;
        }

            // Object visibility
        if ( !prefixChar.equals("@") ) {
            if( this.isKeywordAccessType(word) ) {
                return TYPE_ACCESSIBILITY;
            }
        }

            // File extension specific term in dictionary
        this.typeDictionaryTerm = new com.kstenschke.shifter.models.shiftertypes.Dictionary();
        String fileExtension	= UtilsFile.extractFileExtension(filename);

        if( fileExtension != null && this.typeDictionaryTerm.isTermInDictionary(word, fileExtension) ) {
            return TYPE_DICTIONARY_WORD_EXT_SPECIFIC;
        }

            // Quoted (must be wrapped in single or double quotes or backticks)
        this.typeQuotedString = new com.kstenschke.shifter.models.shiftertypes.QuotedString();
        if (this.typeQuotedString.isQuotedString(prefixChar, postfixChar)) return TYPE_QUOTED_STRING;

            // RGB (must be prefixed with "#")
        this.typeRgbColor = new com.kstenschke.shifter.models.shiftertypes.RbgColor();
        if ( this.typeRgbColor.isRgbColorString(word, prefixChar) ) return TYPE_RGB_COLOR;

            // Pixel value (must consist of numeric value followed by "px")
        this.typePixelValue = new com.kstenschke.shifter.models.shiftertypes.CssUnit();
        if (com.kstenschke.shifter.models.shiftertypes.CssUnit.isCssUnitValue(word)) return TYPE_CSS_UNIT;

        this.typeNumericValue = new com.kstenschke.shifter.models.shiftertypes.NumericValue();
        if (com.kstenschke.shifter.models.shiftertypes.NumericValue.isNumericValue(word)) return TYPE_NUMERIC_VALUE;

            // MonoCharString (= consisting from any amount of the same character)
        this.typeMonoCharacterString	= new com.kstenschke.shifter.models.shiftertypes.StringMonoCharacter();
        if (this.typeMonoCharacterString.isMonoCharacterString(word)) return TYPE_MONO_CHARACTER_STRING;

            // Term in dictionary (anywhere, that is w/o limiting to the current file extension)
        if( this.typeDictionaryTerm.isTermInDictionary(word, false) ) {
            return TYPE_DICTIONARY_WORD_GLOBAL;
        }

        if( com.kstenschke.shifter.models.shiftertypes.StringHtmlEncodable.isHtmlEncodable(word) ) {
            return TYPE_HTML_ENCODABLE_STRING;
        }

        if( com.kstenschke.shifter.models.shiftertypes.StringNumericPostfix.isNumericPostfix(word)) {
            return TYPE_NUMERIC_POSTFIXED_STRING;
        }

        return TYPE_UNKNOWN;
    }

    /**
     * @param   word
     * @return  boolean
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
     * @param	word			      Word to be shifted
     * @param	idWordType		   Word type ID
     * @param	isUp			      Shift up or down?
     * @param	editorText		   Full text of currently edited document
     * @param	caretOffset		   Caret offset in document
     * @param	filename		      Filename of currently edited file
     * @param	editor			   Editor instance
     * @return					      The shifted word
     *
     */
    public String getShiftedWord(String word, int idWordType, boolean isUp, CharSequence editorText, int caretOffset, String filename, Editor editor) {
        switch (idWordType) {
                // ================== String based word types
            case TYPE_ACCESSIBILITY:
                return this.wordTypeAccessibilities.getShifted(word, isUp);

            case TYPE_DICTIONARY_WORD_GLOBAL:
            case TYPE_DICTIONARY_WORD_EXT_SPECIFIC:
                    // The dictionary stored the matching terms-line, we don't need to differ global/ext-specific anymore
                return this.typeDictionaryTerm.getShifted(word, isUp);

                // ================== Generic types (shifting is calculated)
            case TYPE_RGB_COLOR:
                return this.typeRgbColor.getShifted(word, isUp);

            case TYPE_NUMERIC_VALUE:
                return this.typeNumericValue.getShifted(word, isUp, editor);

            case TYPE_CSS_UNIT:
                return this.typePixelValue.getShifted(word, isUp);

            case TYPE_PHP_VARIABLE:
                return this.typePhpVariable.getShifted(word, editorText, isUp);

            case TYPE_QUOTED_STRING:
                return this.typeQuotedString.getShifted(word, editorText, isUp);

            case TYPE_MONO_CHARACTER_STRING:
                return this.typeMonoCharacterString.getShifted(word, isUp);

            case TYPE_DOCCOMMENT_TAG:
                String textAfterCaret   = editorText.toString().substring(caretOffset);
                return this.typeTagInDocComment.getShifted(word, isUp, filename, textAfterCaret);

            case TYPE_DOCCOMMENT_DATATYPE:
                return this.typeDataTypeInDocComment.getShifted(word, isUp, filename);

            case TYPE_HTML_ENCODABLE_STRING:
                return com.kstenschke.shifter.models.shiftertypes.StringHtmlEncodable.getShifted(word);

            case TYPE_NUMERIC_POSTFIXED_STRING:
                return com.kstenschke.shifter.models.shiftertypes.StringNumericPostfix.getShifted(word, isUp);
        }

        return word;
    }

}