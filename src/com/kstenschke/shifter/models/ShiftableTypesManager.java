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

import com.kstenschke.shifter.models.entities.AbstractShiftable;
import com.kstenschke.shifter.models.entities.shiftables.*;
import org.jetbrains.annotations.Nullable;

import static com.kstenschke.shifter.models.ShiftableTypes.Type.*;

/**
 * Manager of "shiftable" word shiftables - detects word type to evoke resp. shifting
 */
class ShiftableTypesManager {

    private ActionContainer actionContainer;

    // Generic shiftables (calculated when shifted)
    private DocCommentTag typeTagInDocComment;
    private DocCommentType typeDataTypeInDocComment;
    private Tupel wordsTupel;

    // Constructor
    ShiftableTypesManager(ActionContainer actionContainer) {
        this.actionContainer = actionContainer;
    }

    public void setPrefixChar(String prefix) {
        actionContainer.prefixChar = prefix;
    }

    AbstractShiftable getShiftable() {
        AbstractShiftable shiftable;

        if (null != (shiftable = new TrailingComment(actionContainer).getInstance())) return shiftable;

        // PHP doc param line is handled in caretLine-shifting fallback
        actionContainer.shiftSelectedText = false;
        actionContainer.shiftCaretLine = true;
        if (null != (shiftable = new PhpDocParamContainingDataType(actionContainer).getInstance())) return shiftable;

        actionContainer.shiftSelectedText = true;
        actionContainer.shiftCaretLine = false;

        if (null != (shiftable = new PhpVariableOrArray(actionContainer).getInstance()) ||
            null != (shiftable = new Parenthesis(actionContainer).getInstance()) ||
            null != (shiftable = new JsVariableDeclarations(actionContainer).getInstance()) ||
            null != (shiftable = new SizzleSelector(actionContainer).getInstance()) ||
            null != (shiftable = new DocCommentType(actionContainer).getInstance()) ||
            null != (shiftable = new AccessType(actionContainer).getInstance()) ||
            null != (shiftable = new DictionaryWordOfSpecificFileType(actionContainer).getInstance()) ||
            null != (shiftable = new JqueryObserver(actionContainer).getInstance()) ||
            null != (shiftable = new TernaryExpression(actionContainer).getInstance()) ||
            null != (shiftable = new QuotedString(actionContainer).getInstance()) ||
            null != (shiftable = new RgbColor(actionContainer).getInstance()) ||
            null != (shiftable = new CssUnit(actionContainer).getInstance()) ||
            null != (shiftable = new NumericValue(actionContainer).getInstance()) ||
            null != (shiftable = new OperatorSign(actionContainer).getInstance()) ||
            null != (shiftable = new RomanNumeral(actionContainer).getInstance()) ||
            // Logical operators "&&" and "||" must be detected before MonoCharStrings to avoid confusing
            null != (shiftable = new LogicalOperator(actionContainer).getInstance()) ||
            null != (shiftable = new MonoCharacterRepetition(actionContainer).getInstance()) ||
            // Term in any dictionary (w/o limiting to edited file's extension)
            null != (shiftable = new DictionaryWord(actionContainer).getInstance()) ||
            null != (shiftable = new NumericPostfixed(actionContainer).getInstance()) ||
            null != (shiftable = new Tupel(actionContainer).getInstance()) ||
            null != (shiftable = new SeparatedPath(actionContainer).getInstance()) ||
            null != (shiftable = new CamelCaseString(actionContainer).getInstance()) ||
            null != (shiftable = new HtmlEncodable(actionContainer).getInstance())
        ) return shiftable;

        // @todo 1. completely remove getType()

        // @todo 2. rework: remove redundant arguments (e.g. from getShifted())

        // @todo 3. make shifting context flexible: actionContainer.selectedText or textAroundCaret / etc.

        // No shiftable type detected
        return null;
    }

