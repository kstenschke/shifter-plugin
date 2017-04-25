package com.kstenschke.shifter.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsFileTest {

    @Test
    public void testExtractFileExtension() throws Exception {
        assertEquals("java", UtilsFile.extractFileExtension("test.java"));
        assertEquals("JAVA", UtilsFile.extractFileExtension("TEST.JAVA"));

        assertNull(UtilsFile.extractFileExtension(""));
        assertNull(UtilsFile.extractFileExtension("file_without_extension"));
        assertNull(UtilsFile.extractFileExtension("/home/docs/file_without_extension"));

        assertEquals(null, UtilsFile.extractFileExtension("./path/to/file_without_extension"));
    }

    @Test
    public void testFilenameEndsWithExtension() throws Exception {
        assertTrue( UtilsFile.filenameEndsWithExtension("index.php") );

        assertFalse( UtilsFile.filenameEndsWithExtension(".") );
        assertFalse( UtilsFile.filenameEndsWithExtension("./") );
        assertFalse( UtilsFile.filenameEndsWithExtension("/./") );
        assertFalse( UtilsFile.filenameEndsWithExtension("php") );
        assertFalse(UtilsFile.filenameEndsWithExtension(""));
    }

    @Test
    public void testIsPhpFile() throws Exception {
        assertTrue( UtilsFile.isPhpFile("index.php") );

        assertFalse( UtilsFile.isPhpFile("style.css") );
        assertFalse( UtilsFile.isPhpFile("php") );
        assertFalse( UtilsFile.isPhpFile(".php") );
    }

    @Test
    public void testIsCssFile() throws Exception {
        assertTrue( UtilsFile.isCssFile("style.css") );

        assertFalse( UtilsFile.isCssFile("index.php") );
        assertFalse( UtilsFile.isCssFile("css") );
        assertFalse( UtilsFile.isCssFile(".css") );
    }

    @Test
    public void testGetFileStreamAsString() throws Exception {

    }
}