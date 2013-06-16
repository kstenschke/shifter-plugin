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

package com.kstenschke.shifter;

import com.intellij.ide.util.PropertiesComponent;
import com.kstenschke.shifter.resources.forms.PluginConfiguration;
import org.jetbrains.annotations.NonNls;

/**
 * Utility functions for preferences handling
 * All preferences of the shifter plugin are stored on application level (not per project)
 */
public class ShifterPreferences {

	//  @NonNls = element is not a string requiring internationalization and it does not contain such strings.
	@NonNls
	private static final String PROPERTY_DICTIONARY = "PluginShifter.Dictionary";

	/**
	 * Store dictionary preference
	 *
	 * @param   dictionary      Contents to be stored in dictionary preference
	 */
	public static void saveDictionary(String dictionary) {
		PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

		propertiesComponent.setValue(PROPERTY_DICTIONARY, dictionary);
	}

	/**
	 * Get dictionary preference
	 *
	 * @return String
	 */
	public static String getDictionary() {
		PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

		String dictionary = propertiesComponent.getValue(PROPERTY_DICTIONARY);

		if (dictionary == null) {
			dictionary = "";
		}

		return dictionary;
	}

}
