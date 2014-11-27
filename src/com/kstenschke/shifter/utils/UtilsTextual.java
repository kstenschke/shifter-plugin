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
package com.kstenschke.shifter.utils;

import com.intellij.openapi.editor.Document;
import com.kstenschke.shifter.models.shiftertypes.OperatorSign;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Static helper methods for analysis and manipulation of texts
 */
public class UtilsTextual {

	/**
	 * @param   str         String to be checked
	 * @return  boolean     Is the given string fully lower case?
	 */
	public static boolean isAllUppercase(String str) {
		return str.equals( str.toUpperCase() );
	}

	/**
	 * @param   str         String to be checked
	 * @return  boolean     Is the given string is a comma separated list?
	 */
	public static boolean isCommaSeparatedList(String str) {
        if( ! str.contains(",") ) {
            return false;
        }

            // If the string is quoted: detect whether items are quoted each
            // => there must be (amountCommas+1)*2 quotes altogether
            // Ex:  "a","b"         => 1 comma, 4 quotes
            //      "a","b","c","d" => 3 commas, 8 quotes
            // Otherwise it should be treated as a quoted string and not as a list.
        if( isWrappedIntoQuotes(str) ) {
            String quoteChar    = str.substring(0, 1);
            int amountQuotes    = StringUtils.countMatches(str, quoteChar);
            int amountCommas    = StringUtils.countMatches(str, ",");

            if( amountQuotes != (amountCommas + 1) * 2 ) {
                return false;
            }
        }

        return true;
	}

    /**
     * @param   str
     * @return  boolean     Is the given string wrapped into single- or double quotes?
     */
    private static boolean isWrappedIntoQuotes(String str) {
        return isWrappedWith(str, "'") || isWrappedWith(str, "\"");
    }

    /**
     * @param   str
     * @param   wrap
     * @return  boolean Is the given string wrapped into the wrapper string?
     */
    private static boolean isWrappedWith(String str, String wrap) {
        return str.startsWith(wrap) && str.endsWith(wrap);
    }

	/**
	 * @param   str         String to be checked
	 * @return  boolean     Does the given string contain any slash or backslash?
	 */
	public static boolean containsAnySlashes(String str) {
		return str.contains("\\") || str.contains("/");
	}

	/**
	 * @param   str         String to be checked
	 * @return  boolean     Does the given string contain single or double quotes?
	 */
	public static boolean containsAnyQuotes(String str) {
		return str.contains("\"") || str.contains("'");
	}

	/**
	 * @param   str         String to be checked
	 * @return  String      Given string w/ contained slashes swapped against backslashes and vise versa
	 */
	public static String swapSlashes(String str) {
		str	= str.replace("\\", "###SHIFTERSLASH###");
		str	= str.replace("/", "\\");
		str	= str.replace("###SHIFTERSLASH###", "/");

		return str;
	}

	/**
	 * @param   str         String to be checked
	 * @return  String      Given string w/ contained single quotes swapped against double quotes and vise versa
	 */
	public static String swapQuotes(String str) {
		str	= str.replace("\"", "###SHIFTERSINGLEQUOTE###");
		str	= str.replace("'", "\"");
		str	= str.replace("###SHIFTERSINGLEQUOTE###", "'");

		return str;
	}

	/**
	 * @param   str      String to be converted
	 * @return  String   Given string converted to lower case with only first char in upper case
	 */
	public static String toUcFirst(String str) {
		return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
	}

	/**
	 * Check whether given string is lower case with only first char in upper case
	 *
	 * @param   str      String to be checked
	 * @return  boolean  If the string is lower case with only first char in upper case.
	 */
	public static boolean isUcFirst(String str) {
		return str.equals(UtilsTextual.toUcFirst(str));
	}

