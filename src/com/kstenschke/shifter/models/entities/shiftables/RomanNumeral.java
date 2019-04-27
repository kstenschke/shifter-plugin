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
import com.kstenschke.shifter.utils.UtilsTextual;

import javax.annotation.Nullable;

// Roman number: increment / decrement
public class RomanNumeral extends AbstractShiftable {

    private ActionContainer actionContainer;

    public final String ACTION_TEXT = "Shift Roman Numeral";

    // Constructor
    public RomanNumeral(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable: string must be a CSS length value
    public RomanNumeral getInstance() {
        if (null == actionContainer) return null;

        String str = actionContainer.selectedText;
        return UtilsTextual.containsOnly(str, new String[]{"I", "V", "X", "L", "C", "D", "M"})
                ? this : null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.ROMAN_NUMERAL;
    }

    public String getShifted(
            String value,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        int intVal = new com.kstenschke.shifter.models.entities.RomanNumeral(value).toInt();

        return 1 == intVal && !actionContainer.isShiftUp
                ? value
                : new com.kstenschke.shifter.models.entities.RomanNumeral(actionContainer.isShiftUp ? intVal + 1 : intVal -1).toString();
    }
}