/*
 * Copyright 2011-2014 Kay Stenschke
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

/**
 * Pixel value class
 */
public class CssUnit {

	/**
	 * Constructor
	 */
	public CssUnit() {

	}

	/**
	 * @param	str			String to be checked
	 * @return	boolean     Does the given string represents a CSS length value?
	 */
	public static boolean isCssUnitValue(String str) {
		return ( str.matches("[0-9]*(cm|em|in|pt|px)") );
	}

	/**
	 * @param	value       The full length value, post-fixed by its unit
	 * @param	isUp		Shifting up or down?
	 * @return	String      Length (em / px / pt / cm / in) value shifted up or down by 1px
	 */
	public String getShifted(String value, boolean isUp) {
			// Get int from PX value
		String unit = value.substring(value.length() -2);
		value       = value.replace(unit, "");
		int numericValue  = Integer.parseInt(value);

			// Shift up/down by 1
		numericValue = numericValue + (isUp ? 1 : -1);

			// prepend with unit again
		return Integer.toString(numericValue).concat(unit);
	}

}
