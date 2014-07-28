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

package com.kstenschke.shifter.models.shiftertypes;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;

import java.awt.*;
import java.util.Date;

/**
 * Numeric value class, also contains UNIX timestamp handling
 */
public class NumericValue {

	private static final int SECS_PER_DAY	= 86400000;

	/**
	 * Constructor
	 */
	public NumericValue() {

	}

	/**
	 * @param	str			String to be checked
	 * @return	boolean     Does the given string represent a CSS length value?
	 */
	public static boolean isNumericValue(String str) {
		return (str.matches("[0-9]+"));
	}

	/**
	 * @param	value       String representing a numeric value
	 * @param	isUp		Shifting up or down?
	 * @return	String      Value shifted up or down by one
	 */
	public String getShifted(String value, boolean isUp, Editor editor) {
		if( value.length() <= 7 ) {
                // Integer
			return Integer.toString( Integer.parseInt(value) + (isUp ? 1 : -1) );
		}

        return getShiftedUnixTimestamp(value, isUp, editor);
    }

    /**
     * @param   value
     * @param   isUp
     * @param   editor
     * @return  String          UNIX timestamp shifted plus/minus one day
     */
    private String getShiftedUnixTimestamp(String value, boolean isUp, Editor editor) {
        long shiftedTimestamp	= Long.parseLong(value) * 1000 + (isUp ? SECS_PER_DAY : -SECS_PER_DAY);

        // Create and show balloon with human-readable date
        Balloon.Position pos = Balloon.Position.above;
        String balloonText   = new Date(shiftedTimestamp).toString();
        BalloonBuilder builder = JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(balloonText, null, new JBColor(new Color(255, 255, 231), new Color(255, 255, 231)), null);
        Balloon balloon = builder.createBalloon();

        Point caretPos                = editor.visualPositionToXY(editor.getCaretModel().getVisualPosition());
        RelativePoint balloonPosition = new RelativePoint(editor.getContentComponent(), caretPos);
        balloon.show(balloonPosition, pos);

        return Long.toString( shiftedTimestamp / 1000 );
    }

}