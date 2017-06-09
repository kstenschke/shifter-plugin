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
        str = str.toLowerCase();

        if (str.matches("\\w*name|\\w*title|\\w*url")) {
            return "string";
        }
        if (str.matches("(\\w*delim(iter)*|\\w*dir(ectory)*|\\w*domain|\\w*key|\\w*link|\\w*name|\\w*path\\w*|\\w*prefix|\\w*suffix|charlist|comment|\\w*file(name)*|format|glue|haystack|html|intput|locale|message|name|needle|output|replace(ment)*|salt|separator|str(ing)*|url)\\d*")) {
            return "string";
        }
        if (str.matches("(arr(ay)|\\w*pieces|\\w*list|\\w*items|\\w*ids)\\d*")) {
            return "array";
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
        if (str.matches("(\\w*s)\\d*|\\w*arr(ay)*|\\w*items|\\w*data|data\\w*")) {
            return "array";
        }
        if (str.matches("(do\\w*|has\\w+|is\\w+|return\\w*|should\\w*)")) {
            return "bool";
        }

        return "unknown";
    }
}