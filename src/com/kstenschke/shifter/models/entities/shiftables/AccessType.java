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
import com.kstenschke.shifter.models.ShiftablesEnum;
import com.kstenschke.shifter.models.entities.AbstractShiftable;
import com.kstenschke.shifter.models.entities.StaticWordType;
import org.jetbrains.annotations.Nullable;

public class AccessType extends AbstractShiftable {

    public final String ACTION_TEXT = "Shift Access Type";

    private static StaticWordType accessTypes;

    // Constructor
    public AccessType(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    public AccessType getInstance(@Nullable Boolean checkIfShiftable) {
        return null != actionContainer &&
               detect(actionContainer.prefixChar, actionContainer.getStringToBeShifted())
                ? this : null;
    }

    public static boolean detect(String prefixChar, String wordToBeShifted) {
        if ("@".equals(prefixChar)) return false;

        String[] keywordsAccessType = {"public", "protected", "private"};
        accessTypes = new StaticWordType(keywordsAccessType);

        return accessTypes.hasWord(wordToBeShifted);
    }

    public ShiftablesEnum.Type getType() {
        return ShiftablesEnum.Type.ACCESS_TYPE;
    }

    /**
     * @param  value    The full length value, post-fixed by its unit
     * @return String   Length (em / px / pt / cm / in / rem / vw / vh / vmin / vmax) value shifted up or down by 1 unit
     */
    public String getShifted(
            String value,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        return accessTypes.getShifted(actionContainer.getStringToBeShifted(), actionContainer.isShiftUp);
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        String shifted = getShifted(actionContainer.getStringToBeShifted());
        actionContainer.writeUndoable(
            actionContainer.getRunnableReplaceSelection(shifted),
            ACTION_TEXT
        );
        return true;
    }
}