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
package com.kstenschke.shifter.models.entities.shiftables;

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.ShiftableSelectionWithPopup;
import com.kstenschke.shifter.models.ShiftableTypes;
import com.kstenschke.shifter.models.entities.AbstractShiftable;
import com.kstenschke.shifter.utils.UtilsFile;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.Nullable;

import static com.kstenschke.shifter.models.ShiftableTypes.Type.PHP_DOCUMENT;

// PHP Variable (word w/ $ prefix)
public class PhpDocument extends AbstractShiftable {

    static final String ACTION_TEXT_SHIFT_SELECTION = "Shift Selection";

    public final String ACTION_TEXT = "Shift PHP";

    // Constructor
    public PhpDocument(ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    public PhpDocument getInstance(@Nullable Boolean checkIfShiftable) {
        return UtilsFile.isPhpFile(actionContainer.filename) &&
               null != actionContainer.selectedText
                ? this
                : null;
    }

    @Override
    public ShiftableTypes.Type getType() {
        return PHP_DOCUMENT;
    }

    public String getShifted(
            String str,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        return null;
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        AbstractShiftable shiftableType;

        final PhpConcatenation phpConcatenation = new PhpConcatenation(actionContainer);
        if (null != phpConcatenation.getInstance()) {
            actionContainer.writeUndoable(
                    () -> new ShiftableSelectionWithPopup(actionContainer).shiftPhpConcatenationOrSwapQuotesInDocument(phpConcatenation),
                    ACTION_TEXT_SHIFT_SELECTION);
            return true;
        }

        if (Comment.isHtmlComment(actionContainer.selectedText)) {
            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceSelection(Comment.getPhpBlockCommentFromHtmlComment(actionContainer.selectedText)),
                    ACTION_TEXT_SHIFT_SELECTION);
            return true;
        }
        shiftableType = new Comment(actionContainer);
        if (Comment.isPhpBlockComment(actionContainer.selectedText)) {
            actionContainer.writeUndoable(
                    actionContainer.getRunnableReplaceSelection(shiftableType.getShifted(actionContainer.selectedText)),
                    ACTION_TEXT_SHIFT_SELECTION);
            return true;
        }
        return false;
    }
}