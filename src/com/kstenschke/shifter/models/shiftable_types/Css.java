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

import com.kstenschke.shifter.models.comparators.CssAttributesStyleLineComparator;
import com.kstenschke.shifter.utils.UtilsTextual;

import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.trim;

/**
 * Cascading Stylesheet - sort all attributes in all selectors alphabetically
 */
public class Css {

    public static final String ACTION_TEXT = "Shift CSS";

    public static String getShifted(String value) {
        return value.contains("{") && value.contains("}")
            ? sortAttributeStyleLinesInsideSelectors(value)
            : sortAttributeStyleLines(value);
    }

    private static String sortAttributeStyleLinesInsideSelectors(String value) {
        // Split CSS into groups of attribute-style lines per selector
        String[] attributeGroups = value.split("([^\r\n,{}]+)(,(?=[^}]*\\{)|\\s*\\{)");
        String[] attributeGroupsSorted = new String[attributeGroups.length];

        // 1. Collect groups of attribute-style lines per selector
        int indexMatch = 0;
        for (String attributeGroup : attributeGroups) {
            if (indexMatch > 0) {
                List<String> lines = splitAttributesIntoLines(attributeGroup);
                prepareAttributeStyleLinesForConcat(lines);
                sortAttributeStyles(lines);
                attributeGroupsSorted[indexMatch] = UtilsTextual.rtrim(UtilsTextual.joinLines(lines).toString());

                value = value.replaceFirst(Pattern.quote(attributeGroup), "###SHIFTERMARKER" + indexMatch + "###");
            }
            indexMatch++;
        }
        // 2. Replace attribute-rule groups by their sorted variant
        for (int indexMarker = 1; indexMarker < indexMatch; indexMarker++) {
            value = value.replaceFirst(
                    "###SHIFTERMARKER" + indexMarker + "###",
                    attributeGroupsSorted[indexMarker]);
        }

        return value;
    }

    /**
     * Sort given lines (each being an attribute-style definition tupel, like: "<attribute>:<style>")
     *
     * @param  value
     * @return String
     */
    private static String sortAttributeStyleLines(String value) {
        List<String> lines = splitAttributesIntoLines(value);
        if (doAllButLastLineEndWithSemicolon(lines)) {
            lines.set(lines.size() - 1, UtilsTextual.rtrim(lines.get(lines.size() - 1)) + ";");
        }

        sortAttributeStyles(lines);

        return UtilsTextual.rtrim(UtilsTextual.joinLines(lines).toString());
    }

    private static boolean doAllButLastLineEndWithSemicolon(List<String> lines) {
        int amountLines = lines.size();
        int index = 0;
        for (String line : lines) {
            if (index >= amountLines - 1) {
                return !trim(line).endsWith(";");
            }
            if (!trim(line).endsWith(";")) {
                return false;
            }
            index++;
        }

        return true;
    }

    private static List<String> splitAttributesIntoLines(String str) {
        return Arrays.asList(str.split("\\n"));
    }

    /**
     * Sort CSS lines containing "<attribute>:<style>"
     *
     * @param  list Passed by reference
     */
    private static void sortAttributeStyles(List<String> list) {
        list.sort(new CssAttributesStyleLineComparator());
    }

    /**
     * Ensure that all attribute lines end w/ ";\n"
     *
     * @param  lines    Lists are passed by reference
     */
    private static void prepareAttributeStyleLinesForConcat(List<String> lines) {
        int index = 0;
        for (String line : lines) {
            String trimmed = trim(line);
            if (!trimmed.isEmpty() && !"}".equals(trimmed)) {
                line = UtilsTextual.rtrim(line);

                if (!line.endsWith(";")) {
                    line = line + ";";
                }
                line = line + "\n";
            }

            lines.set(index, line);
            index++;
        }
    }
}
