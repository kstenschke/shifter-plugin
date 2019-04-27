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
import com.kstenschke.shifter.models.ShiftableTypes;
import com.kstenschke.shifter.models.entities.AbstractShiftable;

import javax.annotation.Nullable;

// Toggle surrounding parenthesis: "(" and ")" ===> "[" and "]" ===> "{" and "}" ===> "(" and ")" ...
public class Parenthesis extends AbstractShiftable {

    private ActionContainer actionContainer;

    public final String ACTION_TEXT = "Shift Parenthesis";

    // Constructor
    public Parenthesis(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    public Parenthesis getInstance() {
        if (null == actionContainer) return null;

        String str = actionContainer.selectedText.trim();

        return
                isWrappedInRoundBrackets(str) ||
                isWrappedInSquareBrackets(str) ||
                isWrappedInCurlyBrackets(str)
                    ? this : null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.PARENTHESIS;
    }

    public String getShifted(
            String str,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        if (isWrappedInRoundBrackets(str))
            return "[" + str.substring(1, str.length() - 1) + "]";

        if (isWrappedInSquareBrackets(str))
            return "{" + str.substring(1, str.length() - 1) + "}";

        // Is wrapped in curly brackets
        return "(" + str.substring(1, str.length() - 1) + ")";
    }

    private static boolean isWrappedInRoundBrackets(String str) {
        return str.startsWith("(") && str.endsWith(")");
    }

    private static boolean isWrappedInSquareBrackets(String str) {
        return str.startsWith("[") && str.endsWith("]");
    }

    private static boolean isWrappedInCurlyBrackets(String str) {
        return str.startsWith("{") && str.endsWith("}");
    }
}