	/**
	 * @param	text
	 * @param	cursorOffset
	 * @return	Null|String
	 */
	public static String getOperatorAtOffset(CharSequence text, int cursorOffset) {
		int textLength = text.length();
		if (textLength == 0 || cursorOffset >= textLength) return null;

		String operatorToTheLeft = cursorOffset > 2
				? text.subSequence(cursorOffset - 2, cursorOffset + 1).toString()
				: null;

		if(	operatorToTheLeft != null && OperatorSign.isWhitespaceWrappedOperator(operatorToTheLeft) ) {
			return operatorToTheLeft.trim();
		}

		String operatorToTheRight = cursorOffset < textLength - 2 && (cursorOffset == 0 || Character.isWhitespace(text.charAt(cursorOffset-1)))
				? text.subSequence(cursorOffset - 1, cursorOffset + 2).toString()
				: null;

		if( operatorToTheRight != null && OperatorSign.isWhitespaceWrappedOperator(operatorToTheRight) ) {
			return operatorToTheRight.trim();
		}

			// No operator found
		return null;
	}

	public static Integer getStartOfOperatorAtOffset(CharSequence text, int cursorOffset) {
		int textLength = text.length();
		if (textLength == 0 || cursorOffset >= textLength) return null;

		String operatorToTheLeft = cursorOffset > 2
				? text.subSequence(cursorOffset - 2, cursorOffset + 1).toString()
				: null;

		if(	operatorToTheLeft != null && OperatorSign.isWhitespaceWrappedOperator(operatorToTheLeft) ) {
			return cursorOffset - 1;
		}

		String operatorToTheRight = cursorOffset < textLength - 2 && (cursorOffset == 0 || Character.isWhitespace(text.charAt(cursorOffset-1)))
				? text.subSequence(cursorOffset - 1, cursorOffset + 2).toString()
				: null;

		if( operatorToTheRight != null && OperatorSign.isWhitespaceWrappedOperator(operatorToTheRight) ) {
			return cursorOffset;
		}

		return null;
	}

	/**
	 * Get word at caret offset out of given text
	 *
	 * @param   text           The full text
	 * @param   cursorOffset   Character offset of caret
	 * @return                 The extracted word or null
	 */
	public static String getWordAtOffset(CharSequence text, int cursorOffset) {
		int textLength = text.length();

		if ( textLength == 0 || cursorOffset >= textLength )  return null;

		if (cursorOffset > 0
				  && !Character.isJavaIdentifierPart(text.charAt(cursorOffset))
				  && Character.isJavaIdentifierPart(text.charAt(cursorOffset - 1))
		) {
			cursorOffset--;
		}

		if (Character.isJavaIdentifierPart(text.charAt(cursorOffset))) {
			int start = cursorOffset;
			int end = cursorOffset;

			while (start > 0 && Character.isJavaIdentifierPart(text.charAt(start - 1))) {
				start--;
			}

			while (end < textLength && Character.isJavaIdentifierPart((text.charAt(end)))) {
				end++;
			}

			return text.subSequence(start, end).toString();
		}

		return null;
	}

	/**
	 * @param   text           Text containing the sequence
	 * @param   offsetStart    Sub sequence start character offset
	 * @param   offsetEnd      Sub sequence end character offset
	 * @return                 Sub sequence of given offsets out of given text
	 */
	public static String getSubString(CharSequence text, int offsetStart, int offsetEnd) {
		if (text.length() == 0) return null;

		return text.subSequence(offsetStart, offsetEnd).toString();
	}

	/**
	 * @param   text        Full text
	 * @param   offset      Offset from before which to extract one character
	 * @return  String      Character BEFORE word at given caret offset
	 */
	public static String getCharBeforeOffset(CharSequence text, int offset) {
		if (text.length() == 0 || offset == 0) return "";

		if (offset > 0 ) {
			return text.subSequence(offset-1, offset).toString();
		}

		return "";
	}

	/**
	 * @param   text        Full text
	 * @param   offset      Offset from after which to extract one character
	 * @return  String      Character AFTER word at caret offset
	 */
	public static String getCharAfterOffset(CharSequence text, int offset) {
		if (text.length() < offset+2 || offset == 0) return "";

		if (offset > 0 ) {
			return text.subSequence(offset+1, offset+2).toString();
		}

		return "";
	}

