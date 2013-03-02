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
import com.kstenschke.shifter.ShifterTypesManager;
import com.kstenschke.shifter.helpers.TextualHelper;

/**
 * Shiftable word
 */
public class ShiftableWord {

	private final ShifterTypesManager dictionary;

	private final String word;

	private final String filename;

	private final int wordType;

	private final Boolean isShiftable;

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
		this.dictionary = new ShifterTypesManager();

		this.word         = word;
		this.editorText   = editorText;
		this.caretOffset  = caretOffset;
		this.filename     = filename;

			// Detect word type
		this.wordType = dictionary.getWordType(word, prefixChar, postfixChar, line, filename);

			// Can the word be shifted?
		this.isShiftable = this.wordType != ShifterTypesManager.TYPE_UNKNOWN;
	}



	/**
	 * Get shifted up/down word
	 *
	 * @param	isUp	Shift up or down?
	 * @return			Next upper/lower word
	 */
	public String getShifted(Boolean isUp, Editor editor) {
		if (!this.isShiftable) {
			return this.word;
		}

			// Call actual shifting
		String shiftedWord = dictionary.getShiftedWord(this.word, this.wordType, isUp, this.editorText, this.caretOffset, filename, editor);

			// Keep original word casing
		if(      this.wordType != ShifterTypesManager.TYPE_PHPVARIABLE
			 &&   this.wordType != ShifterTypesManager.TYPE_QUOTEDSTRING
		) {
			if ( TextualHelper.isAllUppercase(this.word) ) {
					// Convert result to upper case
				shiftedWord = shiftedWord.toUpperCase();
			} else if (TextualHelper.isUcFirst(this.word)) {
					// Convert result to upper case first char
				shiftedWord = TextualHelper.toUcFirst(shiftedWord);
			}
		}

		return shiftedWord;
	}

}