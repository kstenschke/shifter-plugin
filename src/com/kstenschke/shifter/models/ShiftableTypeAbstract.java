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

import javax.annotation.Nullable;

public abstract class ShiftableTypeAbstract {

    private ActionContainer actionContainer;

    public ShiftableTypeAbstract(@Nullable ActionContainer actionContainer) {
        this.actionContainer = actionContainer;
    }

    // Get shiftable type or null if not applicable to given actionContainer
    abstract public ShiftableTypeAbstract getShiftableType();

    abstract public String getShifted(
            String word,
            @Nullable ActionContainer actionContainer,
            @Nullable Integer moreCount,
            @Nullable String leadingWhiteSpace
    );
    public String getShifted(String word) {
        return getShifted(word, null, null, null);
    }
    public String getShifted(String word, ActionContainer actionContainer) {
        return getShifted(word, actionContainer, null, null);
    }
    public String getShifted(String word, ActionContainer actionContainer, Integer moreCount) {
        return getShifted(word, actionContainer, moreCount, null);
    }
}
