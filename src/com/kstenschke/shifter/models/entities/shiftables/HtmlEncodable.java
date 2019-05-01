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
import com.kstenschke.shifter.models.ShiftableTypes;
import com.kstenschke.shifter.models.entities.AbstractShiftable;
import org.apache.commons.lang.StringEscapeUtils;

import javax.annotation.Nullable;

// HTML encoded/encode-able (=containing char(s) that be be encoded) String.
public class HtmlEncodable extends AbstractShiftable {

    public final String ACTION_TEXT = "Shift HTML Entities";

    // Constructor
    public HtmlEncodable(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable:
    // Shiftable sting must be encode-able to an HTML special char / or is already HTML encoded
    public HtmlEncodable getInstance() {
        String str = actionContainer.shiftCaretLine
                ? actionContainer.caretLine
                : actionContainer.selectedText;

        Integer strLenOriginal = str.length();

        String encoded = StringEscapeUtils.escapeHtml(str);
        String decoded = StringEscapeUtils.unescapeHtml(str);

        return !strLenOriginal.equals(encoded.length()) ||
               !strLenOriginal.equals(decoded.length())
                ? this : null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.HTML_ENCODABLE;
    }

    /**
     * Shift to HTML encoded/decoded variant of given string
     *
     * @param  word     word to be shifted
     * @return String
     */
    public String getShifted(
            String word,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        Integer strLenOriginal = word.length();

        String decoded = StringEscapeUtils.unescapeHtml(word);
        Integer strLenDecoded = decoded.length();

        if (!strLenOriginal.equals(strLenDecoded)) return decoded;

        String encoded = StringEscapeUtils.escapeHtml(word);
        Integer strLenEncoded = encoded.length();

        return !strLenOriginal.equals(strLenEncoded) ? encoded : word;
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        actionContainer.writeUndoable(
                actionContainer.getRunnableReplaceSelection(
                        getShifted(actionContainer.selectedText)),
                ACTION_TEXT);
        return true;
    }
}