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
import com.kstenschke.shifter.utils.UtilsPhp;

import javax.annotation.Nullable;

import static org.apache.commons.lang.StringUtils.trim;

// PHPDoc @param comment
public class PhpDocParamContainingDataType extends PhpDocParam {

    private ActionContainer actionContainer;

    // Constructor
    public PhpDocParamContainingDataType(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable: string must be a PHP variable
    public PhpDocParamContainingDataType getInstance() {
        return (super.getInstance() == null ||
                !containsDataType(actionContainer.caretLine))
            ? null
            : this;
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