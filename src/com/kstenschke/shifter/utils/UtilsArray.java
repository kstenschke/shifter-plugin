/*
 * Copyright Kay Stenschke
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Static helper methods for arrays
 */
public class UtilsArray {

    /**
     * Find strings position in array
     *
     * @param haystack String array to search in
     * @param needle   String to be looked for
     * @return int     Found position or -1
     */
    public static int getOffset(String[] haystack, String needle) {
        for (int i = 0; i < haystack.length; i++) {
            if (haystack[i].equals(needle)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Concatenate strings of given array glued by given delimiter
     *
     * @param stringsArr Array of strings
     * @param glue       Glue in between concatenated strings
     * @return String    All items of stringsArr concatenated w/ glue
     */
    public static String implode(String[] stringsArr, String glue) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < stringsArr.length; i++) {
            out.append(0 == i ? "" : glue).append(stringsArr[i]);
        }

        return out.toString();
    }

    /**
     * This String utility or util method can be used to merge 2 arrays of
     * string values. If the input arrays are like this array1 = {"a", "b" ,
     * "c"} array2 = {"c", "d", "e"} Then the output array will have {"a", "b" ,
     * "c", "d", "e"}
     * <p/>
     * This takes care of eliminating duplicates and checks null values.
     *
     * @param array1 Array of strings
     * @param array2 Array of strings
     * @return array Merged array containing each of the elements of array1 and array2
     */
    private static String[] mergeArrays(String[] array1, String[] array2) {
        if (null == array1 || array1.length == 0) {
            return array2;
        }
        if (null == array2 || array2.length == 0) {
            return array1;
        }

        List<String> array1List = Arrays.asList(array1);
        List<String> array2List = Arrays.asList(array2);

        List<String> result = new ArrayList<>(array1List);

        List<String> tmp = new ArrayList<>(array1List);
        tmp.retainAll(array2List);

        result.removeAll(tmp);
        result.addAll(array2List);

        return result.toArray(new String[0]);
    }

    /**
     * Merge three string arrays
     *
     * @param array1 Array of strings
     * @param array2 Array of strings
     * @param array3 Array of strings
     * @return array Merged array containing each of the elements of array1, array2 and array3
     */
    public static String[] mergeArrays(String[] array1, String[] array2, String[] array3) {
        return mergeArrays(mergeArrays(array1, array2), array3);
    }

    public static boolean hasDuplicateItems(String[] array) {
        for (int j = 0; j < array.length; j++) {
            for (int k = j + 1; k < array.length; k++) {
                if (array[k].equals(array[j])) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String[] reduceDuplicateItems(String[] items) {
        return new HashSet<>(Arrays.asList(items)).toArray(new String[0]);
    }
}
