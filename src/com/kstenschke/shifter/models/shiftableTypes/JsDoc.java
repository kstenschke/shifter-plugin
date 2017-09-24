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
package com.kstenschke.shifter.models.shiftableTypes;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.kstenschke.shifter.utils.UtilsEnvironment;
import com.kstenschke.shifter.utils.UtilsPhp;
import com.kstenschke.shifter.utils.UtilsTextual;
import org.jetbrains.annotations.NonNls;

import static org.apache.commons.lang.StringUtils.trim;

/**
 * JavaScript DOC @param comment
 */
public class JsDoc {

    @NonNls
    private static final String REGEX_DATATYPES_NATIVE = "(array|boolean|date|event|function|null|number|object|string|undefined|\\*)";
    @NonNls
    private static final String REGEX_DATATYPES_ALIEN  = "(bool|float|int|integer|void)";

    /**
     * @param  str
     * @return boolean
     */
    public static boolean isJsDocBlock(String str) {
        str = trim(str);

        return str.startsWith("/**") && str.endsWith("*/")
            && (   (UtilsTextual.isMultiLine(str) && (str.contains("@param") || str.contains("@return")))
                || str.contains("@type"));
    }

    /**
     * Check whether given string represents a JsDoc @param comment
     *
     * @param  str     String to be checked
     * @return boolean
     */
    public static boolean isAtParamLine(String str) {
        str = trim(str);

        return str.startsWith("* ") && str.contains("@param");
    }

    public static boolean isAtTypeLine(String str) {
        str = trim(str);

        return str.contains("@type")
            && (str.startsWith("* ") || Comment.isBlockComment(str, true, true));
    }

    public static boolean isInvalidAtReturnsLine(String str) {
        str = trim(str);

        return str.startsWith("*") && str.contains("@return") && !str.contains("@returns");
    }

    public static boolean isAtReturnsLine(String str, boolean allowInvalidReturnsKeyword) {
        str = trim(str);

        return str.startsWith("*")
            && (str.contains("@returns ") || (allowInvalidReturnsKeyword && str.contains("@return ")));
    }

    /**
     * @param   str
     * @return  Is native or "alien" (valid e.g. in JavaScript, Java, etc.) data type
     */
    public static boolean isDataType(String str) {
        str = trim(str.toLowerCase());

        return str.matches(REGEX_DATATYPES_NATIVE) || str.matches(REGEX_DATATYPES_ALIEN);
    }

    public static boolean isWordRightOfAtKeyword(String word, String line) {
        String keywords[] = new String[]{"@param", "@return", "@type"};
        for (String keyword : keywords) {
            if (line.contains(keyword)) {
                line = trim(line.split(keyword)[1]);
                return line.startsWith(word);
            }
        }

        return false;
    }

    private static boolean containsDataType(String str, String lhs) {
        str = trim(str.toLowerCase());

        if (
           // JavaScript primitive data types
              str.contains(lhs + "array")
           || str.contains(lhs + "boolean")
           || str.contains(lhs + "null")
           || str.contains(lhs + "number")
           || str.contains(lhs + "object")
           || str.contains(lhs + "string")
           || str.contains(lhs + "undefined")
           // Complex JavaScript (object) data types
           || str.contains(lhs + "date")
           || str.contains(lhs + "event")
           || str.contains(lhs + "function")
        ) {
            return true;
        }

        // Non-JavaScript types known to other languages
        return str.contains(lhs + "bool")
            || str.contains(lhs + "float")
            || str.contains(lhs + "int")
            || str.contains(lhs + "void");
    }

