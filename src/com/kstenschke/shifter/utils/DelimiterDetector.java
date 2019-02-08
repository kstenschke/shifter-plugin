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
package com.kstenschke.shifter.utils;

import java.util.List;

/**
 * Utility Class: Detect common delimiter (before newline) of list of lines
 */
class DelimiterDetector {

    private boolean findingDelimiterFailed;
    private char commonDelimiter;
    private boolean isDelimitedLastLine;

    /**
     * Constructor
     *
     * @param lines
     */
    DelimiterDetector(List<String> lines) {
        commonDelimiter = ' ';

        int amountLines = lines.size();
        if (amountLines <= 2) {
            findingDelimiterFailed = true;
            return;
        }

        int lineLength;

        findingDelimiterFailed = false;
        int lineNumber = 0;
        for (String line : lines) {
            line = line.trim();
            lineLength = line.length();

            if (lineLength > 0) {
                char currentDelimiter = line.charAt(lineLength - 1);
                if (0 == lineNumber) {
                    commonDelimiter = currentDelimiter;
                } else {
                    boolean isLastLine = lineNumber == amountLines - 1;
                    if (!isLastLine && currentDelimiter != commonDelimiter) {
                        findingDelimiterFailed = true;
                    }
                }
            }
            lineNumber++;
        }

        String lastLine = lines.get(amountLines - 1).trim();
        isDelimitedLastLine = lastLine.endsWith(String.valueOf(commonDelimiter));
    }

    /**
     * @return String|null  Common delimiter if found, or null
     */
    String getCommonDelimiter() {
        return findingDelimiterFailed ? null : String.valueOf(commonDelimiter);
    }

    boolean isFoundDelimiter() {
        return !findingDelimiterFailed;
    }

    boolean isDelimitedLastLine() {
        return !findingDelimiterFailed && isDelimitedLastLine;
    }
}