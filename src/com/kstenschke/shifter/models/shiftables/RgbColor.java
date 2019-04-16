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
package com.kstenschke.shifter.models.shiftables;

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.AbstractShiftable;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * RGB color class
 */
public class RgbColor extends AbstractShiftable {

    private ActionContainer actionContainer;

    public static final String ACTION_TEXT = "Shift RGB Color";

    // Constructor
    public RgbColor(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Check whether given string represents a hex RGB color, prefix must be "#"
    public RgbColor getShiftableType() {
        String str = actionContainer.selectedText;

        return !
               (!"#".equals(actionContainer.prefixChar) ||
               !(str.matches("[0-9a-fA-F]{3}") ||
               str.matches("[0-9a-fA-F]{6}")))
                ? this : null;
    }

    public String getShifted(
            String rgbStr,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        String rgbStrSixFold = rgbStr.length() == 3 ? sixfoldTripleColor(rgbStr) : rgbStr;

        if (actionContainer.isShiftUp && !isWhite(rgbStrSixFold)) return lightenRgbString(rgbStrSixFold);

        return isBlack(rgbStrSixFold) ? rgbStrSixFold : darkenRgbString(rgbStr);
    }

    /**
     * Check whether given String represents RGB white (fff, FFF, ffffff, FFFFFF)
     *
     * @param str RBG color string to be checked
     * @return boolean.
     */
    private static boolean isWhite(String str) {
        return "fff".equalsIgnoreCase(str) || "ffffff".equalsIgnoreCase(str);
    }

    /**
     * Check whether given String represents RGB black (000, 000000)
     *
     * @param str RGB color string
     * @return boolean.
     */
    private static boolean isBlack(String str) {
        return "000".equalsIgnoreCase(str) || "000000".equalsIgnoreCase(str);
    }

    /**
     * Convert three digit color string into six digits
     *
     * @param rgbStr RGB color string
     * @return String
     */
    private static String sixfoldTripleColor(String rgbStr) {
        String red   = rgbStr.substring(0, 1);
        String green = rgbStr.substring(1, 2);
        String blue  = rgbStr.substring(2, 3);

        return "".concat(red).concat(red).concat(green).concat(green).concat(blue).concat(blue);
    }

    /**
     * Get Color object of given RGB string
     *
     * @param rgbStr String representing an RGB color
     * @return Color object
     */
    @SuppressWarnings("UseJBColor")
    private static Color getColorFromRgbString(String rgbStr) {
        int red   = Integer.parseInt(rgbStr.substring(0, 2), 16);
        int green = Integer.parseInt(rgbStr.substring(2, 4), 16);
        int blue  = Integer.parseInt(rgbStr.substring(4, 6), 16);

        return new Color(red, green, blue);
    }

    /**
     * Shift given RGB color hex string to be lighter
     *
     * @param rgbStr String of RGB color
     * @return String
     */
    private static String lightenRgbString(String rgbStr) {
        Color lighterColor = lighter(getColorFromRgbString(rgbStr));

        return getHexFromColor(lighterColor);
    }

    /**
     * Shift given RGB color hex string to be darker
     *
     * @param rgbStr RGB color string
     * @return String
     */
    private static String darkenRgbString(String rgbStr) {
        Color darkerColor = darker(getColorFromRgbString(rgbStr));

        return getHexFromColor(darkerColor);
    }

    /**
     * Return the hex name of a specified color.
     *
     * @param color Color to get hex name of.
     * @return Hex name of color: "rrggbb".
     */
    private static String getHexFromColor(Color color) {
        String rHex = Integer.toString(color.getRed(), 16);
        String gHex = Integer.toString(color.getGreen(), 16);
        String bHex = Integer.toString(color.getBlue(), 16);

        return (rHex.length() == 2 ? "" + rHex : "0" + rHex)
             + (gHex.length() == 2 ? "" + gHex : "0" + gHex)
             + (bHex.length() == 2 ? "" + bHex : "0" + bHex);
    }

    /**
     * Darken given color
     *
     * @param color Color to make darker
     * @return Darker color
     */
    private static Color darker(Color color) {
        return addToRGB(color, -1);
    }

    /**
     * Lighten given color
     *
     * @param  color    Color to be made lighter.
     * @return Color    Lighter color
     */
    private static Color lighter(Color color) {
        return addToRGB(color, 1);
    }

    /**
     * Increment RGB values of given color by given amount
     *
     * @param  color
     * @param  amount
     * @return Color
     */
    @SuppressWarnings("UseJBColor")
    private static Color addToRGB(Color color, int amount) {
        float amountF = (float) amount;

        int red   = Math.round(color.getRed() + amountF);
        int green = Math.round(color.getGreen() + amountF);
        int blue  = Math.round(color.getBlue() + amountF);

        red   = red < 0 ? 0 : red > 255 ? 255 : red;
        green = green < 0 ? 0 : green > 255 ? 255 : green;
        blue  = blue < 0 ? 0 : blue > 255 ? 255 : blue;

        return new Color(red, green, blue, color.getAlpha());
    }
}