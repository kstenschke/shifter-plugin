/*
 * Copyright 2011-2018 Kay Stenschke
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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.Map;

class PluginConfigurationListenerRestoreSettings implements MouseListener {

    private final PluginConfiguration pluginConfiguration;

    private Font original;

    /**
     * Constructor
     *
     * @param pluginConfiguration
     */
    public PluginConfigurationListenerRestoreSettings(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        pluginConfiguration.reset();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        original = mouseEvent.getComponent().getFont();
        Map attributes = original.getAttributes();
        //noinspection unchecked
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        //noinspection unchecked
        mouseEvent.getComponent().setFont(original.deriveFont(attributes));
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        mouseEvent.getComponent().setFont(original);
    }
}