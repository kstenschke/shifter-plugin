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
import com.kstenschke.shifter.models.ShiftableTypeAbstract;

import javax.annotation.Nullable;

import static org.apache.commons.lang.StringUtils.trim;

class PhpDocComment extends ShiftableTypeAbstract {

    private ActionContainer actionContainer;

    // Constructor
    public PhpDocComment(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Check whether given String is a PHP doc comment block
    public PhpDocComment getShiftableType() {
        String str = trim(actionContainer.selectedText);
        String lines[] = str.split("\n");

        return lines.length > 2 && str.startsWith("/**") &&
                str.endsWith("*/") && str.contains(" * ")
                ? this : null;
    }

    public String getShifted(
            String variable,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        String str = actionContainer.selectedText;
        String lines[] = str.split("\n");
        StringBuilder shifted = new StringBuilder();

        int indexLine = 1;
        PhpDocParam phpDocParam = new PhpDocParam(null);
        for (String line : lines) {
            if (containsAtParam(line) &&
                !phpDocParam.containsDataType(line) &&
                phpDocParam.containsVariableName(line)
            ) {
                // PHP doc @param comment that contains variable name but no data type: guess data type by variable name
                line = phpDocParam.getShifted(line);
            }
            shifted.append(line).append(indexLine < lines.length ? "\n" : "");
            indexLine++;
        }

        return shifted.toString();
    }

    static boolean containsAtParam(String str) {
        return str.contains("@param ");
    }
}