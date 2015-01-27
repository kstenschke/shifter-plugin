/*
 * Copyright 2011-2015 Kay Stenschke
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

import com.intellij.ide.util.PropertiesComponent;
import com.thoughtworks.xstream.mapper.Mapper;
import org.jetbrains.annotations.NonNls;

/**
 * Utility functions for preferences handling
 * All preferences of the shifter plugin are stored on application level (not per project)
 */
public class ShifterPreferences {

    @NonNls
    private static final String PROPERTY_DICTIONARY = "PluginShifter.Dictionary";
    @NonNls
    private static final String PROPERTY_SORTING_MODE = "PluginShifter.SortingMode";
    @NonNls
    private static final String PROPERTY_SHIFTING_MODE_TIMESTAMP = "PluginShifter.ShiftingModeTimestamps";
    @NonNls
    private static final String PROPERTY_IS_ACTIVE_PRESERVE_CASE = "PluginShifter.IsActivePreserveCase";
    @NonNls
    private static final String PROPERTY_SIZE_SHIFT_MORE = "PluginShifter.SizeShiftMore";

        // Sorting modes
    public static final Integer SORTING_MODE_CASE_SENSITIVE     = 0;
    public static final Integer SORTING_MODE_CASE_INSENSITIVE   = 1;

        // Timestamp shifting modes
    public static final Integer SHIFTING_MODE_TIMESTAMP_SECONDS     = 0;
    public static final Integer SHIFTING_MODE_TIMESTAMP_MILLISECONDS= 1;

    /**
     * Store dictionary
     *
     * @param	dictionary		Contents to be stored in dictionary preference
     */
    public static void saveDictionary(String dictionary) {
        PropertiesComponent.getInstance().setValue(PROPERTY_DICTIONARY, dictionary);
    }

    /**
     * Store sorting mode
     *
     * @param	mode		case sensitive / insensitive
     */
    public static void saveSortingMode(Integer mode) {
        PropertiesComponent.getInstance().setValue(PROPERTY_SORTING_MODE, mode.toString());
    }

    public static void saveShiftMoreSize(String size) {
        PropertiesComponent.getInstance().setValue(PROPERTY_SIZE_SHIFT_MORE, size);
    }

    public static void saveShiftingModeTimestamps(Integer mode) {
        PropertiesComponent.getInstance().setValue(PROPERTY_SHIFTING_MODE_TIMESTAMP, mode.toString());
    }

    /**
     * @param	isActive
     */
    public static void saveIsActivePreserveCase(boolean isActive) {
        PropertiesComponent.getInstance().setValue(PROPERTY_IS_ACTIVE_PRESERVE_CASE, isActive ? "1":"0");
    }

    /**
     * @return	String  Dictionary
     */
    public static String getDictionary() {
        String dictionary = null;

        try {
            PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
            if (propertiesComponent != null) {
                dictionary = propertiesComponent.getValue(PROPERTY_DICTIONARY);
            }
        } catch(NullPointerException e) {
            return "";
        }

        return dictionary == null ? "" : dictionary;
    }

    /**
     * @return	int
     */
    public static int getShiftMoreSize() {
        try {
            String size = PropertiesComponent.getInstance().getValue(PROPERTY_SIZE_SHIFT_MORE);

            return size == null ? 10 : Integer.parseInt(size);
        } catch(NullPointerException e) {
            return 10;
        }
    }

    /**
     * @return	int     Sorting mode: case sensitive / insensitive
     */
    public static Integer getSortingMode() {
        try {
            String modeStr = PropertiesComponent.getInstance().getValue(PROPERTY_SORTING_MODE);

            return modeStr == null ? SORTING_MODE_CASE_INSENSITIVE : Integer.parseInt(modeStr);
        } catch(NullPointerException e) {
            return SORTING_MODE_CASE_INSENSITIVE;
        }
    }

    public static Integer getShiftingModeOfTimestamps() {
        try {
            String modeStr = PropertiesComponent.getInstance().getValue(PROPERTY_SHIFTING_MODE_TIMESTAMP);

            return modeStr == null ? SHIFTING_MODE_TIMESTAMP_SECONDS : Integer.parseInt(modeStr);
        } catch(NullPointerException e) {
            return SHIFTING_MODE_TIMESTAMP_SECONDS;
        }
    }

    /**
     * @return  Boolean     (Note: Object and not primitive boolean is required here)
     */
    public static Boolean getIsActivePreserveCase() {
        try {
            String value = PropertiesComponent.getInstance().getValue(PROPERTY_IS_ACTIVE_PRESERVE_CASE);

            return value == null || value.equals("1");
        } catch (NullPointerException e) {
            return true;
        }
    }
}
