package com.kstenschke.shifter.utils;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsFileTest {

    @Test
    @Ignore
    public void extractFileExtension() {
    }

    @Test
    public void isPhpFile() {
        assertFalse(UtilsFile.isCssFile(null));
        assertFalse(UtilsFile.isPhpFile("php"));
        assertFalse(UtilsFile.isPhpFile(".php"));
        assertFalse(UtilsFile.isPhpFile("a"));
        assertFalse(UtilsFile.isPhpFile("a.txt"));
        assertFalse(UtilsFile.isPhpFile("a.php."));
        assertFalse(UtilsFile.isPhpFile("a.php.x"));

        assertTrue(UtilsFile.isPhpFile("a.php"));
        assertTrue(UtilsFile.isPhpFile("/a.php"));
        assertTrue(UtilsFile.isPhpFile("A.php"));
    }

    @Test
    public void isCssFile() {
        assertFalse(UtilsFile.isCssFile(null));
        assertFalse(UtilsFile.isCssFile("css"));
        assertFalse(UtilsFile.isCssFile(".css"));
        assertFalse(UtilsFile.isCssFile(".scss"));
        assertFalse(UtilsFile.isCssFile("a.css."));
        assertFalse(UtilsFile.isCssFile("a"));
        assertFalse(UtilsFile.isCssFile("a.scss."));
        assertFalse(UtilsFile.isCssFile("a.css.x"));
        assertFalse(UtilsFile.isCssFile("a.scss.x"));
        assertFalse(UtilsFile.isCssFile("a.txt"));
        assertFalse(UtilsFile.isCssFile("/.scss"));

        assertTrue(UtilsFile.isCssFile("a.css"));
        assertTrue(UtilsFile.isCssFile("a.scss"));
        assertTrue(UtilsFile.isCssFile("/a.css"));
        assertTrue(UtilsFile.isCssFile("/a.scss"));
        assertTrue(UtilsFile.isCssFile("A.CSS"));
        assertTrue(UtilsFile.isCssFile("A.SCSS"));
    }

    @Test
    public void isJavaScriptFile() {
        assertFalse(UtilsFile.isJavaScriptFile(null, true));
        assertFalse(UtilsFile.isJavaScriptFile("js", true));
        assertFalse(UtilsFile.isJavaScriptFile(".js", true));
        assertFalse(UtilsFile.isJavaScriptFile("a.js.", true));
        assertFalse(UtilsFile.isJavaScriptFile("a", true));
        assertFalse(UtilsFile.isJavaScriptFile("a.js.x", true));
        assertFalse(UtilsFile.isJavaScriptFile("a.txt", true));

        assertFalse(UtilsFile.isJavaScriptFile(null, false));
        assertFalse(UtilsFile.isJavaScriptFile("js", false));
        assertFalse(UtilsFile.isJavaScriptFile(".js", false));
        assertFalse(UtilsFile.isJavaScriptFile("a.js.", false));
        assertFalse(UtilsFile.isJavaScriptFile("a", false));
        assertFalse(UtilsFile.isJavaScriptFile("a.js.x", false));
        assertFalse(UtilsFile.isJavaScriptFile("a.txt", false));

        assertTrue(UtilsFile.isJavaScriptFile("a.js", true));
        assertTrue(UtilsFile.isJavaScriptFile("/a.js", true));
        assertTrue(UtilsFile.isJavaScriptFile("A.js", true));

        assertTrue(UtilsFile.isJavaScriptFile("a.js", false));
        assertTrue(UtilsFile.isJavaScriptFile("/a.js", false));
        assertTrue(UtilsFile.isJavaScriptFile("A.js", false));
    }

    @Test
    @Ignore
    public void getFileStreamAsString() {
    }
}