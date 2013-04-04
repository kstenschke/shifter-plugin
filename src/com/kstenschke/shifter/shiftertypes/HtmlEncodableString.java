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

import com.intellij.openapi.editor.Editor;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML encoded/encodable (=containing char(s) that be be encoded) String.
 */
public class HtmlEncodableString {

	/**
	 * Constructor
	 */
	public HtmlEncodableString() {

	}



	/**
	 * Check whether given character can be encoded to an HTML special char / or is already HTML encoded
	 *
	 * @param   str            String to be shifted currently
	 * @return  Boolean.
	 */
	public static Boolean isHTMLencodable(String str) {
		Integer strLenOriginal	= str.length();

		String encoded			= StringEscapeUtils.escapeHtml(str);
		String decoded			= StringEscapeUtils.unescapeHtml(str);
		Integer strLenEncoded	= encoded.length();
		Integer strLenDecoded	= decoded.length();

		return !strLenOriginal.equals(strLenEncoded) || !strLenOriginal.equals(strLenDecoded);
	}




	/**
	 * Shift to HTML encoded/decoded variant of given string
	 *
	 * @param   word           word to be shifted
	 * @return  String
	 */
	public static String getShifted(String word) {
		Integer strLenOriginal	= word.length();

		String decoded			= StringEscapeUtils.unescapeHtml(word);
		Integer strLenDecoded	= decoded.length();

		if( !strLenOriginal.equals(strLenDecoded) ) {
			return decoded;
		}

		String encoded			= StringEscapeUtils.escapeHtml(word);
		Integer strLenEncoded	= encoded.length();

		if( !strLenOriginal.equals(strLenEncoded) ) {
			return encoded;
		}

		return word;
	}

}