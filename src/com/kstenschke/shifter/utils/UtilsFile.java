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

public class UtilsFile {

    /**
     * @param  filename         Filename from which to extract the extension
     * @param  toLowerCase
     * @return The extension    Everything after the last "." in the full filename
     */
    public static String extractFileExtension(String filename, boolean toLowerCase) {
        if (filename == null || filename.isEmpty() || filename.length() < 3 || !filename.contains(".")) {
            return null;
        }

        if (filename.contains("/")) {
            filename = filename.substring(filename.lastIndexOf("/") + 1);
        }
        if (filename.contains("\\")) {
            filename = filename.substring(filename.lastIndexOf("\\") + 1);
        }

        return toLowerCase
                ? filename.substring(filename.lastIndexOf('.') + 1).toLowerCase()
                : filename.substring(filename.lastIndexOf('.') + 1);
    }

    public static String extractFileExtension(String filename) {
        return extractFileExtension(filename, false);
    }

    public static boolean filenameEndsWithExtension(String filename) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            return false;
        }

        String[] parts = filename.split("\\.");

        return parts.length > 1 && parts[0].length() > 0 && parts[1].length() > 2;
    }

    public static boolean isPhpFile(String filename) {
        return filenameEndsWithExtension(filename) && extractFileExtension(filename).matches("(php|phtml)");
    }

    public static boolean isCssFile(String filename) {
        return filenameEndsWithExtension(filename) && extractFileExtension(filename).matches("(css|scss|sass|less|styl)");
    }

    /**
     * @param  is       Input stream
     * @return String   Full contents of given stream as string
     */
    public static String getFileStreamAsString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");

        return s.hasNext() ? s.next() : "";
    }
}