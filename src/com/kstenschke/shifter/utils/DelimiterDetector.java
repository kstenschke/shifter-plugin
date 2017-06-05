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
package com.kstenschke.shifter.utils;

import java.util.List;

/**
 * Utility Class: Detect common delimiter of list of lines
 */
public class DelimiterDetector {

    private final List<String> lines;
    private boolean findingDelimiterFailed = true;
    private char commonDelimiter = ' ';
    private boolean isDelimitedLastLine;

    /**
     * Constructor
     *
     * @param lines
     */
    public DelimiterDetector(List<String> lines) {
        this.lines = lines;
        this.commonDelimiter = ' ';

        int amountLines = this.lines.size();
        if (amountLines <= 2) {
            this.findingDelimiterFailed = true;
            return;
        }

        int lenLine;

        this.findingDelimiterFailed = false;
        int lineNumber = 0;
        for (String line : this.lines) {
            line = line.trim();
            lenLine = line.length();

            if (lenLine > 0) {
                char currentDelimiter = line.charAt(lenLine - 1);
                if (lineNumber == 0) {
                    this.commonDelimiter = currentDelimiter;
                } else {
                    boolean isLastLine = lineNumber == amountLines - 1;
                    if (!isLastLine && currentDelimiter != this.commonDelimiter) {
                        this.findingDelimiterFailed = true;
                    }
                }
            }
            lineNumber++;
        }

        String lastLine = lines.get(amountLines - 1).trim();
        this.isDelimitedLastLine = lastLine.endsWith(String.valueOf(this.commonDelimiter));
    }

    /**
     * @return String|null  Common delimiter if found, or null
     */
    public String getCommonDelimiter() {
        return this.findingDelimiterFailed ? null : String.valueOf(this.commonDelimiter);
    }

    public boolean isFoundDelimiter() {
        return !this.findingDelimiterFailed;
    }

    public boolean isDelimitedLastLine() {
        return !this.findingDelimiterFailed && this.isDelimitedLastLine;
    }
}