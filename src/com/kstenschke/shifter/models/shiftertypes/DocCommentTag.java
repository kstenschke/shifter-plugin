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

import com.kstenschke.shifter.utils.UtilsArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DocCommentType class
 */
public class DocCommentTag {

	private final String[] tagsJavaScript;

	private final String[] tagsJava;

	private final String[] tagsPHP;

	/**
	 * Constructor
	 */
	public DocCommentTag() {
		tagsJavaScript = new String[]{ "author", "class", "constructor", "deprecated", "exception", "method", "module", "namespace", "param", "private", "property", "return", "see", "this", "throws", "type", "version"};
		tagsJava       = new String[]{ "author", "version", "param", "return", "exception", "throws", "see", "since", "serial", "deprecated"};
		tagsPHP        = new String[]{ "abstract", "access", "author", "constant", "deprecated", "final", "global", "magic", "module", "param", "package", "return", "see", "static", "subpackage", "throws", "todo", "var", "version" };
	}

	/**
	 * @return  Array       String array with all recognized doc comment tags
	 */
	private String[]   getAllTags() {
		return UtilsArray.mergeStringArrays(this.tagsJavaScript, this.tagsJava, this.tagsPHP);
	}

	/**
	 * @return  String      Pipe-separated list (as string) with all recognized doc comment tags
	 */
	public String   getAllTagsPiped() {
		String[] allTags = this.getAllTags();

		return UtilsArray.implode(allTags, "|");
	}

	/**
	 * Check whether given String looks like a doc comment line
	 *
	 * @param   line       Line the caret is at
	 * @return  boolean.
	 */
	private boolean isDocCommentLineContext(String line) {
		String allTagsPiped = this.getAllTagsPiped();
		String regExPatternLine = "\\s+\\*\\s+@(" + allTagsPiped + ")";

		List<String> allMatches = new ArrayList<String>();
		Matcher m = Pattern.compile(regExPatternLine).matcher(line.toLowerCase());
		while (m.find()) {
			if( !allMatches.contains(m.group())) {
				allMatches.add(m.group());
			}
		}

		return !allMatches.isEmpty();
	}

	/**
	 * @param   prefixChar  Prefix character
	 * @param   line        Whole line containing the word
	 * @return  boolean     Does the given String represent a data type (number / integer / string /...) from a doc comment (param / return /...)?
	 */
	public boolean isDocCommentTag(String prefixChar, String line) {
		return "@".equals(prefixChar) && this.isDocCommentLineContext(line);
	}

	/**
	 * @param   word              String to be shifted
	 * @param   isUp              Shift up or down?
	 * @param   filename          Filename of the edited file
	 * @param   textAfterCaret    Document text after the caret
	 * @return                    Shifting result
	 */
	public String getShifted(String word, boolean isUp, String filename, String textAfterCaret) {
		String[] commentTags   = this.getTagsByFilename(filename);
		int amountTags   = commentTags.length;

		if( amountTags > 0 ) {
			String wordLower  = word.toLowerCase();
			List<String> commentTagsList = Arrays.asList(commentTags);
			int curIndex   =  commentTagsList.indexOf(wordLower);

			if( curIndex > -1 ) {
				if( isUp ) {
					curIndex++;
					if( curIndex == amountTags ) {
						curIndex = 0;
					}
				} else {
					curIndex--;
					if( curIndex == -1 ) {
						curIndex = amountTags - 1;
					}
				}

				String shiftedWord   = commentTagsList.get(curIndex);

				if( "method".equals(shiftedWord) ) {
					shiftedWord = shiftedWord + parseNextMethod(textAfterCaret);
				}

				return shiftedWord;
			}
		}

		return word;
	}

	/**
	 * Find first JavaScript function's name out of given code
	 *
	 * @param   jsCode   JavaScript source code to be analyzed
	 * @return  String   JavaScript method name
	 */
	private String parseNextMethod(String jsCode) {
		List<String> allMatches = new ArrayList<String>();

		String regExPattern = "[a-zA-Z_$][0-9a-zA-Z_$]*\\s*:\\s*function";

		Matcher m = Pattern.compile(regExPattern).matcher(jsCode);
		while (m.find()) {
			if( !allMatches.contains(m.group())) {
				allMatches.add(m.group());
			}
		}

		String methodName  = "";

		if(!allMatches.isEmpty()) {
			methodName  = allMatches.get(0).replace("function", "").replace(":", "").trim();
			methodName  = "\t" + methodName;
		}

		return methodName;
	}

	/**
	 * Return array of data types of detected language of edited file
	 *
	 * @param   filename   Filename of edited file
	 * @return  String[]
	 */
	private String[] getTagsByFilename(String filename) {
		if( filename != null ) {
			String filenameLower = filename.toLowerCase();

			if( filenameLower.endsWith(".js") )  {
				// JavaScript comment types
				return this.tagsJavaScript;
			}

			if (filenameLower.endsWith(".java")) {
				// Java comment tags in the recommended order
				return this.tagsJava;
			}
		}

		return this.tagsPHP;
	}

}