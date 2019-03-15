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
package com.kstenschke.shifter.models.shiftable_types;

/**
 * Pixel value class
 */
public class AccessType {

    @SuppressWarnings("unused")
    public static final String ACTION_TEXT = "Shift Access Type";

    private StaticWordType accessTypes;

    public boolean isAccessType(String word) {
        if (null == word) {
            return false;
        }
        String[] keywordsAccessType = {"public", "private", "protected"};
        accessTypes = new StaticWordType(keywordsAccessType);

        return accessTypes.hasWord(word);
    }

    /**
     * @param  value    The full length value, post-fixed by its unit
     * @param  isUp     Shifting up or down?
     * @return String   Length (em / px / pt / cm / in / rem / vw / vh / vmin / vmax) value shifted up or down by 1 unit
     */
    public String getShifted(String value, boolean isUp) {
        return accessTypes.getShifted(value, isUp);
    }
}