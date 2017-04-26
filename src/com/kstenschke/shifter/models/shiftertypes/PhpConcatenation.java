/*
 * Copyright 2011-2017 Kay Stenschke
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
 * PHP Variable (word w/ $ prefix)
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
        String strJoined = UtilsTextual.removeLineBreaks(str.trim());

        if (strJoined.length() > 4 && strJoined.contains(".")) {
            this.extractParts(strJoined);
            if (this.partLHS != null && this.partRHS != null) {
                this.isPhpConcatenation = true;
                if (this.offsetDot != null) {
                    char charBeforeDot = strJoined.charAt(this.offsetDot - 1);
                    char charAfterDot = strJoined.charAt(this.offsetDot + 1);
                    if ((charBeforeDot == ' ' && charAfterDot == ' ') || (charBeforeDot == '\t' && charAfterDot == '\t')) {
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
     *      $x . $y
     *      'aaaa' . 'bbbb'
     *      "aaaa" . "bbbb"
     *      'a.a.a' . "bbb'aaa'bbb.bbb"
     *      'aa\'a.aa' . "bb\b.bb"            (and combinations of those)
     *
     * @param  str
     */
    private void extractParts(String str) {
        String strTrimmed = str.trim();
        Integer strLen    = strTrimmed.length();

        if (strLen > 4) {
            // Detect LHS Type / how it ends
            String leftHandSide = strTrimmed.substring(0, 1);
            String endChar = detectConcatenationTypeAndGetEndingChar(leftHandSide, false);

            if (endChar != null) {
                Integer currentOffset = 1;
                String currentChar;
                boolean isFailed = false;

                // Iterate until the end of the LHS part (denoted by ending character of detected operand type)
                boolean isFoundEndOfLHS = false;
                boolean isFoundDot = false;

                while (currentOffset < strLen && !isFoundEndOfLHS) {
                    currentChar    = strTrimmed.substring(currentOffset, currentOffset+1);
                    if (currentChar.equals(endChar)) {
                        // ignore escaped end-char
                        if (currentOffset > 0 && !"\\".equals(strTrimmed.substring(currentOffset-1, currentOffset))) {
                            isFoundEndOfLHS = true;
                            if (".".equals(endChar)) {
                                isFoundDot = true;
                                this.offsetDot    = currentOffset;
                            } else {
                                leftHandSide += currentChar;
                            }
                        }
                    } else {
                        leftHandSide += currentChar;
                    }
                    currentOffset++;
                }
                // Iterate until dot found, abort search if illegal (=other than dot or white-space) character found
                while (currentOffset < strLen && !isFoundDot && !isFailed) {
                    currentChar = strTrimmed.substring(currentOffset, currentOffset+1);
                    if (".".equals(currentChar)) {
                        if (currentOffset > 0 && !"\\".equals(strTrimmed.substring(currentOffset-1, currentOffset))) {
                            isFoundDot = true;
                            this.offsetDot = currentOffset;
                            currentOffset++;
                        }
                    } else if (" ".equals(currentChar) || "\t".equals(currentChar)) {
                        currentOffset++;
                    } else {
                        isFailed    = true;
                    }
                }
                // Look for RHS part
                if (!isFailed && isFoundDot) {
                    strTrimmed = strTrimmed.substring(currentOffset).trim();
                    strLen = strTrimmed.length();
                    currentOffset = 0;

                    String rightHandSide = strTrimmed.substring(currentOffset, currentOffset+1);

                    endChar    = detectConcatenationTypeAndGetEndingChar(rightHandSide, true);
                    if (endChar != null) {
                        currentOffset+=1;
                        boolean isFoundEndOfRHS = false;

                        while (currentOffset < strLen && !isFoundEndOfRHS) {
                            currentChar    = strTrimmed.substring(currentOffset, currentOffset+1);
                            if (currentChar.equals(endChar) || ("".equals(endChar) && strLen == currentOffset+1)) {
                                // ignore escaped end-char
                                if (currentOffset > 0 && !"\\".equals(strTrimmed.substring(currentOffset-1, currentOffset))) {
                                    isFoundEndOfRHS = true;
                                    rightHandSide += currentChar;
                                    currentOffset++;

                                    // If concatenation ends at current offset,
                                    if (strLen > currentOffset && strTrimmed.substring(currentOffset).trim().length() == 0) {
                                        // the string should as well
                                        isFailed = true;
                                    }

                                    if (!isFailed) {
                                        this.partLHS    = leftHandSide;
                                        this.partRHS    = rightHandSide;
                                    }
                                }
                            } else {
                                rightHandSide += currentChar;
                                currentOffset++;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param  str
     * @param  isRHS    Boolean: PHP variables on right-hand-side of concatenation have NO ending character
     * @return String   Ending character    . / " / ' / empty char (=ending is end of string) / null (=no type detected)
     */
    private String detectConcatenationTypeAndGetEndingChar(String str, boolean isRHS) {
        if ("$".equals(str)) {
            // Identified part as PHP Variable
            return isRHS ? "" : ".";
        }
        if ("\"".equals(str)) {
            // Identified parts as string wrapped within double quotes
            return "\"";
        }
        if ("\'".equals(str)) {
            // Identified parts as string wrapped within single quotes
            return "\'";
        }

        return null;
    }

    /**
     * @return  boolean
     */
    public boolean isPhpConcatenation() {
        return this.isPhpConcatenation;
    }

    /**
     * @return  String
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