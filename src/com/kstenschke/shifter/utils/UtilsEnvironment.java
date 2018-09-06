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
package com.kstenschke.shifter.utils;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtilBase;
import com.kstenschke.shifter.ShifterPreferences;

import javax.swing.*;
import java.awt.*;

public class UtilsEnvironment {

    /**
     * @return The currently opened project
     */
    public static Project getOpenProject() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();

        return (projects.length > 0) ? projects[0] : null;
    }

    public static String getDocumentFilename(com.intellij.openapi.editor.Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);

        return null == file ? "" : file.getName();
    }

    public static boolean replaceWordAtCaretInDocument(Document document, int caretOffset, String charSequence) {
        String documentText = document.getText();
        int offsetStart = UtilsTextual.getStartOfWordAtOffset(documentText, caretOffset);
        int offsetEnd   = UtilsTextual.getOffsetEndOfWordAtOffset(documentText, caretOffset);

        document.replaceString(offsetStart, offsetEnd, charSequence);

        return true;
    }

    public static void reformatSubString(Editor editor, Project project, int offsetStart, int offsetEnd) {
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (null == psiFile) {
            return;
        }

        CodeStyleManager.getInstance(project).reformatText( psiFile, offsetStart, offsetEnd);
    }

    public static void reformatSelection(Editor editor, Project project) {
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (null == psiFile) {
            return;
        }
        SelectionModel selectionModel = editor.getSelectionModel();
        int offsetStart = selectionModel.getSelectionStart();
        int offsetEnd   = selectionModel.getSelectionEnd();
        if (offsetStart == offsetEnd) {
            return;
        }

        CodeStyleManager.getInstance(project).reformatText( psiFile, offsetStart, offsetEnd);
    }

    /**
     * @param   editor
     * @param   idDialog
     * @param   dialog
     * @param   title
     */
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