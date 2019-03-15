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
package com.kstenschke.shifter.utils;

import com.intellij.openapi.editor.Document;
import com.kstenschke.shifter.models.comparators.AlphanumComparator;
import com.kstenschke.shifter.models.shiftable_types.DocCommentTag;
import com.kstenschke.shifter.models.shiftable_types.OperatorSign;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static helper methods for analysis and manipulation of texts
 */
public class UtilsTextual {

    private final static Pattern TRIM_RIGHT = Pattern.compile("\\s+$");

    public static String rtrim(String s) {
        return null == s
            ? ""
            : TRIM_RIGHT.matcher(s).replaceAll("");
    }

    /**
     * @param  str         String to be checked
     * @return boolean     Is the given string fully lower case?
     */
    public static boolean isAllUppercase(String str) {
        return str.equals(str.toUpperCase());
    }

    public static boolean isMultiLine(@Nullable String str) {
        return null != str && str.contains("\n");
    }

    /**
     * @param  lines
     * @param  reverse
     * @return Given lines sorted alphabetically ascending / descending
     */
    public static void sortLinesNatural(List<String> lines, boolean reverse) {
        DelimiterDetector delimiterDetector = new DelimiterDetector(lines);
        boolean isDelimitedLastLine = delimiterDetector.isDelimitedLastLine();

        lines.sort(new AlphanumComparator());
        if (reverse) {
            Collections.reverse(lines);
        }

        if (delimiterDetector.isFoundDelimiter() && !isDelimitedLastLine) {
            // Maintain detected lines delimiter (ex: comma-separated values, w/ last item w/o trailing comma)
            addDelimiter(lines, delimiterDetector.getCommonDelimiter());
        }
    }

