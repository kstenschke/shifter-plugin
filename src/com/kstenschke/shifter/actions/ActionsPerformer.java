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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.kstenschke.shifter.*;
import com.kstenschke.shifter.shiftertypes.CssLengthValue;
import com.kstenschke.shifter.shiftertypes.NumericValue;
import com.kstenschke.shifter.shiftertypes.StringHtmlEncodable;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class ActionsPerformer {

	private final Editor editor;
	private Document document;

	private CharSequence editorText;

	private int caretOffset;
	private SelectionModel selectionModel;
	private boolean hasSelection;

	/**
	 * Constructor
	 */
	ActionsPerformer(final AnActionEvent event) {
		this.editor = event.getData(PlatformDataKeys.EDITOR);

		if (this.editor != null) {
			this.document       = this.editor.getDocument();
            this.editorText     = this.document.getCharsSequence();
            this.caretOffset    = this.editor.getCaretModel().getOffset();
            this.selectionModel = this.editor.getSelectionModel();
            this.hasSelection   = this.selectionModel.hasSelection();
		}
	}

	/**
     * @param    shiftUp     Shift up or down?
     */
	public void write(boolean shiftUp) {
		if (this.editor != null) {
			if (this.hasSelection) {
                if( this.selectionModel.getBlockSelectionStarts().length > 1 ) {
                        // Shift block selection: do word-shifting if all items are identical
                    shiftBlockSelection(shiftUp);
                } else {
                        // Shift regular selection: sort lines alphabetically
                    shiftSelection(shiftUp);
                }
			} else {
				// Shift word at caret
                String filename     = this.getFilename();
				String word	        = UtilsTextual.getWordAtOffset(editorText, caretOffset);

				int lineNumber      = document.getLineNumber(caretOffset);
				int offsetLineStart = document.getLineStartOffset(lineNumber);
				int offsetLineEnd   = document.getLineEndOffset(lineNumber);

                String line = editorText.subSequence(offsetLineStart, offsetLineEnd).toString();

				Boolean isWordShifted = false;

				if ( word != null && ! word.isEmpty() ) {
                    isWordShifted = getShiftedWord(shiftUp, filename, word, line, null, true).equals(word);
				}
				    // Word at caret wasn't identified/shifted, try shifting the whole line
				if ( !isWordShifted ) {
					ShiftableLine shiftableLine = new ShiftableLine(line, editorText, caretOffset, filename);

					// Replace line by shifted one
					CharSequence shiftedLine = shiftableLine.getShifted(shiftUp, editor);
					if( shiftedLine != null ) {
						document.replaceString(offsetLineStart, offsetLineStart + line.length(), shiftedLine);
					}
				}
			}
		}
	}

    /**
     * @return  String  filename of currently edited document
     */
    private String getFilename() {
        VirtualFile file	= FileDocumentManager.getInstance().getFile(document);

        return (file != null) ? file.getName() : "";
    }

    /**
     * @param   shiftUp
     * @param   filename
     * @param   word
     * @param   line
     * @param   wordOffset  null = calculate from word at offset
     * @return  Boolean
     */
    private String getShiftedWord(boolean shiftUp, String filename, String word, String line, @Nullable Integer wordOffset, Boolean replaceInDocument) {
        Boolean wordShifted = false;

        if( wordOffset == null ) {
                // Extract offset of word at caret
            wordOffset = UtilsTextual.getStartOfWordAtOffset(this.editorText, this.caretOffset);
        }

        String prefixChar   = UtilsTextual.getCharBeforeOffset(this.editorText, wordOffset);
        String postfixChar  = UtilsTextual.getCharAfterOffset(this.editorText, wordOffset + word.length() - 1);

            // Identify word type and shift it accordingly
        ShiftableWord shiftableWord = new ShiftableWord(word, prefixChar, postfixChar, line, this.editorText, this.caretOffset, filename);

            // Comprehend negative values of numeric types
        if( NumericValue.isNumericValue(word) || CssLengthValue.isCssLengthValue(word) ) {
            if ( prefixChar.equals("-") ) {
                word = "-" + word;
                wordOffset--;
            }
        }

        String newWord = shiftableWord.getShifted(shiftUp, editor);

        if (newWord != null && newWord.length() > 0 && !newWord.matches(word) && wordOffset != null ) {
            if( replaceInDocument ) {
                    // Replace word at caret by shifted one (if any)
                document.replaceString(wordOffset, wordOffset + word.length(), newWord);
            }
            wordShifted = true;
        }

        return wordShifted ? newWord : word;
    }

    /**
     * @param   shiftUp
     */
    private void shiftBlockSelection(Boolean shiftUp) {
        int[] blockSelectionStarts                  = this.selectionModel.getBlockSelectionStarts();
        int[] blockSelectionEnds                    = this.selectionModel.getBlockSelectionEnds();

        if( this.areBlockItemsIdentical(blockSelectionStarts, blockSelectionEnds) ) {
            String word         = editorText.subSequence( blockSelectionStarts[0], blockSelectionEnds[0]).toString();
            String line         = UtilsTextual.extractLine( this.document, this.document.getLineNumber(blockSelectionStarts[0]) ).trim();
            Integer wordOffset  = UtilsTextual.getStartOfWordAtOffset(this.editorText, blockSelectionStarts[0]);
            String newWord      = this.getShiftedWord(shiftUp, this.getFilename(), word, line, wordOffset, false);

            if( newWord != null && ! newWord.equals(word) ) {
                for(int i= blockSelectionEnds.length-1; i >= 0; i--) {
                    document.replaceString(blockSelectionStarts[i], blockSelectionEnds[i], newWord);
                }
            }
        }
    }

    /**
     * @param   blockSelectionStarts
     * @param   blockSelectionEnds
     * @return  Boolean
     */
    private Boolean areBlockItemsIdentical(int[] blockSelectionStarts, int[] blockSelectionEnds) {
        String firstItem = editorText.subSequence( blockSelectionStarts[0], blockSelectionEnds[0]).toString();
        String currentItem;

        for(int i=1; i< blockSelectionStarts.length; i++) {
            currentItem = editorText.subSequence( blockSelectionStarts[i], blockSelectionEnds[i]).toString();
            if( ! currentItem.equals(firstItem) ) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param   shiftUp
     */
    private void shiftSelection(boolean shiftUp) {
        int offsetStart         = selectionModel.getSelectionStart();
        int offsetEnd           = selectionModel.getSelectionEnd();
        int lineNumberSelStart  = document.getLineNumber(offsetStart);
        int lineNumberSelEnd    = document.getLineNumber(offsetEnd);

        if (document.getLineStartOffset(lineNumberSelEnd) == offsetEnd) {
            lineNumberSelEnd--;
        }

        // Extract text as a list of lines
        List<String> lines = UtilsTextual.extractLines(document, lineNumberSelStart, lineNumberSelEnd);

        if( lines.size() > 1) {
                // Sort lines alphabetically
            StringBuilder sortedText = UtilsTextual.joinLines(this.sortLines(lines, shiftUp));
            int offsetLineStart     = document.getLineStartOffset(lineNumberSelStart);
            int offsetLineEnd       = document.getLineEndOffset(lineNumberSelEnd) + document.getLineSeparatorLength(lineNumberSelEnd);

            document.replaceString(offsetLineStart, offsetLineEnd, sortedText);
        } else {
            // Selection within one line
            String selectedText  = UtilsTextual.getSubString(editorText, offsetStart, offsetEnd);

            if( UtilsTextual.isCommaSeparatedList(selectedText) ) {
                String sortedList = this.sortCommaSeparatedList(selectedText, shiftUp);

                document.replaceString(offsetStart, offsetEnd, sortedList);
            } else if( UtilsTextual.containsAnyQuotes(selectedText) ) {
                document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapQuotes(selectedText));
            } else if( UtilsTextual.containsAnySlashes(selectedText) ) {
                document.replaceString(offsetStart, offsetEnd, UtilsTextual.swapSlashes(selectedText));
            } else if(StringHtmlEncodable.isHtmlEncodable(selectedText)) {
                document.replaceString( offsetStart, offsetEnd, StringHtmlEncodable.getShifted(selectedText) );
            }
        }
    }

    /**
	 * @param   lines
	 * @param   shiftUp
	 * @return  Given lines sorted alphabetically ascending / descending
	 */
	private List<String> sortLines(List<String> lines, Boolean shiftUp) {
        if( ShifterPreferences.getSortingMode().equals(ShifterPreferences.SORTING_MODE_CASE_INSENSITIVE) ) {
		    Collections.sort(lines, String.CASE_INSENSITIVE_ORDER);
        } else {
		    Collections.sort(lines);
        }

		if( !shiftUp ) {
			Collections.reverse(lines);
		}

		return lines;
	}

	/**
	 * @param   selectedText
	 * @param   shiftUp
	 * @return  Given comma separated list, sorted alphabetically ascending / descending
	 */
	private String sortCommaSeparatedList(String selectedText, Boolean shiftUp) {
		String[] items = selectedText.split(",(\\s)*");

        if( ShifterPreferences.getSortingMode().equals(ShifterPreferences.SORTING_MODE_CASE_INSENSITIVE) ) {
		    Arrays.sort(items, String.CASE_INSENSITIVE_ORDER);
        } else {
            Arrays.sort(items);
        }

		if( !shiftUp ) {
			Collections.reverse(Arrays.asList(items));
		}

		return UtilsArray.implode(items, ", ");
	}

}
