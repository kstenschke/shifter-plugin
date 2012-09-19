/*
 * Copyright 2012 Kay Stenschke
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

/**
 * "BooleanString" class
 */
public class BooleanString {

	private String [] [] keywordPairs = {
		{"before", "after"},
		{"enable", "disable"},
		{"enabled", "disabled"},
		{"expand", "collapse"},
		{"from", "until"},
		{"horizontal", "vertical"},
		{"min", "max"},
		{"ok", "cancel"},
		{"on", "off"},
		{"pass", "fail"},
		{"positive", "negative"},
		{"show", "hide"},
		{"shown", "hidden"},
		{"true", "false"},
		{"yes", "no"}
	};

	private String regExPattern;


	/**
	 * Constructor
	 */
	public BooleanString() {
		regExPattern	= "|";

		for (String[] keywordPair : keywordPairs) {
			regExPattern = regExPattern + keywordPair[0] + "|" + keywordPair[1] + "|";
		}
	}



	/**
	 * Check whether given string represents any of the known boolean keyword data pairs
	 *
	 * @param   str        String to be checked
	 * @return  Boolean.
	 */
	public Boolean isBooleanString(String str) {

		return (str.toLowerCase()).matches(regExPattern);
	}



	/**
	 * @param word    Word to get shifted
	 * @return        Shifting result
	 */
	public String getShifted(String word) {
		word  = word.toLowerCase();

		String toggled;

		for (String[] keywordPair : keywordPairs) {
			toggled = this.toggleBoolean(word, keywordPair[0], keywordPair[1]);
			if( toggled != null ) return toggled;
		}

		return word;
	}



	/**
	 * Compare given string to given "positive" and "negative" keyword and return the opposite of the current or null if none matched
	 *
	 * @param	strCurrent		String to be matched and toggled
	 * @param	strPositive		Positive keyword
	 * @param	strNegative		Negative keyword
	 * @return					The toggled keyword if matched or null
	 */
	private String toggleBoolean(String strCurrent, String strPositive, String strNegative) {
		if( strCurrent.equals(strPositive) ) {
			return strNegative;
		} else if( strCurrent.equals(strNegative) ) {
			return strPositive;
		}

		return null;
	}

}