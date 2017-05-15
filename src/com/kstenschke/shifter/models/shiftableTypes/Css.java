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

        String attributeGroups[]       = value.split("([^\r\n,{}]+)(,(?=[^}]*\\{)|\\s*\\{)");
        String attributeGroupsSorted[] = new String[attributeGroups.length];

        int indexMatch = 0;
        for (String attributeGroup : attributeGroups) {
            if (indexMatch > 0) {
                List<String> lines = splitAttributesIntoLines(attributeGroup);
                lines              = prepareAttributeLineForConcat(lines);

                List<String> linesSorted = sortAttributes(lines);
                attributeGroupsSorted[indexMatch] = UtilsTextual.rtrim(UtilsTextual.joinLines(linesSorted).toString());

                try {
                    value = value.replaceFirst(attributeGroup, "###SHIFTERMARKER" + indexMatch + "###");
                } catch (java.util.regex.PatternSyntaxException e) {
                    // @todo handle exception, occurs e.g. when line contains escaped char, e.g: "content: "\E028";"
                }
            }
            indexMatch++;
        }
        for (int indexMarker = 1; indexMarker < indexMatch; indexMarker++) {
            value = value.replaceFirst(
                    "###SHIFTERMARKER" + indexMarker + "###",
                    (indexMarker == 1 ? "\n" : "") + attributeGroupsSorted[indexMarker]);
        }

        return value;
    }

    private static List<String> splitAttributesIntoLines(String str) {
        return Arrays.asList(str.split("\\n"));
    }

    /**
     * Ensure that all attribute lines end w/ ";\n"
     *
     * @param  lines
     * @return List<String>
     */
    private static List<String> prepareAttributeLineForConcat(List<String> lines) {
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

    /**
     * Sort CSS attribute-lines alphabetical, w/ shorter one of otherwise identically beginning attributes to front
     *
     * @param  list
     * @return List<String>
     */
    private static List<String> sortAttributes(List<String> list) {
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

                // Ensure closing of selector is last item
                if (trim(str2).equals("}")) {
                    return -1;
                } else if (trim(str1).equals("}")) {
                    return 1;
                }

                String attribute1 = trim(str1.split(":")[0]);
                String attribute2 = trim(str2.split(":")[0]);

                // Move vendor-attributes (prefixed w/ "-") behind
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

                // Regular compare
                return str1.compareTo(str2);
            }
        });

        return list;
    }
}