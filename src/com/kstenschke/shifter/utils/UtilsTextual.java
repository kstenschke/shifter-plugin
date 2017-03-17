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
package com.kstenschke.shifter.utils;

import com.intellij.openapi.editor.Document;
import com.kstenschke.shifter.models.shiftertypes.OperatorSign;
import com.kstenschke.shifter.utils.natorder.NaturalOrderComparator;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static helper methods for analysis and manipulation of texts
 */
public class UtilsTextual {

    /**
     * @param  str         String to be checked
     * @return boolean     Is the given string fully lower case?
     */
    public static boolean isAllUppercase(String str) {
        return str.equals( str.toUpperCase());
    }

    /**
     * @param  lines
     * @param  shiftUp
     * @return Given lines sorted alphabetically ascending / descending
     */
    public static List<String> sortLines(List<String> lines, boolean shiftUp) {
        UtilsLinesList.DelimiterDetector delimiterDetector = new UtilsLinesList.DelimiterDetector(lines);

        Collections.sort(lines, new NaturalOrderComparator());

        if (!shiftUp) {
            Collections.reverse(lines);
        }

        if (delimiterDetector.isFoundDelimiter()) {
            // Maintain detected lines delimiter
            lines = UtilsLinesList.addDelimiter(lines, delimiterDetector.getCommonDelimiter(), delimiterDetector.isDelimitedLastLine());
        }

        return lines;
    }

    /**
     * @param  haystack
     * @param  needle
     * @return boolean
     */
    public static boolean containsCaseInSensitive(String haystack, String needle) {
        return Pattern.compile(Pattern.quote(needle), Pattern.CASE_INSENSITIVE).matcher(haystack).find();
    }

    /**
     * @param  str
     * @param  characters
     * @return boolean
     */
    public static boolean containsOnly(String str, String[] characters) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        for(String c : characters) {
            str = str.replaceAll(c, "");
        }

