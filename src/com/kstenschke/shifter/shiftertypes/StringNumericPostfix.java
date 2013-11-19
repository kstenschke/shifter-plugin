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

package com.kstenschke.shifter.shiftertypes;

/**
 * String with numeric postfix
 */
public class StringNumericPostfix {

	/**
	 * Constructor
	 */
	public StringNumericPostfix() {

	}

	/**
	 * @param word String to be analyzed
	 * @return Boolean.
	 */
	public static Boolean isNumericPostfix(String word) {
		return word.matches("^.+?\\d$");
	}

	/**
	 * Shift numeric postfix of string
	 *
	 * @param word Quoted word to be shifted
	 * @param isUp Shifting up or down?
	 * @return String
	 */
	public static String getShifted(String word, Boolean isUp) {
		int index;
		int startIndex = word.length()-1;
		for (index = startIndex; index >= 0 ; index--) {
			String curChar = word.substring(index, index+1);
			if(curChar.matches("\\d")) {
				break;
			}
		}

		String leadPart = word.substring(0, index);
		String numericPart = word.substring(index);

		int shiftedNumber = isUp ? Integer.parseInt(numericPart)+1 : Integer.parseInt(numericPart)-1;

		return leadPart + shiftedNumber;
	}

}
