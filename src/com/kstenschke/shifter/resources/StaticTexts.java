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
package com.kstenschke.shifter.resources;

import org.jetbrains.annotations.NonNls;

public class StaticTexts {

    @NonNls
    public static final String SETTINGS_COMPONENT_NAME = "Shifter Settings";

    @NonNls
    public static final String ACTION_LABEL_SHIFT_DOWN      = "Shift-Down";
    @NonNls
    public static final String ACTION_LABEL_SHIFT_UP        = "Shift-Up";
    @NonNls
    public static final String ACTION_LABEL_SHIFT_UP_MORE   = "Shift-Up More";
    @NonNls
    public static final String ACTION_LABEL_SHIFT_DOWN_MORE = "Shift-Down More";
    @NonNls
    public static final String TITLE_NUMERIC_BLOCK_OPTIONS  = "Shift Numeric Block Selection";

    @NonNls
    public static final String TITLE_REDUCE_DUPLICATE_LINES    = "Reduce duplicate lines?";
    @NonNls
    public static final String MESSAGE_REDUCE_DUPLICATE_LINES  = "Duplicated lines detected. Reduce to single occurrences?";
    @NonNls
    public static final String TITLE_REDUCE_DUPLICATED_ITEMS   = "Reduce duplicate items?";
    @NonNls
    public static final String MESSAGE_REDUCE_DUPLICATED_ITEMS = "Duplicated items detected. Reduce to single occurrences?";

    @NonNls
    public static final String POPUP_TITLE_SHIFT = "Shifting Mode";
    @NonNls

    public static final String SHIFT_OPTION_MULTILINE_BLOCK_COMMENT_TO_ONE_SINGLE_COMMENT       = "Merge lines into one single-line comment";
    @NonNls
    public static final String SHIFT_OPTION_MULTILINE_BLOCK_COMMENT_TO_MULTIPLE_SINGLE_COMMENTS = "Convert into multiple single-line comments";

    @NonNls
    public static final String SHIFT_OPTION_MULTIPLE_LINE_COMMENTS_TO_BLOCK_COMMENT = "Convert to block comment";
    @NonNls
    public static final String SHIFT_OPTION_MULTIPLE_LINE_COMMENTS_MERGE = "Merge into one comment";
    @NonNls
    public static final String SHIFT_OPTION_MULTIPLE_LINE_SORT_DESCENDING = "Sort comments alphabetically descending";
    @NonNls
    public static final String SHIFT_OPTION_MULTIPLE_LINE_SORT_ASCENDING = "Sort comments alphabetically ascending";

    @NonNls
    public static final String SHIFT_OPTION_LINES_SORT = "Sort lines alphabetical";
    @NonNls
    public static final String SHIFT_OPTION_LIST_ITEMS_SORT = "Sort items alphabetical";
    @NonNls
    public static final String SHIFT_OPTION_LIST_ITEMS_SWAP = "Swap list items";
    @NonNls
    public static final String SHIFT_OPTION_QUOTES_SWAP = "Swap single and double quotes";
    @NonNls
    public static final String SHIFT_OPTION_QUOTES_DOUBLE_TO_SINGLE = "Convert double to single quoutes";
    @NonNls
    public static final String SHIFT_OPTION_QUOTES_SINGLE_TO_DOUBLE = "Convert single to double quoutes";
    @NonNls
    public static final String SHIFT_OPTION_UNESCAPE_QUOTES = "Unescape escaped quoutes";
    @NonNls
    public static final String SHIFT_OPTION_CAMEL_WORDS_SWAP_ORDER = "Swap words order";
    @NonNls
    public static final String SHIFT_OPTION_CONCATENATION_ITEMS_SWAP_ORDER = "Swap items order";
    @NonNls
    public static final String SHIFT_OPTION_PATH_PAIR_SWAP_ORDER = "Swap path items order";
    @NonNls
    public static final String SHIFT_OPTION_CAMEL_CASE_TO_PATH = "Convert to minus-separated path";
    @NonNls
    public static final String SHIFT_OPTION_PATH_TO_CAMEL_CASE = "Convert to camel-cased string";
    @NonNls
    public static final String SHIFT_OPTION_SWAP_PARENTHESIS = "Toggle (...) and [...]";
    @NonNls
    public static final String SHIFT_OPTION_SLASHES_SWAP = "Toggle \\ and /";
    @NonNls
    public static final String SHIFT_OPTION_CONVERT_PHP_ARRAY_TO_LONG_SYNTAX = "Convert to PHP array long syntax";
}