        return str.isEmpty();
    }

    /**
     * @param  str
     * @return boolean  Is the given string wrapped into single- or double quotes?
     */
    public static boolean isWrappedIntoQuotes(String str) {
        return isWrappedWith(str, "'") || isWrappedWith(str, "\"");
    }

    /**
     * @param  str
     * @param  wrap
     * @return boolean Is the given string wrapped into the wrapper string?
     */
    private static boolean isWrappedWith(String str, String wrap) {
        return str.startsWith(wrap) && str.endsWith(wrap);
    }

    /**
     * @param  str         String to be checked
     * @return boolean     Does the given string contain any slash or backslash?
     */
    public static boolean containsAnySlashes(String str) {
        return str.contains("\\") || str.contains("/");
    }

    /**
     * @param  str         String to be checked
     * @return boolean     Does the given string contain single or double quotes?
     */
    public static boolean containsAnyQuotes(String str) {
        return str.contains("\"") || str.contains("'");
    }

    /**
     * @param  str         String to be checked
     * @return String      Given string w/ contained slashes swapped against backslashes and vise versa
     */
    public static String swapSlashes(String str) {
        return str.
            replace("\\", "###SHIFTERSLASH###").
            replace("/", "\\").
            replace("###SHIFTERSLASH###", "/");
    }

    /**
     * @param  str         String to be checked
     * @return String      Given string w/ contained single quotes swapped against double quotes and vise versa
     */
    public static String swapQuotes(String str) {
        return str.
            replace("\"", "###SHIFTERSINGLEQUOTE###").
            replace("'", "\"").
            replace("###SHIFTERSINGLEQUOTE###", "'");
    }

    /**
     * @param  str      String to be converted
     * @return String   Given string converted to lower case w/ only first char in upper case
     */
    public static String toUcFirst(String str) {
        return str.isEmpty() ? "" : Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }

    /**
     * @param  str      String to be converted
     * @return String   Given string w/ first char in lower case
     */
    public static String toLcFirst(String str) {
        return str.isEmpty() ? "" : Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * Check whether given string is lower case w/ only first char in upper case
     *
     * @param  str      String to be checked
     * @return boolean  If the string is lower case w/ only first char in upper case.
     */
    public static boolean isUcFirst(String str) {
        return str.isEmpty() || str.equals(UtilsTextual.toUcFirst(str));
    }

    public static boolean isCamelCase(String str) {
        return str.matches("/\\b([A-Z][a-z]*){2,}\\b/");
    }

    /**
     * Find operator DIRECTLY neighbouring (closer than any other character)
     *
     * @param  str
     * @param  offset
     * @return Null|String
     */
    public static String getOperatorAtOffset(CharSequence str, int offset) {
        int textLength = str.length();
        if (textLength == 0 || offset >= textLength || str.toString().trim().isEmpty()) {
            return null;
        }

        String operatorOnLHS = offset > 2
                ? str.subSequence(offset - 2, offset + 1).toString()
                : null;

        if (operatorOnLHS != null && OperatorSign.isWhitespaceWrappedOperator(operatorOnLHS)) {
             return operatorOnLHS.trim();
        }

        String operatorToTheRight =
                offset < textLength - 2
                && offset > 0 && Character.isWhitespace(str.charAt(offset-1))

                ? str.subSequence(offset - 1, offset + 2).toString()
                : null;

        return (operatorToTheRight != null && OperatorSign.isWhitespaceWrappedOperator(operatorToTheRight))
                ? operatorToTheRight.trim()
                // No operator found
                : null;
    }

    /**
     * @param  str
     * @param  offset
     * @return Integer
     */
    public static Integer getStartOfOperatorAtOffset(CharSequence str, int offset) {
        int textLength = str.length();
        if (textLength == 0 || offset >= textLength) {
            return null;
        }

        String operatorToTheLeft = offset > 2
                ? str.subSequence(offset - 2, offset + 1).toString()
                : null;

        if (operatorToTheLeft != null && OperatorSign.isWhitespaceWrappedOperator(operatorToTheLeft)) {
            return offset - 1;
        }

        String operatorToTheRight = offset < (textLength - 2) &&
                (offset > 0 && Character.isWhitespace(str.charAt(offset-1)))
                ? str.subSequence(offset - 1, offset + 2).toString()
                : null;

        return (operatorToTheRight != null && OperatorSign.isWhitespaceWrappedOperator(operatorToTheRight))
                ? offset
                : null;
    }

    /**
     * Get word at caret offset out of given text
     *
     * @param  str             The full text
     * @param  offset          Character offset of caret
     * @param  allowHyphens    Treat "-" as word character?
     * @return                 The extracted word or null
     */
    public static String getWordAtOffset(CharSequence str, int offset, boolean allowHyphens) {
        int textLength = str.length();

        if (textLength == 0 || offset < 0 || offset >= textLength) {
            return null;
        }

        if (offset > 0
                  && ! isJavaIdentifierPart(str.charAt(offset), allowHyphens)
                  &&   isJavaIdentifierPart(str.charAt(offset - 1), allowHyphens)
        ) {
            offset--;
        }

        if (! isJavaIdentifierPart(str.charAt(offset), allowHyphens)) {
            return null;
        }

        int start = offset;
        int end   = offset;

        while (start > 0 && isJavaIdentifierPart(str.charAt(start - 1), allowHyphens)) {
            start--;
        }

        while (end < textLength && isJavaIdentifierPart(str.charAt(end), allowHyphens)) {
            end++;
        }

        return str.subSequence(start, end).toString();
    }

    /**
     * @param  c
     * @param  allowHyphens
     * @return boolean
     */
    public static boolean isJavaIdentifierPart(char c, boolean allowHyphens) {
        return allowHyphens
                ? Character.isJavaIdentifierPart(c) || c == '-'
                : Character.isJavaIdentifierPart(c);
    }

    /**
     * @param  str
     * @param  offsetStart    Sub sequence start character offset
     * @param  offsetEnd      Sub sequence end character offset
     * @return String         Sub sequence of given offsets out of given text
     */
    public static String getSubString(CharSequence str, int offsetStart, int offsetEnd) {
        return str.length() == 0 ? null : str.subSequence(offsetStart, offsetEnd).toString();
    }

    /**
     * @param  str        Full text
     * @param  offset      Offset from before which to extract one character
     * @return String      Character BEFORE word at given caret offset
     */
    public static String getCharBeforeOffset(CharSequence str, int offset) {
        if (str.length() == 0 || offset == 0) {
            return "";
        }

        return offset > 0
                ? str.subSequence(offset-1, offset).toString()
                : "";
    }

    /**
     * @param  str        Full text
     * @param  offset      Offset from after which to extract one character
     * @return String      Character AFTER word at caret offset
     */
    public static String getCharAfterOffset(CharSequence str, int offset) {
        if (str.length() < offset+2 || offset == 0) {
            return "";
        }

        return offset > 0 ? str.subSequence(offset+1, offset+2).toString() : "";
    }

    /**
     * @param  str         Text to be analyzed
     * @param  offset      Character offset in text, intersecting the word dealing w/
     * @return int         Starting position offset of word at given offset in given CharSequence
     */
    public static int getStartOfWordAtOffset(CharSequence str, int offset) {
        if (str.length() == 0) {
            return 0;
        }

        if (offset > 0 && !Character.isJavaIdentifierPart(str.charAt(offset)) && Character.isJavaIdentifierPart(str.charAt(offset - 1))) {
            offset--;
        }

        if (! Character.isJavaIdentifierPart(str.charAt(offset))) {
            return 0;
        }

        int start = offset;
        while (start > 0 && Character.isJavaIdentifierPart(str.charAt(start - 1))) {
            start--;
        }

        return start;
    }

    /**
     * @param  doc          Document
     * @param  startLine    Number of first line to be extracted
     * @param  endLine      Number of last line of extract
     * @return List<String> Extracted list of lines
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
     * @param  doc           Document to extract the line from
     * @param  lineNumber    Number of line to be extracted
     * @return String        The extracted line
     */
    public static String extractLine(Document doc, int lineNumber) {
        int lineSeparatorLength = doc.getLineSeparatorLength(lineNumber);

        int startOffset = doc.getLineStartOffset(lineNumber);
        int endOffset   = doc.getLineEndOffset(lineNumber) + lineSeparatorLength;

        String line = doc.getCharsSequence().subSequence(startOffset, endOffset).toString();

        // If last line has no \n, add it one
        // This causes adding a \n at the end of file when sort is applied on whole file and the file does not end
        // w/ \n... This is fixed after.
        if (lineSeparatorLength == 0) {
            line += "\n";
        }

        return line;
    }

    /**
     * @param  str
     * @param  offset
     * @return String
     */
    public static String extractLineAroundOffset(String str, int offset) {
        int lenText      = str.length();

        int offsetStart = offset;
        while (offsetStart > 0 && str.charAt(offsetStart-1) != '\n') {
            offsetStart--;
        }

        int offsetEnd = offset;
        while (offsetEnd < lenText && str.charAt(offsetEnd) != '\n') {
            offsetEnd++;
        }

        return str.substring(offsetStart, offsetEnd).trim();
    }

    /**
     * @param  lines            List of lines (strings) to be joined
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
     * @param  string
     * @param  toReplace
     * @param  replacement
     * @return string       Given string w/ last occurrence of "toReplace" replaced w/ "replacement"
     */
    public static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);

        return pos == -1
            ? string
            :   string.substring(0, pos)
              + replacement
              + string.substring(pos + toReplace.length(), string.length());
    }

    /**
     * @param  numberString
     * @param  length
     * @return String           Given numerical string, w/ given length (if >= original length)
     */
    public static String formatAmountDigits(String numberString, int length) {
        while (numberString.length() < length) {
            numberString = "0" + numberString;
        }

        return numberString;
    }

    /**
     * Check given (alphabetically sorted) lines for any line(s) being duplicated
     *
     * @param  lines
     * @return boolean
     */
    public static boolean hasDuplicateLines(String lines) {
        String[] linesArray = lines.split("\n");
        String previousLine = "";

        int index = 0;
        for(String currentLine : linesArray) {
            if (index > 0 && currentLine.equals(previousLine)) {
                return true;
            }
            index++;
            previousLine = currentLine;
        }

        return false;
    }

    public static String reduceDuplicateLines(String lines) {
        String[] linesArray = lines.split("\n");

        String[] resultLines = new String[linesArray.length];
        int index = 0;
        int resultIndex = 0;
        String previousLine = "";

        for(String currentLine : linesArray) {
            if (index == 0 || !currentLine.equals(previousLine)) {
                resultLines[resultIndex] = currentLine;
                resultIndex++;
            }
            index++;
            previousLine = currentLine;
        }

        return StringUtils.join(resultLines, "\n");
    }

    public static String getLeadingWhitespace(String str) {
        String whitespace = "";
        int offset = 0;
        int length = str.length();
        while (offset < length && Character.isWhitespace(str.charAt(offset))) {
            whitespace += str.charAt(offset);
            offset++;
        }

        return whitespace;
    }

    /**
     * Use regEx matcher to extract all variables in given code
     *
     * @param  str
     * @return List<String>     All PHP var names
     */
    @NotNull
    public static List<String> extractPhpVariables(String str) {
        return getPregMatches(str, "\\$[a-zA-Z0-9_]+");
    }

    @NotNull
    public static List<String> extractQuotedStrings(String text, String quoteCharacter) {
        return getPregMatches(
                text,
                "(?<=" + quoteCharacter + ")[a-zA-Z0-9_]+(?=" + quoteCharacter + ")"
        );
    }

    @NotNull
    public static List<String> getPregMatches(String str, String pattern) {
        Matcher m = Pattern.compile(pattern).matcher(str);

        List<String> allMatches = new ArrayList<String>();
        while (m.find()) {
            if (!allMatches.contains(m.group())) {
                allMatches.add(m.group());
            }
        }
        return allMatches;
    }
}