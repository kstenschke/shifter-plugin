package com.kstenschke.shifter.utils;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class UtilsTextualTest {
    @Test
    public void rtrim() {
        assertEquals("", UtilsTextual.rtrim(null));
        assertEquals("", UtilsTextual.rtrim(""));
        assertEquals("", UtilsTextual.rtrim(" "));
        assertEquals("", UtilsTextual.rtrim("\t"));
        assertEquals("", UtilsTextual.rtrim("\n"));
        assertEquals("", UtilsTextual.rtrim("\n "));
        assertEquals("1", UtilsTextual.rtrim("1"));
        assertEquals("\n1", UtilsTextual.rtrim("\n1 \n\n\n"));
    }

    @Test
    public void isAllUppercase() {
        assertFalse(UtilsTextual.isAllUppercase("f"));
        assertFalse(UtilsTextual.isAllUppercase("Fo"));
        assertFalse(UtilsTextual.isAllUppercase(" FOo"));

        assertTrue(UtilsTextual.isAllUppercase("F"));
        assertTrue(UtilsTextual.isAllUppercase("FO"));
        assertTrue(UtilsTextual.isAllUppercase("FOO "));
        assertTrue(UtilsTextual.isAllUppercase(" FOO "));

        assertTrue(UtilsTextual.isAllUppercase(""));
    }

    @Test
    public void isMultiLine() {
        assertFalse(UtilsTextual.isMultiLine(null));
        assertFalse(UtilsTextual.isMultiLine(""));
        assertFalse(UtilsTextual.isMultiLine("foo bar"));
        assertFalse(UtilsTextual.isMultiLine("foo bar\tbaz"));
        assertFalse(UtilsTextual.isMultiLine("\t\t"));

        assertTrue(UtilsTextual.isMultiLine("\n"));
        assertTrue(UtilsTextual.isMultiLine("a\nb"));
    }

    @Test
    public void sortLinesNatural() {
        ArrayList<String> lines = new ArrayList<>(0);
        UtilsTextual.sortLinesNatural(lines, false);
        assertEquals("[]", lines.toString());

        lines = new ArrayList<>(0);
        UtilsTextual.sortLinesNatural(lines, true);
        assertEquals("[]", lines.toString());

        lines = new ArrayList<>(3);
        lines.add("1. foo\n");
        lines.add("100. baz\n");
        lines.add("2. bar\n");
        UtilsTextual.sortLinesNatural(lines, false);
        assertEquals(
                "[1. foo\n, 2. bar\n, 100. baz\n]",
                lines.toString()
        );

        lines = new ArrayList<>(3);
        lines.add("1. foo\n");
        lines.add("100. baz\n");
        lines.add("2. bar\n");
        UtilsTextual.sortLinesNatural(lines, true);
        assertEquals(
                "[100. baz\n, 2. bar\n, 1. foo\n]",
                lines.toString()
        );
    }

    @Test
    public void equalsAnyOf() {
        assertFalse(UtilsTextual.equalsAnyOf(null, null));
        assertFalse(UtilsTextual.equalsAnyOf(null, new String[]{}));
        assertFalse(UtilsTextual.equalsAnyOf(null, new String[]{""}));
        assertFalse(UtilsTextual.equalsAnyOf(null, new String[]{"foo"}));
        assertFalse(UtilsTextual.equalsAnyOf("", null));
        assertFalse(UtilsTextual.equalsAnyOf("foo", null));
        assertFalse(UtilsTextual.equalsAnyOf("foo", new String[]{"bar", "baz", "qux"}));

        assertTrue(UtilsTextual.equalsAnyOf("", new String[]{""}));
        assertTrue(UtilsTextual.equalsAnyOf("foo", new String[]{"foo"}));
        assertTrue(UtilsTextual.equalsAnyOf("bar", new String[]{"foo", "bar", "baz"}));
    }

    @Test
    public void containsCaseInSensitive() {
        assertFalse(UtilsTextual.containsCaseInSensitive(null, null));
        assertFalse(UtilsTextual.containsCaseInSensitive(null, ""));
        assertFalse(UtilsTextual.containsCaseInSensitive("", ""));
        assertFalse(UtilsTextual.containsCaseInSensitive(null, "foo"));
        assertFalse(UtilsTextual.containsCaseInSensitive("foo", ""));
        assertFalse(UtilsTextual.containsCaseInSensitive("foo", "b"));
        assertFalse(UtilsTextual.containsCaseInSensitive("foo", "bar"));

        assertTrue(UtilsTextual.containsCaseInSensitive("foo", "foo"));
        assertTrue(UtilsTextual.containsCaseInSensitive("foo", "o"));
        assertTrue(UtilsTextual.containsCaseInSensitive("foo", "FOO"));
        assertTrue(UtilsTextual.containsCaseInSensitive("foo", "O"));
        assertTrue(UtilsTextual.containsCaseInSensitive("fooBar", "ob"));
        assertTrue(UtilsTextual.containsCaseInSensitive("fooBar", "OB"));
        assertTrue(UtilsTextual.containsCaseInSensitive("foo\nBar", "o\nb"));
        assertTrue(UtilsTextual.containsCaseInSensitive("foo\nBar", "O\nB"));
    }

    @Test
    public void containsOnly() {
        assertFalse(UtilsTextual.containsOnly(null, null));
        assertFalse(UtilsTextual.containsOnly(null, new String[]{}));
        assertFalse(UtilsTextual.containsOnly(null, new String[]{"foo"}));
        assertFalse(UtilsTextual.containsOnly("", new String[]{}));
        assertFalse(UtilsTextual.containsOnly("", new String[]{"f"}));
        assertFalse(UtilsTextual.containsOnly("foo", new String[]{"f"}));
        assertFalse(UtilsTextual.containsOnly("foo", new String[]{"f", "O"}));

        assertTrue(UtilsTextual.containsOnly("foo", new String[]{"f", "o"}));
    }

    @Test
    public void isAlphaNumericAndMinus() {
        assertFalse(UtilsTextual.isAlphaNumericAndMinus(null));
        assertFalse(UtilsTextual.isAlphaNumericAndMinus(""));
        assertFalse(UtilsTextual.isAlphaNumericAndMinus("0 "));
        assertFalse(UtilsTextual.isAlphaNumericAndMinus("(0"));
        assertFalse(UtilsTextual.isAlphaNumericAndMinus("a "));
        assertFalse(UtilsTextual.isAlphaNumericAndMinus("0 "));
        assertFalse(UtilsTextual.isAlphaNumericAndMinus("a0 "));
        assertFalse(UtilsTextual.isAlphaNumericAndMinus("a0-1 "));

        assertTrue(UtilsTextual.isAlphaNumericAndMinus("foo"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("Foo"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("0"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("1"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("2"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("3"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("4"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("5"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("6"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("7"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("8"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("9"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("a0"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("a0-1"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("Foo0"));
        assertTrue(UtilsTextual.isAlphaNumericAndMinus("Foo0-1"));
    }

    @Test
    public void startsAlphabetic() {
        assertFalse(UtilsTextual.startsAlphabetic(null));
        assertFalse(UtilsTextual.startsAlphabetic(""));
        assertFalse(UtilsTextual.startsAlphabetic("0 "));
        assertFalse(UtilsTextual.startsAlphabetic("(0"));
        assertFalse(UtilsTextual.startsAlphabetic(" a"));

        assertTrue(UtilsTextual.startsAlphabetic("a"));
        assertTrue(UtilsTextual.startsAlphabetic("A"));
        assertTrue(UtilsTextual.startsAlphabetic("Foo bar baz"));
    }

    @Test
    public void startsNumeric() {
        assertFalse(UtilsTextual.startsNumeric(null));
        assertFalse(UtilsTextual.startsNumeric(""));
        assertFalse(UtilsTextual.startsNumeric("(0"));
        assertFalse(UtilsTextual.startsNumeric(" a"));
        assertFalse(UtilsTextual.startsNumeric("a"));
        assertFalse(UtilsTextual.startsNumeric("A"));
        assertFalse(UtilsTextual.startsNumeric("Foo bar baz"));

        assertTrue(UtilsTextual.startsNumeric("0"));
        assertTrue(UtilsTextual.startsNumeric("1"));
        assertTrue(UtilsTextual.startsNumeric("2"));
        assertTrue(UtilsTextual.startsNumeric("3"));
        assertTrue(UtilsTextual.startsNumeric("4"));
        assertTrue(UtilsTextual.startsNumeric("5"));
        assertTrue(UtilsTextual.startsNumeric("6"));
        assertTrue(UtilsTextual.startsNumeric("7"));
        assertTrue(UtilsTextual.startsNumeric("8"));
        assertTrue(UtilsTextual.startsNumeric("9"));
        assertTrue(UtilsTextual.startsNumeric("9 foo bar baz"));
    }

    @Test
    public void isWrappedWithQuotes() {
        assertFalse(UtilsTextual.isWrappedWithQuotes(null));
        assertFalse(UtilsTextual.isWrappedWithQuotes(""));
        assertFalse(UtilsTextual.isWrappedWithQuotes("(0"));
        assertFalse(UtilsTextual.isWrappedWithQuotes(" a"));
        assertFalse(UtilsTextual.isWrappedWithQuotes("a"));
        assertFalse(UtilsTextual.isWrappedWithQuotes("A"));
        assertFalse(UtilsTextual.isWrappedWithQuotes("Foo bar baz"));
        assertFalse(UtilsTextual.isWrappedWithQuotes("'Foo bar baz'\n"));

        assertTrue(UtilsTextual.isWrappedWithQuotes("''"));
        assertTrue(UtilsTextual.isWrappedWithQuotes("\"\""));
        assertTrue(UtilsTextual.isWrappedWithQuotes("\"foo\""));
        assertTrue(UtilsTextual.isWrappedWithQuotes("'foo'"));
        assertTrue(UtilsTextual.isWrappedWithQuotes("\"foo\nbar\nbaz\""));
        assertTrue(UtilsTextual.isWrappedWithQuotes("'foo\nbar\nbaz'"));
    }

    @Test
    public void containsSlashes() {
        assertFalse(UtilsTextual.containsSlashes(null));
        assertFalse(UtilsTextual.containsSlashes(""));
        assertFalse(UtilsTextual.containsSlashes(" "));

        assertTrue(UtilsTextual.containsSlashes("\\"));
        assertTrue(UtilsTextual.containsSlashes("/"));
    }

    @Test
    public void swapSlashes() {
        assertEquals(null, UtilsTextual.swapSlashes(null));
        assertEquals("", UtilsTextual.swapSlashes(""));
        assertEquals("foo", UtilsTextual.swapSlashes("foo"));
        assertEquals(" ", UtilsTextual.swapSlashes(" "));

        assertEquals("\\", UtilsTextual.swapSlashes("/"));
        assertEquals("/", UtilsTextual.swapSlashes("\\"));
        assertEquals("http://", UtilsTextual.swapSlashes("http:\\\\"));
        assertEquals("f\\o/o\\b/a\\r/b\\a/z\\", UtilsTextual.swapSlashes("f/o\\o/b\\a/r\\b/a\\z/"));
    }

    @Test
    public void swapQuotes() {
        assertEquals(null, UtilsTextual.swapQuotes(null));
        assertEquals("", UtilsTextual.swapQuotes(""));
        assertEquals(" ", UtilsTextual.swapQuotes(" "));
        assertEquals("foo", UtilsTextual.swapQuotes("foo"));

        assertEquals("'", UtilsTextual.swapQuotes("\""));
        assertEquals("\"", UtilsTextual.swapQuotes("\'"));
        assertEquals("'\"'", UtilsTextual.swapQuotes("\"'\""));

        assertEquals("'\"", UtilsTextual.swapQuotes("'\"", false, false));
        assertEquals("''", UtilsTextual.swapQuotes("'\"", false, true));
        assertEquals("\"\"", UtilsTextual.swapQuotes("'\"", true, false));
        assertEquals("\"'", UtilsTextual.swapQuotes("'\"", true, true));
    }

    @Test
    public void toUcFirstRestLower() {
        assertEquals(null, UtilsTextual.toUcFirstRestLower(null));
        assertEquals("", UtilsTextual.toUcFirstRestLower(""));
        assertEquals(" ", UtilsTextual.toUcFirstRestLower(" "));

        assertEquals("Foo", UtilsTextual.toUcFirstRestLower("foo"));
        assertEquals("Foo", UtilsTextual.toUcFirstRestLower("Foo"));
        assertEquals("Foo", UtilsTextual.toUcFirstRestLower("FOO"));
        assertEquals("Foo bar baz", UtilsTextual.toUcFirstRestLower("FOO Bar baz"));
    }

    @Test
    public void toLcFirst() {
        assertEquals(null, UtilsTextual.toLcFirst(null));
        assertEquals("", UtilsTextual.toLcFirst(""));
        assertEquals(" ", UtilsTextual.toLcFirst(" "));

        assertEquals("foo", UtilsTextual.toLcFirst("foo"));
        assertEquals("foo", UtilsTextual.toLcFirst("Foo"));
        assertEquals("fOO", UtilsTextual.toLcFirst("FOO"));
        assertEquals("fOO Bar baz", UtilsTextual.toLcFirst("FOO Bar baz"));
    }

    @Test
    public void isLcFirst() {
        assertFalse(UtilsTextual.isLcFirst(null));
        assertFalse(UtilsTextual.isLcFirst(""));
        assertFalse(UtilsTextual.isLcFirst(" "));
        assertFalse(UtilsTextual.isLcFirst("Foo"));
        assertFalse(UtilsTextual.isLcFirst(" foo"));

        assertTrue(UtilsTextual.isLcFirst("foo"));
        assertTrue(UtilsTextual.isLcFirst("fOO"));
    }

    @Test
    public void isUcFirstRestLower() {
        assertFalse(UtilsTextual.isUcFirstRestLower(null));
        assertFalse(UtilsTextual.isUcFirstRestLower(""));
        assertFalse(UtilsTextual.isUcFirstRestLower(" "));
        assertFalse(UtilsTextual.isUcFirstRestLower("fOO"));
        assertFalse(UtilsTextual.isUcFirstRestLower("foo"));
        assertFalse(UtilsTextual.isUcFirstRestLower("FOO"));
        assertFalse(UtilsTextual.isUcFirstRestLower(" Foo"));

        assertTrue(UtilsTextual.isUcFirstRestLower("Foo"));
        assertTrue(UtilsTextual.isUcFirstRestLower("Foo bar"));
    }

    @Test
    @Ignore
    public void isUpperCamelCase() {
        assertFalse(UtilsTextual.isUpperCamelCase(null));
        assertFalse(UtilsTextual.isUpperCamelCase(""));
        assertFalse(UtilsTextual.isUpperCamelCase(" "));
        assertFalse(UtilsTextual.isUpperCamelCase("f"));
        assertFalse(UtilsTextual.isUpperCamelCase("F"));
        assertFalse(UtilsTextual.isUpperCamelCase("FOO"));
        assertFalse(UtilsTextual.isUpperCamelCase("foo"));
        assertFalse(UtilsTextual.isUpperCamelCase("Foo-bar"));
        assertFalse(UtilsTextual.isUpperCamelCase("FooBar "));
        assertFalse(UtilsTextual.isUpperCamelCase("Foo"));

        assertTrue(UtilsTextual.isUpperCamelCase("FooBar"));
        assertTrue(UtilsTextual.isUpperCamelCase("FooBarBaz"));
    }

    @Test
    @Ignore
    public void isCamelCase() {
        assertFalse(UtilsTextual.isCamelCase(null));
        assertFalse(UtilsTextual.isCamelCase(""));
        assertFalse(UtilsTextual.isCamelCase(" "));
        assertFalse(UtilsTextual.isCamelCase("f"));
        assertFalse(UtilsTextual.isCamelCase("F"));
        assertFalse(UtilsTextual.isCamelCase("FOO"));
        assertFalse(UtilsTextual.isCamelCase("foo"));
        assertFalse(UtilsTextual.isCamelCase("Foo-bar"));
        assertFalse(UtilsTextual.isCamelCase("FooBar "));
        assertFalse(UtilsTextual.isCamelCase("Foo"));

        assertTrue(UtilsTextual.isCamelCase("fooBar"));
        assertTrue(UtilsTextual.isCamelCase("fooBarBaz"));
        assertTrue(UtilsTextual.isCamelCase("FooBar"));
        assertTrue(UtilsTextual.isCamelCase("FooBarBaz"));
    }

    @Test
    @Ignore
    public void splitCamelCaseIntoWords() {
    }


    @Test
    @Ignore
    public void getOperatorAtOffset() {
    }

    @Test
    @Ignore
    public void getStartOfOperatorAtOffset() {
    }

    @Test
    public void getWordAtOffset() {
        assertNull(UtilsTextual.getWordAtOffset(null, 0, true));
        assertNull(UtilsTextual.getWordAtOffset(null, 0, false));
        assertNull(UtilsTextual.getWordAtOffset("", 0, true));
        assertNull(UtilsTextual.getWordAtOffset("", 0, false));
        assertNull(UtilsTextual.getWordAtOffset("foo", 3, true));
        assertNull(UtilsTextual.getWordAtOffset("foo", 3, false));
        assertNull(UtilsTextual.getWordAtOffset("foo", 4, true));
        assertNull(UtilsTextual.getWordAtOffset("foo", 4, false));
        assertNull(UtilsTextual.getWordAtOffset("foo", -1, true));
        assertNull(UtilsTextual.getWordAtOffset("foo", -1, false));
        assertNull(UtilsTextual.getWordAtOffset(" ", 0, true));
        assertNull(UtilsTextual.getWordAtOffset("    bar", 3, true));

        assertEquals("foo", UtilsTextual.getWordAtOffset("foo", 0, true));
        assertEquals("foo", UtilsTextual.getWordAtOffset("foo", 1, true));
        assertEquals("foo", UtilsTextual.getWordAtOffset("foo", 2, true));
        assertEquals("bar", UtilsTextual.getWordAtOffset("foo bar", 4, true));
        assertEquals("bar", UtilsTextual.getWordAtOffset("foo bar", 5, true));
        assertEquals("bar", UtilsTextual.getWordAtOffset("foo bar", 6, true));
    }

    @Test
    public void getSubString() {
        assertEquals("foo", UtilsTextual.getSubString("foo", 0, 3));
        assertEquals("foo", UtilsTextual.getSubString("foo", 0, 4));
        assertEquals("f", UtilsTextual.getSubString("foo", 0, 1));
        assertEquals("fo", UtilsTextual.getSubString("foo", 0, 2));
        assertEquals("foo\nbar", UtilsTextual.getSubString("foo\nbar", 0, 7));

        assertNull(UtilsTextual.getSubString("foo", -1, 4));
        assertNull(UtilsTextual.getSubString("foo", 5, 8));
        assertNull(UtilsTextual.getSubString("", 0, 3));
        assertNull(UtilsTextual.getSubString("", 0, 0));
        assertNull(UtilsTextual.getSubString("foo", 3, 2));
    }

    @Test
    public void subStringCount() {
        assertEquals(0, UtilsTextual.subStringCount("", ""));
        assertEquals(0, UtilsTextual.subStringCount("", "foo"));
        assertEquals(0, UtilsTextual.subStringCount("foo", ""));
        assertEquals(0, UtilsTextual.subStringCount("foo", "bar"));
        assertEquals(2, UtilsTextual.subStringCount("foo", "o"));
        assertEquals(2, UtilsTextual.subStringCount("foo bar baz", "ba"));
    }

    @Test
    public void getCharBeforeOffset() {
        assertEquals("", UtilsTextual.getCharBeforeOffset("foo", 0));
        assertEquals("", UtilsTextual.getCharBeforeOffset("foo", -1));
        assertEquals("f", UtilsTextual.getCharBeforeOffset("foo", 1));
        assertEquals("o", UtilsTextual.getCharBeforeOffset("foo", 3));
        assertEquals("o", UtilsTextual.getCharBeforeOffset("foo", 4));
    }

    @Test
    public void getCharAfterOffset() {
        assertEquals("f", UtilsTextual.getCharAfterOffset("foo", -1));
        assertEquals("o", UtilsTextual.getCharAfterOffset("foo", 0));
        assertEquals("o", UtilsTextual.getCharAfterOffset("foo", 1));
        assertEquals("", UtilsTextual.getCharAfterOffset("foo", 3));
        assertEquals("", UtilsTextual.getCharAfterOffset("foo", 4));
    }

    @Test
    public void getOffsetEndOfWordAtOffset() {
        assertEquals(3, UtilsTextual.getOffsetEndOfWordAtOffset("foo bar", 0));
        assertEquals(3, UtilsTextual.getOffsetEndOfWordAtOffset("foo bar", 2));
        assertEquals(3, UtilsTextual.getOffsetEndOfWordAtOffset("foo bar", 3));
        assertEquals(7, UtilsTextual.getOffsetEndOfWordAtOffset("foo bar", 4));

        assertEquals(0, UtilsTextual.getOffsetEndOfWordAtOffset("foo", -1));

        assertEquals(0, UtilsTextual.getOffsetEndOfWordAtOffset("", 20));
        assertEquals(3, UtilsTextual.getOffsetEndOfWordAtOffset("foo", 20));
    }

    @Test
    @Ignore
    public void getStartOfWordAtOffset() {
    }

    @Test
    @Ignore
    public void extractLines() {
    }

    @Test
    @Ignore
    public void joinLines() {
    }

    @Test
    @Ignore
    public void removeLineBreaks() {
    }

    @Test
    @Ignore
    public void replaceLast() {
    }

    @Test
    @Ignore
    public void formatAmountDigits() {
    }

    @Test
    @Ignore
    public void hasDuplicateLines() {
    }

    @Test
    @Ignore
    public void reduceDuplicateLines() {
    }

    @Test
    @Ignore
    public void getLeadWhitespace() {
    }

    @Test
    @Ignore
    public void extractQuotedStrings() {
    }

    @Test
    @Ignore
    public void getPregMatches() {
    }
}