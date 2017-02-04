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
package com.kstenschke.shifter.models;

import org.apache.commons.lang.StringUtils;

// Shiftable block selection
public class ShiftableBlock {

    /**
     * @param  blockSelectionStarts
     * @param  blockSelectionEnds
     * @param  editorText
     * @return boolean
     */
    public static boolean areNumericValues(int[] blockSelectionStarts, int[] blockSelectionEnds, CharSequence editorText) {
        String currentItem;

        for (int i = 1; i < blockSelectionStarts.length; i++) {
            currentItem = editorText.subSequence(blockSelectionStarts[i], blockSelectionEnds[i]).toString();
            if (!StringUtils.isNumeric(currentItem)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param  blockSelectionStarts
     * @param  blockSelectionEnds
     * @param  editorText
     * @return boolean
     */
    public static boolean areBlockItemsIdentical(int[] blockSelectionStarts, int[] blockSelectionEnds, CharSequence editorText) {
        String firstItem = editorText.subSequence(blockSelectionStarts[0], blockSelectionEnds[0]).toString();
        String currentItem;

        for (int i = 1; i < blockSelectionStarts.length; i++) {
            currentItem = editorText.subSequence(blockSelectionStarts[i], blockSelectionEnds[i]).toString();
            if (!currentItem.equals(firstItem)) {
                return false;
            }
        }

        return true;
    }

}
