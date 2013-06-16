/*
 * Copyright 2011-2013 Kay Stenschke
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

package com.kstenschke.shifter;

import com.intellij.openapi.editor.Editor;
import com.kstenschke.shifter.helpers.FileHelper;
import com.kstenschke.shifter.shiftertypes.*;

/**
 * Manager of "shiftable" word types - detects word type to evoke resp. shifting
 */
public class ShifterTypesManager {

	public static final int TYPE_UNKNOWN = 0;

	// Dictionary (list of strings) based types
	private static final int TYPE_ACCESSIBILITY = 1;
	private static final int TYPE_DICTIONARYWORD_EXTSPECIFIC = 2;
	private static final int TYPE_DICTIONARYWORD_GLOBAL = 3;

	// Generic types
	public static final int TYPE_QUOTEDSTRING = 50;
	private static final int TYPE_HTMLENCODABLESTRING = 51;
	private static final int TYPE_MONOCHARACTERSTRING = 52;
	private static final int TYPE_RGBCOLOR = 53;
	public static final int TYPE_CSS_LENGTH_VALUE = 54; // em, px, pt, cm, in
	private static final int TYPE_DOCCOMMENT_TAG = 55;
	private static final int TYPE_DOCCOMMENT_DATATYPE = 56;
	public static final int TYPE_PHPVARIABLE = 57;
	public static final int TYPE_NUMERIC_VALUE = 58;
	public static final int TYPE_NUMERIC_POSTFIX = 59;

	// Word type objects
	private StaticWordType wordTypeAccessibilities;
	private Dictionary typeDictionaryTerm;

	// Generic types (calculated when shifted)
	private QuotedString typeQuotedString;
	private StringMonoCharacter typeMonoCharacterString;
	private RbgColor typeRgbColor;
	private CssLengthValue typePixelValue;
	private NumericValue typeNumericValue;
	private PhpVariable typePhpVariable;
	private DocCommentTag typeTagInDocComment;
	private DocCommentType typeDataTypeInDocComment;

	/**
	 * Constructor
	 */
	public ShifterTypesManager() {

	}

	/**
	 * Detect word type (get the one with highest priority to be shifted) of given string
	 *
	 * @param   word         Word whose type shall be identified
	 * @param   prefixChar      Prefix character
	 * @param   postfixChar      Postfix character
	 * @param   line         Whole line the caret is in
	 * @param   filename      Name of edited file
	 * @return int
	 */
	public int getWordType(String word, String prefixChar, String postfixChar, String line, String filename) {
		// PHP variable (must be prefixed with "$")
		this.typePhpVariable = new PhpVariable();
		if (this.typePhpVariable.isPhpVariable(word)) return TYPE_PHPVARIABLE;

		// DocComment types (must be prefixed with "@")
		this.typeDataTypeInDocComment = new DocCommentType();
		boolean isDocCommentLineContext = this.typeDataTypeInDocComment.isDocCommentTypeLineContext(line);

		if (isDocCommentLineContext) {
			this.typeTagInDocComment = new DocCommentTag();

			if (prefixChar.matches("@") && this.typeTagInDocComment.isDocCommentTag(word, prefixChar, line)) {
				return TYPE_DOCCOMMENT_TAG;
			}

			if (this.typeDataTypeInDocComment.isDocCommentType(word, prefixChar, line)) return TYPE_DOCCOMMENT_DATATYPE;
		}

		// Object visibility
		if (!prefixChar.equals("@")) {
			String[] keywordsAccessibilities = {"public", "private", "protected"};
			this.wordTypeAccessibilities = new StaticWordType(TYPE_ACCESSIBILITY, keywordsAccessibilities);

			if (this.wordTypeAccessibilities.hasWord(word)) {
				return TYPE_ACCESSIBILITY;
			}
		}

		// File extension specific term in dictionary
		this.typeDictionaryTerm = new Dictionary();
		String fileExtension = FileHelper.extractFileExtension(filename);

		if (fileExtension != null
				  && this.typeDictionaryTerm.isTermInDictionary(word, fileExtension)
				  ) {
			return TYPE_DICTIONARYWORD_EXTSPECIFIC;
		}

		// Quoted (must be wrapped in single or double quotes or backticks)
		this.typeQuotedString = new QuotedString();
		if (this.typeQuotedString.isQuotedString(word, prefixChar, postfixChar)) return TYPE_QUOTEDSTRING;

		// RGB (must be prefixed with "#")
		this.typeRgbColor = new RbgColor();
		if (this.typeRgbColor.isRgbColorString(word, prefixChar)) return TYPE_RGBCOLOR;

		// Pixel value (must consist of numeric value followed by "px")
		this.typePixelValue = new CssLengthValue();
		if (CssLengthValue.isCssLengthValue(word)) return TYPE_CSS_LENGTH_VALUE;

		this.typeNumericValue = new NumericValue();
		if (NumericValue.isNumericValue(word)) return TYPE_NUMERIC_VALUE;

		// MonoCharString (= consisting from any amount of the same character)
		this.typeMonoCharacterString = new StringMonoCharacter();
		if (this.typeMonoCharacterString.isMonoCharacterString(word)) return TYPE_MONOCHARACTERSTRING;

		// Term in dictionary (anywhere, that is w/o limiting to the current file extension)
		if (this.typeDictionaryTerm.isTermInDictionary(word)) {
			return TYPE_DICTIONARYWORD_GLOBAL;
		}

		if (StringHtmlEncodable.isHtmlEncodable(word)) {
			return TYPE_HTMLENCODABLESTRING;
		}

		if (StringNumericPostfix.isNumericPostfix(word)) {
			return TYPE_NUMERIC_POSTFIX;
		}

		return TYPE_UNKNOWN;
	}

