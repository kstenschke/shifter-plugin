/*
 * Copyright Kay Stenschke
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

import org.jetbrains.annotations.Nullable;

public class UtilsFile {

    public static String extractFileExtension(@Nullable String filename) {
        return extractFileExtension(filename, false);
    }

    /**
     * @param  filename         Filename from which to extract the extension
     * @param  toLowerCase      Convert extension to lower case?
     * @return The extension    Everything after the last "." in the full filename
     */
    public static String extractFileExtension(@Nullable String filename, boolean toLowerCase) {
        if (null == filename ||
            filename.isEmpty() ||
            filename.length() < 3 ||
            !filename.contains(".")
        ) {
            return "";
        }
        filename = getBasename(filename);
        if ("".equals(filename)) {
            return filename;
        }

        return toLowerCase
                ? filename.substring(filename.lastIndexOf('.') + 1).toLowerCase()
                : filename.substring(filename.lastIndexOf('.') + 1);
    }

    private static String getBasename(@Nullable String filename) {
        if (null == filename) {
            return "";
        }
        if (filename.contains("/")) {
            filename = filename.substring(filename.lastIndexOf("/") + 1);
        }
        if (filename.contains("\\")) {
            filename = filename.substring(filename.lastIndexOf("\\") + 1);
        }
        return filename;
    }

    private static boolean filenameEndsWithExtension(@Nullable String filename) {
        filename = getBasename(filename);
        if (null == filename || filename.isEmpty() || !filename.contains(".")) {
            return false;
        }

        String[] parts = filename.split("\\.");

        return parts.length > 1 && parts[0].length() > 0 && parts[1].length() >= 2;
    }

    public static boolean isPhpFile(@Nullable String filename) {
        filename = getBasename(filename).toLowerCase();
        return filenameEndsWithExtension(filename) && extractFileExtension(filename).matches("(php|phtml)");
    }

    public static boolean isCssFile(@Nullable String filename) {
        filename = getBasename(filename).toLowerCase();
        return filenameEndsWithExtension(filename) && extractFileExtension(filename).matches("(css|scss|sass|less|styl)");
    }

    public static boolean isJavaScriptFile(@Nullable String filename, boolean allowTypeScript) {
        filename = getBasename(filename).toLowerCase();
        if (!filenameEndsWithExtension(filename)) {
            return false;
        }

        return extractFileExtension(filename).matches(allowTypeScript ? "(js|ts)" : "(js)");
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
