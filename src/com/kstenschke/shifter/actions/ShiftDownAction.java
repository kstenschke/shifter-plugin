/*
 * Copyright 2012 Kay Stenschke
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
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.kstenschke.shifter.helpers.ArrayHelper;
import com.kstenschke.shifter.helpers.TextualHelper;
import com.kstenschke.shifter.models.ShiftableLine;
import com.kstenschke.shifter.models.ShiftableWord;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;



/**
 * Shift-Down Action
 */
public class ShiftDownAction extends AnAction {

	/**
	 * @param   event    ActionSystem event
	 */
	public void update(AnAction event) {
//        event.getPresentation().setEnabled(event.getDataContext().getData(DataConstants.EDITOR) != null);
	}



	/**
	 * Perform shift down
	 *
	 * @param   event    ActionSystem event
	 */
	public void actionPerformed(final AnActionEvent event) {

		Project currentProject = event.getData(PlatformDataKeys.PROJECT);
		//Project currentProject = (Project) event.getDataContext().getData(DataConstants.PROJECT);

		CommandProcessor.getInstance().executeCommand(currentProject, new Runnable() {
			public void run() {

				ApplicationManager.getApplication().runWriteAction(new Runnable() {
					public void run() {
						Editor editor = event.getData(PlatformDataKeys.EDITOR);
						//Editor editor = (Editor) event.getDataContext().getData(DataConstants.EDITOR);

				if (editor != null) {
					final Document document = editor.getDocument();
					CharSequence editorText = document.getCharsSequence();

					int caretOffset = editor.getCaretModel().getOffset();

					SelectionModel selectionModel = editor.getSelectionModel();
					boolean hasSelection = selectionModel.hasSelection();

					if (hasSelection) {
						//-------------------- Shift selection: sort lines alphabetically ascending
						int offsetStart   = selectionModel.getSelectionStart();
						int offsetEnd     = selectionModel.getSelectionEnd();

						int lineNumberSelStart = document.getLineNumber(offsetStart);
						int lineNumberSelEnd = document.getLineNumber(offsetEnd);

						if (document.getLineStartOffset(lineNumberSelEnd) == offsetEnd) {
							lineNumberSelEnd--;
						}

						// Extract text as a list of lines
						List<String> lines = TextualHelper.extractLines(document, lineNumberSelStart, lineNumberSelEnd);

						if( lines.size() > 1) {
							// sort lines ascending
							Collections.sort(lines);

							StringBuilder sortedText = TextualHelper.joinLines(lines);

							// Replace text
							int offsetLineStart  = document.getLineStartOffset(lineNumberSelStart);
							int offsetLineEnd    = document.getLineEndOffset(lineNumberSelEnd) + document.getLineSeparatorLength(lineNumberSelEnd);

							editor.getDocument().replaceString(offsetLineStart, offsetLineEnd, sortedText);
						} else {
							// Selection within one line
							String selectedText  = TextualHelper.getSubString(editorText, offsetStart, offsetEnd);
							if( TextualHelper.isCommaSeparatedList(selectedText) ) {
								String[] items = selectedText.split(",(\\s)*");
								Arrays.sort(items);
								String sortedList = ArrayHelper.implode(items, ", ");

								document.replaceString(offsetStart, offsetEnd, sortedList);
							}
						}
					} else {
						//-------------------- Shift word at caret
						VirtualFile file	= FileDocumentManager.getInstance().getFile(document);
						String filename		= (file != null) ? file.getName() : "";

						String word	= TextualHelper.getWordAtOffset(editorText, caretOffset);

						int lineNumber = document.getLineNumber(caretOffset);
						int offsetLineStart = document.getLineStartOffset(lineNumber);
						int offsetLineEnd = document.getLineEndOffset(lineNumber);
						String line = editorText.subSequence(offsetLineStart, offsetLineEnd).toString();

						Boolean wordShifted = false;

						if ( word != null && ! word.isEmpty() ) {
							int wordOffset = TextualHelper.getStartOfWordAtOffset(editorText, caretOffset);
							String prefixChar = TextualHelper.getCharBeforeOffset(editorText, wordOffset);
							String postfixChar = TextualHelper.getCharAfterOffset(editorText, wordOffset + word.length() - 1);

								// Identify word type and shift it accordingly
							ShiftableWord shiftableWord = new ShiftableWord(word, prefixChar, postfixChar, line, editorText, caretOffset, filename);
							String newWord = shiftableWord.getShifted(false, editor);

							if (newWord != null && newWord.length() > 0 && !newWord.matches(word)) {
									// Replace word at caret by shifted one (if any)
								document.replaceString(wordOffset, wordOffset + word.length(), newWord);
								wordShifted = true;
							}
						}
							// -------------------- Word at caret wasn't identified/shifted, try shifting the whole line
						if ( !wordShifted ) {
								ShiftableLine shiftableLine = new ShiftableLine(line, editorText, caretOffset, filename);

										  // Replace line by shifted one
								CharSequence shiftedLine = shiftableLine.getShifted(false, editor);
								if( shiftedLine != null ) {
									document.replaceString(offsetLineStart, offsetLineStart + line.length(), shiftedLine);
								}
						}
					}
				}

			}
		});

            }}, "Shift Down", UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
	}

}