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
package com.kstenschke.shifter.models.shiftableTypes;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.ui.components.JBList;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.trim;

/**
 * Included comment shiftableTypes:
 *
 * 1. Single-line comment => // ...
 * 2. Block comment       => /* ... *\/
 * 3. HTML comment        => <!-- ... -->
 */
public class Comment {

    /**
     * @param  str     String to be shifted currently
     * @return boolean
     */
    public static boolean isComment(String str) {
        str = str.trim();

        return str.startsWith("//") || isBlockComment(str);
    }

    public static boolean isBlockComment(String str) {
        str = str.trim();

        return str.startsWith("/*") && str.endsWith("*/");
    }

    public static boolean isMultipleSingleLineComments(String str) {
        if (!str.contains("\n")) {
            return false;
        }
        String lines[] = str.split("\n");
        for (String line : lines) {
            if (!trim(line).startsWith("//")) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPhpBlockComment(String str) {
        str = str.trim();

        return str.startsWith("<?php /*") && str.endsWith("*/ ?>");
    }

    public static  boolean isHtmlComment(String str) {
        str = str.trim();

        return str.startsWith("<!--") && str.endsWith("-->");
    }

    /**
     * @param  str
     * @return String
     */
    public static String getShifted(String str, String filename, Project project) {
        if (filename != null && UtilsFile.isPhpFile(filename) && isPhpBlockComment(str)) {
            // PHP Block-comment inside PHP or PHTML: convert to HTML comment
            return "<!-- " + str.substring(8, str.length() - 5).trim() + " -->";
        }

        // Default comment shifting: toggle among single-line and block-comment style
        str = str.trim();

        if (str.startsWith("//")) {
            if (!str.endsWith(" ")) {
                str += " ";
            }
            // Convert single line comment to block comment
            return "/*" + str.substring(2) + "*/";
        }

        str = str.substring(2, str.length() - 2);

        // This is a single-lined block comment, otherwise shiftMultiLineBlockCommentInDocument() is called
        // Convert block- to single line comment
        if (str.contains("\n")) {
            return "//" + str.replace("\n", " ");
        }

        return "//" + (str.startsWith("* ")
            // Convert a single-lined block-comment in DOC format to "// ..." and not "//* ..."
            ? str.substring(1)
            : str);
    }

    /**
     * @param str
     * @return
     */
    public static String getPhpBlockCommentFromHtmlComment(String str) {
        return "<?php /* " + str.substring(4, str.length() - 3).trim() + " */ ?>";
    }

    /**
     * Shift multi-lined block comment into single line comment(s)
     * Show popup and perform selected shifting mode: join lines into 1 or convert into multiple single line comments
     *
     * @param str
     * @param project
     * @param document
     * @param offsetStart
     * @param offsetEnd
     */
    public static void shiftMultiLineBlockCommentInDocument(final String str, final Project project, final Document document, final int offsetStart, final int offsetEnd) {
        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_MULTILINE_BLOCK_COMMENT_TO_ONE_SINGLE_COMMENT);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_MULTILINE_BLOCK_COMMENT_TO_MULTIPLE_SINGLE_COMMENTS);

        final Object[] options = shiftOptions.toArray(new String[shiftOptions.size()]);
        final JBList modes = new JBList(options);

        PopupChooserBuilder popup = JBPopupFactory.getInstance().createListPopupBuilder(modes);
        popup.setTitle(StaticTexts.POPUP_TITLE_SHIFT).setItemChoosenCallback(new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        // Callback when item chosen
                        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                                    public void run() {
                                        final int index = modes.getSelectedIndex();
                                        String shifted = index == 0
                                            ? shiftMultipleBlockCommentLines(str, true)
                                            : shiftMultipleBlockCommentLines(str, false);

                                        document.replaceString(offsetStart, offsetEnd, shifted);
                                    }
                                },
                                null, null);
                    }
                });
            }
        }).setMovable(true).createPopup().showCenteredInCurrentWindow(project);
    }

    public static void shiftMultipleSingleLineCommentsInDocument(final String str, final Project project, final Document document, final int offsetStart, final int offsetEnd) {
        List<String> shiftOptions = new ArrayList<String>();
        shiftOptions.add(StaticTexts.SHIFT_OPTION_MULTIPLE_LINE_COMMENTS_MERGE);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_MULTIPLE_LINE_COMMENTS_TO_BLOCK_COMMENT);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_MULTIPLE_LINE_SORT_ASCENDING);
        shiftOptions.add(StaticTexts.SHIFT_OPTION_MULTIPLE_LINE_SORT_DESCENDING);

        final Object[] options = shiftOptions.toArray(new String[shiftOptions.size()]);
        final JBList modes = new JBList(options);

        PopupChooserBuilder popup = JBPopupFactory.getInstance().createListPopupBuilder(modes);
        popup.setTitle(StaticTexts.POPUP_TITLE_SHIFT).setItemChoosenCallback(new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        // Callback when item chosen
                        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                                    public void run() {
                                        final int index = modes.getSelectedIndex();
                                        String shifted;

                                        switch (index) {
                                            case 0:
                                                shifted = mergeMultipleLineComments(str);
                                                break;
                                            case 1:
                                                shifted = convertMultipleLineCommentsToBlockComment(str);
                                                break;
                                            case 2:
                                                shifted = sortLineComments(str, true);
                                                break;
                                            case 3:
                                            default:
                                                shifted = sortLineComments(str, false);
                                                break;
                                        }
                                        document.replaceString(offsetStart, offsetEnd, shifted);
                                    }
                                },
                                null, null);
                    }
                });
            }
        }).setMovable(true).createPopup().showCenteredInCurrentWindow(project);
    }

    private static String shiftMultipleBlockCommentLines(String str, boolean merge) {
        str = trim(str).substring(2);

        String lines[] = str.split("\n");
        int index = 0;

        String result = "//";
        for (String line : lines) {
            line = trim(line);
            if (line.startsWith("* ")) {
                line = line.substring(2);
            }
            line = trim(line);
            if (index == 0 && line.startsWith("*")) {
                line = trim(line.substring(1));
            }
            if (!line.isEmpty()) {
                result += merge
                    ? " " + line
                    : (index == 0 ? "" : "\n") + "// " + line;
            }
            index++;
        }

        // Remove trailing "*/"
        result = result.substring(0, result.length() - 2);

        if (!merge) {
            // Remove empty comment lines
            result = result.replace("\n//\n", "\n");
            if (result.startsWith("//\n")) {
                result = result.substring(3);
            }
            if (result.endsWith("\n// ")) {
                result = result.substring(0, result.length() - 4);
            }
        }

        return result;
    }

    private static String convertMultipleLineCommentsToBlockComment(String str) {
        String lines[] = str.split("\n");
        String result  = "";
        int index = 0;
        for (String line : lines) {
            result += (index == 0 ? "" : "\n") + " * " + trim(trim(line).substring(2));
            index++;
        }

        return "/**\n" + result + "\n */";
    }

    private static String mergeMultipleLineComments(String str) {
        String lines[] = str.split("\n");
        String result  = "";
        int index = 0;
        for (String line : lines) {
            result += (index == 0 ? "" : " ") + trim(trim(line).substring(2));
            index++;
        }

        return "// " + result;
    }

    private static String sortLineComments(String str, boolean shiftUp) {
        List<String> lines       = Arrays.asList(str.split("\n"));
        List<String> shiftedList = UtilsTextual.sortLinesNatural(lines, shiftUp);
        String result = "";
        int index = 0;
        for(String line : shiftedList) {
            result += (index == 0 ? "" : "\n") + line;
            index++;
        }

        return result;
    }
}