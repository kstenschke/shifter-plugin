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
package com.kstenschke.shifter.models;

public class ShiftableTypes {

    public enum Type {
        UNKNOWN,

        // Dictionaric types
        ACCESS_TYPE,
        DICTIONARY_WORD_EXT_SPECIFIC,
        DICTIONARY_WORD_GLOBAL,

        // Numeric types
        NUMERIC_VALUE,
        NUMERIC_POSTFIXED,
        ROMAN_NUMERAL,

        // Generic shiftable types
        QUOTED_STRING,
        PARENTHESIS,
        HTML_ENCODABLE,
        CAMEL_CASE_STRING,
        SEPARATED_PATH,
        WORDS_TUPEL,

        // Operators (<, >, +, -, etc.) and expressions
        OPERATOR_SIGN,
        LOGICAL_OPERATOR,
        MONO_CHARACTER_REPETITION,
        TERNARY_EXPRESSION,

        RGB_COLOR,

        // CSS unit (%, cm, em, in, pt, px, rem, vw, vh, vmin, vmax)
        CSS_UNIT,

        // DOC comment related
        DOC_COMMENT_TAG,
        DOC_COMMENT_DATA_TYPE,
        PHP_DOC_PARAM_CONTAINING_DATA_TYPE,

        // PHP specific
        PHP_VARIABLE_OR_ARRAY,

        // JavaScript specific
        JS_VARIABLE_DECLARATIONS,
        SIZZLE_SELECTOR,
        JQUERY_OBSERVER,

        TRAILING_COMMENT,

        XML_ATTRIBUTES,
        CONCATENATION_JS,
        CONCATENATION_PHP,
        PHP_DOC_PARAM,
        DICTIONARY_WORD,
        SEPARATED_LIST,
        COMMENT,
        PHP_DOC_COMMENT,
        CSS,
        JS_DOC,
        LOGICAL_CONJUNCTION,
        PHP_DOCUMENT,
        STRING_CONTAINING_SLASHES,
        // @todo merge into words_tupel
        WORD_PAIR,
        MULTIPLE_LINES,
        CONCATENATION_JS_IN_TS
    }
}