/*
 * Copyright 2011-2016 Kay Stenschke
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

import com.kstenschke.shifter.utils.UtilsMap;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * Pixel value class
 */
public class CssUnit {

	private static final String UNIT_CM	= "cm";
	private static final String UNIT_EM	= "em";
	private static final String UNIT_IN	= "in";
	private static final String UNIT_MM	= "mm";
	private static final String UNIT_PC	= "pc";
	private static final String UNIT_PT	= "pt";
	private static final String UNIT_PX	= "px";

	/**
	 * @param	str			String to be checked
	 * @return	boolean     Does the given string represents a CSS length value?
	 */
	public static boolean isCssUnitValue(String str) {
		return str.matches("[0-9]*(%|cm|em|in|pt|px)");
	}

    public static boolean isCssUnit(String str) {
        return str.matches("(%|cm|em|in|pt|px)");
    }

	/**
	 * @param	value       The full length value, post-fixed by its unit
	 * @param	isUp		Shifting up or down?
	 * @return	String      Length (em / px / pt / cm / in) value shifted up or down by 1px
	 */
	public String getShifted(String value, boolean isUp) {
		// Get int from PX value
		String unit = value.substring(value.length() -2);
		int numericValue  = Integer.parseInt( value.replace(unit, "") );

		// Shift up/down by 1
		numericValue = numericValue + (isUp ? 1 : -1);

		// prepend with unit again
		return Integer.toString(numericValue).concat(unit);
	}

	/**
	 * @param	stylesheet	CSS content
	 * @return	most prominently used unit of given stylesheet, 'px' if none used yet
	 */
	public static String determineMostProminentUnit(String stylesheet) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		map.put(UNIT_CM, StringUtils.countMatches(stylesheet, UNIT_CM + ";"));
		map.put(UNIT_EM, StringUtils.countMatches(stylesheet, UNIT_EM + ";"));
		map.put(UNIT_IN, StringUtils.countMatches(stylesheet, UNIT_IN + ";"));
		map.put(UNIT_MM, StringUtils.countMatches(stylesheet, UNIT_MM + ";"));
		map.put(UNIT_PC, StringUtils.countMatches(stylesheet, UNIT_PC + ";"));
		map.put(UNIT_PT, StringUtils.countMatches(stylesheet, UNIT_PT + ";"));
		map.put(UNIT_PX, StringUtils.countMatches(stylesheet, UNIT_PX + ";"));

		int sum = UtilsMap.getSumOfValues(map);
		if( sum == 0 ) {
			return "px";
		}

		return UtilsMap.getKeyOfHighestValue(map);
	}

}
