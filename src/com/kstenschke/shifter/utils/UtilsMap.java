/*
 * Copyright 2011-2018 Kay Stenschke
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
package com.kstenschke.shifter.utils;

import java.util.HashMap;
import java.util.Iterator;

public class UtilsMap {

    /**
     * @param  map
     * @return int
     */
    public static int getSumOfValues(HashMap<String, Integer> map) {
        int sum = 0;
        for(int value : map.values()){
            sum+= value;
        }
        return sum;
    }

    /**
     * @param  map
     * @return String
     */
    public static String getKeyOfHighestValue(HashMap<String, Integer> map) {
        int max    = 0;
        String key = "";

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry pairs = (java.util.Map.Entry) it.next();
            int value = Integer.parseInt( pairs.getValue().toString());
            if (value > max) {
                max = value;
                key = pairs.getKey().toString();
            }
            // Avoid ConcurrentModificationException
            it.remove();
        }

        return key;
    }
}