/*
 * Copyright 2011-2015 Kay Stenschke
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
 * PHP Variable (word with $ prefix), includes array definition (toggle long versus shorthand syntax)
 */
public class PhpVariable {

	// Detected array? Shifts among long and shorthand than: array(...) <=> [...]
	private boolean isArray = false;

	private boolean isConventionalArray = false; // shorthand (since PHP5.4) or long syntax array?

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
		boolean isVariable = identifier.toLowerCase().matches("[a-zA-Z0-9_]+");

		if( ! isVariable ) {
			// Detect array definition
			this.isArray = this.isPhpArray(str);
		}

		return isVariable || this.isArray;
	}

	/**
	 * @param	str
	 * @return	Boolean
	 */
	private Boolean isPhpArray(String str) {
		this.isConventionalArray =     str.matches("(array\\s*\\()((.|\\n|\\r|\\s)*)(\\)(;)*)");
		boolean isShorthandArray = ! this.isConventionalArray && str.matches("(\\[)((.|\\n|\\r|\\s)*)(\\])");

		return this.isConventionalArray || isShorthandArray;
	}

	/**
	 * Shift PX value up/down by 16px
	 *
	 * @param	variable		Variable name string
	 * @param	editorText		Text of edited document
	 * @param	isUp			Shift up or down?
	 * @param	moreCount		Current "more" count, starting with 1. If non-more shift: null
	 * @return	String
	 */
	public String getShifted(String variable, CharSequence editorText, Boolean isUp, Integer moreCount) {
		if( this.isArray ) return getShiftedArray(variable);

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
		List<String> allLeadChars = null;
		if( moreCount != null && moreCount == 1) {
			// During "shift more": iterate over variables reduced to first per every lead-character
			allMatches 		= this.reducePhpVarsToFirstPerLeadChar(allMatches);
			allLeadChars	= this.getLeadChars(allMatches);
		}

		int amountVars = allMatches.size();

			// Find position of given variable
		Integer curIndex = (moreCount== null || moreCount > 1)
				? allMatches.indexOf(variable)
				: allLeadChars.indexOf(variable.substring(1, 2));

		if( curIndex == -1 || amountVars == 0 ) {
			return variable;
		}

			// Find next/previous variable name (only once during iterations of "shift more")
		if(moreCount == null || moreCount == 1) {
			if (isUp) {
				curIndex++;
				if (curIndex == amountVars) {
					curIndex = 0;
				}
			} else {
				curIndex--;
				if (curIndex == -1) {
					curIndex = amountVars - 1;
				}
			}
		}

		return allMatches.get(curIndex);
	}

	/**
	 * @param	allMatches
	 * @return
	 */
	private List<String> reducePhpVarsToFirstPerLeadChar(List<String> allMatches) {
		List<String> reducedMatches = new ArrayList<String>();
		String leadCharPrev = "";
		String leadCharCur;
		for(String currentMatch : allMatches) {
			leadCharCur = currentMatch.substring(1,2);
			if(!leadCharCur.matches(leadCharPrev)) {
				reducedMatches.add(currentMatch);
			}
			leadCharPrev = leadCharCur;
		}

		return reducedMatches;
	}

	/**
	 * @param	matches
	 * @return	List of first letters of given matches
	 */
	private List<String> getLeadChars(List<String> matches) {
		List<String> leadChars = new ArrayList<String>();

		for(String currentMatch : matches) {
			leadChars.add(currentMatch.substring(1,2));
		}

		return leadChars;
	}

	/**
	 * @param   variable
	 * @return	String		converted array(...) <=> [...]
	 */
	public String getShiftedArray(String variable) {
		if( this.isConventionalArray ) {
			variable = variable.replaceFirst("array", "[");
			variable = variable.replaceFirst("\\(", "");
			variable = UtilsTextual.replaceLast(variable, ")", "]");
		} else {
			variable = variable.replaceFirst("\\[", "array(");
			variable = UtilsTextual.replaceLast(variable, "]", ")");
		}

		return variable;
	}

}
