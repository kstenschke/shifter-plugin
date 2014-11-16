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
import com.kstenschke.shifter.utils.UtilsTextual;

/**
 * Shiftable word
 */
public class ShiftableWord {

	private final ShifterTypesManager shifterTypesManager;
	private final String word;
	private final String filename;
	private final int wordType;
	private final boolean isShiftable;
	private final CharSequence editorText;
	private final int caretOffset;

	/**
	 * Constructor
	 *
	 * @param	word			Shiftable word
	 * @param	prefixChar		Char before the word, "#"?
	 * @param	postfixChar		Char after the word, "#"?
	 * @param	line			Whole line to possibly guess the context
	 * @param	editorText		Whole text currently in editor
	 * @param	caretOffset		Caret offset in document
	 * @param	filename		Filename of the edited file
	 */
	public ShiftableWord(String word, String prefixChar, String postfixChar, String line, CharSequence editorText, int caretOffset, String filename) {
		this.shifterTypesManager = new ShifterTypesManager();

		this.editorText   = editorText;
		this.caretOffset  = caretOffset;
		this.filename     = filename;

			// Detect word type
		this.wordType = shifterTypesManager.getWordType(word, prefixChar, postfixChar, line, filename);

			// Comprehend negative values of numeric types
		if( this.wordType == ShifterTypesManager.TYPE_CSS_UNIT || this.wordType == ShifterTypesManager.TYPE_NUMERIC_VALUE) {
			if( prefixChar.equals("-") ) {
				word = "-" + word;
			}
		}
		this.word   = word;

			// Can the word be shifted?
		this.isShiftable = this.wordType != ShifterTypesManager.TYPE_UNKNOWN;
	}

	public int getWordType() {
		return wordType;
	}

	/**
	 * Get shifted up/down word
	 *
	 * @param	isUp	Shift up or down?
	 * @return			Next upper/lower word
	 */
	public String getShifted(boolean isUp, Editor editor) {
		if (!this.isShiftable) {
			return this.word;
		}

			// Call actual shifting
		String shiftedWord = shifterTypesManager.getShiftedWord(this.word, this.wordType, isUp, this.editorText, this.caretOffset, filename, editor);

			// Keep original word casing
		if(      this.wordType != ShifterTypesManager.TYPE_PHP_VARIABLE
			 &&  this.wordType != ShifterTypesManager.TYPE_QUOTED_STRING
		) {
            if( ShifterPreferences.getIsActivePreserveCase() ) {
                if ( UtilsTextual.isAllUppercase(this.word) ) {
                        // Convert result to upper case
                    shiftedWord = shiftedWord.toUpperCase();
                } else if (UtilsTextual.isUcFirst(this.word)) {
                        // Convert result to upper case first char
                    shiftedWord = UtilsTextual.toUcFirst(shiftedWord);
                }
			}
		}

		return shiftedWord;
	}

	/**
	 * Post-process: do additional modifications on word after it has been shifted
	 *
	 * @param	word
	 * @return	Post-processed word
	 */
	public String postProcess(String word) {
		int wordType = this.getWordType();

		if(this.filename.toLowerCase().endsWith(".css")) {
			switch(wordType) {
				// "0" was shifted to a different numeric value, inside a CSS file, so we can add a measure unit
				case ShifterTypesManager.TYPE_NUMERIC_VALUE:
					word	+= "px";	//@todo	determine and use most prominent measure unit from current file
					break;

				case ShifterTypesManager.TYPE_CSS_UNIT:
					// Correct "0px" (or other unit) to "0"
					if( word.startsWith("0") ) {
						word = "0";
					}
					break;
			}
		}

		return word;
	}

}