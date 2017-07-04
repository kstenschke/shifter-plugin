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

import org.jetbrains.annotations.NotNull;
import java.util.List;

/**
 * Static helper methods for analysis and manipulation of texts
 */
public class UtilsPhp {

    /**
     * Use regEx matcher to extract all variables in given code
     *
     * @param  str
     * @return List<String>     All PHP var names
     */
    @NotNull
    public static List<String> extractPhpVariables(String str) {
        return UtilsTextual.getPregMatches(str, "\\$[a-zA-Z0-9_]+");
    }

    /**
     * @param  str
     * @return Name of primitive (PHP) data type
     */
    public static String guessPhpDataTypeByName(String str) {
        String camelWords[] = UtilsTextual.splitCamelCaseIntoWords(str, true);
        String lastWord = camelWords[camelWords.length - 1];

        if (   "args".equals(lastWord)
            || "array".equals(lastWord)
            || "data".equals(lastWord)
            || "ids".equals(lastWord)
            || "items".equals(lastWord)
            || "list".equals(lastWord)
            || "pieces".equals(lastWord)
            || "params".equals(lastWord)
            || "parameters".equals(lastWord)
            || "values".equals(lastWord)
        ) {
            return "array";
        }

        if (   "do".equals(camelWords[0])
            || "get".equals(camelWords[0])
            || "has".equals(camelWords[0])
            || "is".equals(camelWords[0])
            || "return".equals(camelWords[0])
            || "should".equals(camelWords[0])
            || "with".equals(camelWords[0]) || "without".equals(camelWords[0])
        ) {
            return "bool";
        }

        str = str.toLowerCase();

        if (str.matches("\\w*name|\\w*title|\\w*url")) {
            return "string";
        }
        if (str.matches("(\\w*delim(iter)*|\\w*dir(ectory)*|\\w*domain|filename\\w*|\\w*key|\\w*link|\\w*name|\\w*path\\w*|\\w*prefix|\\w*suffix|charlist|comment|\\w*file(name)*|format|glue|haystack|html|intput|locale|message|name|needle|output|replace(ment)*|salt|separator|str(ing)*|url)\\d*")) {
            return "string";
        }
        if (str.matches("(\\w*day|\\w*end|\\w*expire|\\w*handle|\\w*height|\\w*hour(s)*|\\w*id|\\w*index|\\w*len(gth)*|\\w*mask|\\w*pointer|\\w*quality|\\w*s(e)*ize|\\w*start|\\w*step(s)*|tick|\\w*year\\w*|ascii|base|blue|ch|chunklen|fp|green|len|limit|\\w*max|\\w*min|\\w*mode|month|\\w*multiplier|now|num|offset|\\w*op(eration)*|\\w*pos(ition)*|red|\\w*time(stamp)*|week|\\w*wid(th)*|x|y)\\d*")) {
            return "int";
        }
        if (str.matches("(\\w*gamma|percent)\\d*")) {
            return "float";
        }
        if (str.matches("(\\wmodel|\\w*obj(ect)*)\\d*")) {
            return "Object";
        }
        if (str.matches("(\\w*s)\\d*")) {
            return "array";
        }
        if (str.matches("action|controller")) {
            return "string";
        }

        return "unknown";
    }
}