	/**
	 * @param   text		Text to be analyzed
	 * @param   offset		Character offset in text, intersecting the word dealing with
	 * @return  int         Starting position offset of word at given offset in given CharSequence
	 */
	public static int getStartOfWordAtOffset(CharSequence text, int offset) {
		if (text.length() == 0) return 0;

		if (offset > 0
				  && !Character.isJavaIdentifierPart(text.charAt(offset))
				  && Character.isJavaIdentifierPart(text.charAt(offset - 1))
				  ) {
			offset--;
		}

		if (Character.isJavaIdentifierPart(text.charAt(offset))) {
			int start = offset;

			while (start > 0 && Character.isJavaIdentifierPart(text.charAt(start - 1))) {
				start--;
			}

			return start;
		}

		return 0;
	}

	/**
	 * @param   doc			Document
	 * @param   startLine	Number of first line to be extracted
	 * @param   endLine     Number of last line of extract
	 * @return				Extracted list of lines
	 */
	public static List<String> extractLines(Document doc, int startLine, int endLine) {
		List<String> lines = new ArrayList<String>(endLine - startLine);

		for (int i = startLine; i <= endLine; i++) {
			String line = UtilsTextual.extractLine(doc, i);

			lines.add(line);
		}

		return lines;
	}

	/**
	 * @param   doc			Document to extract the line from
	 * @param   lineNumber	Number of line to be extracted
	 * @return	String		The extracted line
	 */
	public static String extractLine(Document doc, int lineNumber) {
		int lineSeparatorLength = doc.getLineSeparatorLength(lineNumber);

		int startOffset = doc.getLineStartOffset(lineNumber);
		int endOffset   = doc.getLineEndOffset(lineNumber) + lineSeparatorLength;

		String line = doc.getCharsSequence().subSequence(startOffset, endOffset).toString();

			// If last line has no \n, add it one
			// This causes adding a \n at the end of file when sort is applied on whole file and the file does not end
			// with \n... This is fixed after.
		if (lineSeparatorLength == 0) {
			line += "\n";
		}

		return line;
	}

	/**
	 * @param	text
	 * @param	offset
	 * @return	String
	 */
	public static String extractLineAroundOffset(String text, int offset) {
		int offsetStart = offset;
		while(offsetStart > 0 && text.charAt(offsetStart) != '\n') {
			offsetStart--;
		}

		int lenText	  = text.length();
		int offsetEnd = offset;
		while(offsetEnd < lenText && text.charAt(offsetStart) != '\n') {
			offsetEnd++;
		}
		if(offsetEnd < lenText) {
			offsetEnd--;
		}

		return text.substring(offsetStart, offsetEnd);
	}

	/**
	 * @param lines List of lines (strings) to be joined
	 * @return StringBuilder
	 */
	public static StringBuilder joinLines(List<String> lines) {
		StringBuilder builder = new StringBuilder();

		for (String line : lines) {
			builder.append(line);
		}

		return builder;
	}

	public static String removeLineBreaks(String str) {
		return str.replaceAll("\n", "").replaceAll("\r", "");
	}

	/**
	 * Replace last occurrence of "toReplace" with "replacement"
	 *
	 * @param	string
	 * @param	toReplace
	 * @param	replacement
	 * @return  string
	 */
	public static String replaceLast(String string, String toReplace, String replacement) {
		int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos)
					+ replacement
					+ string.substring(pos + toReplace.length(), string.length());
		} else {
			return string;
		}
	}

	/**
	 * @param   numberString
	 * @param   length
	 * @return  Given numerical string, with given length (if >= original length)
	 */
	public static String formatAmountDigits(String numberString, int length) {
		while(numberString.length() < length) {
			numberString = "0" + numberString;
		}

		return numberString;
	}

}
