/*
 * Copyright 2011-2018 Kay Stenschke
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

import com.kstenschke.shifter.utils.UtilsTextual;

/**
 * Logical conjunction (selected AND && / OR ||, w/ two operands)
 */
public class LogicalConjunction {

    static final public String ACTION_TEXT = "Toggle logical conjunction operands";

    public boolean isOrLogic = false;

    public boolean isLogicalConjunction(String str) {
        if (null == str || str.length() < 4) {
            return false;
        }
        if (str.indexOf("||") > 0 && UtilsTextual.subStringCount(str, "||") == 1) {
            isOrLogic = true;
            return true;
        }
        if (str.indexOf("&&") > 0 && UtilsTextual.subStringCount(str, "&&") == 1) {
            isOrLogic = false;
            return true;
        }

        return false;
    }

    public String swapOrder(String text) {
        String[] parts = text.split(isOrLogic ? "\\|\\|" : "&&");

        return parts[1].trim() + (isOrLogic ? " || " : " && ") + parts[0];
    }

    public String getShifted(String text) {
        return swapOrder(text);
    }
}