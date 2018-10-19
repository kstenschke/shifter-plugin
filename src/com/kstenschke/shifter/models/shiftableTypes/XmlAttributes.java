/*
 * Copyright 2011-2018 Kay Stenschke
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
package com.kstenschke.shifter.models.shiftableTypes;

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.comparators.AlphanumComparator;
import com.kstenschke.shifter.utils.UtilsArray;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Multiple XML attribute-value pairs within one line
 */
public class XmlAttributes {

    public static final String ACTION_TEXT = "Sort Attributes";

    private final ActionContainer actionContainer;

    /**
     * Constructor
     *
     * @param actionContainer
     */
    public XmlAttributes(ActionContainer actionContainer) {
        this.actionContainer = actionContainer;
    }

    /**
     * Check whether shifted string represents multiple xml attributes
     *
     * @param  str
     * @return boolean
     */
    public boolean isXmlAttributes(String str) {
        return str.matches("([A-Za-z-_0-9]*[ ]*=[ ]*[\"'][A-Za-z-_0-9]*[\"'][ ]*){2,99}");
    }

    /**
     * Shift: swap tupel parts
     *
     * @param  str      string to be shifted
     * @param  disableIntentionPopup
     * @return String   The shifted string
     */
    public String getShifted(String str, boolean disableIntentionPopup) {
        if (!disableIntentionPopup) {
            if (QuotedString.containsEscapedQuotes(str)) {
                // Shifted string is xml attributes that can be sorted and the quoting character can be toggled
                // @todo add intention popup
                return "";
            }
        }

        return getShiftedXmlAttributesReplacement(str);
    }

    @NotNull
    private String getShiftedXmlAttributesReplacement(String str) {
        while (str.contains(" =") || str.contains("= ")) {
            str = str.replace(" =", "=").replace("= ", "=");
        }
        while (str.contains("  ")) {
            str = str.replace("  ", " ");
        }

        // Detect value wrapper (single or double quote)
        String quote = UtilsTextual.subStringCount(str, "=\"") > 1 ? "\"" : "'";

        // Split into attribute-value pairs
        String[] attributeValuePairs = str.split("\" ");

        // Sort attribute-value pairs alphabetically by attribute name
        List pairsList = Arrays.asList(attributeValuePairs);
        // @note sorting itemsList, does also update items
        Collections.sort(pairsList, new AlphanumComparator());

        if (!actionContainer.isShiftUp) {
            Collections.reverse(Arrays.asList(pairsList));
        }

        attributeValuePairs = (String[]) pairsList.toArray();
        int index = 0;
        for (String attributeValuePair : attributeValuePairs) {
            if (!attributeValuePair.endsWith(quote)) {
                attributeValuePairs[index] = attributeValuePair + quote;
            }
            index++;
        }

        return UtilsArray.implode(attributeValuePairs, " ");
    }
}