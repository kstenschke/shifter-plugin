/*
 * Copyright Kay Stenschke
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

import com.kstenschke.shifter.ShifterPreferences;
import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.utils.UtilsPhp;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.NotNull;

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

    public void init(String str) {
        isPhpVariableOrArray(str);
    }

    /**
     * Check whether given string represents a PHP variable
     *
     * @param  str     String to be checked
     * @return boolean
     */
    public boolean isPhpVariableOrArray(String str) {
        boolean isVariable = false;
        if (str.startsWith("$")) {
            String identifier = str.substring(1);
            // Must contain a-z,A-Z or 0-9, _
            isVariable = identifier.toLowerCase().matches("[a-zA-Z0-9_]+");
        }

        if (!isVariable) {
            // Detect array definition
            isShiftableArray = isShiftablePhpArray(str);
        }

        return isVariable || isShiftableArray;
    }

    private boolean isShiftablePhpArray(String str) {
        boolean isActiveConvertLongToShort = ShifterPreferences.getIsActiveConvertPhpArrayLongToShort();
        boolean isActiveConvertShortToLong = ShifterPreferences.getIsActiveConvertPhpArrayShortToLong();

        if (!isActiveConvertLongToShort && !isActiveConvertShortToLong) {
            return false;
        }

        isConventionalArray = str.matches("(array\\s*\\()((.|\\n|\\r|\\s)*)(\\)(;)*)");
        boolean isShorthandArray = !isConventionalArray && str.matches("(\\[)((.|\\n|\\r|\\s)*)(])(;)*");

        return (isActiveConvertLongToShort && isConventionalArray) || (isActiveConvertShortToLong && isShorthandArray);
    }

    public static boolean isStaticShiftablePhpArray(String str) {
        boolean isActiveConvertLongToShort = ShifterPreferences.getIsActiveConvertPhpArrayLongToShort();
        boolean isActiveConvertShortToLong = ShifterPreferences.getIsActiveConvertPhpArrayShortToLong();

        if (!isActiveConvertLongToShort && !isActiveConvertShortToLong) {
            return false;
        }

        boolean isConventionalArray = str.matches("(array\\s*\\()((.|\\n|\\r|\\s)*)(\\)(;)*)");
        boolean isShorthandArray = !isConventionalArray && str.matches("(\\[)((.|\\n|\\r|\\s)*)(])(;)*");

        return (isActiveConvertLongToShort && isConventionalArray) || (isActiveConvertShortToLong && isShorthandArray);
    }

    /**
     * Shift PX value up/down by 16px
     *
     * @param  variable     Variable name string
     * @param  actionContainer
     * @param  moreCount    Current "more" count, starting w/ 1. If non-more shift: null
     * @return String
     */
    public String getShifted(String variable, ActionContainer actionContainer, Integer moreCount) {
        if (isShiftableArray) {
            return getShiftedArray(variable);
        }

        // Extract array of all PHP var names
        String text = actionContainer.editorText.toString();
        List<String> phpVariables = UtilsPhp.extractPhpVariables(text);

        // Sort var names alphabetically
        Collections.sort(phpVariables);
        List<String> allLeadChars = null;
        if (null != moreCount && 1 == moreCount) {
            // During "shift more": iterate over variables reduced to first per every lead-character
            phpVariables = reducePhpVarsToFirstPerLeadChar(phpVariables);
            allLeadChars = getLeadChars(phpVariables);
        }

        int amountVars = phpVariables.size();

        // Find position of given variable
        int curIndex = getVariableIndex(variable, moreCount, phpVariables, allLeadChars);
        if (-1 == curIndex || 0 == amountVars) {
            return variable;
        }

        // Find next/previous variable name (only once during iterations of "shift more")
        if (null == moreCount || 1 == moreCount) {
            curIndex = NumericValue.moduloShiftInteger(curIndex, amountVars, actionContainer.isShiftUp);
        }

        return phpVariables.get(curIndex);
    }

    @NotNull
    private Integer getVariableIndex(String variable, Integer moreCount, List<String> phpVariables, List<String> allLeadChars) {
        if (null == moreCount || moreCount > 1) {
            return phpVariables.indexOf(variable);
        }

        return null == allLeadChars
            ? -1
            : allLeadChars.indexOf(variable.substring(1, 2));
    }

    private List<String> reducePhpVarsToFirstPerLeadChar(List<String> allMatches) {
        List<String> reducedMatches = new ArrayList<>();
        String leadCharPrev = "";
        String leadCharCur;
        for (String currentMatch : allMatches) {
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
        List<String> leadChars = new ArrayList<>();

        for (String currentMatch : matches) {
            leadChars.add(currentMatch.substring(1,2));
        }

        return leadChars;
    }

    /**
     * @param  variable
     * @return String   converted array(...) <=> [...]
     */
    public String getShiftedArray(String variable) {
        return isConventionalArray
            ? UtilsTextual.replaceLast(variable.replaceFirst("array", "[").replaceFirst("\\(", ""), ")", "]")
            : UtilsTextual.replaceLast(variable.replaceFirst("\\[", "array("), "]", ")");
    }
}
