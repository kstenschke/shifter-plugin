package com.kstenschke.shifter.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.project.Project;
import com.kstenschke.shifter.models.ShifterPreferences;
import com.kstenschke.shifter.resources.StaticTexts;

class ShiftUpMoreAction extends AnAction {

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

        int times = ShifterPreferences.getShiftMoreSize();
        for(int i=1; i <= times; i++) {
            CommandProcessor.getInstance().executeCommand(currentProject, new Runnable() {
                public void run() {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        public void run() {
                            new ActionsPerformer(event).write(true);
                        }
                    });
                }
            }, StaticTexts.ACTION_LABEL_SHIFT_UP_MORE, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
        }
    }
}
