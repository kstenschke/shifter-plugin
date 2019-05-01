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
import com.kstenschke.shifter.utils.UtilsPhp;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// PHP Variable (word w/ $ prefix), includes array definition (toggle long versus shorthand syntax)
public class PhpVariableOrArray extends AbstractShiftable {

    public final String ACTION_TEXT = "Shift PHP";

    // Detected array definition? Shifts among long and shorthand than: array(...) <=> [...]
    private boolean isShiftableArray = false;

    // Shorthand (since PHP5.4) or long syntax array?
    private boolean isConventionalArray = false;

    // Constructor
    public PhpVariableOrArray(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    // Get instance or null if not applicable
    public PhpVariableOrArray getInstance() {
        String word = actionContainer.getStringToBeShifted();
        if (null == word || word.length() < 2) return null;

        boolean isVariable = false;
        if (word.startsWith("$")) {
            String identifier = word.substring(1);
            // Must contain a-z,A-Z or 0-9, _
            isVariable = identifier.toLowerCase().matches("[a-zA-Z0-9_]+");
        }
        if (!isVariable) {
            // Detect array definition
            isShiftableArray = isShiftablePhpArray(word);
        }

        return isVariable || isShiftableArray ? this : null;
    }

    public ShiftableTypes.Type getType() {
        return ShiftableTypes.Type.PHP_VARIABLE_OR_ARRAY;
    }

    /**
     * Shift PX value up/down by 16px
     *
     * @param  variable     Variable name string
     * @param  moreCount    Current "more" count, starting w/ 1. If non-more shift: null
     * @return String
     */
    public String getShifted(
            String variable,
            Integer moreCount,
            String leadWhitespace,
            boolean updateInDocument,
            boolean disableIntentionPopup
    ) {
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
        if (-1 == curIndex || 0 == amountVars) return variable;

        // Find next/previous variable name (only once during iterations of "shift more")
        if (null == moreCount || 1 == moreCount) {
            curIndex = NumericValue.moduloShiftInteger(curIndex, amountVars, actionContainer.isShiftUp);
        }

        return phpVariables.get(curIndex);
    }

    public boolean shiftSelectionInDocument() {
        return false;
    }

    public static boolean isStaticShiftablePhpArray(String str) {
        boolean isActiveConvertLongToShort = ShifterPreferences.getIsActiveConvertPhpArrayLongToShort();
        boolean isActiveConvertShortToLong = ShifterPreferences.getIsActiveConvertPhpArrayShortToLong();

        if (!isActiveConvertLongToShort && !isActiveConvertShortToLong) return false;

        boolean isConventionalArray = str.matches("(array\\s*\\()((.|\\n|\\r|\\s)*)(\\)(;)*)");
        boolean isShorthandArray = !isConventionalArray && str.matches("(\\[)((.|\\n|\\r|\\s)*)(])(;)*");

        return (isActiveConvertLongToShort && isConventionalArray) || (isActiveConvertShortToLong && isShorthandArray);
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

    private boolean isShiftablePhpArray(String str) {
        boolean isActiveConvertLongToShort = ShifterPreferences.getIsActiveConvertPhpArrayLongToShort();
        boolean isActiveConvertShortToLong = ShifterPreferences.getIsActiveConvertPhpArrayShortToLong();

        if (!isActiveConvertLongToShort && !isActiveConvertShortToLong) return false;

        isConventionalArray = str.matches("(array\\s*\\()((.|\\n|\\r|\\s)*)(\\)(;)*)");
        boolean isShorthandArray = !isConventionalArray && str.matches("(\\[)((.|\\n|\\r|\\s)*)(])(;)*");

        return (isActiveConvertLongToShort && isConventionalArray) || (isActiveConvertShortToLong && isShorthandArray);
    }

    @NotNull
    private Integer getVariableIndex(String variable, Integer moreCount, List<String> phpVariables, List<String> allLeadChars) {
        if (null == moreCount || moreCount > 1) return phpVariables.indexOf(variable);

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
}