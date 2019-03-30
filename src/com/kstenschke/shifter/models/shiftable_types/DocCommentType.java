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
package com.kstenschke.shifter.models.shiftable_types;

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.ShiftableTypeAbstract;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOC comment type class
 */
public class DocCommentType { /*extends ShiftableTypeAbstract {*/

    /**
     * Check whether given String represents a data type (number / integer / string /...) from a doc comment (param / return /...)
     *
     * @param  prefixChar Prefix character
     * @param  line       Whole line containing the word
     * @return boolean
     */
    public boolean isApplicable(String prefixChar, String line) {
        return !("#".equals(prefixChar) ||
                "@".equals(prefixChar)) &&
                isDocCommentTypeLineContext(line);
    }

    /**
     * Check whether given String looks like a DOC comment line
     *
     * @param line Line the caret is at
     * @return boolean.
     */
    public boolean isDocCommentTypeLineContext(String line) {
        String allTags = new DocCommentTag().getAllTagsPiped();
        String regExPatternLine = "\\s*\\*\\s+@(" + allTags + ")\\s*";

        Matcher m = Pattern.compile(regExPatternLine).matcher(line.toLowerCase());

        return m.find();
    }

    /**
     * @param  word     String to be shifted
     * @param  actionContainer
     * @return Shifting result
     */
    public String getShifted(String word, ActionContainer actionContainer) {
        return new DocCommentDataType().getShifted(word, actionContainer.filename, actionContainer.isShiftUp);
    }
}