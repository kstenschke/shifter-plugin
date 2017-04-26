/*
 * Copyright 2011-2017 Kay Stenschke
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
import org.jetbrains.annotations.NonNls;

/**
 * Utility functions for preferences handling
 * All preferences of the shifter plugin are stored on application level (not per project)
 */
public class ShifterPreferences {

    @NonNls
    private static final String PROPERTY_DICTIONARY_TERMS = "PluginShifter.Dictionary.Terms";
    @NonNls
    private static final String PROPERTY_SHIFTING_MODE_TIMESTAMP = "PluginShifter.ShiftingModeTimestamps";
    @NonNls
    private static final String PROPERTY_IS_ACTIVE_PRESERVE_CASE = "PluginShifter.IsActivePreserveCase";
    @NonNls
    private static final String PROPERTY_SIZE_SHIFT_MORE = "PluginShifter.SizeShiftMore";
    @NonNls
    private static final String PROPERTY_MODE_SHIFT_NUMERICAL_BLOCK = "PluginShifter.ModeShiftNumericalBlock";
    @NonNls
    private static final String PROPERTY_FILE_ENDINGS_MILLISECONDS = "PluginShifter.FileEndingsMilliseconds";
    @NonNls
    private static final String PROPERTY_FILE_ENDINGS_SECONDS = "PluginShifter.FileEndingsMilliseconds";

    // Timestamp units by file endings
    public static final String DEFAULT_FILE_ENDINGS_MILLISECONDS = "java,js,py";
    public static final String DEFAULT_FILE_ENDINGS_SECONDS     = "mysql,php,phtml,sql";

    // Sorting modes
    public static final Integer SORTING_MODE_CASE_SENSITIVE     = 0;
    public static final Integer SORTING_MODE_CASE_INSENSITIVE   = 1;

    // Sorting modes: numerical block selection
    public static final Integer SORTING_MODE_NUMERICAL_BLOCK_ENUM    = 0;
    public static final Integer SORTING_MODE_NUMERICAL_BLOCK_INC_DEC = 1;

    // Timestamp shifting modes
    public static final Integer SHIFTING_MODE_TIMESTAMP_SECONDS     = 0;
    public static final Integer SHIFTING_MODE_TIMESTAMP_MILLISECONDS= 1;

    // Dialog IDs
    @NonNls
    public static final String ID_DIALOG_NUMERIC_BLOCK_OPTIONS = "PluginShifter.DialogBlockOptions";

    /**
     * @param   propertyName        Name of the preference property
     * @param   defaultValue        Default value to be set if null
     * @param   setDefaultIfEmpty   Set default also if empty?
     * @return  String
     */
    private static String getProperty(String propertyName, String defaultValue, boolean setDefaultIfEmpty) {
        String value = PropertiesComponent.getInstance().getValue(propertyName);
        if (value == null) {
            value = defaultValue;
        }
        if (value.equals("") && setDefaultIfEmpty && !defaultValue.equals("")) {
            value = defaultValue;
        }

        return value;
    }


    /**
     * @param   idDialog
     */
    public static String getDialogPosition(String idDialog) {
        return getProperty(idDialog + ".Position", "0x0", false);
    }

    /**
     * Store dictionary
     *
     * @param dictionary    Contents to be stored in dictionary preference
     */
    public static void saveDictionary(String dictionary) {
        PropertiesComponent.getInstance().setValue(PROPERTY_DICTIONARY_TERMS, dictionary);
    }

    public static void saveShiftMoreSize(String size) {
        PropertiesComponent.getInstance().setValue(PROPERTY_SIZE_SHIFT_MORE, size);
    }

    public static void saveShiftNumericalBlockMode(int mode) {
        PropertiesComponent.getInstance().setValue(PROPERTY_MODE_SHIFT_NUMERICAL_BLOCK, String.valueOf(mode));
    }

    public static void saveShiftingModeTimestamps(Integer mode) {
        PropertiesComponent.getInstance().setValue(PROPERTY_SHIFTING_MODE_TIMESTAMP, mode.toString());
    }

    /**
     * @param isActive
     */
    public static void saveIsActivePreserveCase(boolean isActive) {
        PropertiesComponent.getInstance().setValue(PROPERTY_IS_ACTIVE_PRESERVE_CASE, isActive ? "1":"0");
    }

    public static void saveMillisecondsFileEndings(String endings) {
        PropertiesComponent.getInstance().setValue(PROPERTY_FILE_ENDINGS_MILLISECONDS, endings);
    }

    public static void saveSecondsFileEndings(String endings) {
        PropertiesComponent.getInstance().setValue(PROPERTY_FILE_ENDINGS_MILLISECONDS, endings);
    }

    private static String getDictionary(String dictionaryName) {
        String dictionary = null;

        try {
            PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
            if (propertiesComponent != null) {
                dictionary = propertiesComponent.getValue(dictionaryName);
            }
        } catch(NullPointerException e) {
            return "";
        }

        return dictionary == null ? "" : dictionary;
    }

    /**
     * @return String  Dictionary
     */
    public static String getTermsDictionary() {
        return getDictionary(PROPERTY_DICTIONARY_TERMS);
    }

    /**
     * @return int
     */
    public static int getShiftMoreSize() {
        try {
            String size = PropertiesComponent.getInstance().getValue(PROPERTY_SIZE_SHIFT_MORE);

            return size == null ? 10 : Integer.parseInt(size);
        } catch(NullPointerException e) {
            return 10;
        }
    }

    public static int getShiftNumericalBlockMode() {
        try {
            String mode = PropertiesComponent.getInstance().getValue(PROPERTY_MODE_SHIFT_NUMERICAL_BLOCK);

            return mode == null ? SORTING_MODE_NUMERICAL_BLOCK_ENUM : Integer.parseInt(mode);
        } catch(NullPointerException e) {
            return SORTING_MODE_NUMERICAL_BLOCK_ENUM;
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

    public static String getMillisecondsFileEndings() {
        try {
            String endings = PropertiesComponent.getInstance().getValue(PROPERTY_FILE_ENDINGS_MILLISECONDS);

            return endings == null ? DEFAULT_FILE_ENDINGS_MILLISECONDS : endings;
        } catch(NullPointerException e) {
            return DEFAULT_FILE_ENDINGS_MILLISECONDS;
        }
    }

    public static String getSecondsFileEndings() {
        try {
            String endings = PropertiesComponent.getInstance().getValue(PROPERTY_FILE_ENDINGS_SECONDS);

            return endings == null ? DEFAULT_FILE_ENDINGS_SECONDS : endings;
        } catch(NullPointerException e) {
            return DEFAULT_FILE_ENDINGS_SECONDS;
        }
    }

    /**
     * @return Boolean  Note: Object and not primitive boolean is required here
     */
    public static Boolean getIsActivePreserveCase() {
        try {
            String value = PropertiesComponent.getInstance().getValue(PROPERTY_IS_ACTIVE_PRESERVE_CASE);

            return value == null || "1".equals(value);
        } catch (NullPointerException e) {
            return true;
        }
    }
}