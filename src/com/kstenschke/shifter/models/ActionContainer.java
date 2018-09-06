package com.kstenschke.shifter.models;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.kstenschke.shifter.utils.UtilsEnvironment;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;

/**
 * Container for event and deducible IDE environment attributes (project, editor, etc.)
 */
public class ActionContainer {
    public AnActionEvent event;

    public boolean shiftUp;

    public Project project;
    public Editor editor;
    public Document document;

    public CharSequence editorText;

    public SelectionModel selectionModel;
    public int caretOffset;

    public int offsetSelectionStart;
    public int offsetSelectionEnd;
    public int lineNumberSelStart;
    public int lineNumberSelEnd;
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
    public ActionContainer(final AnActionEvent event, boolean shiftUp) {
        this.event = event;

        this.shiftUp = shiftUp;

        editor = event.getData(PlatformDataKeys.EDITOR);
        if (null != editor) {
            project        = editor.getProject();
            document       = editor.getDocument();
            editorText     = document.getCharsSequence();
            caretOffset    = editor.getCaretModel().getOffset();

            selectionModel       = editor.getSelectionModel();
            offsetSelectionStart = selectionModel.getSelectionStart();
            offsetSelectionEnd   = selectionModel.getSelectionEnd();
            lineNumberSelStart   = document.getLineNumber(offsetSelectionStart);
            lineNumberSelEnd     = document.getLineNumber(offsetSelectionEnd);
            selectedText         = UtilsTextual.getSubString(editorText, offsetSelectionStart, offsetSelectionEnd);

            caretLineNumber      = document.getLineNumber(caretOffset);
            offsetCaretLineStart = document.getLineStartOffset(caretLineNumber);
            offsetCaretLineEnd   = document.getLineEndOffset(caretLineNumber);
            caretLine            = editorText.subSequence(offsetCaretLineStart, offsetCaretLineEnd).toString();

            filename      = UtilsEnvironment.getDocumentFilename(document);
            fileExtension = UtilsFile.extractFileExtension(filename, true);
        }
    }
}
