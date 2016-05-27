/*
 * Copyright 2011-2016 Kay Stenschke
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
package com.kstenschke.shifter.models.shiftertypes;

import com.kstenschke.shifter.models.ShifterPreferences;
import com.kstenschke.shifter.resources.forms.ShifterConfiguration;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryExpression {

    // Set during extension specific detection of dictionary term
    private String fileExtension;

    private String expression;
    private String replacement;

    // The complete dictionary
    private final String dictionaryContents;

    /**
     * Constructor
     */
    public DictionaryExpression() {
        String contents = ShifterPreferences.getExpressionsDictionary();
        if (contents.isEmpty()) {
            contents = new ShifterConfiguration().getDefaultExpressions();
        }

        this.dictionaryContents = contents;
    }

    /**
     * Check whether the given selection matches an expression in the dictionary
     * + Stores matching line containing the term for use in shifting later
     *
     * @param  selection        String to be matched w/ the shifter dictionary expressions
     * @return boolean
     */
    public boolean findMatchingExpression(String selection) {
        Object[] definitionBlocks = getRelevantFileExtensionsBlockStarts(this.fileExtension);

        for(Object currentBlock : definitionBlocks) {
            String blockStr = (String)currentBlock;

            String tmp = this.dictionaryContents.split(blockStr)[1];
            tmp = tmp.split("\n\\(\\|")[0];

            int x = 0;
        }

        return false;
    }

    /**
     * Get all starting lines of term-blocks (extensions) from dictionary
     *
     * @return Object[]    e.g. [0 => "('js') {", 1 => "('html') {", 2 => ...]
     */
    private Object[] getAllFileExtensionsBlockStarts() {
        List<String> allMatches = new ArrayList<String>();

        String pattern = "\\(\\|([a-z|\\*]+\\|)*\\)(\\s)*\\{";
        Matcher m = Pattern.compile(pattern).matcher(this.dictionaryContents);
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
    private Object[] getRelevantFileExtensionsBlockStarts(String fileExtension) {
        List<String> allMatches = new ArrayList<String>();
        Object[] dictionaryExtensionsBlocks = this.getAllFileExtensionsBlockStarts();

        for (Object dictionaryExtensionsBlock : dictionaryExtensionsBlocks) {
            String curBlock = dictionaryExtensionsBlock.toString();
            if (curBlock.contains("|" + fileExtension + "|")
                || curBlock.contains("|*|")
            ) {
                allMatches.add(curBlock);
            }
        }

        return allMatches.toArray();
    }

    /**
     * Shift given word, using the (already fetched) list of relevant terms
     *
     * @param  word     Word to be shifted
     * @param  isUp     Shifting up? (otherwise down)
     * @return The shifted word
     */
    public String getShifted(String word, boolean isUp) {


        return word;
    }

}
