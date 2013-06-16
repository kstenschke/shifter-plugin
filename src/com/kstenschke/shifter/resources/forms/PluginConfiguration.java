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

package com.kstenschke.shifter.resources.forms;

import com.intellij.openapi.options.Configurable;
import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.helpers.FileHelper;

import javax.swing.*;
import java.awt.event.*;
import java.io.InputStream;


public class PluginConfiguration {

	public JPanel rootPanel;
	private JButton buttonReset;
	private JTextArea textAreaDictionary;
	private JPanel buttons;
	private JScrollPane jspane;


	/**
	 * Constructor
	 */
	public PluginConfiguration() {
			// Initialize the form
		InitForm();
		
			// Add action listeners to buttons
		this.buttonReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClickReset(e);
			}
		});
	}


	
	/**
	 * Initialize the form: fill-in dictionary content from stored preference or factory default
	 */
	private void InitForm() {
		String dictionary	= ShifterPreferences.getDictionary();

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

		return dictionaryStream == null ? "" : FileHelper.getFileStreamAsString(dictionaryStream);
	}



	public String getDictionaryContents() {
		return this.textAreaDictionary.getText();
	}



	/**
	 * Reset the form to factory default
	 */
	private void onClickReset(ActionEvent e) {
		this.textAreaDictionary.setText( getDefaultDictionary() );
	}
	
	

	public JPanel getRootPanel() {
		return rootPanel;
	}



	/**
	 * Config modified?
	 *
	 * @return	Boolean
	 */
	public boolean isModified() {
		return ! this.textAreaDictionary.getText().equals( ShifterPreferences.getDictionary() );
	}



	public void setData() {

	}



	public String getData() {
		return this.textAreaDictionary.getText();
	}




	private void createUIComponents() {
		// TODO: place custom component creation code here
	}

}
