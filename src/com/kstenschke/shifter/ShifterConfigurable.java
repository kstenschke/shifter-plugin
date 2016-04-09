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

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.kstenschke.shifter.resources.forms.ShifterConfiguration;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ShifterConfigurable implements Configurable {

    private ShifterConfiguration settingsPanel;

    /**
     * Constructor
     */
    public ShifterConfigurable() {

    }

    public String getDisplayName() {
        return "Shifter";
    }

    @Nullable
    @NonNls
    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (settingsPanel == null) {
            settingsPanel = new ShifterConfiguration();
        }

        settingsPanel.init();

        return settingsPanel.getRootPanel();
    }

    public boolean isModified() {
        return settingsPanel != null && settingsPanel.isModified();
    }

    public void apply() throws ConfigurationException {
        if (settingsPanel != null) {
            settingsPanel.apply();
        }
    }

    public void reset() {
        if (settingsPanel != null) {
            settingsPanel.reset();
        }
    }

    public void disposeUIResources() {
        settingsPanel = null;
    }

}