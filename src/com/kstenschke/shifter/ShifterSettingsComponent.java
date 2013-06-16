/*
 * Copyright 2012 Kay Stenschke
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

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.kstenschke.shifter.resources.forms.PluginConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class ShifterSettingsComponent implements ProjectComponent, Configurable {

	// Note: Components shown in the IDEA settings dialog have 32x32 icons.
	private ImageIcon icon = new ImageIcon("/com/kstenschke/shifter/resources/images/blank32x32.png");

	private PluginConfiguration settingsPanel = null;

	public JComponent createComponent() {
		if (settingsPanel == null) {
			settingsPanel = new PluginConfiguration();
		}

		reset();

		return settingsPanel.getRootPanel();
	}

	@Nls
	public String getDisplayName() {
		return "Shifter Dictionary";
	}

	public boolean isModified() {
		return settingsPanel != null && settingsPanel.isModified();
	}

	public void disposeUIResources() {
		settingsPanel = null;
	}

	public void reset() {
		if (settingsPanel != null) {
			// Reset settingsPanel data from component
// 			settingsPanel.setData();
		}
	}

	/**
	 * Get the icon of this {@link com.intellij.openapi.options.Configurable}.
	 */
	public Icon getIcon() {
		if (icon == null) {
			icon = new ImageIcon("/com/kstenschke/shifter/resources/images/blank32x32.png");
		}

		return icon;
	}

	public void apply() throws ConfigurationException {
		if (settingsPanel != null) {
			// Get data from settingsPanel to component
			String dictionary = settingsPanel.getData();
			if (dictionary != null) {
				ShifterPreferences.saveDictionary(dictionary);
			}

			applyGlobalSettings();
		}
	}

	public String getHelpTopic() {
		return null;
	}

	private void applyGlobalSettings() {

	}

	public ShifterSettingsComponent(Project project) {

	}

	public void initComponent() {
		// TODO: insert component initialization logic here
	}

	public void disposeComponent() {
		settingsPanel = null;
	}

	@NotNull
	public String getComponentName() {
		return "Shifter Settings";
	}

	public void projectOpened() {
		// called when project is opened
	}

	public void projectClosed() {
		// called when project is being closed
	}

}