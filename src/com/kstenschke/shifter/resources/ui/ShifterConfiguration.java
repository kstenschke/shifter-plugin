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
import com.kstenschke.shifter.listeners.ListenerRestoreSettings;
import com.kstenschke.shifter.utils.UtilsFile;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class ShifterConfiguration {

    private JPanel rootPanel;
    private JTextArea textAreaDictionaryTerms;
    private JCheckBox checkBoxPreserveCase;

    private JRadioButton radioButtonShiftInSeconds;
    private JRadioButton radioButtonShiftInMilliseconds;
    private JScrollPane scrollPaneDictionaryTerms;
    private JPanel jPanelOptions;
    private JPanel jPanelTopBar;
    private JSpinner spinnerShiftMore;
    private JTextField restoreSettings;
    private JTextPane thisDictionaryConfiguresShiftableTextPane;
    private JTextField inputSecondsEndings;
    private JTextField inputMilisecondsEndings;

    /**
     * Constructor
     */
    public ShifterConfiguration() {
        initFormValues();
        initFormListeners();
    }

    /**
     * Refresh settings and dictionary content from stored preference or factory default
     */
    public void initFormValues() {
        int shiftMoreValue = ShifterPreferences.getShiftMoreSize();
        this.spinnerShiftMore.setModel( new SpinnerNumberModel(shiftMoreValue, 2, 999, 1));

        if (ShifterPreferences.getShiftingModeOfTimestamps().equals(ShifterPreferences.SHIFTING_MODE_TIMESTAMP_SECONDS)) {
            this.radioButtonShiftInSeconds.setSelected(true);
        } else {
            this.radioButtonShiftInMilliseconds.setSelected(true);
        }

        this.inputMilisecondsEndings.setText(ShifterPreferences.getMillisecondsFileEndings());
        this.inputSecondsEndings.setText(ShifterPreferences.getSecondsFileEndings());

        boolean isActivePreserveCase  = ShifterPreferences.getIsActivePreserveCase();
        this.checkBoxPreserveCase.setSelected(isActivePreserveCase);

        String termsDictionary   = ShifterPreferences.getTermsDictionary();
        if (termsDictionary == null || termsDictionary.isEmpty())  {
            termsDictionary = getDefaultTerms();
        }
        textAreaDictionaryTerms.setText(termsDictionary);
    }

    private void initFormListeners() {
        restoreSettings.setBackground(null);
        restoreSettings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        restoreSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        ListenerRestoreSettings listenerRestoreSettings = new ListenerRestoreSettings(this);
        restoreSettings.addMouseListener(listenerRestoreSettings);
    }

    public String getDefaultTerms() {
        //@note for the .txt resource to be included in the jar, it must be set in compiler resource settings
        InputStream dictionaryStream= this.getClass().getResourceAsStream("terms.txt");

        return dictionaryStream == null ? "" : UtilsFile.getFileStreamAsString(dictionaryStream);
    }

    /**
     * Reset default settings
     */
    public void reset() {
        spinnerShiftMore.setValue(10);
        radioButtonShiftInSeconds.setSelected(true);
        inputMilisecondsEndings.setText(ShifterPreferences.DEFAULT_FILE_ENDINGS_MILLISECONDS);
        inputSecondsEndings.setText(ShifterPreferences.DEFAULT_FILE_ENDINGS_SECONDS);
        this.textAreaDictionaryTerms.setText(getDefaultTerms());
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
        return checkBoxPreserveCase.isSelected();
    }

    /**
     * Config modified?
     *
     * @return boolean
     */
    public boolean isModified() {
        return   
             Integer.parseInt(this.spinnerShiftMore.getValue().toString()) != ShifterPreferences.getShiftMoreSize()
          || !this.textAreaDictionaryTerms.getText().equals(ShifterPreferences.getTermsDictionary())
          || !ShifterPreferences.getIsActivePreserveCase().equals(this.checkBoxPreserveCase.isSelected())
          || !ShifterPreferences.getShiftingModeOfTimestamps().equals(this.getSelectedShiftingModeOfTimestamps())
          || !ShifterPreferences.getMillisecondsFileEndings().equals(this.inputMilisecondsEndings.getText())
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
        ShifterPreferences.saveMilisecondsFileEndings(this.inputMilisecondsEndings.getToolTipText());
        ShifterPreferences.saveSecondsFileEndings(this.inputSecondsEndings.getToolTipText());

        // Store dictionary
        String dictionary = this.getDictionary();
        if (dictionary != null) {
            ShifterPreferences.saveDictionary(dictionary);
        }
    }

}