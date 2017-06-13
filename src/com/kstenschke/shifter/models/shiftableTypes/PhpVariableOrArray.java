/*
 * Copyright 2011-2017 Kay Stenschke
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
package com.kstenschke.shifter.models.shiftableTypes;

import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.utils.UtilsPhp;
import com.kstenschke.shifter.utils.UtilsTextual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PHP Variable (word w/ $ prefix), includes array definition (toggle long versus shorthand syntax)
 */
public class PhpVariableOrArray {

    // Detected array definition? Shifts among long and shorthand than: array(...) <=> [...]
    private boolean isShiftableArray = false;

    // Shorthand (since PHP5.4) or long syntax array?
    private boolean isConventionalArray = false;

    /**
     * Check whether given string represents a PHP variable
     *
     * @param  str     String to be checked
     * @return boolean
     */
    public Boolean isPhpVariableOrArray(String str) {
        boolean isVariable = false;
        if (str.startsWith("$")) {
            String identifier = str.substring(1);
            // Must contain a-z,A-Z or 0-9, _
            isVariable = identifier.toLowerCase().matches("[a-zA-Z0-9_]+");
        }

        if (!isVariable) {
            // Detect array definition
            this.isShiftableArray = this.isShiftablePhpArray(str);
        }

        return isVariable || this.isShiftableArray;
    }

    /**
     * @param  str
     * @return Boolean
     */
    private Boolean isShiftablePhpArray(String str) {
        boolean isActiveConvertLongToShort = ShifterPreferences.getIsActiveConvertPhpArrayLongToShort();
        boolean isActiveConvertShortToLong = ShifterPreferences.getIsActiveConvertPhpArrayShortToLong();

        if (!isActiveConvertLongToShort && !isActiveConvertShortToLong) {
            return false;
        }

        this.isConventionalArray = str.matches("(array\\s*\\()((.|\\n|\\r|\\s)*)(\\)(;)*)");
        boolean isShorthandArray = !this.isConventionalArray && str.matches("(\\[)((.|\\n|\\r|\\s)*)(])(;)*");

        return (isActiveConvertLongToShort && this.isConventionalArray) || (isActiveConvertShortToLong && isShorthandArray);
    }

    /**
     * Shift PX value up/down by 16px
     *
     * @param  variable     Variable name string
     * @param  editorText   Text of edited document
     * @param  isUp         Shift up or down?
     * @param  moreCount    Current "more" count, starting w/ 1. If non-more shift: null
     * @return String
     */
    public String getShifted(String variable, CharSequence editorText, Boolean isUp, Integer moreCount) {
        if (this.isShiftableArray) {
            return getShiftedArray(variable);
        }

        // Extract array of all PHP var names
        String text = editorText.toString();
        List<String> phpVariables = UtilsPhp.extractPhpVariables(text);

        // Sort var names alphabetically
        Collections.sort(phpVariables);
        List<String> allLeadChars = null;
        if (moreCount != null && moreCount == 1) {
            // During "shift more": iterate over variables reduced to first per every lead-character
            phpVariables = this.reducePhpVarsToFirstPerLeadChar(phpVariables);
            allLeadChars = this.getLeadChars(phpVariables);
        }

        int amountVars = phpVariables.size();

        // Find position of given variable
        Integer curIndex = (moreCount== null || moreCount > 1)
            ? phpVariables.indexOf(variable)
            : allLeadChars.indexOf(variable.substring(1, 2));

        if (curIndex == -1 || amountVars == 0) {
            return variable;
        }

        // Find next/previous variable name (only once during iterations of "shift more")
        if (moreCount == null || moreCount == 1) {
            curIndex = NumericValue.moduloShiftInteger(curIndex, amountVars, isUp);
        }

        return phpVariables.get(curIndex);
    }

    /**
     * @param  allMatches
     * @return List<String>
     */
    private List<String> reducePhpVarsToFirstPerLeadChar(List<String> allMatches) {
        List<String> reducedMatches = new ArrayList<String>();
        String leadCharPrev = "";
        String leadCharCur;
        for(String currentMatch : allMatches) {
            leadCharCur = currentMatch.substring(1,2);
            if (!leadCharCur.matches(leadCharPrev)) {
                reducedMatches.add(currentMatch);
            }
            leadCharPrev = leadCharCur;
        }

        return reducedMatches;
    }

    /**
     * @param  matches
     * @return List of first letters of given matches
     */
    private List<String> getLeadChars(List<String> matches) {
        List<String> leadChars = new ArrayList<String>();

        for(String currentMatch : matches) {
            leadChars.add(currentMatch.substring(1,2));
        }

        return leadChars;
    }

    /**
     * @param  variable
     * @return String   converted array(...) <=> [...]
     */
    private String getShiftedArray(String variable) {
        return this.isConventionalArray
            ? UtilsTextual.replaceLast(variable.replaceFirst("array", "[").replaceFirst("\\(", ""), ")", "]")
            : UtilsTextual.replaceLast(variable.replaceFirst("\\[", "array("), "]", ")");
    }
}