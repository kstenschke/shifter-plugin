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

package com.kstenschke.shifter.models;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;

import java.awt.*;
import java.util.Date;

/**
 * Numeric value class, also contains UNIX timestamp handling
 */
class NumericValue {

	private static final int SECS_PER_DAY	= 86400000;



	/**
	 * Constructor
	 */
	public NumericValue() {

	}



	/**
	 * Check whether given String represents a CSS px value
	 *
	 * @param	str			String to be checked
	 * @return	Boolean.
	 */
	public Boolean isNumericValue(String str) {
		return (str.matches("[0-9]+"));
	}



	/**
	 * Shift numeric value up/down by 1
	 *
	 * @param	numericValue		String representing a numeric value
	 * @param	isUp				Shifting up or down?
	 * @return	String
	 */
	public String getShifted(String numericValue, Boolean isUp, Editor editor) {
			// Integer
		if( numericValue.length() <= 7 ) {
			int intValue = Integer.parseInt(numericValue);

				// Shift up/down by 1
			intValue = intValue + (isUp ? 1 : -1);

			return Integer.toString(intValue);
		}

			// UNIX timestamp? Shift it plus/minus one day
		long shiftedTimestamp	= Long.parseLong(numericValue) * 1000 + (isUp ? SECS_PER_DAY : -SECS_PER_DAY);

			// Create and show balloon with human-readable date
		Balloon.Position pos = Balloon.Position.above;
		String balloonText   = new Date(shiftedTimestamp).toString();
		BalloonBuilder builder = JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(balloonText, null, new Color(255, 255, 231), null);
		Balloon balloon = builder.createBalloon();

		Point caretPos                = editor.visualPositionToXY(editor.getCaretModel().getVisualPosition());
		RelativePoint balloonPosition = new RelativePoint(editor.getContentComponent(), caretPos);
		balloon.show(balloonPosition, pos);

		return Long.toString( shiftedTimestamp / 1000 );
	}

}