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

import com.kstenschke.shifter.utils.UtilsTextual;

import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.trim;

/**
 * Cascading Stylesheet - sort all attributes in all selectors alphabetically
 */
public class Css {

    /**
     * @param  value
     * @return String
     */
    public static String getShifted(String value) {
        if (!value.contains("{") && !value.contains("}")) {
            return null;
        }

        // Split CSS into groups of attribute-style lines per selector
        String attributeGroups[]       = value.split("([^\r\n,{}]+)(,(?=[^}]*\\{)|\\s*\\{)");
        String attributeGroupsSorted[] = new String[attributeGroups.length];

        // 1. Collect groups of attribute-style lines per selector
        int indexMatch = 0;
        for (String attributeGroup : attributeGroups) {
            if (indexMatch > 0) {
                List<String> lines = splitAttributesIntoLines(attributeGroup);
                lines              = prepareAttributeStyleLinesForConcat(lines);

                List<String> linesSorted = sortAttributeStyles(lines);
                attributeGroupsSorted[indexMatch] = UtilsTextual.rtrim(UtilsTextual.joinLines(linesSorted).toString());

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

    private static List<String> splitAttributesIntoLines(String str) {
        return Arrays.asList(str.split("\\n"));
    }

    /**
     * Sort CSS lines containing "<attribute>:<style>"
     *
     * @param  list
     * @return List<String>
     */
    private static List<String> sortAttributeStyles(List<String> list) {
        Collections.sort(list, new Comparator<String>() {
            /**
             * @param  str1
             * @param  str2
             * @return -1 if o1 is greater, 0 is o1 and o2 are equal, 1 if o2 is greater
             */
            @Override
            public int compare(String str1, String str2) {
                if (str1.equals(str2)) {
                    return 0;
                }

                String str1Trimmed = trim(str1);
                String str2Trimmed = trim(str2);

                // Move empty line to the very end
                if (str2Trimmed.isEmpty()) {
                    return -1;
                } else if (str1Trimmed.isEmpty()) {
                    return 1;
                }

                // Ensure closing of selector is at the end
                if (str2Trimmed.equals("}")) {
                    return -1;
                } else if (str1Trimmed.equals("}")) {
                    return 1;
                }

                String parts1[] = str1.split(":");
                String attribute1 = trim(parts1[0]);
                String style1     = trim(parts1[1]);

                String parts2[] = str2.split(":");
                String attribute2 = trim(parts2[0]);
                String style2     = trim(parts2[1]);


                // Move vendor-attributes (prefixed w/ "-", ex: "-moz-transition: opacity .3s;") behind
                boolean attribute1IsVendor = attribute1.startsWith("-");
                boolean attribute2IsVendor = attribute2.startsWith("-");
                if (attribute1IsVendor && !attribute2IsVendor) {
                    return 1;
                } else if (attribute2IsVendor && !attribute1IsVendor) {
                    return -1;
                }

                // Move shorter of otherwise identically beginning attributes ahead
                int attribute1Length = attribute1.length();
                int attribute2Length = attribute2.length();
                if (attribute1Length > attribute2Length) {
                    if (attribute1.startsWith(attribute2)) {
                        return 1;
                    }
                } else if (attribute1Length < attribute2Length && attribute2.startsWith(attribute1)) {
                    return -1;
                }

                // Move vendor-styles (prefixed w/ "-", ex: "width: -moz-calc(19.75rem - 1px);") behind
                if (attribute1.equals(attribute2)) {
                    boolean style1IsVendor = style1.matches("^(\\-[a-z])$");
                    boolean style2IsVendor = style2.matches("^(\\-[a-z])$");
                    if (style1IsVendor && !style2IsVendor) {
                        return 1;
                    } else if (style2IsVendor && !style1IsVendor) {
                        return -1;
                    }
                }

                // Regular compare
                return str1.compareTo(str2);
            }
        });

        return list;
    }

    /**
     * Ensure that all attribute lines end w/ ";\n"
     *
     * @param  lines
     * @return List<String>
     */
    private static List<String> prepareAttributeStyleLinesForConcat(List<String> lines) {
        int index = 0;
        for (String line : lines) {
            String trimmed = trim(line);
            if (!trimmed.isEmpty() && !trimmed.equals("}")) {
                line = UtilsTextual.rtrim(line);

                if (!line.endsWith(";")) {
                    line = line + ";";
                }
                line = line + "\n";
            }

            lines.set(index, line);
            index++;
        }

        return lines;
    }
}