    // Detect word type (get the one w/ highest priority to be shifted) of given string
    ShiftableTypes.Type getWordType() {
        AbstractShiftable shiftable;

        // Selected code line w/ trailing //-comment: moves the comment into a new caretLine before the code
        if (null != (shiftable = new TrailingComment(actionContainer).getInstance())) return shiftable.getType();

        PhpDocParam phpDocParam = new PhpDocParam(actionContainer);
        actionContainer.shiftSelectedText = false;
        actionContainer.shiftCaretLine = true;
        if (null != phpDocParam.getInstance() &&
            !phpDocParam.containsDataType(actionContainer.caretLine)) {
//            return TYPE_PHP_DOC_PARAM_LINE;
            // PHP doc param line is handled in caretLine-shifting fallback
            return UNKNOWN;
        }

        if (null != (shiftable = new PhpVariableOrArray(actionContainer).getInstance()) ||
            null != (shiftable = new Parenthesis(actionContainer).getInstance()) ||
            null != (shiftable = new JsVariableDeclarations(actionContainer).getInstance()) ||
            null != (shiftable = new SizzleSelector(actionContainer).getInstance())
        ) return shiftable.getType();

        // DocComment shiftables (must be prefixed w/ "@")
        typeDataTypeInDocComment = new DocCommentType(actionContainer);
        if (typeDataTypeInDocComment.isDocCommentTypeLineContext(actionContainer.caretLine)) {
            typeTagInDocComment = new DocCommentTag(actionContainer);
            if (actionContainer.prefixChar.matches("@") &&
                null != typeTagInDocComment.getInstance()
            ) return DOC_COMMENT_TAG;

            if (null != typeDataTypeInDocComment.getInstance()) return DOC_COMMENT_DATA_TYPE;
        }

        if (null != (shiftable = new AccessType(actionContainer).getInstance()) ||
            null != (shiftable = new DictionaryWordOfSpecificFileType(actionContainer).getInstance()) ||
            null != (shiftable = new JqueryObserver(actionContainer).getInstance()) ||
            null != (shiftable = new TernaryExpression(actionContainer).getInstance()) ||
            null != (shiftable = new QuotedString(actionContainer).getInstance()) ||
            null != (shiftable = new RgbColor(actionContainer).getInstance()) ||
            null != (shiftable = new CssUnit(actionContainer).getInstance()) ||
            null != (shiftable = new NumericValue(actionContainer).getInstance()) ||
            null != (shiftable = new OperatorSign(actionContainer).getInstance()) ||
            null != (shiftable = new RomanNumeral(actionContainer).getInstance()) ||
            // Logical operators "&&" and "||" must be detected before MonoCharStrings to avoid confusing
            null != (shiftable = new LogicalOperator(actionContainer).getInstance()) ||
            null != (shiftable = new MonoCharacterRepetition(actionContainer).getInstance()) ||
            // Term in dictionary (anywhere, that is w/o limiting to the current file extension)
            null != (shiftable = new DictionaryWord(actionContainer).getInstance()) ||
            null != (shiftable = new NumericPostfixed(actionContainer).getInstance()) ||
            null != (shiftable = new Tupel(actionContainer).getInstance()) ||
            null != (shiftable = new SeparatedPath(actionContainer).getInstance()) ||
            null != (shiftable = new CamelCaseString(actionContainer).getInstance()) ||
            null != (shiftable = new HtmlEncodable(actionContainer).getInstance())
        ) return shiftable.getType();

        return UNKNOWN;
    }

