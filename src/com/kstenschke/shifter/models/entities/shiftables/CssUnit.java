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
import com.kstenschke.shifter.utils.UtilsMap;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;

// Pixel value class
public class CssUnit extends AbstractShiftable {

    private ActionContainer actionContainer;

    public static final String ACTION_TEXT = "Shift CSS Unit";

    private static final String UNIT_CM   = "cm";
    private static final String UNIT_EM   = "em";
    private static final String UNIT_IN   = "in";
    private static final String UNIT_MM   = "mm";
    private static final String UNIT_PC   = "pc";
    private static final String UNIT_PT   = "pt";
    private static final String UNIT_PX   = "px";
    private static final String UNIT_REM  = "rem";
    private static final String UNIT_VH   = "vh";
    private static final String UNIT_VMAX = "vmax";
    private static final String UNIT_VMIN = "vmin";
    private static final String UNIT_VW   = "vw";

    // Constructor
    public CssUnit(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable (selected text not a CSS length value)
    public CssUnit getInstance() {
        if (null == actionContainer) return null;

        String str = actionContainer.selectedText;

        return str.matches("[0-9]*(%|cm|em|in|pt|px|rem|vw|vh|vmin|vmax)") ? this : null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.CSS_UNIT;
    }

    public static boolean isCssUnit(String str) {
        return str.matches("(%|cm|em|in|pt|px|rem|vw|vh|vmin|vmax)");
    }

    /**
     * @param  value    The full length value, post-fixed by its unit
     * @param  moreCount
     * @param  leadWhitespace
     * @return String   Length (em / px / pt / cm / in / rem / vw / vh / vmin / vmax) value shifted up or down by 1 unit
     */
    public String getShifted(
            String value,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        // Get int from PX value
        String unit = detectUnit(value);
        try {
            int numericValue = "".equals(unit)
                    ? Integer.parseInt(value)
                    : Integer.parseInt(value.replace(unit, ""));

            // Shift up/down by 1
            numericValue = numericValue + (actionContainer.isShiftUp ? 1 : -1);

            // Prepend w/ unit again
            return Integer.toString(numericValue).concat(unit);
        } catch(NumberFormatException e) {
            // Silence
        }

        return value;
    }

    @NotNull
    private String detectUnit(String value) {
        // 4-digit units
        if (value.endsWith(UNIT_VMAX) || value.endsWith(UNIT_VMIN)) return value.substring(value.length() - 4);

        // 3-digit units
        if (value.endsWith(UNIT_REM)) return value.substring(value.length() - 3);

        // 2-digit units
        if (   value.endsWith(UNIT_CM) || value.endsWith(UNIT_EM)
            || value.endsWith(UNIT_IN) || value.endsWith(UNIT_MM)
            || value.endsWith(UNIT_PC) || value.endsWith(UNIT_PT) || value.endsWith(UNIT_PX)
            || value.endsWith(UNIT_VH) || value.endsWith(UNIT_VW)
        ) {
            return value.substring(value.length() - 2);
        }

        return "";
    }

    /**
     * @param  stylesheet   CSS content
     * @return String       most prominently used unit of given stylesheet, 'px' if none used yet
     */
    public static String determineMostProminentUnit(String stylesheet) {
        HashMap<String, Integer> map = new HashMap<>();

        map.put(UNIT_CM,   StringUtils.countMatches(stylesheet,UNIT_CM + ";"));
        map.put(UNIT_EM,   StringUtils.countMatches(stylesheet,UNIT_EM + ";"));
        map.put(UNIT_IN,   StringUtils.countMatches(stylesheet,UNIT_IN + ";"));
        map.put(UNIT_MM,   StringUtils.countMatches(stylesheet,UNIT_MM + ";"));
        map.put(UNIT_PC,   StringUtils.countMatches(stylesheet,UNIT_PC + ";"));
        map.put(UNIT_PT,   StringUtils.countMatches(stylesheet,UNIT_PT + ";"));
        map.put(UNIT_PX,   StringUtils.countMatches(stylesheet,UNIT_PX + ";"));
        map.put(UNIT_REM,  StringUtils.countMatches(stylesheet,UNIT_REM + ";"));
        map.put(UNIT_VW,   StringUtils.countMatches(stylesheet,UNIT_VW + ";"));
        map.put(UNIT_VH,   StringUtils.countMatches(stylesheet,UNIT_VH + ";"));
        map.put(UNIT_VMIN, StringUtils.countMatches(stylesheet,UNIT_VMIN + ";"));
        map.put(UNIT_VMAX, StringUtils.countMatches(stylesheet,UNIT_VMAX + ";"));

        return UtilsMap.getSumOfValues(map) == 0 ? "px" : UtilsMap.getKeyOfHighestValue(map);
    }
}