/*
 * Copyright 2011-2016 Kay Stenschke
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

import java.util.Arrays;
import java.util.List;

/**
 * DocCommentType class
 */
class DocCommentDataType {

    private final String[] typesJavaScript;
    private final String[] typesJava;
    private final String[] typesPHP;
    private final String[] typesObjectiveC;

    /**
     * Constructor
     */
    public DocCommentDataType() {
        typesJavaScript = new String[]{ "array", "boolean", "element", "event", "function", "number", "null", "object", "string", "undefined" };
        typesJava       = new String[]{ "boolean", "byte", "char", "double", "float", "int", "long", "short", "string" };
        typesPHP        = new String[]{ "array", "boolean", "float", "integer", "null", "object", "resource", "string" };
        typesObjectiveC = new String[]{ "int", "char", "float", "double", "id", "BOOL", "long", "short", "signed", "unsigned" };
    }

    /**
     * @param  word        String to be shifted
     * @param  filename    Filename of the edited file
     * @param  isUp        Shifting up or down?
     * @return String      Shifting result
     */
    public String getShifted(String word, String filename, boolean isUp) {
        String[] dataTypes   = this.getDataTypesByFilename(filename);
        int amountTypes   = dataTypes.length;
        String wordLower  = word.toLowerCase();

        if( amountTypes > 0 ) {
            List<String> dataTypesList = Arrays.asList(dataTypes);
            int curIndex   =  dataTypesList.indexOf(wordLower);

                if( isUp ) {
                    // Shift up, if word at caret was not identified: take first item
                    curIndex++;
                    if( curIndex == amountTypes ) {
                        curIndex = 0;
                    }
                } else {
                    curIndex--;
                    if( curIndex == -1 ) {
                        curIndex = amountTypes - 1;
                    }
                }

                return dataTypesList.get(curIndex);
            }

        return wordLower;
    }

    /**
     * Return array of data types of detected language of edited file
     *
     * @param  filename    Filename of edited file
     * @return String[]
     */
    private String[] getDataTypesByFilename(String filename) {
        if( filename != null ) {
            String filenameLower = filename.toLowerCase();

            if( filenameLower.endsWith(".js") ) {
                // JavaScript data types
                return this.typesJavaScript;
            }
            if (filenameLower.endsWith(".java") ) {
                // Java primitive data types
                return this.typesJava;
            }
            if ( filenameLower.endsWith(".m") ) {
                // Objective-C "method" file
                return this.typesObjectiveC;
            }
        }

        // Default, e.g. PHP
        return this.typesPHP;
    }

}