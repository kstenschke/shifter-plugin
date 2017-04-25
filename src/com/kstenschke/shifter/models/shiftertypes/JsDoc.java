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

import com.intellij.openapi.editor.Document;
import com.kstenschke.shifter.utils.UtilsEnvironment;
import static org.apache.commons.lang.StringUtils.trim;

/**
 * JavaScript DOC @param comment
 */
public class JsDoc {

    /**
     * Check whether given string represents a JsDoc @param comment
     *
     * @param  str     String to be checked
     * @return boolean
     */
    public static Boolean isJsDocParamLine(String str) {
        str = trim(str);

        return str.startsWith("*") && str.contains("@param");
    }

    public static Boolean isInvalidJsDocReturnLine(String str) {
        str = trim(str);

        return str.startsWith("*") && str.contains("@return") && !str.contains("@returns");
    }

    public static Boolean isJsDocParamDataType(String str) {
        str = trim(str.toLowerCase());

        return str.equals("array")
                || str.equals("boolean")
                || str.equals("function")
                || str.equals("null")
                || str.equals("number")
                || str.equals("object")
                || str.equals("string")
                || str.equals("symbol")
                || str.equals("undefined");
    }

    public static Boolean containsCompounds(String str) {
        return str.contains("{") && str.contains("}");
    }

    /**
     * Actual shifting method
     *
     * @param  word
     * @param  document
     * @param  caretOffset
     * @return boolean
     */
    public static boolean addCompoundsAroundDataTypeAtCaretInDocument(String word, Document document, int caretOffset) {
        return UtilsEnvironment.replaceWordAtCaretInDocument(document, caretOffset, "{" + word + "}");
    }

    public static boolean correctInvalidReturnsCommentInDocument(Document document, int caretOffset) {
        return UtilsEnvironment.replaceWordAtCaretInDocument(document, caretOffset, "returns");
    }
}