    public static boolean containsCompounds(String str) {
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

    /**
     * @param line
     * @param docCommentType        "@param" / "@returns" / "@type"
     * @return
     */
    private static String addCompoundsToDataType(String line, String docCommentType) {
        line = line.replaceAll("(?i)(" + docCommentType + "\\s*)" + REGEX_DATATYPES_NATIVE, "$1{$2}");

        return line.replaceAll("(?i)(" + docCommentType + "\\s*)" + REGEX_DATATYPES_ALIEN, "$1{$2}");
    }

    public static boolean correctInvalidReturnsCommentInDocument(Document document, int caretOffset) {
        return UtilsEnvironment.replaceWordAtCaretInDocument(document, caretOffset, "returns");
    }

    /**
     * Correct invalid JsDoc block comment
     *
     * Correct "@return" into "@returns"
     * Add curly brackets around data shiftableTypes in "@param" and "@returns" lines
     * Correct invalid data shiftableTypes into existing primitive data shiftableTypes (event => Object, int(eger) => number)
     *
     * @param document
     * @param offsetStart
     * @param offsetEnd
     * @return
     */
    public static boolean correctDocBlockInDocument(Editor editor, Document document, int offsetStart, int offsetEnd) {
        String documentText = document.getText();
        String docBlock = documentText.substring(offsetStart, offsetEnd);
        String lines[] = docBlock.split("\n");

        String docBlockCorrected = "";
        int index = 0;
        for (String line : lines) {
            if (isAtParamLine(line) || isAtReturnsLine(line, true) || isAtTypeLine(line)) {
                line = correctAtKeywordLine(line);
            }

            docBlockCorrected += (index > 0 ? "\n" : "") + line;
            index++;
        }
        docBlockCorrected = reduceDoubleEmptyCommentLines(docBlockCorrected);

        if (!docBlockCorrected.equals(docBlock)) {
            document.replaceString(offsetStart, offsetEnd, docBlockCorrected);
            UtilsEnvironment.reformatSubString(editor, editor.getProject(), offsetStart, offsetEnd);
            return true;
        }

        return false;
    }

    /**
     * Correct JsDoc line
     *
     * @param line
     * @param keyword   "@param" / "@returns" / "@type"
     * @return
     */
    private static String correctAtKeywordLine(String line, String keyword) {
        line = correctInvalidAtReturnsStatement(line);

        if (!containsCompounds(line) && containsDataType(line, " ")) {
            line = addCompoundsToDataType(line, keyword);
        }
        line = correctInvalidDataTypes(line, "{", true);
        line = correctInvalidDataTypes(line, "|", true);

        return containsDataType(line, "{") ? line : addDataType(line);
    }

    public static String correctAtKeywordLine(String line) {
        String keywords[] = new String[]{"@param", "@returns", "@type"};
        for (String keyword : keywords) {
            line = correctAtKeywordLine(line, keyword);
        }
        return line;
    }

    private static String correctInvalidAtReturnsStatement(String line) {
        return line.replace(" @return ", " @returns ");
    }

    private static String correctInvalidDataTypes(String line) {
        return correctInvalidDataTypes(line, "", false);
    }
    private static String correctInvalidDataTypes(String line, String lhs, boolean allowVoid) {
        if (!allowVoid) {
            line = line.replace(lhs + "void",    lhs + "undefined");
        }
        return line
                .replace(lhs + "array",      lhs + "Array")
                .replace(lhs + "bool",       lhs + "boolean")
                .replace(lhs + "booleanean", lhs + "boolean")
                .replace(lhs + "date",       lhs + "Date")
                .replace(lhs + "event",      lhs + "Event")
                .replace(lhs + "float",      lhs + "number")
                .replace(lhs + "int",        lhs + "number")
                .replace(lhs + "integer",    lhs + "number")
                .replace(lhs + "object",     lhs + "Object");
    }

    private static String reduceDoubleEmptyCommentLines(String block) {
        String lines[] = block.split("\n");
        String blockCleaned = "";

        boolean wasPreviousEmpty = false;
        int index = 0;
        for (String line : lines) {
            boolean isEmpty = index == 0 || (trim(trim(line).replaceAll("\\*", "")).isEmpty());

            if (index == 0 || !(isEmpty && wasPreviousEmpty)) {
                blockCleaned += (index > 0 ? "\n" : "") + line;
            }
            wasPreviousEmpty = isEmpty;
            index++;
        }

        return blockCleaned;
    }

    private static String addDataType(String line) {
        String parameterName =
                trim(trim(line.replaceAll("\\*", ""))
                .replace("@param", "")
                .replace("@returns", "")
                .replace("@type", ""));

        if (parameterName.contains(" ")) {
            parameterName = parameterName.split("\\s")[0];
        }

        if (parameterName.isEmpty()) {
            return line;
        }
        String jsDocParameterName = "{" + guessDataTypeByParameterName(parameterName) + "}";

        if (line.contains(jsDocParameterName)) {
            return line;
        }
        return line.replace(
                parameterName,
                jsDocParameterName + (isAtReturnsLine(line, false)
                        ? ""
                        : " " + parameterName));
    }

    /**
     * @param  parameterName
     * @return String
     */
    private static String guessDataTypeByParameterName(String parameterName) {
        String parameterNameLower = parameterName.toLowerCase();
        String camelWords[] = UtilsTextual.splitCamelCaseIntoWords(parameterName, true);
        String lastWord = camelWords[camelWords.length - 1];

        if (parameterName.startsWith("$") || parameterName.matches("(?i)(\\w*elem)")) {
            return "*";
        }
        if (parameterName.matches("(?i)(\\w*date\\w*)")) {
            return "Date";
        }
        if (parameterName.equals("e")) {
            return "Event";
        }
        if (lastWord.matches("func|function|callback")) {
            return "Function";
        }
        if (parameterName.length() == 1) {
            // e.g. x, y, i, etc.
            return "number";
        }
        if ("params".equals(parameterName) || parameterName.matches("(?i)(\\w*obj\\w*)")) {
            return "Object";
        }
        if ("useragent".equals(parameterNameLower)) {
            return "string";
        }
        if (parameterName.equals("void")) {
            // Intercept "id"-ending before it is mistaken for a numeric "ID" parameter
            return "void";
        }

        return correctInvalidDataTypes(UtilsPhp.guessDataTypeByParameterName(parameterName));
    }
}