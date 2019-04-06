package com.kstenschke.shifter.models.shiftable_types;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommentTest {

    @Test
    public void isComment() {
        /*
        assertFalse(Comment.getShiftableType(null));
        assertFalse(Comment.getShiftableType(""));
        assertFalse(Comment.getShiftableType("/"));
        assertFalse(Comment.getShiftableType("; //"));
        assertFalse(Comment.getShiftableType("// comment\nnot a comment"));
        assertFalse(Comment.getShiftableType("/* not closed"));
        assertFalse(Comment.getShiftableType("<!-- not closed"));

        assertTrue(Comment.getShiftableType("//"));
        assertTrue(Comment.getShiftableType("// line comment"));
        assertTrue(Comment.getShiftableType("// multi-line comment\n// line 2"));
        // assertTrue(Comment.getShiftableType("/* block comment * / "));
        // assertTrue(Comment.getShiftableType("/** DOC comment * / "));
        // assertTrue(Comment.getShiftableType("/**\n DOC comment\n * / "));

        assertTrue(Comment.getShiftableType("\n//"));
        assertTrue(Comment.getShiftableType("\n// line comment"));
        assertTrue(Comment.getShiftableType("\n/* block comment * / "));
        assertTrue(Comment.getShiftableType("\n/** DOC comment * / "));
        */
    }

    @Test
    public void isBlockComment() {
        assertFalse(Comment.isBlockComment(null));

        assertFalse(Comment.isBlockComment("// "));
        assertFalse(Comment.isBlockComment("/* "));
        assertFalse(Comment.isBlockComment(" */"));
        assertFalse(Comment.isBlockComment(" /*/"));

        assertTrue(Comment.isBlockComment("/* */"));
        assertTrue(Comment.isBlockComment("/* Block comment */"));
        assertTrue(Comment.isBlockComment("/*\n Block comment\n */"));
        assertTrue(Comment.isBlockComment("/**/"));
        
        assertTrue(Comment.isBlockComment("/** DOC comment */"));
        assertTrue(Comment.isBlockComment("/** DOC comment **/"));
    }

    @Test
    @Ignore
    public void isMultipleSingleLineComments() {
    }

    @Test
    public void isPhpBlockComment() {
        assertFalse(Comment.isPhpBlockComment(null));

        assertFalse(Comment.isPhpBlockComment("<?php ?>"));
        assertFalse(Comment.isPhpBlockComment("<?php // ?>"));
        assertFalse(Comment.isPhpBlockComment("<?php /* ?>"));
        assertFalse(Comment.isPhpBlockComment("<?php */ ?>"));

        assertFalse(Comment.isPhpBlockComment("<? ?>"));
        assertFalse(Comment.isPhpBlockComment("<? // ?>"));
        assertFalse(Comment.isPhpBlockComment("<? /* ?>"));
        assertFalse(Comment.isPhpBlockComment("<? */ ?>"));
        assertFalse(Comment.isPhpBlockComment("<? /*/ ?>"));

        assertFalse(Comment.isPhpBlockComment("/* */"));
        assertFalse(Comment.isPhpBlockComment("/* Block comment */"));
        assertFalse(Comment.isPhpBlockComment("/*\n Block comment\n */"));
        assertFalse(Comment.isPhpBlockComment("/** DOC comment */"));

        assertFalse(Comment.isPhpBlockComment("foo<?php /* Block comment */ ?>"));
        assertFalse(Comment.isPhpBlockComment("foo<? /* Block comment */ ?>"));
        assertFalse(Comment.isPhpBlockComment("<?php /* Block comment */ ?>bar"));
        assertFalse(Comment.isPhpBlockComment("<? /* Block comment */ ?>bar"));

        assertTrue(Comment.isPhpBlockComment("<? /* */ ?>"));
        assertTrue(Comment.isPhpBlockComment("<? /* Block comment */ ?>"));
        assertTrue(Comment.isPhpBlockComment("<? /*\n Block comment\n */ ?>"));
        assertTrue(Comment.isPhpBlockComment("<? /**/ ?>"));
        
        assertTrue(Comment.isPhpBlockComment("<? /** DOC comment */ ?>"));
        assertTrue(Comment.isPhpBlockComment("<? /** DOC comment **/ ?>"));

        assertTrue(Comment.isPhpBlockComment("<?php /* */ ?>"));
        assertTrue(Comment.isPhpBlockComment("<?php /* Block comment */ ?>"));
        assertTrue(Comment.isPhpBlockComment("<?php /*\n Block comment\n */ ?>"));
        assertTrue(Comment.isPhpBlockComment("<?php /** DOC comment */ ?>"));
        assertTrue(Comment.isPhpBlockComment("<?php /** DOC comment **/ ?>"));
        assertTrue(Comment.isPhpBlockComment("<?php /**/ ?>"));
    }

    @Test
    public void isHtmlComment() {
        assertFalse(Comment.isHtmlComment(null));
        assertFalse(Comment.isHtmlComment("<!-->"));

        assertTrue(Comment.isHtmlComment("<!-- HTML comment -->"));
        assertTrue(Comment.isHtmlComment("<!--\n HTML comment\n -->"));
    }

    @Test
    @Ignore
    public void getShifted() {
    }

    @Test
    @Ignore
    public void getPhpBlockCommentFromHtmlComment() {
    }
}