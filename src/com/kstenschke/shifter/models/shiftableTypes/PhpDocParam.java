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

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.utils.UtilsEnvironment;
import com.kstenschke.shifter.utils.UtilsPhp;
import static org.apache.commons.lang.StringUtils.trim;

/**
 * PHP DOC @param comment
 */
public class PhpDocParam {

    /**
     * Check whether given string represents a PHP variable
     *
     * @param  str     String to be checked
     * @return boolean
     */
    public static Boolean isPhpDocParamLine(String str) {
        str = trim(str);

        return str.startsWith("*") && str.contains("@param");
    }

    public static Boolean containsDataType(String str) {
        str = trim(str.toLowerCase());

        return     str.contains("array")
                || str.contains("bool")
                || str.contains("float")
                || str.contains("int")
                || str.contains("resource")
                || str.contains("string")
                || str.contains("object")
                || str.contains("null");
    }

    public static Boolean containsVariableName(String str) {
        return     str.contains("$");
    }

    /**
     * @param  str
     * @return String
     */
    private static String extractVariableName(String str) {
        return trim(str.replace("* @param", "")).split(" ")[0];
    }

    /**
     * Guess (by variable name) and insert data type
     *
     * @param  line     e.g. "* @param $var"
     * @return string
     */
    public static String getShifted(String line) {
        String variableName = trim(extractVariableName(line).replace("$", ""));

        return insertDataTypeIntoParamLine(line, UtilsPhp.guessDataTypeByParameterName(variableName));
    }

    private static String insertDataTypeIntoParamLine(String line, String dataType) {
        return line.replace("@param", "@param " + dataType);
    }

    public static boolean shiftSelectedPhpDocInDocument(final ActionContainer actionContainer) {
        if (PhpDocComment.isPhpDocComment(actionContainer.selectedText) && PhpDocComment.containsAtParam(actionContainer.selectedText)) {
            final String shifted = PhpDocComment.getShifted(actionContainer.selectedText);
            if (!shifted.equals(actionContainer.selectedText)) {
                // PHP DOC comment block: guess missing data shiftableTypes by resp. variable names
                actionContainer.writeUndoable(
                        new Runnable() {
                            @Override
                            public void run() {
                                actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, shifted);
                                UtilsEnvironment.reformatSelection(actionContainer.editor, actionContainer.project);
                            }
                        },
                        "Shift Php DOC comment");

                return true;
            }
        }
        if (!actionContainer.selectedText.contains("\n")
          && DocCommentType.isDocCommentTypeLineContext(actionContainer.selectedText)
          && isPhpDocParamLine(actionContainer.selectedText)
          && !containsDataType(actionContainer.selectedText)) {
            String variableName = trim(extractVariableName(actionContainer.selectedText).replace("$", ""));
            final String dataType     = UtilsPhp.guessDataTypeByParameterName(variableName);
            if (!dataType.equals("unknown")) {
                // PHP DOC @param caretLine w/o data type, e.g. "* @param $name"
                actionContainer.writeUndoable(
                        new Runnable() {
                            @Override
                            public void run() {
                                actionContainer.document.replaceString(actionContainer.offsetSelectionStart, actionContainer.offsetSelectionEnd, insertDataTypeIntoParamLine(actionContainer.selectedText, dataType));
                            }
                        },
                        "Shift Php DOC param");

                return true;
            }
        }
        return false;
    }
}