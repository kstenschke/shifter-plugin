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

	/**
	 * Check whether given String represents a number
	 *
	 * @param   str        String to be checked
	 * @return  Boolean.
	 */
	public Boolean isBooleanString(String str) {
		String regExPattern = "yes|no|true|false|ok|cancel|on|off";

		return (str.toLowerCase()).matches(regExPattern);
	}



	/**
	 * @param word    Word to get shifted
	 * @return        Shifting result
	 */
	public String getShifted(String word) {
		word  = word.toLowerCase();

		// true / false
		if( word.equals("true") ) {
			return "false";
		}
		if( word.equals("false") ) {
			return "true";
		}

		// YES / NO
		if( word.equals("yes") ) {
			return "no";
		}
		if( word.equals("no") ) {
			return "yes";
		}

		// ok / cancel
		if( word.equals("ok") ) {
			return "cancel";
		}
		if( word.equals("cancel") ) {
			return "ok";
		}

		// on / off
		if( word.equals("on") ) {
			return "off";
		}
		if( word.equals("off") ) {
			return "on";
		}

		return word;
	}

}
