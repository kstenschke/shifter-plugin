/*
 * Copyright 2012 Kay Stenschke
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

package com.kstenschke.shifter.models;

/**
 * Pixel value class
 */
public class PixelValue {

	/**
	 * Constructor
	 */
	public PixelValue() {

	}



	/**
	 * Check whether given String represents a CSS px value
	 *
	 * @param	str			String to be checked
	 * @return	Boolean.
	 */
	public Boolean isPixelValue(String str) {
		return (str.matches("[0-9]*px"));
	}



	/**
	 * Shift PX value up/down by 1px
	 *
	 * @param	pxValue		Current word
	 * @param	isUp		Shifting up or down?
	 * @return	String
	 */
	public String getShifted(String pxValue, Boolean isUp) {
		// Get int from PX value
		pxValue = pxValue.replace("px", "");
		int numericValue = Integer.parseInt(pxValue);

		// Shift up/down by 1px
		numericValue = numericValue + (isUp ? 1 : -1);

		// Add "px" to numeric value
		return Integer.toString(numericValue).concat("px");
	}

}
