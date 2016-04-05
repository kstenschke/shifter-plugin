/*
 * Copyright 2011-2016 Kay Stenschke
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
import com.kstenschke.shifter.models.ShifterPreferences;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.resources.forms.ShifterConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

class ShifterProjectComponent implements ProjectComponent, Configurable {

    private ShifterConfiguration settingsPanel = null;

	/**
	 * Constructor
	 *
	 * @param project
     */
    public ShifterProjectComponent(Project project) {

    }

    /**
     * @return  JComponent
     */
    public JComponent createComponent() {
        if (settingsPanel == null) {
            settingsPanel = new ShifterConfiguration();
        }

        reset();

        return settingsPanel.getRootPanel();
    }

    @Nls
    public String getDisplayName() {
		return StaticTexts.SETTINGS_DISPLAY_NAME;
	}

    public boolean isModified() {
        return settingsPanel != null && settingsPanel.isModified();
    }

    public void disposeUIResources() {
		settingsPanel = null;
	}

    public void reset() {

    }

    public void apply() throws ConfigurationException {
        if (settingsPanel != null) {
            settingsPanel.apply();
        }
    }

    public String getHelpTopic() {
		return null;
	}

    public void initComponent() {
        if(settingsPanel != null) settingsPanel.init();
    }

    public void disposeComponent() {
		settingsPanel = null;
	}

    @NotNull
    public String getComponentName() {
		return StaticTexts.SETTINGS_COMPONENT_NAME;
	}

    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }

}
