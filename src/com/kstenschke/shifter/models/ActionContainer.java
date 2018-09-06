package com.kstenschke.shifter.models;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.utils.UtilsEnvironment;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;

import javax.annotation.Nullable;

/**
 * Container for event and deducible IDE environment attributes (project, editor, etc.)
 */
public class ActionContainer {
    public final boolean isShiftUp;
    public final boolean isShiftMore;

    public Project project;
    public Editor editor;
    public Document document;

    public CharSequence editorText;
    public String documentText;

    public SelectionModel selectionModel;
    public int caretOffset;

    public int offsetSelectionStart;
    public int offsetSelectionEnd;
    public int lineNumberSelStart;
    public int lineNumberSelEnd;
    public int offsetSelectionLineStart;
    public int offsetSelectionLineEnd;
    public String selectedText;

    public int caretLineNumber;
    public int offsetCaretLineStart;
    public int offsetCaretLineEnd;
    public String caretLine;

    public String filename;
    public String fileExtension;

    /**
     * Constructor
     */
    public ActionContainer(final AnActionEvent event, boolean isShiftUp, boolean isShiftMore) {
        this.isShiftUp = isShiftUp;
        this.isShiftMore = isShiftMore;

        editor = event.getData(PlatformDataKeys.EDITOR);
        if (null != editor) {
            project        = editor.getProject();
            document       = editor.getDocument();
            editorText     = document.getCharsSequence();
            documentText   = document.getText();

            selectionModel       = editor.getSelectionModel();
            offsetSelectionStart = selectionModel.getSelectionStart();
            offsetSelectionEnd   = selectionModel.getSelectionEnd();
            lineNumberSelStart   = document.getLineNumber(offsetSelectionStart);
            lineNumberSelEnd     = document.getLineNumber(offsetSelectionEnd);
            selectedText         = UtilsTextual.getSubString(editorText, offsetSelectionStart, offsetSelectionEnd);

            caretOffset          = editor.getCaretModel().getOffset();
            caretLineNumber      = document.getLineNumber(caretOffset);
            offsetCaretLineStart = document.getLineStartOffset(caretLineNumber);
            offsetCaretLineEnd   = document.getLineEndOffset(caretLineNumber);
            caretLine            = editorText.subSequence(offsetCaretLineStart, offsetCaretLineEnd).toString();

            offsetSelectionLineStart = document.getLineStartOffset(lineNumberSelStart);
            offsetSelectionLineEnd   = document.getLineEndOffset(lineNumberSelEnd) + document.getLineSeparatorLength(lineNumberSelEnd);

            filename      = UtilsEnvironment.getDocumentFilename(document);
            fileExtension = UtilsFile.extractFileExtension(filename, true);
        }
    }

    public void writeUndoable(final Runnable runnable) {
        writeUndoable(runnable, null);
    }

    public void writeUndoable(final Runnable runnable, @Nullable String actionText) {
        if (null == actionText) {
            actionText = getDefaultActionText();
        }
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        runnable.run();
                    }
                });
            }
        }, actionText, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
    }

    private String getDefaultActionText() {
        if (isShiftMore) {
            return isShiftUp ? StaticTexts.ACTION_LABEL_SHIFT_UP_MORE : StaticTexts.ACTION_LABEL_SHIFT_DOWN_MORE;
        }

        return isShiftUp ? StaticTexts.ACTION_LABEL_SHIFT_UP : StaticTexts.ACTION_LABEL_SHIFT_DOWN;
    }
}
