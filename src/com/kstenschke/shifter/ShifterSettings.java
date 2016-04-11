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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.resources.forms.ShifterConfiguration;
import org.jetbrains.annotations.NotNull;

public class ShifterSettings implements ApplicationComponent {

    public ShifterConfiguration settingsPanel = null;

    /**
     * Constructor
     */
    public ShifterSettings() {
        this.settingsPanel = new ShifterConfiguration();
    }

    public static ShifterSettings getInstance() {
        return ApplicationManager.getApplication().getComponent(ShifterSettings.class);
    }

    @Override
    public void initComponent() {

    }

    public void disposeComponent() {
        settingsPanel = null;
    }

    @NotNull
    public String getComponentName() {
        return StaticTexts.SETTINGS_COMPONENT_NAME;
    }

}
