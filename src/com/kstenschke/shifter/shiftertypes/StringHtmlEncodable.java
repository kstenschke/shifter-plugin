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

package com.kstenschke.shifter.shiftertypes;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * HTML encoded/encodable (=containing char(s) that be be encoded) String.
 */
public class StringHtmlEncodable {

    /**
	 * Check whether given character can be encoded to an HTML special char / or is already HTML encoded
	 *
	 * @param str String to be shifted currently
	 * @return boolean.
	 */
	public static boolean isHtmlEncodable(String str) {
		Integer strLenOriginal = str.length();

		String encoded = StringEscapeUtils.escapeHtml(str);
		String decoded = StringEscapeUtils.unescapeHtml(str);

		return !strLenOriginal.equals(encoded.length()) || !strLenOriginal.equals(decoded.length());
	}

	/**
	 * Shift to HTML encoded/decoded variant of given string
	 *
	 * @param word word to be shifted
	 * @return String
	 */
	public static String getShifted(String word) {
		Integer strLenOriginal = word.length();

		String decoded = StringEscapeUtils.unescapeHtml(word);
		Integer strLenDecoded = decoded.length();

		if (!strLenOriginal.equals(strLenDecoded)) return decoded;

		String encoded = StringEscapeUtils.escapeHtml(word);
		Integer strLenEncoded = encoded.length();

		if (!strLenOriginal.equals(strLenEncoded)) return encoded;

		return word;
	}

}