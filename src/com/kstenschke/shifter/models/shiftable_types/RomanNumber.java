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
import com.kstenschke.shifter.models.entities.RomanNumeral;
import com.kstenschke.shifter.models.ShiftableTypeAbstract;
import com.kstenschke.shifter.utils.UtilsTextual;

import javax.annotation.Nullable;

/**
 * Roman number class
 */
public class RomanNumber extends ShiftableTypeAbstract {

    private ActionContainer actionContainer;

    // Constructor
    public RomanNumber(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Does the given string represent a CSS length value?
    public RomanNumber getShiftableType() {
        String str = actionContainer.selectedText;
        return UtilsTextual.containsOnly(str, new String[]{"I", "V", "X", "L", "C", "D", "M"})
                ? this : null;
    }

    public String getShifted(
            String value,
            ActionContainer actionContainer,
            Integer moreCount,
            String leadingWhiteSpace
    ) {
        int intVal = new RomanNumeral(value).toInt();

        return 1 == intVal && !actionContainer.isShiftUp
                ? value
                : new RomanNumeral(actionContainer.isShiftUp ? intVal + 1 : intVal -1).toString();
    }
}