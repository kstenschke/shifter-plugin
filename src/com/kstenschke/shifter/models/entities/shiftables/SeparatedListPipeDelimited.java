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
import com.kstenschke.shifter.models.ShiftableSelectionWithPopup;
import com.kstenschke.shifter.models.ShiftableTypes;
import com.kstenschke.shifter.utils.UtilsTextual;

import org.apache.commons.lang.StringUtils;
import javax.annotation.Nullable;
import java.util.regex.Pattern;

// Separated list (delimiters e.g: ",", "|")
public class SeparatedListPipeDelimited extends SeparatedList {

    public final String ACTION_TEXT = "Shift List";

    // Constructor
    public SeparatedListPipeDelimited(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    public SeparatedListPipeDelimited getInstance(@Nullable Boolean checkIfShiftable) {
        if (// @todo make shiftable also in non-selection
            null == actionContainer.selectedText
        ) return null;

        String str = actionContainer.selectedText;
        String delimiter = "|";

        if (!str.contains(delimiter)
            || str.trim().length() == delimiter.length()
            || str.replaceAll(Pattern.quote(delimiter), "").length() == 0
        ) return null;

        if (!UtilsTextual.isWrappedWithQuotes(str)) return this;

        // If the string is quoted: detect whether items are quoted each
        // => there must be (amountCommas+1)*2 quotes altogether
        // Ex:  "a","b"         => 1 comma, 4 quotes
        //      "a","b","c","d" => 3 commas, 8 quotes
        // Otherwise it should be treated as a quoted string and not as a list.
        String quoteChar     = str.substring(0, 1);
        int amountQuotes     = StringUtils.countMatches(str, quoteChar);
        int amountDelimiters = StringUtils.countMatches(str, delimiter);

        if ((amountDelimiters + 1) * 2 != amountQuotes) return null;

        // Ensure not confusing w/ || of logical conjunctions
        LogicalConjunction logicalConjunction = new LogicalConjunction(actionContainer).getInstance(null);

        return null != logicalConjunction &&
               logicalConjunction.isOrLogic
                    ? null : this;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.SEPARATED_LIST;
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        new ShiftableSelectionWithPopup(actionContainer).sortListOrSwapQuotesOrInterpolateTypeScriptInDocument(
                "\\|(\\s)*",
                "|",
                null != new ConcatenationJsInTs(actionContainer).getInstance(),
                actionContainer.isShiftUp);
        return true;
    }
}