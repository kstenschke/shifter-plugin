/*
 * Copyright 2011-2019 Kay Stenschke
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
package com.kstenschke.shifter.models.entities.shiftables;

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.ShiftableTypes;
import com.kstenschke.shifter.models.entities.AbstractShiftable;
import com.kstenschke.shifter.utils.UtilsTextual;

// PHP Variable (word w/ $ prefix)
public class PhpConcatenation extends AbstractShiftable {

    public final String ACTION_TEXT = "Shift PHP Concatenation";

    private boolean isShiftable = true;
    private boolean isPhpConcatenation = false;
    private boolean isDotWhitespaceWrapped = false;
    private Integer offsetDot = null;
    private String dotWrapChar;

    private String partLHS = null;
    private String partRHS = null;

    // Constructor
    public PhpConcatenation(ActionContainer actionContainer) {
        super(actionContainer);

        // @todo make shiftable also in non-selection
        if (null == actionContainer.selectedText) {
            isShiftable = false;
            return;
        }

        String str = actionContainer.selectedText;
        String strJoined = UtilsTextual.removeLineBreaks(str.trim());
        if (strJoined.length() <= 4 || !strJoined.contains(".")) return;

        extractParts(strJoined);
        if (null == partLHS || null == partRHS) return;

        isPhpConcatenation = true;
        if (null == offsetDot) return;

        char charBeforeDot = strJoined.charAt(offsetDot - 1);
        char charAfterDot = strJoined.charAt(offsetDot + 1);
        if ((' ' == charBeforeDot && ' ' == charAfterDot) ||
                (charBeforeDot == '\t' && charAfterDot == '\t')
        ) {
            isDotWhitespaceWrapped = true;
            dotWrapChar = String.valueOf(charAfterDot);
        }
    }

    // Get instance or null if not applicable
    public PhpConcatenation getInstance() {
        return isShiftable && isPhpConcatenation ? this : null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.PHP_CONCATENATION;
    }

    /**
     * @return  String  Concatenation w/ left- and right-hand-side parts interchanged
     */
    public String getShifted(
            String str,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        String dotWrapChar = null == this.dotWrapChar ? "" : this.dotWrapChar;

        String concatenation =
                (null == partRHS ? "" : partRHS)
                        + (isDotWhitespaceWrapped ? dotWrapChar : "")
                        + "."
                        + (isDotWhitespaceWrapped ? dotWrapChar : "")
                        + (null == partLHS ? "" : partLHS);

        return concatenation.trim();
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

        if (strLen <= 4) return;

        // Detect LHS Type / how it ends
        StringBuilder leftHandSide = new StringBuilder(strTrimmed.substring(0, 1));
        String endChar = detectConcatenationTypeAndGetEndingChar(leftHandSide.toString(), false);
        if (null == endChar) return;

        Integer currentOffset = 1;
        String currentChar;

        // Iterate until the end of the LHS part (denoted by ending character of detected operand type)
        boolean isFoundEndOfLHS = false;
        boolean isFoundDot = false;

        while (currentOffset < strLen && !isFoundEndOfLHS) {
            currentChar = strTrimmed.substring(currentOffset, currentOffset + 1);
            if (currentChar.equals(endChar)) {
                // Ignore escaped end-char
                if (currentOffset > 0 && !"\\".equals(strTrimmed.substring(currentOffset - 1, currentOffset))) {
                    isFoundEndOfLHS = true;
                    if (".".equals(endChar)) {
                        isFoundDot = true;
                        offsetDot = currentOffset;
                    } else {
                        leftHandSide.append(currentChar);
                    }
                }
            } else {
                leftHandSide.append(currentChar);
            }
            currentOffset++;
        }

        // Iterate until dot found, abort search if illegal (=other than dot or white-space) character found
        while (currentOffset < strLen && !isFoundDot) {
            currentChar = strTrimmed.substring(currentOffset, currentOffset + 1);
            if (".".equals(currentChar)) {
                if (currentOffset > 0 && !"\\".equals(strTrimmed.substring(currentOffset - 1, currentOffset))) {
                    isFoundDot = true;
                    offsetDot = currentOffset;
                    currentOffset++;
                }
            } else if (" ".equals(currentChar) || "\t".equals(currentChar)) {
                currentOffset++;
            } else return;
        }
        if (!isFoundDot) return;

        // Look for RHS part
        strTrimmed = strTrimmed.substring(currentOffset).trim();
        strLen = strTrimmed.length();
        currentOffset = 0;

        String rightHandSide = strTrimmed.substring(currentOffset, currentOffset + 1);

        endChar = detectConcatenationTypeAndGetEndingChar(rightHandSide, true);
        boolean isFailed = false;
        if (null == endChar) return;

        currentOffset += 1;
        boolean isFoundEndOfRHS = false;
        while (currentOffset < strLen && !isFoundEndOfRHS) {
            currentChar = strTrimmed.substring(currentOffset, currentOffset + 1);
            if (currentChar.equals(endChar) || ("".equals(endChar) && currentOffset + 1 == strLen)) {
                // Ignore escaped end-char
                if (currentOffset > 0 && !"\\".equals(strTrimmed.substring(currentOffset - 1, currentOffset))) {
                    isFoundEndOfRHS = true;
                    rightHandSide += currentChar;
                    currentOffset++;

                    if (strLen > currentOffset && strTrimmed.substring(currentOffset).trim().length() == 0) {
                        // If concatenation ends at current offset, the string should as well
                        isFailed = true;
                    }

                    if (!isFailed) {
                        partLHS = leftHandSide.toString();
                        partRHS = rightHandSide;
                    }
                }
            } else {
                rightHandSide += currentChar;
                currentOffset++;
            }
        }
    }

    /**
     * @param  str
     * @param  isRHS    Boolean: PHP variables on right-hand-side of concatenation have NO ending character
     * @return String   Ending character    . / " / ' / empty char (=ending is end of string) / null (=no type detected)
     */
    private String detectConcatenationTypeAndGetEndingChar(String str, boolean isRHS) {
        // Part is PHP Variable?
        if ("$".equals(str)) return isRHS ? "" : ".";

        // Parts are string wrapped within double quotes?
        if ("\"".equals(str)) return "\"";

        // Parts are string wrapped within single quotes?
        if ("\'".equals(str)) return "\'";

        return null;
    }
}