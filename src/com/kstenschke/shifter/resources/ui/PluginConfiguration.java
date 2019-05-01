/*
 * Copyright 2011-2019 Kay Stenschke
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

package com.kstenschke.shifter.resources.ui;

import com.intellij.util.ui.UIUtil;
import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.resources.ShifterIcons;
import com.kstenschke.shifter.utils.UtilsFile;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class PluginConfiguration {

    private JPanel rootPanel;

    private JTextArea textAreaDictionaryTerms;

    private JRadioButton radioButtonShiftInSeconds;
    private JRadioButton radioButtonShiftInMilliseconds;

    private JScrollPane scrollPaneDictionaryTerms;

    private JPanel jPanelOptions;
    private JPanel jPanelTopBar;

    private JSpinner spinnerShiftMore;

    private JTextPane thisDictionaryConfiguresShiftableTextPane;

    private JTextField inputSecondsEndings;
    private JTextField inputMillisecondsEndings;
    private JTextField restoreSettings;

    private JCheckBox checkboxPreserveCase;
    private JCheckBox checkboxConvertSingleQuotes;
    private JCheckBox checkboxConvertDoubleQuotes;
    private JCheckBox checkboxPhpArrayLongToShort;
    private JCheckBox checkboxPhpArrayShortToLong;
    private JTabbedPane tabDictionary;

    // Constructor
    public PluginConfiguration() {
        tabDictionary.setIconAt(1, UIUtil.isUnderDarcula() ? ShifterIcons.ICON_DICTIONARY_DARK : ShifterIcons.ICON_DICTIONARY);

        initFormValues();
        initFormListeners();
    }

    /**
     * Refresh settings and dictionary content from stored preference or factory default
     */
    public void initFormValues() {
        spinnerShiftMore.setModel( new SpinnerNumberModel(ShifterPreferences.getShiftMoreSize(), 2, 999, 1));

        if (ShifterPreferences.getShiftingModeOfTimestamps().equals(ShifterPreferences.SHIFTING_MODE_TIMESTAMP_SECONDS)) {
            radioButtonShiftInSeconds.setSelected(true);
        } else {
            radioButtonShiftInMilliseconds.setSelected(true);
        }

        inputMillisecondsEndings.setText(ShifterPreferences.getMillisecondsFileEndings());
        inputSecondsEndings.setText(ShifterPreferences.getSecondsFileEndings());

        checkboxPreserveCase.setSelected(ShifterPreferences.getIsActivePreserveCase());
        checkboxConvertSingleQuotes.setSelected(ShifterPreferences.getIsActiveConvertSingleQuotes());
        checkboxConvertDoubleQuotes.setSelected(ShifterPreferences.getIsActiveConvertDoubleQuotes());
        checkboxPhpArrayShortToLong.setSelected(ShifterPreferences.getIsActiveConvertPhpArrayShortToLong());
        checkboxPhpArrayLongToShort.setSelected(ShifterPreferences.getIsActiveConvertPhpArrayLongToShort());

        String termsDictionary   = ShifterPreferences.getDictionary();
        if (null == termsDictionary || termsDictionary.isEmpty())  {
            termsDictionary = getDefaultDictionary();
        }
        textAreaDictionaryTerms.setText(termsDictionary);
    }

    private void initFormListeners() {
        restoreSettings.setBackground(null);
        restoreSettings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        restoreSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        PluginConfigurationListenerRestoreSettings listenerRestoreSettings = new PluginConfigurationListenerRestoreSettings(this);
        restoreSettings.addMouseListener(listenerRestoreSettings);
    }

    public String getDefaultDictionary() {
        //@note for the .txt resource to be included in the jar, it must be set in compiler resource settings
        InputStream dictionaryStream= getClass().getResourceAsStream("dictionary.txt");

        return null == dictionaryStream ? "" : UtilsFile.getFileStreamAsString(dictionaryStream);
    }

    // Reset default settings
    void reset() {
        checkboxPreserveCase.setSelected(true);
        checkboxConvertDoubleQuotes.setSelected(true);
        checkboxConvertSingleQuotes.setSelected(false);
        checkboxPhpArrayLongToShort.setSelected(true);

        checkboxPhpArrayShortToLong.setSelected(false);

        radioButtonShiftInSeconds.setSelected(true);

        spinnerShiftMore.setValue(10);

        inputMillisecondsEndings.setText(ShifterPreferences.DEFAULT_FILE_ENDINGS_MILLISECONDS);
        inputSecondsEndings.setText(ShifterPreferences.DEFAULT_FILE_ENDINGS_SECONDS);

        textAreaDictionaryTerms.setText(getDefaultDictionary());
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    private String getShiftMoreSize() {
        return spinnerShiftMore.getValue().toString();
    }

    private Integer getSelectedShiftingModeOfTimestamps() {
        return radioButtonShiftInSeconds.isSelected()
                ? ShifterPreferences.SHIFTING_MODE_TIMESTAMP_SECONDS
                : ShifterPreferences.SHIFTING_MODE_TIMESTAMP_MILLISECONDS;
    }

    private boolean getIsActivePreserveCase() {
        return checkboxPreserveCase.isSelected();
    }

    public boolean isModified() {
        return   
             Integer.parseInt(spinnerShiftMore.getValue().toString()) != ShifterPreferences.getShiftMoreSize()
          || !textAreaDictionaryTerms.getText().equals(ShifterPreferences.getDictionary())
          || !ShifterPreferences.getIsActivePreserveCase().equals(checkboxPreserveCase.isSelected())
          || !ShifterPreferences.getIsActiveConvertSingleQuotes().equals(checkboxConvertSingleQuotes.isSelected())
          || !ShifterPreferences.getIsActiveConvertDoubleQuotes().equals(checkboxConvertDoubleQuotes.isSelected())
          || !ShifterPreferences.getIsActiveConvertPhpArrayLongToShort().equals(checkboxPhpArrayLongToShort.isSelected())
          || !ShifterPreferences.getIsActiveConvertPhpArrayShortToLong().equals(checkboxPhpArrayShortToLong.isSelected())
          || !ShifterPreferences.getShiftingModeOfTimestamps().equals(getSelectedShiftingModeOfTimestamps())
          || !ShifterPreferences.getMillisecondsFileEndings().equals(inputMillisecondsEndings.getText())
          || !ShifterPreferences.getSecondsFileEndings().equals(inputSecondsEndings.getText())
        ;
    }

    private String getDictionary() {
        return textAreaDictionaryTerms.getText();
    }

    public void apply() {
        // Store configuration
        ShifterPreferences.saveShiftMoreSize(getShiftMoreSize());
        ShifterPreferences.saveIsActivePreserveCase(getIsActivePreserveCase());
        ShifterPreferences.saveShiftingModeTimestamps(getSelectedShiftingModeOfTimestamps());
        ShifterPreferences.saveMillisecondsFileEndings(inputMillisecondsEndings.getToolTipText());
        ShifterPreferences.saveSecondsFileEndings(inputSecondsEndings.getToolTipText());
        ShifterPreferences.saveConvertQuoteActiveModes(checkboxConvertSingleQuotes.isSelected(), checkboxConvertDoubleQuotes.isSelected());
        ShifterPreferences.saveConvertPhpArrayActiveModes(checkboxPhpArrayLongToShort.isSelected(), checkboxPhpArrayShortToLong.isSelected());

        // Store dictionary
        String dictionary = getDictionary();
        if (null != dictionary) {
            ShifterPreferences.saveDictionary(dictionary);
        }
    }
}