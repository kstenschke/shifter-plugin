/*
 * Copyright 2011-2017 Kay Stenschke
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
package com.kstenschke.shifter.models.shiftertypes;

import com.kstenschke.shifter.utils.UtilsArray;
import com.kstenschke.shifter.utils.UtilsTextual;
import com.kstenschke.shifter.utils.natorder.NaturalOrderComparator;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Quoted String.
 */
public class SeparatedList {

    public static boolean isSeparatedList(String word) {
        return isSeparatedList(word, ",");
    }

    public static boolean isSeparatedList(String str, String delimiter) {
        if (!str.contains(delimiter)) {
            return false;
        }

        // If the string is quoted: detect whether items are quoted each
        // => there must be (amountCommas+1)*2 quotes altogether
        // Ex:  "a","b"         => 1 comma, 4 quotes
        //      "a","b","c","d" => 3 commas, 8 quotes
        // Otherwise it should be treated as a quoted string and not as a list.
        if (UtilsTextual.isWrappedIntoQuotes(str)) {
            String quoteChar     = str.substring(0, 1);
            int amountQuotes     = StringUtils.countMatches(str, quoteChar);
            int amountDelimiters = StringUtils.countMatches(str, delimiter);

            if (amountQuotes != (amountDelimiters + 1) * 2) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param  selectedText
     * @param  delimiterSplitPattern
     * @param  delimiterGlue
     * @param  shiftUp
     * @return Given delimiter separated list, sorted (natural) alphabetically ascending / descending
     */
    public static String sortSeparatedList(String selectedText, String delimiterSplitPattern, String delimiterGlue, boolean shiftUp) {
        String[] items = selectedText.split(delimiterSplitPattern);

        if (items.length == 2) {
            // Only 2 items: treat as tupel - always toggle order
            return items[1] + delimiterGlue + items[0];
        }

        List itemsList = Arrays.asList(items);
        Collections.sort(itemsList, new NaturalOrderComparator());
        items = (String[])itemsList.toArray();

        if (!shiftUp) {
            Collections.reverse(Arrays.asList(items));
        }

        if (UtilsArray.hasDuplicateItems(items) && JOptionPane.showConfirmDialog(
                null,
                "Duplicated items detected. Reduce to single occurrences?",
                "Reduce duplicate items?",
                JOptionPane.OK_CANCEL_OPTION
        ) == JOptionPane.OK_OPTION) {
            items = UtilsArray.reduceDuplicateItems(items);
        }

        return UtilsArray.implode(items, delimiterGlue);
    }

    /**
     * @param word
     * @param delimiterSplitPattern
     * @param delimiterGlue
     * @param shiftUp
     * @return
     */
    public static String getShifted(String word, String delimiterSplitPattern, String delimiterGlue, boolean shiftUp) {
        return sortSeparatedList(word, delimiterSplitPattern, delimiterGlue, shiftUp);
    }
}