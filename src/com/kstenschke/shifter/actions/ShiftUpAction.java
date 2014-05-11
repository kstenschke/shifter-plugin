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

package com.kstenschke.shifter.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.project.Project;
import com.kstenschke.shifter.StaticTexts;


/**
 * Shift-Up Action
 */
class ShiftUpAction extends AnAction {

	/**
	 * Disable when no editor available
	 *
	 * @param	event		Action system event
	 */
	public void update( AnActionEvent event ) {
		event.getPresentation().setEnabled(event.getData(PlatformDataKeys.EDITOR) != null);
	}

	/**
	 * Perform shift up
	 *
	 * @param   event    ActionSystem event
	 */
	public void actionPerformed(final AnActionEvent event) {
		Project currentProject = event.getData(PlatformDataKeys.PROJECT);

		CommandProcessor.getInstance().executeCommand(currentProject, new Runnable() {
			public void run() {
				ApplicationManager.getApplication().runWriteAction(new Runnable() {
					public void run() {
						new ActionsPerformer(event).write(true);
					}
				});
			}
		}, StaticTexts.ACTION_LABEL_SHIFT_UP, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
	}

}