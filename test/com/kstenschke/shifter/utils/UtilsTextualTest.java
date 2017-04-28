package com.kstenschke.shifter.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UtilsTextualTest {

    @Test
    public void testIsAllUppercase() throws Exception {
        assertTrue(UtilsTextual.isAllUppercase("ALL UPPERCASE SENTENCE."));

        assertFalse(UtilsTextual.isAllUppercase("all lowercase sentence."));
        assertFalse(UtilsTextual.isAllUppercase("aCamelCasedWord"));
        assertFalse(UtilsTextual.isAllUppercase("A MIXED case Sentence."));
        assertFalse(UtilsTextual.isAllUppercase("some,words,separated,by,commas,all,words,are,lower-cased"));
    }

    @Test
    public void testIsMultiLine() throws Exception {
        assertTrue(UtilsTextual.isMultiLine("line one\nline two"));
        assertTrue(UtilsTextual.isMultiLine("\n"));

        assertFalse(UtilsTextual.isMultiLine(""));
        assertFalse(UtilsTextual.isMultiLine("A"));
        assertFalse(UtilsTextual.isMultiLine("A single line."));

        assertFalse(UtilsTextual.isMultiLine(null));
    }

    @Test
    public void testSortLines() throws Exception {
        // @todo implement
    }

    @Test
    public void testContainsCaseInSensitive() throws Exception {
        assertTrue(UtilsTextual.containsCaseInSensitive("hello world", "World"));
        assertTrue(UtilsTextual.containsCaseInSensitive("hello world", "WORLD"));
        assertTrue(UtilsTextual.containsCaseInSensitive("hello WORLD", "world"));

        assertFalse(UtilsTextual.containsCaseInSensitive("hello world", "x"));
        assertFalse(UtilsTextual.containsCaseInSensitive("hello world", "X"));
        assertFalse(UtilsTextual.containsCaseInSensitive("", "X"));

        assertFalse(UtilsTextual.containsCaseInSensitive(null, "X"));
    }

    @Test
    public void testContainsOnly() throws Exception {
        assertTrue(UtilsTextual.containsOnly("a", new String[]{"a"}));
        assertTrue(UtilsTextual.containsOnly("aa", new String[]{"a"}));
        assertTrue(UtilsTextual.containsOnly("aaa", new String[]{"a"}));

        assertTrue(UtilsTextual.containsOnly("abc", new String[]{"a", "b", "c"}));

        assertFalse(UtilsTextual.containsOnly("abc", new String[]{"a"}));
        assertFalse(UtilsTextual.containsOnly("abc", new String[]{"a", "b"}));

        assertFalse(UtilsTextual.containsOnly(null, new String[]{"a", "b"}));
    }

    @Test
    public void testIsWrappedIntoQuotes() throws Exception {
        assertTrue(UtilsTextual.isWrappedIntoQuotes("\"x\""));
        assertTrue(UtilsTextual.isWrappedIntoQuotes("\"\""));

        assertTrue(UtilsTextual.isWrappedIntoQuotes("'x'"));
        assertTrue(UtilsTextual.isWrappedIntoQuotes("''"));

        assertTrue(UtilsTextual.isWrappedIntoQuotes("\"'\""));

        assertTrue(UtilsTextual.isWrappedIntoQuotes("'"));
        assertTrue(UtilsTextual.isWrappedIntoQuotes("\""));

        assertFalse(UtilsTextual.isWrappedIntoQuotes("x"));
        assertFalse(UtilsTextual.isWrappedIntoQuotes(" \"x\""));
        assertFalse(UtilsTextual.isWrappedIntoQuotes("\t\"x\""));

        assertFalse(UtilsTextual.isWrappedIntoQuotes(null));
    }

    @Test
    public void testIsWrappedWith() throws Exception {
        assertTrue(UtilsTextual.isWrappedWith("'", "'", false, false));
        assertTrue(UtilsTextual.isWrappedWith("''", "'", true, false));
        assertTrue(UtilsTextual.isWrappedWith("'hello world'", "'", true, true));

        assertFalse(UtilsTextual.isWrappedWith("'", "'", true, false));
        assertFalse(UtilsTextual.isWrappedWith("'", "'", true, true));
        assertFalse(UtilsTextual.isWrappedWith("'", "'", false, true));

        assertFalse(UtilsTextual.isWrappedWith("''", "'", true, true));
        assertFalse(UtilsTextual.isWrappedWith("''", "'", false, true));

        assertFalse(UtilsTextual.isWrappedWith(null, "'", false, false));
        assertFalse(UtilsTextual.isWrappedWith(null, "'", true, false));
        assertFalse(UtilsTextual.isWrappedWith(null, "'", true, true));
        assertFalse(UtilsTextual.isWrappedWith(null, "'", false, true));
    }

    @Test
    public void testContainsSlashes() throws Exception {
        assertTrue(UtilsTextual.containsSlashes("http://www.xxx.ch/"));
        assertTrue(UtilsTextual.containsSlashes("A single quote is written \\ ' and a backslash \\"));

        assertFalse(UtilsTextual.containsSlashes("some,words,separated,by,commas,all,words,are,lower-cased"));
        assertFalse(UtilsTextual.containsSlashes("all lowercase sentence"));
        assertFalse(UtilsTextual.containsSlashes("ALL UPPERCASE SENTENCE."));

        assertFalse(UtilsTextual.containsSlashes(""));
        assertFalse(UtilsTextual.containsSlashes(null));
    }

    @Test
    public void testContainsQuotes() throws Exception {
        assertTrue(UtilsTextual.containsQuotes("'"));
        assertTrue(UtilsTextual.containsQuotes("'This sentence is single-quoted'"));
        assertTrue(UtilsTextual.containsQuotes("\"This sentence is double-quoted\""));

        assertFalse(UtilsTextual.containsQuotes("all lowercase sentence"));
        assertFalse(UtilsTextual.containsQuotes("aCamelCasedWord"));
        assertFalse(UtilsTextual.containsQuotes("A MIXED case Sentence."));

        assertFalse(UtilsTextual.containsQuotes(""));
        assertFalse(UtilsTextual.containsQuotes(null));
    }

    @Test
    public void testSwapSlashes() throws Exception {
        assertEquals(null, UtilsTextual.swapSlashes(null));

        assertEquals("", UtilsTextual.swapSlashes(""));
        assertEquals("x", UtilsTextual.swapSlashes("x"));

        assertEquals("\\/", UtilsTextual.swapSlashes("/\\"));

        assertEquals("http://www.domain.com/", UtilsTextual.swapSlashes("http:\\\\www.domain.com\\"));
        assertEquals("http:\\\\www.domain.com\\", UtilsTextual.swapSlashes("http://www.domain.com/"));

        assertEquals(null, UtilsTextual.swapSlashes(null));
    }

    @Test
    public void testSwapQuotes() throws Exception {
        assertEquals("", UtilsTextual.swapQuotes(""));
        assertEquals("\"", UtilsTextual.swapQuotes("'"));
        assertEquals("'", UtilsTextual.swapQuotes("\""));

        assertEquals("i say \"bam\"", UtilsTextual.swapQuotes("i say \'bam\'"));
        assertEquals("you say \'hey\'", UtilsTextual.swapQuotes("you say \"hey\""));
        assertEquals("\'hey\"BAM!\"\'", UtilsTextual.swapQuotes("\"hey\'BAM!\'\""));

        assertEquals(null, UtilsTextual.swapQuotes(null));
    }

    @Test
    public void testToUcFirst() throws Exception {
        assertEquals("Bam bam hey", UtilsTextual.toUcFirst("bam bam hey"));
        assertEquals("Bam bam hey", UtilsTextual.toUcFirst("BAM BAM HEY"));

        assertEquals("", UtilsTextual.toUcFirst(""));
        assertEquals(null, UtilsTextual.toUcFirst(null));
    }

    @Test
    public void testToLcFirst() throws Exception {
        assertEquals("bam bam hey", UtilsTextual.toLcFirst("Bam bam hey"));
        assertEquals("bam bam hey", UtilsTextual.toLcFirst("bam bam hey"));

        assertEquals("", UtilsTextual.toLcFirst(""));
        assertEquals(null, UtilsTextual.toUcFirst(null));
    }

    @Test
    public void testIsLcFirst() throws Exception {
        assertTrue(UtilsTextual.isLcFirst("bam bam hey"));

        assertFalse(UtilsTextual.isLcFirst("Bam bam hey"));
        assertFalse(UtilsTextual.isLcFirst("BAM BAM HEY"));
    }

    @Test
    public void testIsUcFirst() throws Exception {
        assertTrue(UtilsTextual.isUcFirst("Bam bam hey"));
        assertTrue(UtilsTextual.isUcFirst(""));

        assertFalse(UtilsTextual.isUcFirst("bam bam hey"));
        assertFalse(UtilsTextual.isUcFirst("BAM BAM HEY"));
    }

    @Test
    public void testIsUpperCamelCase() throws Exception {
        assertTrue(UtilsTextual.isUpperCamelCase("BamHey"));

        assertFalse(UtilsTextual.isUpperCamelCase("Bamhey"));
        assertFalse(UtilsTextual.isUpperCamelCase("BH"));

        assertFalse(UtilsTextual.isUpperCamelCase(""));
        assertFalse(UtilsTextual.isUpperCamelCase(null));
    }

    @Test
    public void testIsLowerCamelCase() throws Exception {
        assertTrue(UtilsTextual.isLowerCamelCase("bamHey"));

        assertFalse(UtilsTextual.isLowerCamelCase("bamhey"));
        assertFalse(UtilsTextual.isLowerCamelCase("Bh"));
        assertFalse(UtilsTextual.isLowerCamelCase("bh"));

        assertFalse(UtilsTextual.isLowerCamelCase("BamHey"));
        assertFalse(UtilsTextual.isLowerCamelCase("Bamhey"));
        assertFalse(UtilsTextual.isLowerCamelCase("BH"));

        assertFalse(UtilsTextual.isLowerCamelCase(""));
        assertFalse(UtilsTextual.isLowerCamelCase(null));
    }

    @Test
    public void testIsCommaSeparatedList() throws Exception {
//        assertTrue(UtilsTextual.isCommaSeparatedList("some,words,separated,by,commas,all,words,are,lower-cased"));
//        assertTrue(UtilsTextual.isCommaSeparatedList("SOME,WORDS,SEPARATED,BY,COMMAS,ALL,WORDS,ARE,UPPERCASED"));
//        assertTrue(UtilsTextual.isCommaSeparatedList("some,WORDS,separated,BY,commas,THE,words,ARE,mixed,CASED"));
//
//        assertFalse(UtilsTextual.isCommaSeparatedList("ALL UPPERCASE SENTENCE."));
//        assertFalse(UtilsTextual.isCommaSeparatedList("all lowercase sentence."));
//        assertFalse(UtilsTextual.isCommaSeparatedList("aCamelCasedWord"));
    }

    @Test
    public void testGetOperatorAtOffset() throws Exception {
        assertEquals("+", UtilsTextual.getOperatorAtOffset("1 + 2", 2));
        assertEquals("-", UtilsTextual.getOperatorAtOffset("2 - 1", 2));
        assertEquals("*", UtilsTextual.getOperatorAtOffset("2 * 1", 2));
        assertEquals("/", UtilsTextual.getOperatorAtOffset("2 / 1", 2));
        assertEquals("%", UtilsTextual.getOperatorAtOffset("2 % 1", 2));
        assertEquals("<", UtilsTextual.getOperatorAtOffset("2 < 1", 2));
        assertEquals(">", UtilsTextual.getOperatorAtOffset("2 > 1", 2));

        assertNull("+", UtilsTextual.getOperatorAtOffset("1 + 2", 0));
        assertNull("-", UtilsTextual.getOperatorAtOffset("2 - 1", 0));
        assertNull("*", UtilsTextual.getOperatorAtOffset("2 * 1", 0));
        assertNull("/", UtilsTextual.getOperatorAtOffset("2 / 1", 0));
        assertNull("%", UtilsTextual.getOperatorAtOffset("2 % 1", 0));
        assertNull("<", UtilsTextual.getOperatorAtOffset("2 < 1", 0));
        assertNull(">", UtilsTextual.getOperatorAtOffset("2 > 1", 0));

        assertNull("+", UtilsTextual.getOperatorAtOffset("1  + 2", 2));
        assertNull("-", UtilsTextual.getOperatorAtOffset("2  - 1", 2));
        assertNull("*", UtilsTextual.getOperatorAtOffset("2  * 1", 2));
        assertNull("/", UtilsTextual.getOperatorAtOffset("2  / 1", 2));
        assertNull("%", UtilsTextual.getOperatorAtOffset("2  % 1", 2));
        assertNull("<", UtilsTextual.getOperatorAtOffset("2  < 1", 2));
        assertNull(">", UtilsTextual.getOperatorAtOffset("2  > 1", 2));
    }

    @Test
    public void testGetStartOfOperatorAtOffset() throws Exception {

    }

    @Test
    public void testGetWordAtOffset() throws Exception {
//        assertEquals("bam", UtilsTextual.getWordAtOffset("bam lam, ding, dong, what a bam.", 0));
//        assertEquals("lam", UtilsTextual.getWordAtOffset("bam lam, ding, dong, what a bam.", 5));
//        assertEquals("ding", UtilsTextual.getWordAtOffset("bam lam, ding, dong, what a bam.", 10));
//
//        assertEquals(null, UtilsTextual.getWordAtOffset("bam lam, ding, dong, what a bam.", -1));
    }

    @Test
    public void testGetSubString() throws Exception {

    }

    @Test
    public void testGetCharBeforeOffset() throws Exception {

    }

    @Test
    public void testGetCharAfterOffset() throws Exception {

    }

    @Test
    public void testGetStartOfWordAtOffset() throws Exception {

    }

    @Test
    public void testExtractLines() throws Exception {

    }

    @Test
    public void testExtractLine() throws Exception {

    }

    @Test
    public void testExtractLineAroundOffset() throws Exception {
        assertEquals("bam bam,", UtilsTextual.extractLineAroundOffset("bam bam,\ney,\nwhat a bam.", 3));
        assertEquals("ey,", UtilsTextual.extractLineAroundOffset("bam bam,\ney,\nwhat a bam.", 10));
        assertEquals("what a bam.", UtilsTextual.extractLineAroundOffset("bam bam,\ney,\nwhat a bam.", 15));
    }

    @Test
    public void testJoinLines() throws Exception {
        List<String> lines = new ArrayList<String>();
        lines.add("what");
        lines.add("a");
        lines.add("bam");

        assertEquals("whatabam", UtilsTextual.joinLines(lines).toString());
    }

    @Test
    public void testRemoveLineBreaks() throws Exception {
        assertEquals("bam bam,ey,what a bam.", UtilsTextual.removeLineBreaks("bam bam,\ney,\nwhat a bam."));
        assertEquals("bam bam,ey,what a bam.", UtilsTextual.removeLineBreaks("bam bam,\rey,\rwhat a bam."));
        assertEquals("bam bam,ey,what a bam.", UtilsTextual.removeLineBreaks("bam bam,\ney,\rwhat a bam."));
        assertEquals("bam bam,ey,what a bam.", UtilsTextual.removeLineBreaks("bam bam,\n\rey,\n\rwhat a bam."));
    }

    @Test
    public void testReplaceLast() throws Exception {
        assertEquals("bam bam, ey, what a bam.", UtilsTextual.replaceLast("bam bam, ey, what a bam bam.", " bam", ""));
        assertEquals("bam bam, ey hey", UtilsTextual.replaceLast("bam bam, ey hey", "x", "y"));
    }

    @Test
    public void testFormatAmountDigits() throws Exception {
        assertEquals(5, UtilsTextual.formatAmountDigits("7", 5).length());
        assertEquals(5, UtilsTextual.formatAmountDigits("55", 5).length());
        assertEquals(5, UtilsTextual.formatAmountDigits("321", 5).length());

        assertEquals(2, UtilsTextual.formatAmountDigits("55", 1).length());
        assertEquals(3, UtilsTextual.formatAmountDigits("321", 1).length());
    }
}