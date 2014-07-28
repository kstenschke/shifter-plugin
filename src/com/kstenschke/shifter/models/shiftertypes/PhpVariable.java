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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PHP Variable (word with $ prefix)
 */
public class PhpVariable {

	/**
	 * Constructor
	 */
	public PhpVariable() {

	}

	/**
	 * Check whether given string represents a PHP variable
	 *
	 * @param	str			String to be checked
	 * @return	boolean.
	 */
	public Boolean isPhpVariable(String str) {
			// Must begin with "$"
		if ( ! str.startsWith("$") ) {
			return false;
		}

		String identifier = str.substring(1);
			// Must contain a-z,A-Z or 0-9, _
		return identifier.toLowerCase().matches("[a-zA-Z0-9_]+");
	}

	/**
	 * Shift PX value up/down by 16px
	 *
	 * @param	variable		Variable name string
	 * @param	editorText		Text of edited document
	 * @param	isUp			Shift up or down?
	 * @return	String
	 */
	public String getShifted(String variable, CharSequence editorText, Boolean isUp) {
         // Get full text of currently edited document
	   String text = editorText.toString();

			// Use regEx matcher to extract array of all PHP var names

		List<String> allMatches = new ArrayList<String>();
		Matcher m = Pattern.compile("\\$[a-zA-Z0-9_]+").matcher(text);
		while (m.find()) {
			if( !allMatches.contains(m.group())) {
				allMatches.add(m.group());
			}
		}

			// Sort var names alphabetically
		Collections.sort(allMatches);
		int amountVars = allMatches.size();

			// Find position of given variable
		int curIndex   = allMatches.indexOf(variable);

		if( curIndex == -1 || amountVars == 0 ) {
			return variable;
		}

			// Return next/previous variable name
		if( isUp ) {
			curIndex++;
			if( curIndex == amountVars ) {
				curIndex = 0;
			}
		} else {
			curIndex--;
			if( curIndex == -1 ) {
				curIndex = amountVars - 1;
			}
		}

		return allMatches.get(curIndex);
	}

}
