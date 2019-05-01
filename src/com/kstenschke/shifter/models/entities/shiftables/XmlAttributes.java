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
import com.kstenschke.shifter.models.comparators.AlphanumComparator;
import com.kstenschke.shifter.utils.UtilsArray;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Multiple XML attribute-value pairs within one line
public class XmlAttributes extends AbstractShiftable {

    public static final String ACTION_TEXT = "Sort Attributes";

    // Constructor
    public XmlAttributes(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable: string must represent multiple XML attributes
    public XmlAttributes getInstance() {
        if (null == actionContainer ||
            // @todo make shiftable also in non-selection
            null == actionContainer.selectedText
        ) return null;

        String str = actionContainer.selectedText;

        return str.matches("([A-Za-z-_0-9]*[ ]*=[ ]*[\"'][A-Za-z-_0-9]*[\"'][ ]*){2,99}")
                ? this : null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.XML_ATTRIBUTES;
    }

    public String getShifted(
            String str,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        if (!disableIntentionPopup && QuotedString.containsEscapedQuotes(str)) return "";
        // @todo add intention popup

        // Shifted string is xml attributes that can be sorted and the quoting character can be toggled
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
        pairsList.sort(new AlphanumComparator());

        if (!actionContainer.isShiftUp) {
            //noinspection ArraysAsListWithZeroOrOneArgument
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