    static boolean equalsAnyOf(String str, String words[]) {
        if (null == str || null == words) {
            return false;
        }
        for (String word : words) {
            if (str.equals(word)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsCaseInSensitive(@Nullable String haystack, String needle) {
        return
                null != haystack
                && null != needle
                && !needle.equals("")
                && Pattern.compile(Pattern.quote(needle), Pattern.CASE_INSENSITIVE).matcher(haystack).find();
    }

    public static boolean containsOnly(@Nullable String str, String[] characters) {
        if (null == str || str.isEmpty()) {
            return false;
        }

        for (String c : characters) {
            str = str.replaceAll(c, "");
        }

        return str.isEmpty();
    }

    public static boolean isAlphaNumericAndMinus(@Nullable String str) {
        return !(null == str || str.isEmpty()) && str.matches("[a-zA-z0-9\\-]+");
    }

    public static boolean startsAlphabetic(@Nullable String str) {
        return !(null == str || str.isEmpty()) && StringUtils.isAlpha(String.valueOf(str.charAt(0)));
    }

    public static boolean startsNumeric(@Nullable String str) {
        return !(null == str || str.isEmpty()) && Character.isDigit(str.charAt(0));
    }

    public static boolean isWrappedWithQuotes(@Nullable String str) {
        return isWrappedWith(str, "'") || isWrappedWith(str, "\"");
    }

    private static boolean isWrappedWith(@Nullable String str, String wrap) {
        return null != str && (str.startsWith(wrap) && str.endsWith(wrap));
    }

    /**
     * @param  str         String to be checked
     * @return boolean     Does the given string contain any slash or backslash?
     */
    public static boolean containsSlashes(String str) {
        return null != str && (str.contains("\\") || str.contains("/"));
    }

    /**
     * @param  str         String to be checked
     * @return String      Given string w/ contained slashes swapped against backslashes and vise versa
     */
    public static String swapSlashes(@Nullable String str) {
        if (null == str) {
            return null;
        }

        char chars[] = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {
            if (chars[i] == '/') {
                chars[i] = '\\';
            } else if (chars[i] == '\\') {
                chars[i] = '/';
            }
        }

        return new String(chars);
    }

    public static String swapQuotes(String str, boolean singleToDouble, boolean doubleToSingle) {
        if (null == str || (!singleToDouble && !doubleToSingle)) {
            return str;
        }

        char chars[] = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {
            if (chars[i] == '\'' && singleToDouble) {
                chars[i] = '"';
            } else if (chars[i] == '"' && doubleToSingle) {
                chars[i] = '\'';
            }
        }

        return new String(chars);
    }

    /**
     * @param  str         String to be checked
     * @return String      Given string w/ contained single quotes swapped against double quotes and vise versa
     */
    public static String swapQuotes(String str) {
        return swapQuotes(str, true, true);
    }

    /**
     * @param  str      String to be converted
     * @return String   Given string converted to lower case w/ only first char in upper case (rest lower)
     */
    public static String toUcFirstRestLower(@Nullable String str) {
        if (null == str) {
            return null;
        }

        return str.isEmpty()
                ? ""
                : Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }

    /**
     * @param  str      String to be converted
     * @return String   Given string w/ first char in lower case
     */
    public static String toLcFirst(String str) {
        return null == str || "".equals(str) ? str : Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    public static boolean isLcFirst(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }

        char leadChar = str.charAt(0);

        return Character.isAlphabetic(leadChar) && Character.toLowerCase(leadChar) == leadChar;
    }

    /**
     * Check whether given string is lower case w/ only first char in upper case
     *
     * @param  str      String to be checked
     * @return boolean  If the string is lower case w/ only first char in upper case.
     */
    public static boolean isUcFirstRestLower(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }

        char leadChar = str.charAt(0);

        return Character.isAlphabetic(leadChar) && str.equals(UtilsTextual.toUcFirstRestLower(str));
    }

    public static boolean isUpperCamelCase(@Nullable String str) {
        return null != str && !"".equals(str) && str.matches("[A-Z]([A-Z0-9]*[a-z][a-z0-9]*[A-Z]|[a-z0-9]*[A-Z][A-Z0-9]*[a-z])[A-Za-z0-9]*");
    }

    private static boolean isLowerCamelCase(@Nullable String str) {
        return null != str && !"".equals(str)  && str.matches("[a-z]([A-Z0-9]*[a-z][a-z0-9]*[A-Z]|[a-z0-9]*[A-Z][A-Z0-9]*[a-z])[A-Za-z0-9]*");
    }

    public static boolean isCamelCase(@Nullable String str) {
        return null != str && !"".equals(str) && str.length() > 2 && (isLowerCamelCase(str) || isUpperCamelCase(str));
    }

    /**
     * @param str   CamelCased string w/ lower or upper lead character
     * @return String[]
     */
    @NotNull
    public static String[] splitCamelCaseIntoWords(@Nullable String str) {
        return splitCamelCaseIntoWords(str, false);
    }
    public static String[] splitCamelCaseIntoWords(@Nullable String str, boolean toLower) {
        if (null == str) {
            return new String[0];
        }

        boolean isUcFirst = isUcFirstRestLower(str);
        if (isUcFirst) {
            str = UtilsTextual.toLcFirst(str);
        }
        String parts[] = str.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");

        if (toLower) {
            return toLower(parts);
        }

        if (isUcFirst) {
            parts[0] = toUcFirstRestLower(parts[0]);
        }

        return parts;
    }

    private static String[] toLower(String[] strings) {
        int index = 0;
        for (String part : strings) {
            strings[index] = part.toLowerCase();
            index++;
        }

        return strings;
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
        if (0 == textLength || offset >= textLength || str.toString().trim().isEmpty()) {
            return null;
        }

        String lineAroundOffset = getLineAtOffset(str.toString(), offset);
        if (DocCommentTag.isDocCommentLine(lineAroundOffset)) {
            // Prevent mistaking the beginning of a DOC comment line (e.g. " * @param ") for an asterisk operator
            return null;
        }

        String operatorOnLHS = offset > 2
                ? str.subSequence(offset - 2, offset + 1).toString()
                : null;

        if (null != operatorOnLHS && OperatorSign.isWhitespaceWrappedOperator(operatorOnLHS)) {
             return operatorOnLHS.trim();
        }

        String operatorToTheRight =
            offset < textLength - 2
            && offset > 0 && Character.isWhitespace(str.charAt(offset-1))
                ? str.subSequence(offset - 1, offset + 2).toString()
                : null;

        return (null != operatorToTheRight && OperatorSign.isWhitespaceWrappedOperator(operatorToTheRight))
                ? operatorToTheRight.trim()
                // No operator found
                : null;
    }

    public static Integer getStartOfOperatorAtOffset(CharSequence str, int offset) {
        int textLength = str.length();
        if (0 == textLength || offset >= textLength) {
            return null;
        }

        String operatorToTheLeft = offset > 2
                ? str.subSequence(offset - 2, offset + 1).toString()
                : null;

        if (null != operatorToTheLeft && OperatorSign.isWhitespaceWrappedOperator(operatorToTheLeft)) {
            return offset - 1;
        }

        String operatorToTheRight = offset < (textLength - 2) &&
                (offset > 0 && Character.isWhitespace(str.charAt(offset-1)))
                ? str.subSequence(offset - 1, offset + 2).toString()
                : null;

        return (null != operatorToTheRight && OperatorSign.isWhitespaceWrappedOperator(operatorToTheRight))
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
        if (null == str) {
            return null;
        }
        int textLength = str.length();

        if (0 == textLength || offset < 0 || offset >= textLength) {
            return null;
        }

        // Initialize offset
        if (offset > 0
         && !isJavaIdentifierPart(str.charAt(offset), allowHyphens)
         && isJavaIdentifierPart(str.charAt(offset - 1), allowHyphens)
        ) {
            offset--;
        }
        if (!isJavaIdentifierPart(str.charAt(offset), allowHyphens)) {
            return null;
        }

        // Decrement offset until start of word or CharSequence
        int start = offset;
        int end   = offset;
        while (start > 0 && isJavaIdentifierPart(str.charAt(start - 1), allowHyphens)) {
            start--;
        }

        // Increment offset until end of word or CharSequence
        while (end < textLength && isJavaIdentifierPart(str.charAt(end), allowHyphens)) {
            end++;
        }

        return str.subSequence(start, end).toString();
    }

    private static boolean isJavaIdentifierPart(char c, boolean allowHyphens) {
        return allowHyphens
                ? Character.isJavaIdentifierPart(c) || '-' == c
                : Character.isJavaIdentifierPart(c);
    }

    public static String getSubString(CharSequence str, int offsetStart, int offsetEndExclusive) {
        int length = str.length();
        if (
                length == 0
                || offsetStart > length
                || offsetEndExclusive < offsetStart
                || offsetStart < 0
        ) {
            return null;
        }

        if (offsetEndExclusive >= length) {
            return str.subSequence(offsetStart, length).toString();
        }

        return str.subSequence(offsetStart, offsetEndExclusive).toString();
    }

    public static int subStringCount(String str, String subStr) {
        if (str.equals("") || subStr.equals("")) {
            return 0;
        }
        int count = 0;
        for (int pos = str.indexOf(subStr); pos >= 0; pos = str.indexOf(subStr, pos + 1)) {
            count++;
        }

        return count;
    }

    public static String getCharBeforeOffset(CharSequence str, int offset) {
        int length = str.length();
        if (length == 0 || offset <= 0) {
            return "";
        }
        if (offset > length) {
            offset = length;
        }

        return offset > 0
                ? str.subSequence(offset - 1, offset).toString()
                : "";
    }

    /**
     * @param  str        Full text
     * @param  offset      Offset from after which to extract one character
     * @return String      Character AFTER word at caret offset
     */
    public static String getCharAfterOffset(CharSequence str, int offset) {
        return str.length() < offset + 2 || -1 > offset
                ? ""
                : str.subSequence(offset + 1, offset + 2).toString();
    }

    static int getOffsetEndOfWordAtOffset(CharSequence str, int offset) {
        int strLength = str.length();
        if (0 == strLength || offset < 0) {
            return 0;
        }
        if (offset > strLength) {
            return strLength;
        }
        while(offset < strLength) {
            if (!Character.isJavaIdentifierPart(str.charAt(offset))) {
                return offset;
            }
            offset++;
        }

        return strLength;
    }

    /**
     * @param  str         Text to be analyzed
     * @param  offset      Character offset in text, intersecting the word dealing w/
     * @return int         Starting position offset of word at given offset in given CharSequence
     */
    public static int getStartOfWordAtOffset(CharSequence str, int offset) {
        int strLength = str.length();
        if (0 == strLength || offset < 0) {
            return 0;
        }
        if (offset >= strLength) {
            return getStartOfWordAtOffset(str, strLength - 1);
        }

        if (offset > 0 && !Character.isJavaIdentifierPart(str.charAt(offset)) && Character.isJavaIdentifierPart(str.charAt(offset - 1))) {
            offset--;
        }

        if (!Character.isJavaIdentifierPart(str.charAt(offset))) {
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
        List<String> lines = new ArrayList<>(endLine - startLine);

        for (int i = startLine; i <= endLine; i++) {
            String line = UtilsTextual.getLine(doc, i);

            lines.add(line);
        }

        return lines;
    }

    /**
     * @param  doc           Document to extract the line from
     * @param  lineNumber    Number of line to be extracted
     * @return String        The extracted line
     */
    private static String getLine(Document doc, int lineNumber) {
        int lineSeparatorLength = doc.getLineSeparatorLength(lineNumber);

        int startOffset = doc.getLineStartOffset(lineNumber);
        int endOffset   = doc.getLineEndOffset(lineNumber) + lineSeparatorLength;

        String line = doc.getCharsSequence().subSequence(startOffset, endOffset).toString();

        // If last line has no \n, add it one
        // This causes adding a \n at the end of file when sort is applied on whole file and the file does not end
        // w/ \n... This is fixed after.
        return line + (0 == lineSeparatorLength ? "\n" : "");
    }

    private static String getLineAtOffset(String str, int offset) {
        int lenText      = str.length();

        int offsetStart = offset;
        while (offsetStart > 0 && str.charAt(offsetStart - 1) != '\n') {
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

        return -1 == pos
            ? string
            :   string.substring(0, pos)
              + replacement
              + string.substring(pos + toReplace.length());
    }

    /**
     * @param  numberString
     * @param  length
     * @return String           Given numerical string, w/ given length (if >= original length)
     */
    public static String formatAmountDigits(String numberString, int length) {
        StringBuilder numberStringBuilder = new StringBuilder(numberString);
        while (numberStringBuilder.length() < length) {
            numberStringBuilder.insert(0, "0");
        }

        return numberStringBuilder.toString();
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
        for (String currentLine : linesArray) {
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

        for (String currentLine : linesArray) {
            if (0 == index || !currentLine.equals(previousLine)) {
                resultLines[resultIndex] = currentLine;
                resultIndex++;
            }
            index++;
            previousLine = currentLine;
        }

        return StringUtils.join(resultLines, "\n");
    }

    public static String getLeadWhitespace(@Nullable String str) {
        if (null == str) {
            return null;
        }
        StringBuilder whitespace = new StringBuilder();
        int offset = 0;
        int length = str.length();
        while (offset < length && Character.isWhitespace(str.charAt(offset))) {
            whitespace.append(str.charAt(offset));
            offset++;
        }

        return whitespace.toString();
    }

    @NotNull
    public static List<String> extractQuotedStrings(String text, String quoteCharacter) {
        return getPregMatches(
                text,
                "(?<=" + quoteCharacter + ")[a-zA-Z0-9_]+(?=" + quoteCharacter + ")"
        );
    }

    @NotNull
    static List<String> getPregMatches(@Nullable String str, String pattern) {
        if (null == str) {
            return new ArrayList<>();
        }
        Matcher m = Pattern.compile(pattern).matcher(str);

        List<String> allMatches = new ArrayList<>();
        while (m.find()) {
            if (!allMatches.contains(m.group())) {
                allMatches.add(m.group());
            }
        }
        return allMatches;
    }

    /**
     * Add given delimiter to ending of each of given lines (last line is not being delimited)
     *
     * @param lines         Passed by reference
     * @param delimiter
     */
    private static void addDelimiter(List<String> lines, String delimiter) {
        if (null == delimiter) {
            return;
        }

        int amountLines = lines.size();
        int index = 0;

        for (String line : lines) {
            line = UtilsTextual.rtrim(line);

            boolean isLastLine = index + 1 == amountLines;
            if (!isLastLine && !line.endsWith(delimiter)) {
                line = line + delimiter;
            }
            if (isLastLine && line.endsWith(delimiter)) {
                // Remove delimiter from last line
                line = line.substring(0, line.length() - 1);
            }

            lines.set(index, line + "\n");
            index++;
        }
    }
}