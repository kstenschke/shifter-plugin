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
 * BooleanString class
 */
public class BooleanString {

	private String[] typesJavaScript;



	/**
	 * Constructor
	 */
	public BooleanString() {

	}



	/**
	 * Check whether given string represents any of the known boolean keyword data pairs
	 *
	 * @param   str        String to be checked
	 * @return  Boolean.
	 */
	public Boolean isBooleanString(String str) {
		String regExPattern = "yes|no" + "|true|false" + "|ok|cancel" + "|on|off" + "|shown|hidden"
							+ "|positive|negative" + "|from|until" + "|enable|disable|" + "|enabled|disabled"
							+ "|pass|fail" + "|min|max";

		return (str.toLowerCase()).matches(regExPattern);
	}



	/**
	 * @param word    Word to get shifted
	 * @return        Shifting result
	 */
	public String getShifted(String word) {
		word  = word.toLowerCase();

		String toggled;

			// true / false
		toggled = this.toggleBoolean(word, "true", "false");
		if( toggled != null ) return toggled;

			// yes / no
		toggled = this.toggleBoolean(word, "yes", "no");
		if( toggled != null ) return toggled;

			// ok / cancel
		toggled = this.toggleBoolean(word, "ok", "cancel");
		if( toggled != null ) return toggled;

			// on / off
		toggled = this.toggleBoolean(word, "on", "off");
		if( toggled != null ) return toggled;

			// shown / hidden
		toggled = this.toggleBoolean(word, "shown", "hidden");
		if( toggled != null ) return toggled;

			// positive / negative
		toggled = this.toggleBoolean(word, "positive", "negative");
		if( toggled != null ) return toggled;

			// from / until
		toggled = this.toggleBoolean(word, "from", "until");
		if( toggled != null ) return toggled;

			// enable / disable
		toggled = this.toggleBoolean(word, "enable", "disable");
		if( toggled != null ) return toggled;

		// enabled / disabled
		toggled = this.toggleBoolean(word, "enabled", "disabled");
		if( toggled != null ) return toggled;

			// pass / fail
		toggled = this.toggleBoolean(word, "pass", "fail");
		if( toggled != null ) return toggled;

			// min / max
		toggled = this.toggleBoolean(word, "min", "max");
		if( toggled != null ) return toggled;


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