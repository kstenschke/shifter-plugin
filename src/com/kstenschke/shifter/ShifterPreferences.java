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

package com.kstenschke.shifter;

import com.intellij.ide.util.PropertiesComponent;
import org.codehaus.groovy.util.StringUtil;
import org.jetbrains.annotations.NonNls;

/**
 * Utility functions for preferences handling
 * All preferences of the shifter plugin are stored on application level (not per project)
 */
public class ShifterPreferences {

	@NonNls
	private static final String PROPERTY_DICTIONARY = "PluginShifter.Dictionary";
    @NonNls
    public static final String PROPERTY_SORTING_MODE = "PluginShifter.SortingMode";

        // Sorting modes
    public static final Integer SORTING_MODE_CASE_SENSITIVE     = 0;
    public static final Integer SORTING_MODE_CASE_INSENSITIVE   = 1;

	/**
	 * Store dictionary
	 *
	 * @param	dictionary		Contents to be stored in dictionary preference
	 */
	public static void saveDictionary(String dictionary) {
		PropertiesComponent.getInstance().setValue(PROPERTY_DICTIONARY, dictionary);
	}

	/**
	 * Load dictionary
	 *
	 * @return	String
	 */
	public static String getDictionary() {
		String dictionary = PropertiesComponent.getInstance().getValue(PROPERTY_DICTIONARY);

		if( dictionary == null ) {
			dictionary = "";
		}

		return dictionary;
	}

    /**
     * Store sorting mode
     *
     * @param	mode		case sensitive / insensitive
     */
    public static void saveSortingMode(Integer mode) {
        PropertiesComponent.getInstance().setValue(PROPERTY_SORTING_MODE, mode.toString());
    }

    /**
     * Load sorting mode
     *
     * @return	Integer     mode: case sensitive / insensitive
     */
    public static Integer getSortingMode() {
        String modeStr = PropertiesComponent.getInstance().getValue(PROPERTY_SORTING_MODE);

        return modeStr == null ? SORTING_MODE_CASE_INSENSITIVE : Integer.parseInt(modeStr);
    }
}
