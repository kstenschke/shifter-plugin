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

package com.kstenschke.shifter.resources.forms;

import com.kstenschke.shifter.models.ShifterPreferences;
import com.kstenschke.shifter.resources.Icons;
import com.kstenschke.shifter.utils.UtilsFile;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.io.InputStream;

public class PluginConfiguration {

	private JPanel rootPanel;
	private JButton buttonReset;
	private JTextArea textAreaDictionary;
    private JCheckBox checkBoxPreserveCase;

    private JRadioButton radioButtonCaseSensitive;
    private JRadioButton radioButtonCaseInsensitive;
    private JRadioButton radioButtonShiftInSeconds;
    private JRadioButton radioButtonShiftInMilliseconds;

    public Boolean hasSomethingChanged = false;

    /**
	 * Constructor
	 */
	public PluginConfiguration() {
		initFormValues();
        initFormListeners();
        initFormIcons();
    }

    private void initFormIcons() {
        this.buttonReset.setIcon(Icons.ICON_RESET);
    }

    private void initFormListeners() {
        ChangeListener somethingChangedListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                hasSomethingChanged = true;
            }
        };
        radioButtonShiftInSeconds.addChangeListener(somethingChangedListener);
        radioButtonShiftInMilliseconds.addChangeListener(somethingChangedListener);
        radioButtonCaseSensitive.addChangeListener(somethingChangedListener);
        radioButtonCaseInsensitive.addChangeListener(somethingChangedListener);

        this.buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickReset();
            }
        });
    }

    /**
	 * Initialize the form: fill-in dictionary content from stored preference or factory default
	 */
	private void initFormValues() {
		String dictionary   = ShifterPreferences.getDictionary();

        if( ShifterPreferences.getSortingMode().equals(ShifterPreferences.SORTING_MODE_CASE_SENSITIVE)) {
            this.radioButtonCaseSensitive.setSelected(true);
        } else {
            this.radioButtonCaseInsensitive.setSelected(true);
        }

        if( ShifterPreferences.getShiftingModeOfTimestamps().equals(ShifterPreferences.SHIFTING_MODE_TIMESTAMP_SECONDS)) {
            this.radioButtonShiftInSeconds.setSelected(true);
        } else {
            this.radioButtonShiftInMilliseconds.setSelected(true);
        }

        boolean isActivePreserveCase  = ShifterPreferences.getIsActivePreserveCase();
        this.checkBoxPreserveCase.setSelected(isActivePreserveCase);

		if( dictionary == null || dictionary.equals("") )  {
			dictionary	= getDefaultDictionary();
		}

		this.textAreaDictionary.setText(dictionary);
	}

	/**
	 * Get default dictionary contents
	 *
	 * @return	Default dictionary
	 */
	public String getDefaultDictionary() {
		//@note for the .txt resource to be included in the jar, it must be set in compiler resource settings
		InputStream dictionaryStream= this.getClass().getResourceAsStream("dictionary.txt");

		return dictionaryStream == null ? "" : UtilsFile.getFileStreamAsString(dictionaryStream);
	}

    /**
     * @return  String
     */
	public String getDictionaryContents() {
		return this.textAreaDictionary.getText();
	}

	/**
	 * Reset default settings
	 */
	private void onClickReset() {
        radioButtonCaseInsensitive.setSelected(true);
        radioButtonShiftInSeconds.setSelected(true);
        this.textAreaDictionary.setText( getDefaultDictionary() );
	}

    /**
     * @return  JPanel
     */
	public JPanel getRootPanel() {
		return rootPanel;
	}

    /**
     * @return  Integer     Sorting mode
     */
    public Integer getSelectedSortingMode() {
        return radioButtonCaseSensitive.isSelected()
                ? ShifterPreferences.SORTING_MODE_CASE_SENSITIVE
                : ShifterPreferences.SORTING_MODE_CASE_INSENSITIVE;
    }

    public Integer getSelectedShiftingModeOfTimestamps() {
        return radioButtonShiftInSeconds.isSelected()
                ? ShifterPreferences.SHIFTING_MODE_TIMESTAMP_SECONDS
                : ShifterPreferences.SHIFTING_MODE_TIMESTAMP_MILLISECONDS;
    }

    /**
     * @return  boolean
     */
    public boolean getIsActivePreserveCase() {
        return checkBoxPreserveCase.isSelected();
    }

	/**
	 * Config modified?
	 *
	 * @return	boolean
	 */
	public boolean isModified() {
		return      this.hasSomethingChanged
               || ! this.textAreaDictionary.getText().equals( ShifterPreferences.getDictionary() )
               || ! ShifterPreferences.getSortingMode().equals( this.getSelectedSortingMode() )
               || ! ShifterPreferences.getIsActivePreserveCase().equals(this.checkBoxPreserveCase.isSelected()
               || ! ShifterPreferences.getShiftingModeOfTimestamps().equals( this.getSelectedShiftingModeOfTimestamps())
        );
	}

	public void setData() {

	}

    /**
     * @return  String
     */
	public String getData() {
		return this.textAreaDictionary.getText();
	}

	private void createUIComponents() {

	}

}
