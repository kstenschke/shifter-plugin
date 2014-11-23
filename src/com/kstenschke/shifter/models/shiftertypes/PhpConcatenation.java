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
package com.kstenschke.shifter.models.shiftertypes;

import com.kstenschke.shifter.utils.UtilsTextual;

/**
 * PHP Variable (word with $ prefix)
 */
public class PhpConcatenation {

	private boolean isPhpConcatenation = false;
	private boolean isDotWhitespaceWrapped = false;
	private Integer offsetDot = null;
	private String dotWrapChar;

	private String partLHS = null;
	private String partRHS = null;

	/**
	 * Constructor
	 */
	public PhpConcatenation(String str) {
		str = UtilsTextual.removeLineBreaks( str.trim() );

		if(str.length() > 4) {
			this.extractParts(str);
			if(this.partLHS != null && this.partRHS != null) {
				this.isPhpConcatenation = true;
				if( this.offsetDot != null ) {
					char charBeforeDot = str.charAt(this.offsetDot - 1);
					char charAfterDot = str.charAt(this.offsetDot + 1);
					if((charBeforeDot == ' ' && charAfterDot == ' ') || (charBeforeDot == '\t' && charAfterDot == '\t')) {
						this.isDotWhitespaceWrapped = true;
						this.dotWrapChar = String.valueOf(charAfterDot);
					}
				}
			}
		}
	}

	/**
	 * Iterate over characters of string, try to detect and extract exactly two PHP concatenation operands
	 *
	 * Supported formats:
	 * 		$x . $y
	 * 		'aaaa' . 'bbbb'
	 * 		"aaaa" . "bbbb"
	 * 		'a.a.a' . "bbb'aaa'bbb.bbb"
	 * 		'aa\'a.aa' . "bb\b.bb"			(and combinations of those)
	 *
	 * @param	str
	 */
	private void extractParts(String str) {
		str		= str.trim();
		Integer strLen	= str.length();

		if( strLen > 4 ) {
			// Detect LHS Type / how it ends
			String partLHS = str.substring(0, 1);
			String endChar = detectConcatenationTypeAndGetEndingChar(partLHS, false);

			if( endChar != null ) {
				Integer currentOffset = 1;
				String currentChar = "";
				boolean isFailed = false;

				// Iterate until the end of the LHS part (denoted by ending character of detected operand type)
				boolean isFoundEndOfLHS = false;
				boolean isFoundDot = false;
				while(currentOffset < strLen && ! isFoundEndOfLHS) {
					currentChar	= str.substring(currentOffset, currentOffset+1);
					if( currentChar.equals(endChar)) {
						if( currentOffset > 0
							&& ! str.substring(currentOffset-1, currentOffset).equals("\\")	// ignore escaped end-char
						  ) {
							isFoundEndOfLHS = true;
							if( endChar.equals(".")) {
								isFoundDot = true;
								this.offsetDot	= currentOffset;
							} else {
								partLHS += currentChar;
							}
						}
					} else {
						partLHS += currentChar;
					}
					currentOffset++;
				}
				// Iterate until dot found, abort search if illegal (=other than dot or white-space) character found
				while(currentOffset < strLen && ! isFoundDot && ! isFailed) {
					currentChar = str.substring(currentOffset, currentOffset+1);
					if( currentChar.equals(".")) {
						if( currentOffset > 0 && ! str.substring(currentOffset-1, currentOffset).equals("\\")) {
							isFoundDot = true;
							this.offsetDot = currentOffset;
							currentOffset++;
						}
					} else if(currentChar.equals(" ") || currentChar.equals("\t")) {
						currentOffset++;
					} else {
						isFailed	= true;
					}
				}
				// Look for RHS part
				if(!isFailed && isFoundDot) {
					str = str.substring(currentOffset).trim();
					strLen = str.length();
					currentOffset = 0;

					String partRHS = str.substring(currentOffset, currentOffset+1);

					endChar	= detectConcatenationTypeAndGetEndingChar(partRHS, true);
					if( endChar != null ) {
						currentOffset+=1;
						boolean isFoundEndOfRHS = false;
						while(currentOffset < strLen && ! isFoundEndOfRHS) {
							currentChar	= str.substring(currentOffset, currentOffset+1);
							if( currentChar.equals(endChar) || (endChar.equals("") && strLen==currentOffset+1) ) {
								if( currentOffset > 0
										&& ! str.substring(currentOffset-1, currentOffset).equals("\\")	// ignore escaped end-char
										) {
									isFoundEndOfRHS = true;
									partRHS += currentChar;
									currentOffset++;

									if( strLen > currentOffset) {
										// Any concatenation should end here, does the string do?
										if( str.substring(currentOffset).trim().length() == 0) {
											isFailed = true;
										}
									}

									if(!isFailed) {
										this.partLHS	= partLHS;
										this.partRHS	= partRHS;
									}
								}
							} else {
								partRHS += currentChar;
								currentOffset++;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param	str
	 * @param	isRHS		Boolean: PHP variables on right-hand-side of concatenation have NO ending character
	 * @return	Ending character	. / " / ' / empty char (=ending is end of string) / null (=no type detected)
	 */
	private String detectConcatenationTypeAndGetEndingChar(String str, boolean isRHS) {
		if( str.equals("$")) {
				// Identified part as PHP Variable
			return isRHS ? "" : ".";
		} else if( str.equals("\"")) {
				// Identified parts as string wrapped within double quotes
			return "\"";
		} else if( str.equals("\'")) {
				// Identified parts as string wrapped within single quotes
			return "\'";
		}

		return null;
	}

	/**
	 * @return	boolean
	 */
	public boolean isPhpConcatenation() {
		return this.isPhpConcatenation;
	}

	/**
	 * @return	String
	 */
	public String getShifted() {
		String concatenation =
			  this.partRHS
			+ (this.isDotWhitespaceWrapped ? this.dotWrapChar : "")
			+ "."
			+ (this.isDotWhitespaceWrapped ? this.dotWrapChar : "")
			+ this.partLHS;

		return concatenation.trim();
	}

}
