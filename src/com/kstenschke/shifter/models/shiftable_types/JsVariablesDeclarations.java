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
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * JavaScript Variables (multi-lined declarations of multiple vars)
 */
public class JsVariablesDeclarations extends ShiftableTypeAbstract {

    private ActionContainer actionContainer;

    public static final String ACTION_TEXT = "Shift JS var declarations";

    // Declaration/assignment scope: "const", "let", "var"
    private static String scope;

    public JsVariablesDeclarations(@Nullable ActionContainer actionContainer) {
        super(actionContainer);
    }

    /**
     * Check whether given string represents a declaration of (multiple) JS variables:
     * -selection has multiple lines
     * -each trimmed line starts w/ "var" (at least 2 occurrences)
     * -each trimmed line ends w/ ";"
     * -there can be empty lines
     * -there can be commented lines, beginning w/ "//"
     *
     * @return boolean
     */
    public boolean isApplicable() {
        String str = actionContainer.selectedText.trim();

        if (isMultiLinedMultiVarDeclaration(str, "const")  && StringUtils.countMatches(str, "=") > 1) {
            scope = "const";
            return true;
        }
        if (isMultiLinedMultiVarDeclaration(str, "var")) {
            scope = "var";
            return true;
        }
        if (isMultiLinedMultiVarDeclaration(str, "let")) {
            scope = "let";
            return true;
        }
        return false;
    }

    /**
     * @param  str      text selection to be shifted
     * @return String
     */
    public String getShifted(
            String str,
            ActionContainer actionContainer,
            Integer moreCount,
            String leadingWhiteSpace
    ) {
        String[] lines = str.split("\n");
        StringBuilder shiftedLines = new StringBuilder();

        int lineNumber = 0;
        int indexShifted = 0;
        String shiftedLine;
        for (String line : lines) {
            line = line.trim();
            boolean doShift = !(line.isEmpty() || line.startsWith("//"));
            shiftedLine = doShift ? shiftNonCommentLine(line, indexShifted) : line;
            shiftedLines
                    .append(0 == lineNumber ? "" : "    ")
                    .append(shiftedLine)
                    .append("\n");
            lineNumber++;
            if (doShift) {
                indexShifted++;
            }
        }

        return scope + " " + shiftedLines.substring(0, shiftedLines.length() - 2) + ";";
    }

    private static boolean isMultiLinedMultiVarDeclaration(String str, String scope) {
        return !(
                !str.startsWith(scope)
                        || !str.endsWith(";")
                        || !str.contains("\n")
                        || StringUtils.countMatches(str, scope) < 2
                        || StringUtils.countMatches(str, ";") < 2
        );
    }

    @NotNull
    private static String shiftNonCommentLine(String line, int indexShiftedLine) {
        // Remove scope ("const ", "let " or "var ") from beginning
        line = line.substring(scope.length() + 1);
        if ("const".equals(scope) && (indexShiftedLine > 0)) {
            line = "  " + line;
        }

        if (StringUtils.countMatches(line, "//") == 1) {
            // Handle line ending w/ comment intact
            String[] parts = line.split("//");
            parts[0] = parts[0].trim();
            return parts[0].substring(0, parts[0].length() - 1) + ", //" + parts[1];
        }

        // Replace ";" termination by ","
        return line.substring(0, line.length() - 1) + ",";
    }
}