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
import com.kstenschke.shifter.models.comparators.AlphanumComparator;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.utils.UtilsArray;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Separated list (delimiters e.g: ",", "|")
 */
public class SeparatedList extends ShiftableTypeAbstract {

    private ActionContainer actionContainer;

    // Constructor
    public SeparatedList(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    public SeparatedList getShiftableType() {
        String str = actionContainer.selectedText;
        String delimiter = actionContainer.delimiter;

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

        return (amountDelimiters + 1) * 2 == amountQuotes ? this : null;
    }

    /**
     * @return Given delimiter separated list, sorted (natural) alphabetically ascending / descending
     */
    public String getShifted(
            String word,
            ActionContainer actionContainer,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        String[] items = actionContainer.selectedText.split(actionContainer.delimiterSplitPattern);
        if (items.length == 2) {
            // Only 2 items: treat as tupel - always toggle order
            return items[1] + actionContainer.delimiterGlue + items[0];
        }

        List itemsList = Arrays.asList(items);
        // @note sorting itemsList, does also update items
        itemsList.sort(new AlphanumComparator());

        if (UtilsArray.hasDuplicateItems(items) && JOptionPane.showConfirmDialog(
                null,
                StaticTexts.MESSAGE_REDUCE_DUPLICATED_ITEMS,
                StaticTexts.TITLE_REDUCE_DUPLICATED_ITEMS,
                JOptionPane.OK_CANCEL_OPTION
        ) == JOptionPane.OK_OPTION) {
            items = UtilsArray.reduceDuplicateItems(items);
        }

        if (!actionContainer.isShiftUp) {
            Collections.reverse(Arrays.asList(items));
        }

        return UtilsArray.implode(items, actionContainer.delimiterGlue);
    }
}