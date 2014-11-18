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

package com.kstenschke.shifter.models.shiftertypes;

import com.kstenschke.shifter.utils.UtilsTextual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PHP Variable (word with $ prefix)
 */
public class PhpConcatenation {

	private boolean isPhpConcatenation = false;
	private boolean isConcatenatorWhitespaceWrapped = false;
	private String concatenatorWrap;

	private String[] parts;

	/**
	 * Constructor
	 */
	public PhpConcatenation(String str) {
		str = UtilsTextual.removeLineBreaks( str.trim() );
		this.parts = str.split("\\.");	//@todo	improve to ignore dots that are inlined inside single/double quoted strings

		if( parts.length == 2 ) {
			this.isPhpConcatenation = true;
			this.isConcatenatorWhitespaceWrapped = Character.isWhitespace( parts[1].charAt(0) ) && Character.isWhitespace( parts[0].charAt( parts[0].length()-1 ) );
			this.concatenatorWrap = String.valueOf( parts[1].charAt(0) );
		}
	}

	/**
	 * @return	boolean
	 */
	public boolean isPhpConcatenation() {
		return this.isPhpConcatenation;
	}

	/**
	 * @return	String
	 */
	public String getShifted() {
		String concatenation =
			  this.parts[1]
			+ (this.isConcatenatorWhitespaceWrapped ? this.concatenatorWrap : "")
			+ "."
			+ (this.isConcatenatorWhitespaceWrapped ? this.concatenatorWrap : "")
			+ this.parts[0];

		return concatenation.trim();
	}

}
