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
package com.kstenschke.shifter.models.shiftable_types;

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.utils.UtilsPhp;

import static org.apache.commons.lang.StringUtils.trim;

/**
 * PHPDoc @param comment
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
                // PHPDoc comment block: guess missing data shiftable_types by resp. variable names
                actionContainer.writeUndoable(
                        actionContainer.getRunnableReplaceSelection(shifted, true),
                        "Shift PHPDoc comment");
                return true;
            }
        }
        DocCommentType docCommentType = new DocCommentType(actionContainer);
        if (!actionContainer.selectedText.contains("\n")
          && docCommentType.isDocCommentTypeLineContext(actionContainer.selectedText)
          && isPhpDocParamLine(actionContainer.selectedText)
          && !containsDataType(actionContainer.selectedText)) {
            String variableName = trim(extractVariableName(actionContainer.selectedText).replace("$", ""));
            final String dataType     = UtilsPhp.guessDataTypeByParameterName(variableName);
            if (!"unknown".equals(dataType)) {
                // PHPDoc @param line w/o data type, e.g. "* @param $name"
                actionContainer.writeUndoable(
                        actionContainer.getRunnableReplaceSelection(insertDataTypeIntoParamLine(actionContainer.selectedText, dataType)),
                        "Shift PHPDoc param");
                return true;
            }
        }
        return false;
    }
}