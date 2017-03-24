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
        return trim(str.replace("* @param", ""));
    }

    /**
     * Guess (by variable name) and insert data type
     *
     * @param  line     e.g. "* @param $var"
     * @return string
     */
    public static String getShifted(String line) {
        String variableName = extractVariableName(line).toLowerCase();

        if (   variableName.equals("delimiter")
            || variableName.equals("needle")
            || variableName.equals("substring")
            || variableName.equals("glue")) {
            return insertDataTypeIntoParamLine(line, "string");
        }

        if (variableName.endsWith("id")) {
            return insertDataTypeIntoParamLine(line, "int");
        }

        if (   variableName.startsWith("amount")
            || variableName.startsWith("index")
            || variableName.startsWith("offset")
            || variableName.startsWith("position")) {
            return insertDataTypeIntoParamLine(line, "int");
        }

        if (   variableName.startsWith("is")
            || variableName.startsWith("convert")
            || variableName.startsWith("make")) {
            return insertDataTypeIntoParamLine(line, "bool");
        }

        if (   variableName.endsWith("arr")
            || variableName.endsWith("list")
            || variableName.endsWith("s")) {
            return insertDataTypeIntoParamLine(line, "array");
        }

        if (   variableName.startsWith("path")
            || variableName.startsWith("filename")
            || variableName.endsWith("message")) {
            return insertDataTypeIntoParamLine(line, "string");
        }

        if (  variableName.endsWith("obj")
            || variableName.endsWith("model")
            || variableName.endsWith("object")) {
            return insertDataTypeIntoParamLine(line, "Object");
        }

        if (   variableName.endsWith("config")
            || variableName.startsWith("arr")) {
            return insertDataTypeIntoParamLine(line, "array");
        }

        if (variableName.startsWith("key") || variableName.startsWith("key")) {
            return insertDataTypeIntoParamLine(line, "string");
        }

        return insertDataTypeIntoParamLine(line, "unknown");
    }

    private static String insertDataTypeIntoParamLine(String line, String dataType) {
        return line.replace("@param", "@param " + dataType);
    }
}
