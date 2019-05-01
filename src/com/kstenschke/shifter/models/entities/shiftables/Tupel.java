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
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Tupel (two items w/ delimiter in between)
public class Tupel extends AbstractShiftable {

    public static final String ACTION_TEXT = "Shift Tupel";

    // Defined during detection of being a tupel
    private String delimiter;

    // Constructor
    public Tupel(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    public Tupel getInstance() {
        if (// @todo make shiftable also in non-selection
            null == actionContainer.selectedText
        ) return null;

        String str = actionContainer.selectedText;

        String glues[] = new String[]{
                ",",

                // Multi-character delimiters containing singe-character delimiters must precede those
                "!==", "!=",
                "===", "==",
                "<=", ">=",
                "&&", "||",

                ".", ":",
                "+", "-", "*", "/", "%", "=",
                "&", "|",
                "<", ">",

                // Space must be last to not be prematurely detected around other delimiter
                " ",
        };

        for (String glue : glues) {
            String parts[] = str.split("\\s*" + Pattern.quote(glue) + "\\s*");
            if (parts.length == 2 && !parts[0].isEmpty() && !parts[1].isEmpty()) {
                delimiter = glue;
                return this;
            }
        }

        return null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.WORDS_TUPEL;
    }

    // Swap tupel parts
    public String getShifted(String str) {
        if (
                actionContainer != null &&
                !actionContainer.disableIntentionPopup &&
                " ".equals(delimiter) &&
                !actionContainer.selectedText.isEmpty() &&
                UtilsTextual.subStringCount(str, " ") == 1
        ) {
            DictionaryWord dictionaryWord = new DictionaryWord(actionContainer);
            if (null != dictionaryWord.getInstance()) {
                // Shifted string is a selected tupel, and a two-words term from the dictionary
                new ShiftableSelectionWithPopup(actionContainer)
                        .shiftDictionaryTermOrToggleTupelOrder();

                return "";
            }
        }

        return getShiftedTupelReplacement(str);
    }

    public String getShifted(
            String str,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        return getShifted(str);
    }

    @NotNull
    private String getShiftedTupelReplacement(String str) {
        if (null == delimiter) return str;

        if (!" ".equals(delimiter) && str.contains(" ")) {
            // Edge-case: there is a space and another tupel-delimiter, space overrules than
            // Example: ensure turning "foo bar-baz" into "bar-baz foo", not into "baz-foo bar"
            delimiter = " ";
        }

        // Split into tupel
        String splitPattern = "\\s*" + Pattern.quote(delimiter) + "\\s*";
        String[] parts      = str.split(splitPattern);

        // Retain variable whitespace around delimiters
        Pattern partsPattern = Pattern.compile(splitPattern);
        Matcher matcher      = partsPattern.matcher(str);
        if (matcher.find()) {
            String glueWithWhitespace = matcher.group(0);
            // Swap parts
            return parts[1].trim() + glueWithWhitespace + parts[0].trim();
        }

        return parts[1].trim() + delimiter + parts[0].trim();
    }
}