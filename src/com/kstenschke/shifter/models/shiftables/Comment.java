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
package com.kstenschke.shifter.models.shiftables;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.ui.components.JBList;
import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.AbstractShiftable;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.trim;

/**
 * Included comment shiftables:
 *
 * 1. Single-line comment => // ...
 * 2. Block comment       => /* ... *\/
 * 3. HTML comment        => <!-- ... -->
 */
public class Comment extends AbstractShiftable {

    private ActionContainer actionContainer;

    // Constructor
    public Comment(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    public static final String ACTION_TEXT = "Shift Comment";

    public Comment getShiftableType() {
        String str = actionContainer.selectedText;

        return isComment(str) ? this : null;
    }

    public String getShifted(
            String str,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        if (updateInDocument) {
            shiftSelectionInDocument(actionContainer);
            return null;
        }

        if (null != actionContainer.filename &&
            UtilsFile.isPhpFile(actionContainer.filename) &&
            isPhpBlockComment(actionContainer.selectedText)
        ) {
            // PHP Block-comment inside PHP or PHTML: convert to HTML comment
            return "<!-- " + actionContainer.selectedText.substring(8, actionContainer.selectedText.length() - 5).trim() + " -->";
        }

        // Default comment shifting: toggle among single-line and block-comment style
        str = actionContainer.selectedText.trim();

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

    // Shift selected comment
    private void shiftSelectionInDocument(ActionContainer actionContainer) {
        AbstractShiftable shiftableType;
        if (UtilsTextual.isMultiLine(actionContainer.selectedText)) {
            if (null != (shiftableType = new JsDoc(actionContainer).getShiftableType())) {
                shiftableType.getShifted(
                        actionContainer.selectedText,
                        null,
                        null,
                        true,
                        false);
                return;
            }
            if (isBlockComment(actionContainer.selectedText)) {
                shiftMultiLineBlockCommentInDocument(actionContainer);
                return;
            }
            if (isMultipleSingleLineComments(actionContainer.selectedText)) {
                shiftMultipleSingleLineCommentsInDocument(actionContainer);
                return;
            }
        }

        shiftableType = new Comment(actionContainer);
        actionContainer.writeUndoable(
                actionContainer.getRunnableReplaceSelection(
                        shiftableType.getShifted(
                                actionContainer.selectedText,
                                null,
                                null,
                                true,
                                false)),
                ACTION_TEXT);
    }

    private boolean isComment(String str) {
        if (null == str) {
            return false;
        }

        str = str.trim();

        return str.startsWith("//")
                ? !str.contains("\n") || isMultipleSingleLineComments(str)
                : isBlockComment(str);
    }

    public static boolean isBlockComment(String str) {
        return isBlockComment(str, false, false);
    }

    static boolean isBlockComment(String str, boolean allowDocBlockComment, boolean commentSignsNeedSpaces) {
        if (null == str) {
            return false;
        }

        str = str.trim();

        String innerWrap  = commentSignsNeedSpaces ? " " : "";

        boolean isBlockComment = str.startsWith("/*" + innerWrap) && str.endsWith(innerWrap + "*/")
                && str.indexOf("/*") != str.length() - 3;

        return allowDocBlockComment
                ? isBlockComment || (str.startsWith("/**" + innerWrap) && str.endsWith(innerWrap + "*/"))
                : isBlockComment;
    }

    public static boolean isMultipleSingleLineComments(String str) {
        if (null == str) {
            return false;
        }

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
        if (null == str) {
            return false;
        }

        str = str.trim();

        return (str.startsWith("<? /*") || str.startsWith("<?php /*"))
                && str.endsWith("*/ ?>")
                && str.indexOf("/*") != str.lastIndexOf("*/") - 1;
    }

    public static  boolean isHtmlComment(String str) {
        if (null == str) {
            return false;
        }

        str = str.trim();

        return str.startsWith("<!--") && str.endsWith("-->")
                && str.indexOf("<!--") != str.length() -5;
    }

    public static String getPhpBlockCommentFromHtmlComment(String str) {
        if (null == str) {
            return "<?php /* */ ?>";
        }

        int length = str.length();

        return length > 3
                ? "<?php /* " + str.substring(4, length - 3).trim() + " */ ?>"
                : "<?php /* " + str.trim() + " */ ?>";
    }

    /**
     * Shift multi-lined block comment into single line comment(s)
     * Show popup and perform selected shifting mode: join lines into 1 or convert into multiple single line comments
     *
     * @param actionContainer
     */
    public static void shiftMultiLineBlockCommentInDocument(final ActionContainer actionContainer) {
        List<String> shiftOptions = new ArrayList<>();
        shiftOptions.add(StaticTexts.SHIFT_MULTILINE_BLOCK_COMMENT_TO_ONE_SINGLE_COMMENT);
        shiftOptions.add(StaticTexts.SHIFT_MULTILINE_BLOCK_COMMENT_TO_MULTIPLE_SINGLE_COMMENTS);

        final Object[] options = shiftOptions.toArray(new String[0]);

        final JBList modes = new JBList(options);
        PopupChooserBuilder popup = new PopupChooserBuilder(modes);
        popup.setTitle(StaticTexts.POPUP_TITLE_SHIFT).setItemChoosenCallback(
                () -> ApplicationManager.getApplication().runWriteAction(() -> {
            // Callback when item chosen
            CommandProcessor.getInstance().executeCommand(actionContainer.project, () -> {
                final int index = modes.getSelectedIndex();
                final String shiftedBlockCommentLines = 0 == index
                    ? shiftMultipleBlockCommentLines(actionContainer.selectedText, true)
                    : shiftMultipleBlockCommentLines(actionContainer.selectedText, false);

                actionContainer.writeUndoable(
                        actionContainer.getRunnableReplaceSelection(shiftedBlockCommentLines),
                        ACTION_TEXT);
            },
                    null, null);
        })).setMovable(true).createPopup().showCenteredInCurrentWindow(actionContainer.project);
    }

    public static void shiftMultipleSingleLineCommentsInDocument(final ActionContainer actionContainer) {
        List<String> shiftOptions = new ArrayList<>();
        shiftOptions.add(StaticTexts.SHIFT_MULTIPLE_LINE_COMMENTS_MERGE);
        shiftOptions.add(StaticTexts.SHIFT_MULTIPLE_LINE_COMMENTS_TO_BLOCK_COMMENT);
        shiftOptions.add(StaticTexts.SHIFT_MULTIPLE_LINE_SORT_ASCENDING);
        shiftOptions.add(StaticTexts.SHIFT_MULTIPLE_LINE_SORT_DESCENDING);

        final Object[] options = shiftOptions.toArray(new String[0]);
        final JBList modes = new JBList(options);

        //PopupChooserBuilder popup = JBPopupFactory.getInstance().createListPopupBuilder(modes);
        PopupChooserBuilder popup = new PopupChooserBuilder(modes);

        popup.setTitle(StaticTexts.POPUP_TITLE_SHIFT).setItemChoosenCallback(() -> ApplicationManager.getApplication().runWriteAction(() -> {
            // Callback when item chosen
            CommandProcessor.getInstance().executeCommand(actionContainer.project, () -> {
                final int index = modes.getSelectedIndex();
                String shifted;

                switch (index) {
                    case 0:
                        shifted = mergeMultipleLineComments(actionContainer.selectedText);
                        break;
                    case 1:
                        shifted = convertMultipleLineCommentsToBlockComment(actionContainer.selectedText);
                        break;
                    case 2:
                        shifted = sortLineComments(actionContainer.selectedText, false);
                        break;
                    case 3:
                    default:
                        shifted = sortLineComments(actionContainer.selectedText, true);
                        break;
                }
                actionContainer.writeUndoable(actionContainer.getRunnableReplaceSelection(shifted), ACTION_TEXT);
            },
                    null, null);
        })).setMovable(true).createPopup().showCenteredInCurrentWindow(actionContainer.project);
    }

    private static String shiftMultipleBlockCommentLines(String str, boolean merge) {
        str = trim(str).substring(2);

        String lines[] = str.split("\n");
        int index = 0;

        StringBuilder result = new StringBuilder("//");
        for (String line : lines) {
            line = trim(line);
            if (line.startsWith("* ")) {
                line = line.substring(2);
            }
            line = trim(line);
            if (0 == index && line.startsWith("*")) {
                line = trim(line.substring(1));
            }
            if (!line.isEmpty()) {
                result.append(merge
                    ? " " + line
                    : (0 == index ? "" : "\n") + "// " + line);
            }
            index++;
        }

        // Remove trailing "*/"
        result = new StringBuilder(result.substring(0, result.length() - 2));

        if (!merge) {
            // Remove empty comment lines
            result = new StringBuilder(result.toString().replace("\n//\n", "\n"));
            if (result.toString().startsWith("//\n")) {
                result = new StringBuilder(result.substring(3));
            }
            if (result.toString().endsWith("\n// ")) {
                result = new StringBuilder(result.substring(0, result.length() - 4));
            }
        }

        return result.toString();
    }

    private static String convertMultipleLineCommentsToBlockComment(String str) {
        String lines[] = str.split("\n");
        StringBuilder result  = new StringBuilder();
        int index = 0;
        for (String line : lines) {
            result.append(0 == index ? "" : "\n").append(" * ").append(trim(trim(line).substring(2)));
            index++;
        }

        return "/**\n" + result + "\n */";
    }

    private static String mergeMultipleLineComments(String str) {
        String lines[] = str.split("\n");
        StringBuilder result  = new StringBuilder();
        int index = 0;
        for (String line : lines) {
            result.append(0 == index ? "" : " ").append(trim(trim(line).substring(2)));
            index++;
        }

        return "// " + result;
    }

    private static String sortLineComments(String str, boolean reverse) {
        List<String> lines = Arrays.asList(str.split("\n"));
        UtilsTextual.sortLinesNatural(lines, reverse);
        StringBuilder result = new StringBuilder();
        int index = 0;
        for (String line : lines) {
            result.append(0 == index ? "" : "\n").append(line);
            index++;
        }

        return result.toString();
    }
}