
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

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.AbstractShiftable;

import javax.annotation.Nullable;

public class OperatorSign extends AbstractShiftable {

    private ActionContainer actionContainer;

    // Constructor
    public OperatorSign(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    public OperatorSign getShiftableType() {
        String word = actionContainer.firstChar == null
                ? actionContainer.selectedText
                : actionContainer.firstChar;

        return null != word && word.length() == 1 && "+-<>*/%".contains(word)
                ? this : null;
    }

    /**
     * Shift mono-character string
     *
     * @param  word     Quoted word to be shifted
     * @return String
     */
    public String getShifted(
            String word,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        if ("-".equals(word)) return "+";
        if ("+".equals(word)) return "-";
        if ("<".equals(word)) return ">";
        if (">".equals(word)) return "<";
        if ("*".equals(word)) return "/";
        if ("/".equals(word)) return "*";

        return word;
    }

    public boolean isWhitespaceWrappedOperator(String str) {
        actionContainer.firstChar = String.valueOf(str.charAt(1));

        return Character.isWhitespace(str.charAt(0))
                && null != this.getShiftableType()
                && Character.isWhitespace(str.charAt(2));
    }
}