	/**
	 * Shift given word
	 * ShifterTypesManager: get next/previous keyword of given word group
	 * Generic: calculate shifted value
	 *
	 * @param   word          Word to be shifted
	 * @param   idWordType       Word type ID
	 * @param   isUp          Shift up or down?
	 * @param   editorText       Full text of currently edited document
	 * @param   caretOffset       Caret offset in document
	 * @param   filename       Filename of currently edited file
	 * @param   editor          Editor instance
	 * @return The shifted word
	 */
	public String getShiftedWord(String word, int idWordType, Boolean isUp, CharSequence editorText, int caretOffset, String filename, Editor editor) {
		switch (idWordType) {
			// ================== String based word types
			case TYPE_ACCESSIBILITY:
				return this.wordTypeAccessibilities.getShifted(word, isUp);

			case TYPE_DICTIONARYWORD_GLOBAL:
			case TYPE_DICTIONARYWORD_EXTSPECIFIC:
				// The dictionary stored the matching terms-line, we don't need to differ global/ext-specific anymore
				return this.typeDictionaryTerm.getShifted(word, isUp);

			// ================== Generic types (shifting is calculated)
			case TYPE_RGBCOLOR:
				return this.typeRgbColor.getShifted(word, isUp);

			case TYPE_NUMERIC_VALUE:
				return this.typeNumericValue.getShifted(word, isUp, editor);

			case TYPE_CSS_LENGTH_VALUE:
				return this.typePixelValue.getShifted(word, isUp);

			case TYPE_PHPVARIABLE:
				return this.typePhpVariable.getShifted(word, editorText, isUp);

			case TYPE_QUOTEDSTRING:
				return this.typeQuotedString.getShifted(word, editor, editorText, isUp);

			case TYPE_MONOCHARACTERSTRING:
				return this.typeMonoCharacterString.getShifted(word, isUp);

			case TYPE_DOCCOMMENT_TAG:
				String textAfterCaret = editorText.toString().substring(caretOffset);
				return this.typeTagInDocComment.getShifted(word, isUp, filename, textAfterCaret);

			case TYPE_DOCCOMMENT_DATATYPE:
				return this.typeDataTypeInDocComment.getShifted(word, editorText, isUp, filename);

			case TYPE_HTMLENCODABLESTRING:
				return StringHtmlEncodable.getShifted(word);

			case TYPE_NUMERIC_POSTFIX:
				return StringNumericPostfix.getShifted(word, isUp);
		}

		return word;
	}

}