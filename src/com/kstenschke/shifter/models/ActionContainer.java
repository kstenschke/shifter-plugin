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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Container for event and deducible IDE environment attributes (project, editor, etc.)
 */
public class ActionContainer {

    public String prefixChar = "";
    public String postfixChar = "";

    public boolean isLastLineInDocument = false; // regards selected or shifted line/selection

    public boolean isShiftUp;
    private final boolean isShiftMore;

    @Nullable public Project project;
    @Nullable public Editor editor;
    @Nullable public Document document;

    @Nullable public CharSequence editorText;
    String documentText;

    @Nullable public SelectionModel selectionModel;
    public int caretOffset;

    public String textAfterCaret;

    public int offsetSelectionStart;
    public int offsetSelectionEnd;
    int lineNumberSelStart;
    int lineNumberSelEnd;

    boolean shiftSelectedText = true;

    public String selectedText;
    String whiteSpaceLHSinSelection;
    String whiteSpaceRHSinSelection;

    private int offsetCaretLineStart;
    public String caretLine;

    public String filename;
    String fileExtension;

    /**
     * Constructor
     */
    public ActionContainer(final AnActionEvent event, boolean isShiftUp, boolean isShiftMore) {
        this.isShiftUp = isShiftUp;
        this.isShiftMore = isShiftMore;

        if (null == event) return;

        editor = event.getData(PlatformDataKeys.EDITOR);
        if (null == editor) {
            return;
        }

        project = editor.getProject();
        document = editor.getDocument();
        editorText = document.getCharsSequence();
        documentText = document.getText();

        selectionModel = editor.getSelectionModel();
        offsetSelectionStart = selectionModel.getSelectionStart();
        offsetSelectionEnd = selectionModel.getSelectionEnd();
        if (documentText.charAt(offsetSelectionEnd - 1) == '\n') {
            // Prevent including line following a selection being included e.g. in line sorting
            offsetSelectionEnd--;
            selectionModel.setSelection(offsetSelectionStart, offsetSelectionEnd);
        }
        lineNumberSelStart = document.getLineNumber(offsetSelectionStart);
        lineNumberSelEnd = document.getLineNumber(offsetSelectionEnd);
        selectedText = UtilsTextual.getSubString(editorText, offsetSelectionStart, offsetSelectionEnd);

        caretOffset = editor.getCaretModel().getOffset();
        int caretLineNumber = document.getLineNumber(caretOffset);
        offsetCaretLineStart = document.getLineStartOffset(caretLineNumber);
        int offsetCaretLineEnd = document.getLineEndOffset(caretLineNumber);
        caretLine = editorText.subSequence(offsetCaretLineStart, offsetCaretLineEnd).toString();

        filename = UtilsEnvironment.getDocumentFilename(document);
        fileExtension = UtilsFile.extractFileExtension(filename, true);
    }

    public void setIsShiftUp(boolean isShiftUp) {
        this.isShiftUp = isShiftUp;
    }

    /**
     * Trim selection and store whitespace from both sides to properties
     */
    void trimSelectedText() {
        String selectedTextTrimmed = selectedText.trim();

        int index = selectedText.indexOf(selectedTextTrimmed);
        whiteSpaceLHSinSelection = "";
        if (0 == index) {
            whiteSpaceRHSinSelection = selectedText.replace(selectedTextTrimmed, "");
        } else {
            whiteSpaceLHSinSelection = selectedText.substring(0, index);
            whiteSpaceRHSinSelection = selectedText.substring(index + selectedTextTrimmed.length());
        }
        selectedText = selectedTextTrimmed;
    }

    @NotNull
    public Runnable getRunnableReplaceSelection(final String shifted) {
        return getRunnableReplaceSelection(shifted, false);
    }
    @NotNull
    public Runnable getRunnableReplaceSelection(final String shifted, final boolean reformat) {
        return () -> {
            document.replaceString(offsetSelectionStart, offsetSelectionEnd, shifted);
            if (reformat) {
                UtilsEnvironment.reformatSubString(editor, project, offsetSelectionStart, offsetSelectionStart + shifted.length());
            }
        };
    }

    @NotNull
    Runnable getRunnableReplaceCaretLine(final CharSequence shiftedLine) {
        return () -> document.replaceString(
                offsetCaretLineStart,
                offsetCaretLineStart + caretLine.length(),
                shiftedLine);
    }

    void writeUndoable(final Runnable runnable) {
        writeUndoable(runnable, null);
    }

    public void writeUndoable(final Runnable runnable, @Nullable String actionText) {
        if (null == actionText) {
            actionText = getDefaultActionText();
        }
        CommandProcessor.getInstance().executeCommand(
                project,
                () -> ApplicationManager.getApplication().runWriteAction(runnable),
                actionText,
                UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
    }

    private String getDefaultActionText() {
        if (isShiftMore) {
            return isShiftUp ? StaticTexts.ACTION_LABEL_SHIFT_UP_MORE : StaticTexts.ACTION_LABEL_SHIFT_DOWN_MORE;
        }

        return isShiftUp ? StaticTexts.ACTION_LABEL_SHIFT_UP : StaticTexts.ACTION_LABEL_SHIFT_DOWN;
    }
}
