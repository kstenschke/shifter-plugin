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

import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.ShiftableTypes;
import com.kstenschke.shifter.resources.ui.PluginConfiguration;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;

public class DictionaryWordOfSpecificFileType extends DictionaryWord {

    public final String ACTION_TEXT = "Shift Word";

    // Terms-list containing the term to be shifted, set during detection
    private String relevantTermsList;

    // The complete dictionary
    private final String dictionaryContents;

    // Constructor
    public DictionaryWordOfSpecificFileType(@Nullable ActionContainer actionContainer) {
        super(actionContainer);

        String contents = ShifterPreferences.getDictionary();
        if (contents.isEmpty()) {
            contents = new PluginConfiguration().getDefaultDictionary();
        }

        dictionaryContents = contents;
    }

    // Get instance or null if not applicable:
    // Check whether the given term exists in any section of shift-lists of the dictionary
    // + Stores matching line containing the term for use in shifting later
    // Note: this is a global dictionary check, and NOT file extension specific
    public DictionaryWordOfSpecificFileType getInstance() {
        if (null == super.getInstance()) return null;

        return isInFileTypeDictionary();
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.DICTIONARY_WORD_EXT_SPECIFIC;
    }

    // Check whether the given term exists in any section of shift-lists of the dictionary,
    // looking only at lists in blocks having assigned the given extension
    // + Stores first matching line containing the term for use in shifting later
    private DictionaryWordOfSpecificFileType isInFileTypeDictionary() {
        if (null == actionContainer.fileExtension ||
            !dictionaryContents.contains("|" + actionContainer.fileExtension + "|")
        ) return null;

        String term = actionContainer.selectedText;

        // Reduce to first term-list of terms-block(s) of the given file extension, containing the given term
        Object[] blocksOfExtension = getAllFileExtensionsBlockStarts(actionContainer.fileExtension);

        // Go over all blocks of lists of shift-terms, fetch first one containing the term
        for (Object aBlocksOfExtension : blocksOfExtension) {
            String curExtensionsList = aBlocksOfExtension.toString();
            String curShiftTermsBlock = StringUtils.substringBetween(dictionaryContents, curExtensionsList, "}");

            // Term is contained? store list of shifting neighbours
            if (UtilsTextual.containsCaseInSensitive(curShiftTermsBlock, "|" + term + "|")) {
                relevantTermsList = extractFirstMatchingTermsLine(curShiftTermsBlock, term);
                return this;
            }
        }

        return null;
    }
}