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
import com.kstenschke.shifter.models.entities.AbstractShiftable;
import com.kstenschke.shifter.models.entities.StaticWordType;
import com.kstenschke.shifter.utils.UtilsTextual;
import com.kstenschke.shifter.resources.ui.PluginConfiguration;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryWord extends AbstractShiftable {

    public final String ACTION_TEXT = "Shift Word";

    // Terms-list containing the term to be shifted, set during detection
    private String relevantTermsList;

    // The complete dictionary
    private final String dictionaryContents;

    // Constructor
    public DictionaryWord(@Nullable ActionContainer actionContainer) {
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
    public DictionaryWord getInstance() {
        return null != actionContainer &&
               null != actionContainer.fileExtension &&
               isTermInAnyDictionary()
                ? this : null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.DICTIONARY_WORD;
    }

    /**
     * Shift given word, using the (already fetched) list of relevant terms
     *
     * @param  word     Word to be shifted
     * @return String   The shifted word
     */
    public String getShifted(
            String word,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
        if (null == relevantTermsList) return word;

        String shiftTerms = relevantTermsList.replaceFirst("\\|", "");
        shiftTerms = UtilsTextual.replaceLast(shiftTerms, "|", "");

        String[] termsList = shiftTerms.split("\\|");
        if (termsList.length == 0) return word;

        StaticWordType wordType = new StaticWordType(termsList);
        String shiftedWord = wordType.getShifted(word, actionContainer.isShiftUp);

        return shiftedWord.equals(word)
                // Shifting did not change given word, fallback: try case-insensitive
                ? wordType.getShifted(word.toLowerCase(), actionContainer.isShiftUp)
                : shiftedWord;
    }

    public boolean shiftSelectionInDocument(@Nullable Integer moreCount) {
        return false;
    }

    private boolean isTermInAnyDictionary() {
        String term = actionContainer.selectedText;

        // @todo why check for extension in search over all type-dictionaries?
        if (dictionaryContents.contains("|" + actionContainer.fileExtension.toLowerCase() + "|")) {
            // Merge all terms-blocks
            String dictionaryTerms = dictionaryContents;
            Object[] dictionaryExtensionsBlocks = getAllFileExtensionsBlockStarts();

            for (Object dictionaryExtensionsBlock : dictionaryExtensionsBlocks) {
                String currentExtensionsList = dictionaryExtensionsBlock.toString();
                dictionaryTerms = dictionaryTerms.replace(currentExtensionsList, "");
            }

            // Term is contained? store list of shifting neighbours
            if (dictionaryTerms.contains("|" + term + "|")) {
                relevantTermsList = extractFirstMatchingTermsLine(dictionaryTerms, term);
                return true;
            }
            // Not found case-sensitive, try insensitive
            String dictionaryTermsLower = dictionaryTerms.toLowerCase();
            String termLower = term.toLowerCase();
            if (dictionaryTermsLower.contains("|" + termLower + "|")) {
                relevantTermsList = extractFirstMatchingTermsLine(dictionaryTermsLower, termLower);
                return true;
            }
        }

        return false;
    }

    /**
     * @param  termsLines   Terms lines from dictionary
     * @param  term         Word to be shifted
     * @return String       First matching term lLine
     */
    static String extractFirstMatchingTermsLine(String termsLines, String term) {
        String sword = "|" + term + "|";
        String[] allLines = termsLines.split("\n");
        int amountLines = allLines.length;

        String curLine;
        int i = 0;
        while (i < amountLines) {
            curLine = allLines[i];
            curLine = curLine.replaceAll("\\{*", "").replaceAll("}*", "").trim();
            if (!curLine.isEmpty() && curLine.contains(sword)) return curLine;

            i++;
        }

        return null;
    }

    /**
     * Get all starting lines of term-blocks (extensions) from dictionary
     *
     * @return Object[]    e.g. [0 => "('js') {", 1 => "('html') {", 2 => ...]
     */
    protected Object[] getAllFileExtensionsBlockStarts() {
        List<String> allMatches = new ArrayList<>();

        String pattern = "\\(\\|([a-z|*]+\\|)*\\)(\\s)*\\{";
        Matcher m = Pattern.compile(pattern).matcher(dictionaryContents);
        while (m.find()) {
            allMatches.add(m.group());
        }

        return allMatches.toArray();
    }

    /**
     * Get all starting lines of term-blocks (extensions) from dictionary,
     * limited to those containing the given file extension
     *
     * @return Object[]
     */
    protected Object[] getAllFileExtensionsBlockStarts(String fileExtension) {
        List<String> allMatches = new ArrayList<>();
        Object[] dictionaryExtensionsBlocks = getAllFileExtensionsBlockStarts();

        for (Object dictionaryExtensionsBlock : dictionaryExtensionsBlocks) {
            String curBlock = dictionaryExtensionsBlock.toString();
            if (curBlock.contains("|" + fileExtension + "|")) {
                allMatches.add(curBlock);
            }
        }

        return allMatches.toArray();
    }
}