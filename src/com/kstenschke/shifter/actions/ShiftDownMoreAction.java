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
package com.kstenschke.shifter.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.project.Project;
import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.resources.StaticTexts;

class ShiftDownMoreAction extends AnAction {

    /**
     * Disable when no editor available
     *
     * @param event Action system event
     */
    public void update(AnActionEvent event) {
        event.getPresentation().setEnabled(event.getData(PlatformDataKeys.EDITOR) != null);
    }

    /**
     * Perform shift up
     *
     * @param event ActionSystem event
     */
    public void actionPerformed(final AnActionEvent event) {
        ActionAdapter actionAdapter = new ActionAdapter(event, false, true);
        int moreSize = ShifterPreferences.getShiftMoreSize();

        if (actionAdapter.actionContainer.selectionModel.getBlockSelectionStarts().length > 1) {
            // Shift of block selection: is not iterated, but run w/ higher value
            actionAdapter.delegate(moreSize);
            return;
        }

        for (int i = 1; i <= moreSize; i++) {
            actionAdapter.delegate(i);
        }
    }
}