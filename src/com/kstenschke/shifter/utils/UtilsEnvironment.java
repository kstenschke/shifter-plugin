/*
 * Copyright 2011-2019 Kay Stenschke
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
package com.kstenschke.shifter.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtilBase;
import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.models.ActionContainer;

import javax.swing.*;
import java.awt.*;

public class UtilsEnvironment {

    public static String getDocumentFilename(com.intellij.openapi.editor.Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);

        return null == file ? "" : file.getName();
    }

    public static void replaceWordAtCaretInDocument(ActionContainer actionContainer, String charSequence) {
        String documentText = actionContainer.document.getText();
        int offsetStart = UtilsTextual.getStartOfWordAtOffset(documentText, actionContainer.caretOffset);
        int offsetEnd   = UtilsTextual.getOffsetEndOfWordAtOffset(documentText, actionContainer.caretOffset);

        actionContainer.document.replaceString(offsetStart, offsetEnd, charSequence);
    }

    public static void reformatSubString(Editor editor, Project project, int offsetStart, int offsetEnd) {
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (null == psiFile) {
            return;
        }

        CodeStyleManager.getInstance(project).reformatText( psiFile, offsetStart, offsetEnd);
    }

    public static void setDialogVisible(Editor editor, String idDialog, JDialog dialog, String title) {
        Point caretLocation = editor.visualPositionToXY(editor.getCaretModel().getVisualPosition());
        SwingUtilities.convertPointToScreen(caretLocation, editor.getComponent());

        Point location = null;
        String[] position = ShifterPreferences.getDialogPosition(idDialog).split("x");
        if (!(position[0].equals("0") && position[1].equals("0"))) {
            location = new Point(Integer.parseInt(position[0]), Integer.parseInt(position[1]));
        }

        if (null == location) {
            // Center to screen
            dialog.setLocationRelativeTo(null);
        } else {
            dialog.setLocation(location.x, location.y);
        }

        dialog.setTitle(title);
        dialog.pack();
        dialog.setVisible(true);
    }
}