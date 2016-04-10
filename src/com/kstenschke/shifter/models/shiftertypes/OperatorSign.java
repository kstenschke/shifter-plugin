/*
 * Copyright 2011-2016 Kay Stenschke
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

/**
 * Mono-Character String = String that contains only one character (no matter how often)
 */
public class OperatorSign {

	/**
	 * @param word String to be shifted currently
	 * @return boolean.
	 */
	public static boolean isOperatorSign(String word) {
		return word != null && word.length() == 1 && "+-<>*/%".contains(word);
	}

	public static boolean isWhitespaceWrappedOperator(String str) {
		return Character.isWhitespace( str.charAt(0) )
				&& OperatorSign.isOperatorSign( String.valueOf(str.charAt(1)) )
				&& Character.isWhitespace( str.charAt(2) );
	}

	/**
	 * Shift mono-character string
	 *
	 * @param   word Quoted word to be shifted
	 * @return  String
	 */
	public String getShifted(String word) {
		if( "-".equals(word)) {
			return "+";
		}
		if( "+".equals(word)) {
			return "-";
		}
		if( "<".equals(word)) {
			return ">";
		}
		if( ">".equals(word)) {
			return "<";
		}
		if( "*".equals(word)) {
			return "/";
		}
		if( "/".equals(word)) {
			return "*";
		}

		return word;
	}

}
