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
package com.kstenschke.shifter.models.entities.shiftables;

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.entities.AbstractShiftable;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsPhp;

import javax.annotation.Nullable;

import static org.apache.commons.lang.StringUtils.trim;

/**
 * PHPDoc @param comment
 */
public class PhpDocParam extends AbstractShiftable {

    private ActionContainer actionContainer;

    // Constructor
    public PhpDocParam(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Check whether given string represents a PHP variable
    public PhpDocParam getShiftableType() {
        if (!UtilsFile.isPhpFile(actionContainer.filename)) return null;

        String str = actionContainer.shiftCaretLine
                ? actionContainer.caretLine
                : actionContainer.selectedText;
        str = trim(str);

        return str.startsWith("*") && str.contains("@param")
                ? this : null;
    }

    public String getShifted(
            String str,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        if (null == actionContainer || actionContainer.shiftCaretLine)
            return getShiftedCaretLine(null == actionContainer ? str : actionContainer.caretLine);

        AbstractShiftable shiftableType;
        if (null != (shiftableType = new PhpDocComment(actionContainer).getShiftableType()) &&
            PhpDocComment.containsAtParam(actionContainer.selectedText)
        ) {
            final String shifted = shiftableType.getShifted(actionContainer.selectedText);
            if (!shifted.equals(actionContainer.selectedText)) {
                // PHPDoc comment block: guess missing data shiftables by resp. variable names
                actionContainer.writeUndoable(
                        actionContainer.getRunnableReplaceSelection(shifted, true),
                        "Shift PHPDoc comment");
                return null;
            }
        }

        DocCommentType docCommentType = new DocCommentType(actionContainer);
        actionContainer.shiftSelectedText = true;
        actionContainer.shiftCaretLine = false;

        if (!actionContainer.selectedText.contains("\n") &&
            docCommentType.isDocCommentTypeLineContext(actionContainer.selectedText) &&
            null != getShiftableType() &&
            !containsDataType(actionContainer.selectedText)
        ) {
            String variableName = trim(extractVariableName(actionContainer.selectedText).replace("$", ""));
            final String dataType     = UtilsPhp.guessDataTypeByParameterName(variableName);
            if (!"unknown".equals(dataType)) {
                // PHPDoc @param line w/o data type, e.g. "* @param $name"
                actionContainer.writeUndoable(
                        actionContainer.getRunnableReplaceSelection(insertDataTypeIntoParamLine(actionContainer.selectedText, dataType)),
                        "Shift PHPDoc param");
            }
        }
        return null;
    }

    public Boolean containsDataType(String str) {
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

    public Boolean containsVariableName(String str) {
        return str.contains("$");
    }

    /**
     * @param  line     e.g. "* @param $var"
     * @return string
     * @todo merge into other getShifted method
     */
    private static String getShiftedCaretLine(String line) {
        String variableName = trim(extractVariableName(line).replace("$", ""));

        return insertDataTypeIntoParamLine(line, UtilsPhp.guessDataTypeByParameterName(variableName));
    }

    private static String extractVariableName(String str) {
        return trim(str.replace("* @param", "")).split(" ")[0];
    }

    private static String insertDataTypeIntoParamLine(String line, String dataType) {
        return line.replace("@param", "@param " + dataType);
    }
}