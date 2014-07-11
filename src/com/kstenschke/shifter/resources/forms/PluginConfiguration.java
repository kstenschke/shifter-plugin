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

import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.UtilsFile;

import javax.swing.*;
import java.awt.event.*;
import java.io.InputStream;

public class PluginConfiguration {

	private JPanel rootPanel;
	private JButton buttonReset;
	private JTextArea textAreaDictionary;
    private JRadioButton caseSensitiveRadioButton;
    private JRadioButton caseInsensitiveRadioButton;
    private JCheckBox checkBoxpreserveCase;

    /**
	 * Constructor
	 */
	public PluginConfiguration() {
			// Initialize the form
		InitForm();
		
			// Add action listeners to buttons
		this.buttonReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClickReset();
			}
		});
	}

	/**
	 * Initialize the form: fill-in dictionary content from stored preference or factory default
	 */
	private void InitForm() {
		String dictionary	= ShifterPreferences.getDictionary();

        Integer mode    = ShifterPreferences.getSortingMode();
        if( mode.equals(ShifterPreferences.SORTING_MODE_CASE_SENSITIVE)) {
            caseSensitiveRadioButton.setSelected(true);
        } else {
            caseInsensitiveRadioButton.setSelected(true);
        }

        boolean isActivePreserveCase  = ShifterPreferences.getIsActivePreserveCase();
        checkBoxpreserveCase.setSelected(isActivePreserveCase);

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
        caseInsensitiveRadioButton.setSelected(true);
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
        return caseSensitiveRadioButton.isSelected() ?
                ShifterPreferences.SORTING_MODE_CASE_SENSITIVE : ShifterPreferences.SORTING_MODE_CASE_INSENSITIVE;
    }

    /**
     * @return  boolean
     */
    public boolean getIsActivePreserveCase() {
        return checkBoxpreserveCase.isSelected();
    }

	/**
	 * Config modified?
	 *
	 * @return	boolean
	 */
	public boolean isModified() {
		return    ! this.textAreaDictionary.getText().equals( ShifterPreferences.getDictionary() )
               || ! ShifterPreferences.getSortingMode().equals( this.getSelectedSortingMode() )
               || ! ShifterPreferences.getIsActivePreserveCase().equals( this.checkBoxpreserveCase.isSelected()
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
