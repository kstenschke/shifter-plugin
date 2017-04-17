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
package com.kstenschke.shifter.models.shiftertypes;

import static org.apache.commons.lang.StringUtils.trim;

/**
 * PHP Variable (word w/ $ prefix), includes array definition (toggle long versus shorthand syntax)
 */
public class PhpDocParam {

    /**
     * Check whether given string represents a PHP variable
     *
     * @param  str     String to be checked
     * @return boolean
     */
    public static Boolean isPhpDocParamLine(String str) {
        str = trim(str);

        return str.startsWith("*") && str.contains("@param");
    }

    public static Boolean containsDataType(String str) {
        str = trim(str.toLowerCase());

        return     str.contains("array")
                || str.contains("bool")
                || str.contains("int")
                || str.contains("resource")
                || str.contains("string")
                || str.contains("object")
                || str.contains("null");
    }

    public static Boolean containsVariableName(String str) {
        return     str.contains("$");
    }

    /**
     * @param  str
     * @return String
     */
    private static String extractVariableName(String str) {
        return trim(str.replace("* @param", "")).split(" ")[0];
    }

    /**
     * Guess (by variable name) and insert data type
     *
     * @param  line     e.g. "* @param $var"
     * @return string
     */
    public static String getShifted(String line) {
        String variableName = trim(extractVariableName(line).toLowerCase().replace("$", ""));

        if (variableName.matches("(\\w*delim(iter)*|\\w*dir(ectory)*|\\w*domain|\\w*key|\\w*link|\\w*name|\\w*path\\w*|\\w*prefix|\\w*suffix|charlist|comment|\\w*file(name)*|format|glue|haystack|html|intput|locale|message|needle|output|replace(ment)*|salt|separator|str(ing)*|url)\\d*")) {
            return insertDataTypeIntoParamLine(line, "string");
        }
        if (variableName.matches("(arr(ay)|\\w*pieces|\\w*list|\\w*items|\\w*ids)\\d*")) {
            return insertDataTypeIntoParamLine(line, "array");
        }
        if (variableName.matches("(\\w*day|\\w*end|\\w*expire|\\w*handle|\\w*height|\\w*hour(s)*|\\w*id|\\w*index|\\w*len(gth)*|\\w*mask|\\w*pointer|\\w*quality|\\w*s(e)*ize|\\w*steps|\\w*start|\\w*year\\w*|ascii|base|blue|ch|chunklen|fp|green|len|limit|max|min|mode|month|multiplier|now|num|offset|op(eration)*|red|time(stamp)*|week|wid(th)*|x|y)\\d*")) {
            return insertDataTypeIntoParamLine(line, "int");
        }
        if (variableName.matches("(has\\w+|is\\w+|return\\w*|should\\w*)")) {
            return insertDataTypeIntoParamLine(line, "bool");
        }
        if (variableName.matches("(\\w*gamma|percent)\\d*")) {
            return insertDataTypeIntoParamLine(line, "float");
        }
        if (variableName.matches("(\\wmodel|\\w*obj(ect)*)\\d*")) {
            return insertDataTypeIntoParamLine(line, "Object");
        }
        if (variableName.matches("(\\w*s)\\d*|\\w*arr(ay)*|\\w*items|\\w*data|data\\w*")) {
            return insertDataTypeIntoParamLine(line, "array");
        }

        return insertDataTypeIntoParamLine(line, "unknown");
    }

    private static String insertDataTypeIntoParamLine(String line, String dataType) {
        return line.replace("@param", "@param " + dataType);
    }
}