    /**
     * Shift given word
     * ShifterTypesManager: get next/previous keyword of given word group
     * Generic: calculate shifted value
     *
     * @param  actionContainer
     * @param  word         Word to be shifted
     * @param  wordType     Shiftable word type
     * @param  moreCount    Current "more" count, starting w/ 1. If non-more shift: null
     * @return              The shifted word
     */
    String getShiftedWord(
            ActionContainer actionContainer,
            String word,
            ShiftableTypes.Type wordType,
            Integer moreCount
    ) {
        switch (wordType) {
            // String based word shiftables
            case ACCESS_TYPE: return new AccessType(actionContainer).getShifted(word);
            // Numeric values including UNIX and millisecond timestamps
            case DICTIONARY_WORD_GLOBAL:
            case DICTIONARY_WORD_EXT_SPECIFIC: return new DictionaryWord(actionContainer).getShifted(word);
            // Generic shiftables (shifting is calculated)
            case SIZZLE_SELECTOR: return new SizzleSelector(actionContainer).getShifted(word);
            case RGB_COLOR: return new RgbColor(actionContainer).getShifted(word);
            // Numeric values including UNIX and millisecond timestamps
            case NUMERIC_VALUE: return new NumericValue(actionContainer).getShifted(word);
            case CSS_UNIT: return new CssUnit(actionContainer).getShifted(word);
            case JQUERY_OBSERVER: return new JqueryObserver(actionContainer).getShifted(word, null, null);
            case PHP_VARIABLE_OR_ARRAY: return new PhpVariableOrArray(actionContainer).getShifted(word, moreCount);
            case TERNARY_EXPRESSION: return new TernaryExpression(actionContainer).getShifted(word);
            case QUOTED_STRING: return new QuotedString(actionContainer).getShifted(word);
            case PARENTHESIS: return new Parenthesis(actionContainer).getShifted(word);
            case OPERATOR_SIGN: return new OperatorSign(actionContainer).getShifted(word);
            case ROMAN_NUMERAL: return new RomanNumeral(actionContainer).getShifted(word);
            case LOGICAL_OPERATOR: return new LogicalOperator(actionContainer).getShifted(word);
            case MONO_CHARACTER_REPETITION: return new MonoCharacterRepetition(actionContainer).getShifted(word);
            case DOC_COMMENT_TAG:
                actionContainer.textAfterCaret = actionContainer.editorText.toString().substring(actionContainer.caretOffset);
                return typeTagInDocComment.getShifted(word, null);
            case DOC_COMMENT_DATA_TYPE: return typeDataTypeInDocComment.getShifted(word);
            case SEPARATED_PATH: return new SeparatedPath(actionContainer).getShifted(word);
            case CAMEL_CASE_STRING: return new CamelCaseString(actionContainer).getShifted(word);
            case HTML_ENCODABLE: return new HtmlEncodable(actionContainer).getShifted(word);
            case NUMERIC_POSTFIXED: return new NumericPostfixed(actionContainer).getShifted(word);
            case WORDS_TUPEL:
                actionContainer.disableIntentionPopup = true;
                return wordsTupel.getShifted(word);
            default: return word;
        }
    }

    String getShiftedWord(ActionContainer actionContainer, @Nullable Integer moreCount) {
        actionContainer.shiftSelectedText = true;

        AbstractShiftable shiftable = getShiftable();
        return null == shiftable
                ? actionContainer.selectedText
                : shiftable.getShifted(actionContainer.selectedText, moreCount,null);
    }

    String getActionText() {
        // @todo return ShiftableTypeAbstract.ACTION_TXT
        return "@todo return ShiftableTypeAbstract.ACTION_TXT";
        /*switch (wordType) {
            case CSS_UNIT:
                return CssUnit.ACTION_TEXT;
            case HTML_ENCODABLE:
                return HtmlEncodable.ACTION_TEXT;
            case JS_VARIABLE_DECLARATIONS:
                return JsVariablesDeclarations.ACTION_TEXT;
            case LOGICAL_OPERATOR:
                return LogicalOperator.ACTION_TEXT;
            case MONO_CHARACTER_REPETITION:
                return MonoCharacterRepetition.ACTION_TEXT;
            case NUMERIC_VALUE:
                return NumericValue.ACTION_TEXT;
            case RGB_COLOR:
                return RgbColor.ACTION_TEXT;
            case TERNARY_EXPRESSION:
                return TernaryExpression.ACTION_TEXT;
            case TRAILING_COMMENT:
                return TrailingComment.ACTION_TEXT;
            case WORDS_TUPEL:
                return Tupel.ACTION_TEXT;
            default:
                return ACTION_TEXT_SHIFT_SELECTION;
        }
        */
    }
}