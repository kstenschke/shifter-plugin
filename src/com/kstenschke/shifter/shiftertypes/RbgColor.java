/*
 * Copyright 2011-2013 Kay Stenschke
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

package com.kstenschke.shifter.shiftertypes;

import java.awt.*;

/**
 * RGB color class
 */
public class RbgColor {


	/**
	 * Constructor
	 */
	public RbgColor() {

	}



	/**
	 * Check whether given string represents a hex RGB color, prefix must be "#"
	 *
	 * @param	str			String to be checked
	 * @param	prefix		Character found to precede that string
	 * @return	Boolean.
	 */
	public Boolean isRgbColorString(String str, String prefix) {
		return !( !prefix.equals("#")  || !(str.matches("[0-9a-fA-F]{3}") || str.matches("[0-9a-fA-F]{6}")) );
	}


	/**
	 * @param	rgbStr		String representing an RGB color
	 * @param	isUp		Shifting up or down?
	 * @return	String
	 */
	public String getShifted(String rgbStr, Boolean isUp) {
		if (rgbStr.length() == 3) {
				// Convert 3-digit RGB color to 6 digits
			rgbStr = sixfoldTripleColor(rgbStr);
		}

		if (isUp) {
				// Shift up: lighten
			if (!isWhite(rgbStr)) {
				return lightenRgbString(rgbStr);
			}
		} else if (!isBlack(rgbStr)) {
				// Shift down: darken
			return darkenRgbString(rgbStr);
		}

		return rgbStr;
	}



	/**
	 * Check whether given String represents RGB white (fff, FFF, ffffff, FFFFFF)
	 *
	 * @param	str			RBG color string to be checked
	 * @return	Boolean.
	 */
	private static Boolean isWhite(String str) {
		return str.equalsIgnoreCase("fff") || str.equalsIgnoreCase("ffffff");
	}



	/**
	 * Check whether given String represents RGB black (000, 000000)
	 *
	 * @param	str			RGB color string
	 * @return	Boolean.
	 */
	private static Boolean isBlack(String str) {
		return str.equalsIgnoreCase("000") || str.equalsIgnoreCase("000000");
	}



	/**
	 * Convert three digit color string into six digits
	 *
	 * @param	rgbStr		RGB color string
	 * @return	String
	 */
	private static String sixfoldTripleColor(String rgbStr) {
		String R = rgbStr.substring(0, 1);
		String G = rgbStr.substring(1, 2);
		String B = rgbStr.substring(2, 3);

		return "".concat(R).concat(R).concat(G).concat(G).concat(B).concat(B);
	}



	/**
	 * Get Color object of given RGB string
	 *
	 * @param	rgbStr	String representing an RGB color
	 * @return			Color object
	 */
	private static Color getColorFromRgbString(String rgbStr) {
		int R = Integer.parseInt(rgbStr.substring(0, 2), 16);
		int G = Integer.parseInt(rgbStr.substring(2, 4), 16);
		int B = Integer.parseInt(rgbStr.substring(4, 6), 16);

		return new Color(R, G, B);
	}



	/**
	 * Shift given RGB color hex string to be lighter
	 *
	 * @param	rgbStr		String of RGB color
	 * @return	String
	 */
	private static String lightenRgbString(String rgbStr) {
		Color lighterColor = lighter(getColorFromRgbString(rgbStr));

		return getHexFromColor(lighterColor);
	}



	/**
	 * Shift given RGB color hex string to be darker
	 *
	 * @param	rgbStr		RGB color string
	 * @return	String
	 */
	private static String darkenRgbString(String rgbStr) {
		Color darkerColor = darker(getColorFromRgbString(rgbStr));

		return getHexFromColor(darkerColor);
	}



	/**
	 * Return the hex name of a specified color.
	 *
	 * @param	color	Color to get hex name of.
	 * @return			Hex name of color: "rrggbb".
	 */
	private static String getHexFromColor(Color color) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();

		String rHex = Integer.toString(r, 16);
		String gHex = Integer.toString(g, 16);
		String bHex = Integer.toString(b, 16);

		return (rHex.length() == 2 ? "" + rHex : "0" + rHex) +
				  (gHex.length() == 2 ? "" + gHex : "0" + gHex) +
				  (bHex.length() == 2 ? "" + bHex : "0" + bHex);
	}



	/**
	 * Darken given color.
	 *
	 * @param	color	Color to make darker
	 * @return			Darker color.
	 */
	private static Color darker(Color color) {
		int red = Math.round(color.getRed() - 1); //* .0955);
		int green = Math.round(color.getGreen() - 1); //* .0955);
		int blue = Math.round(color.getBlue() - 1); //* .0955);

		if (red < 0) red = 0;
		else if (red > 255) red = 255;
		if (green < 0) green = 0;
		else if (green > 255) green = 255;
		if (blue < 0) blue = 0;
		else if (blue > 255) blue = 255;

		int alpha = color.getAlpha();

		return new Color(red, green, blue, alpha);
	}



	/**
	 * Lighten given color.
	 *
	 * @param	color		Color to be made lighter.
	 * @return	Color		Lighter color.
	 */
	private static Color lighter(Color color) {
		int red = Math.round(color.getRed() + 1); //* .0955);
		int green = Math.round(color.getGreen() + 1); //* .0955);
		int blue = Math.round(color.getBlue() + 1); //* .0955);

		if (red < 0) red = 0;
		else if (red > 255) red = 255;
		if (green < 0) green = 0;
		else if (green > 255) green = 255;
		if (blue < 0) blue = 0;
		else if (blue > 255) blue = 255;

		int alpha = color.getAlpha();

		return new Color(red, green, blue, alpha);
	}

}