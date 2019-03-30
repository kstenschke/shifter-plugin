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

/**
 * Trailing (to line of code) comment - shifting = move comment to new empty caretLine above
 */
public class TrailingComment extends ShiftableTypeAbstract {

    public static final String ACTION_TEXT = "Shift trailing Comment";

    /**
     * @param  word     String to be shifted currently
     * @return boolean
     * TODO    maybe later - implement also for multi-line selections(?)
     */
    public boolean isApplicable(String word, String postfixChar, Boolean isLastLineInDocument) {
        if (
            null == word ||
            !word.contains("//") ||
            (!isLastLineInDocument && !"\n".equals(postfixChar))
        ) {
            return false;
        }

        String[] parts = word.split("//");

        return parts.length == 2 && parts[0].length() > 0 && parts[1].length() > 0;
    }

    public String getShifted(
            String selection,
            ActionContainer actionContainer,
            Integer moreCount,
            String leadingWhiteSpace
    ) {
        String[] parts = selection.split("//");

        return leadingWhiteSpace + "//" + parts[1] + "\n" + parts[0];
    }
}