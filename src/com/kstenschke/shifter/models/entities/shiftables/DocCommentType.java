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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// DOC comment type class
public class DocCommentType extends AbstractShiftable {

    public final String ACTION_TEXT = "Shift DOC comment";

    // Constructor
    public DocCommentType(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable:
    // Check whether given String represents a data type (number / integer / string /...)
    // from a DOC comment (param / return /...)
    public AbstractShiftable getInstance() {
        if (!isDocCommentTypeLineContext(actionContainer.caretLine)) return null;

        AbstractShiftable shiftableType = new DocCommentTag(actionContainer);
        if (actionContainer.prefixChar.matches("@") && null != shiftableType.getInstance()) return shiftableType;

        shiftableType = new DocCommentDataType(actionContainer);
        if (null != shiftableType.getInstance()) return shiftableType;

        return null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.DOC_COMMENT_DATA_TYPE;
    }

    public String getShifted(
            String str,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        return new DocCommentDataType(actionContainer)
                .getShifted(str, moreCount, leadWhitespace);
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        return false;
    }

    /**
     * Check whether given String looks like a DOC comment line
     *
     * @param line Line the caret is at
     * @return boolean.
     */
    public boolean isDocCommentTypeLineContext(String line) {
        String allTags = new DocCommentTag(null).getAllTagsPiped();
        String regExPatternLine = "\\s*\\*\\s+@(" + allTags + ")\\s*";

        Matcher m = Pattern.compile(regExPatternLine).matcher(line.toLowerCase());

        return m.find();
    }
}