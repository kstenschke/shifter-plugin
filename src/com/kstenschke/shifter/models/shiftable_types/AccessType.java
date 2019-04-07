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

/**
 * Pixel value class
 */
public class AccessType extends ShiftableTypeAbstract  {

    private ActionContainer actionContainer;

    public static final String ACTION_TEXT = "Shift Access Type";

    private StaticWordType accessTypes;

    // Constructor
    public AccessType(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    public AccessType getShiftableType() {
        if ("@".equals(actionContainer.prefixChar)) return null;

        String word = actionContainer.selectedText;
        if (null == word) {
            return null;
        }
        String[] keywordsAccessType = {"public", "private", "protected"};
        accessTypes = new StaticWordType(keywordsAccessType);

        return accessTypes.hasWord(word) ? this : null;
    }

    /**
     * @param  value    The full length value, post-fixed by its unit
     * @return String   Length (em / px / pt / cm / in / rem / vw / vh / vmin / vmax) value shifted up or down by 1 unit
     */
    public String getShifted(
            String value,
            ActionContainer actionContainer,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        return accessTypes.getShifted(value, actionContainer.isShiftUp);
    }
}