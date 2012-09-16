package com.kstenschke.shifter.models;

import com.kstenschke.shifter.helpers.ArrayHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DocCommentType class
 */
public class DataType {

	private String[] typesJavaScript;

	private String[] typesJava;

	private String[] typesPHP;

	private String[] typesObjectiveC;



	/**
	 * Constructor
	 */
	public DataType() {
		typesJavaScript = new String[]{ "array", "boolean", "element", "event", "function", "number", "null", "object", "string", "undefined" };
		typesJava       = new String[]{ "boolean", "byte", "char", "double", "float", "int", "long", "short", "string" };
		typesPHP        = new String[]{ "array", "boolean", "float", "integer", "null", "object", "resource", "string" };
		typesObjectiveC = new String[]{ "int", "char", "float", "double", "id", "BOOL", "long", "short", "signed", "unsigned" };
	}



	/**
	 * Returns string array with all recognized doc comment data types
	 *
	 * @return  Array
	 */
	public String[]   getAllTypes() {
		return ArrayHelper.mergeStringArrays(this.typesJavaScript, this.typesJava, this.typesPHP, this.typesObjectiveC);
	}



	/**
	 * Returns pipe-separated list (as string) with all recognized doc comment data types
	 *
	 * @return  String
	 */
	public String   getAllTypesPiped() {
		String[] allTypes = this.getAllTypes();

		return ArrayHelper.implode(allTypes, "|");
	}



	/**
	 * Check whether given String represents any known data type
	 *
	 * @param   word          String to be checked
	 * @return  Boolean.
	 */
	public Boolean isDataType(String word) {
		return !(word == null || word.length() == 0)                      // Word has content
				  && this.getAllTypesPiped().contains(word.toLowerCase());  // Word is a keyword of the data type
	}



	/**
	 * @param   word           String to be shifted
	 * @param   filename       Filename of the edited file
	 * @param   isUp           Shifting up or down?
	 * @return  String         Shifting result
	 */
	public String getShifted(String word, String filename, Boolean isUp) {
		String[] dataTypes   = this.getDataTypesByFilename(filename);
		int amountTypes   = dataTypes.length;

		if( amountTypes > 0 ) {
			word  = word.toLowerCase();
			List<String> dataTypesList = Arrays.asList(dataTypes);
			int curIndex   =  dataTypesList.indexOf(word);

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


		return word;
	}



	/**
	 * Return array of data types of detected language of edited file
	 *
	 * @param   filename   Filename of edited file
	 * @return  String[]
	 */
	public String[] getDataTypesByFilename(String filename) {
		if( filename != null ) {
			filename = filename.toLowerCase();

			if( filename.endsWith(".js") ) {
				// JavaScript data types
				return this.typesJavaScript;
			} else if (filename.endsWith(".java") ) {
				// Java primitive data types
				return this.typesJava;
			} else if ( filename.endsWith(".m") ) {
				// Objective-C "method" file
				return this.typesObjectiveC;
			}
		}
			// Default, e.g. PHP
		return this.typesPHP;
	}

}