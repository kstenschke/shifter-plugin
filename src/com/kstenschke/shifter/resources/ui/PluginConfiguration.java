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

package com.kstenschke.shifter.resources.ui;

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

    /**
     * Constructor
     */
    public PluginConfiguration() {
        tabDictionary.setIconAt(1, ShifterIcons.ICON_DICTIONARY);

        initFormValues();
        initFormListeners();
    }

    /**
     * Refresh settings and dictionary content from stored preference or factory default
     */
    public void initFormValues() {
        this.spinnerShiftMore.setModel( new SpinnerNumberModel(ShifterPreferences.getShiftMoreSize(), 2, 999, 1));

        if (ShifterPreferences.getShiftingModeOfTimestamps().equals(ShifterPreferences.SHIFTING_MODE_TIMESTAMP_SECONDS)) {
            this.radioButtonShiftInSeconds.setSelected(true);
        } else {
            this.radioButtonShiftInMilliseconds.setSelected(true);
        }

        this.inputMillisecondsEndings.setText(ShifterPreferences.getMillisecondsFileEndings());
        this.inputSecondsEndings.setText(ShifterPreferences.getSecondsFileEndings());

        this.checkboxPreserveCase.setSelected(ShifterPreferences.getIsActivePreserveCase());
        this.checkboxConvertSingleQuotes.setSelected(ShifterPreferences.getIsActiveConvertSingleQuotes());
        this.checkboxConvertDoubleQuotes.setSelected(ShifterPreferences.getIsActiveConvertDoubleQuotes());
        this.checkboxPhpArrayShortToLong.setSelected(ShifterPreferences.getIsActiveConvertPhpArrayShortToLong());
        this.checkboxPhpArrayLongToShort.setSelected(ShifterPreferences.getIsActiveConvertPhpArrayLongToShort());

        String termsDictionary   = ShifterPreferences.getDictionary();
        if (termsDictionary == null || termsDictionary.isEmpty())  {
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
        InputStream dictionaryStream= this.getClass().getResourceAsStream("dictionary.txt");

        return dictionaryStream == null ? "" : UtilsFile.getFileStreamAsString(dictionaryStream);
    }

    /**
     * Reset default settings
     */
    public void reset() {
        checkboxPreserveCase.setSelected(true);
        checkboxConvertDoubleQuotes.setSelected(true);
        checkboxConvertSingleQuotes.setSelected(false);
        checkboxPhpArrayLongToShort.setSelected(true);
        checkboxPhpArrayShortToLong.setSelected(false);

        radioButtonShiftInSeconds.setSelected(true);

        spinnerShiftMore.setValue(10);

        inputMillisecondsEndings.setText(ShifterPreferences.DEFAULT_FILE_ENDINGS_MILLISECONDS);
        inputSecondsEndings.setText(ShifterPreferences.DEFAULT_FILE_ENDINGS_SECONDS);

        this.textAreaDictionaryTerms.setText(getDefaultDictionary());
    }

    /**
     * @return  JPanel
     */
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

    /**
     * @return  boolean
     */
    private boolean getIsActivePreserveCase() {
        return checkboxPreserveCase.isSelected();
    }

    /**
     * Config modified?
     *
     * @return boolean
     */
    public boolean isModified() {
        return   
             Integer.parseInt(this.spinnerShiftMore.getValue().toString()) != ShifterPreferences.getShiftMoreSize()
          || !this.textAreaDictionaryTerms.getText().equals(ShifterPreferences.getDictionary())
          || !ShifterPreferences.getIsActivePreserveCase().equals(this.checkboxPreserveCase.isSelected())
          || !ShifterPreferences.getIsActiveConvertSingleQuotes().equals(this.checkboxConvertSingleQuotes.isSelected())
          || !ShifterPreferences.getIsActiveConvertDoubleQuotes().equals(this.checkboxConvertDoubleQuotes.isSelected())
          || !ShifterPreferences.getIsActiveConvertPhpArrayLongToShort().equals(this.checkboxPhpArrayLongToShort.isSelected())
          || !ShifterPreferences.getIsActiveConvertPhpArrayShortToLong().equals(this.checkboxPhpArrayShortToLong.isSelected())
          || !ShifterPreferences.getShiftingModeOfTimestamps().equals(this.getSelectedShiftingModeOfTimestamps())
          || !ShifterPreferences.getMillisecondsFileEndings().equals(this.inputMillisecondsEndings.getText())
          || !ShifterPreferences.getSecondsFileEndings().equals(this.inputSecondsEndings.getText())
        ;
    }

    /**
     * @return  String
     */
    private String getDictionary() {
        return this.textAreaDictionaryTerms.getText();
    }

    public void apply() {
        // Store configuration
        ShifterPreferences.saveShiftMoreSize(this.getShiftMoreSize());
        ShifterPreferences.saveIsActivePreserveCase(this.getIsActivePreserveCase());
        ShifterPreferences.saveShiftingModeTimestamps(this.getSelectedShiftingModeOfTimestamps());
        ShifterPreferences.saveMillisecondsFileEndings(this.inputMillisecondsEndings.getToolTipText());
        ShifterPreferences.saveSecondsFileEndings(this.inputSecondsEndings.getToolTipText());
        ShifterPreferences.saveConvertQuoteActiveModes(this.checkboxConvertSingleQuotes.isSelected(), this.checkboxConvertDoubleQuotes.isSelected());
        ShifterPreferences.saveConvertPhpArrayActiveModes(this.checkboxPhpArrayLongToShort.isSelected(), this.checkboxPhpArrayShortToLong.isSelected());

        // Store dictionary
        String dictionary = this.getDictionary();
        if (dictionary != null) {
            ShifterPreferences.saveDictionary(dictionary);
        }
    }

}