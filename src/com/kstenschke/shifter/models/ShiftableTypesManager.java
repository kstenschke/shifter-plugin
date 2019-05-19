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
import static com.kstenschke.shifter.models.ShiftableTypes.Type.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Manager of "shiftable" word shiftables - detects word type to evoke resp. shifting
class ShiftableTypesManager {

    private ActionContainer actionContainer;

    // Constructor
    ShiftableTypesManager(ActionContainer actionContainer) {
        this.actionContainer = actionContainer;
    }

    public void setPrefixChar(String prefix) {
        actionContainer.prefixChar = prefix;
    }

    List<AbstractShiftable> getShiftables() {
        List<AbstractShiftable> shiftables = new ArrayList<>();

        shiftables.add(new AccessType(actionContainer).getInstance());
        shiftables.add(new CamelCaseString(actionContainer).getInstance());
        shiftables.add(new Comment(actionContainer).getInstance());
        shiftables.add(new ConcatenationJs(actionContainer).getInstance());
        shiftables.add(new ConcatenationJsInTs(actionContainer).getInstance());
        shiftables.add(new ConcatenationPhp(actionContainer).getInstance());
        shiftables.add(new Css(actionContainer).getInstance());
        shiftables.add(new CssUnit(actionContainer).getInstance());
        shiftables.add(new DictionaryWord(actionContainer).getInstance());
        shiftables.add(new DictionaryWordOfSpecificFileType(actionContainer).getInstance());
        shiftables.add(new DocCommentTag(actionContainer).getInstance());
        shiftables.add(new DocCommentType(actionContainer).getInstance());
        shiftables.add(new HtmlEncodable(actionContainer).getInstance());
        shiftables.add(new JqueryObserver(actionContainer).getInstance());
        shiftables.add(new JsDoc(actionContainer).getInstance());
        shiftables.add(new JsVariableDeclarations(actionContainer).getInstance());
        shiftables.add(new LogicalOperator(actionContainer).getInstance());
        shiftables.add(new MonoCharacterRepetition(actionContainer).getInstance());
        shiftables.add(new MultipleLines(actionContainer).getInstance());
        shiftables.add(new NumericPostfixed(actionContainer).getInstance());
        shiftables.add(new NumericValue(actionContainer).getInstance());
        shiftables.add(new OperatorSign(actionContainer).getInstance());
        shiftables.add(new Parenthesis(actionContainer).getInstance());
        shiftables.add(new PhpDocParamContainingDataType(actionContainer).getInstance());
        //shiftables.add(new PhpDocument(actionContainer).getInstance());
        shiftables.add(new QuotedString(actionContainer).getInstance());
        shiftables.add(new PhpVariable(actionContainer).getInstance());
        shiftables.add(new RgbColor(actionContainer).getInstance());
        shiftables.add(new RomanNumeral(actionContainer).getInstance());
        shiftables.add(new SeparatedList(actionContainer).getInstance());
        shiftables.add(new SeparatedListPipeDelimited(actionContainer).getInstance());
        shiftables.add(new SeparatedPath(actionContainer).getInstance());
        shiftables.add(new SizzleSelector(actionContainer).getInstance());
        shiftables.add(new StringContainingSlashes(actionContainer).getInstance());
        shiftables.add(new TernaryExpression(actionContainer).getInstance());
        shiftables.add(new TrailingComment(actionContainer).getInstance());
        //shiftables.add(new WordPair(actionContainer).getInstance());
        shiftables.add(new WordsTupel(actionContainer).getInstance());
        shiftables.add(new XmlAttributes(actionContainer).getInstance());

        shiftables.removeAll(Collections.singleton(null));

        return shiftables;
    }

    // Detect word type (get the one w/ highest priority to be shifted) of given string
    ShiftableTypes.Type getShiftableType() {
        AbstractShiftable shiftable;

        // Selected code line w/ trailing //-comment: moves the comment into a new caretLine before the code
        if (null != (shiftable = new TrailingComment(actionContainer).getInstance())) return shiftable.getType();

        // @todo implement extending phpDocComment sub-types and remove that logic from here
        PhpDocParam phpDocParam = new PhpDocParam(actionContainer);
        actionContainer.shiftSelectedText = false;
        actionContainer.shiftCaretLine = true;
        if (null != phpDocParam.getInstance() &&
            !phpDocParam.containsDataType(actionContainer.caretLine)) {
//            return TYPE_PHP_DOC_PARAM_LINE;
            // PHP doc param line is handled in caretLine-shifting fallback
            return UNKNOWN;
        }

        List<AbstractShiftable> shiftables = getShiftables();
        shiftable = getShiftable(shiftables);

        return null != shiftable
                ? shiftable.getType()
                : UNKNOWN;
    }

    /**
     * Shift given word
     * ShifterTypesManager: get next/previous keyword of given word group
     * Generic: calculate shifted value
     *
     * @param  shiftable
     * @param  word         Word to be shifted
     * @param  moreCount    Current "more" count, starting w/ 1. If non-more shift: null
     * @return              The shifted word
     */
    String getShiftedWord(
            AbstractShiftable shiftable,
            String word,
            Integer moreCount
    ) {
        //if (shiftableType == WORDS_TUPEL) actionContainer.disableIntentionPopup = true;
        return shiftable.getShifted(word);
    }

    static AbstractShiftable getShiftable(@NotNull List<AbstractShiftable> shiftables) {
        return 0 < shiftables.size()
                ? shiftables.get(0)
                : null;
    }

    String getShiftedWord(ActionContainer actionContainer, @Nullable Integer moreCount) {
        if (null == actionContainer) return "";

        actionContainer.shiftSelectedText = true;

        List<AbstractShiftable> shiftables = getShiftables();
        AbstractShiftable shiftable = getShiftable(shiftables);
        return null == shiftable
                ? actionContainer.getStringToBeShifted() // @todo if more than one shiftable: open intention popup
                : shiftable.getShifted(actionContainer.getStringToBeShifted(), moreCount,null);
    }

    String getActionText(@Nullable AbstractShiftable shiftable) {
        return null != shiftable ? shiftable.ACTION_TEXT : "Shift";
    }
}