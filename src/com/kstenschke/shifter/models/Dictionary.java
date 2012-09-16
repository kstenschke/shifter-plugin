package com.kstenschke.shifter.models;


import com.intellij.openapi.editor.Editor;

/**
 * Dictionary of "shiftable" keywords
 */
public class Dictionary {

		// Type IDs
	public static final int TYPE_UNKNOWN = 0;


	public static final int TYPE_ACCESSIBILITY      = 1;
	public static final int TYPE_PHPMAGICALCONSTANT = 25;

	public static final int TYPE_MYSQLDATATYPENUMERIC  = 2;
	public static final int TYPE_MYSQLDATATYPESTRING   = 3;

	public static final int TYPE_MONTH        = 4;
	public static final int TYPE_MONTH_ABBR   = 5;
	public static final int TYPE_WEEKDAY      = 6;
	public static final int TYPE_WEEKDAY_ABRR = 7;

	public static final int TYPE_JSEVENT_MOUSE      = 9;
	public static final int TYPE_JSEVENT_KEYBOARD   = 10;
	public static final int TYPE_JSEVENT_FRAMEOBJ   = 11;
	public static final int TYPE_JSEVENT_FORM       = 12;


	public static final int TYPE_COLOR        = 8;
	public static final int TYPE_ORIENTATION  = 13;
	public static final int TYPE_ADVERB       = 14;

		// Generic types
	public static final int TYPE_QUOTEDSTRING    = 15;
	public static final int TYPE_RGBCOLOR        = 16;
	public static final int TYPE_PIXELVALUE      = 17;
	public static final int TYPE_DOCCOMMENTTAG   = 18;
	public static final int TYPE_PHPVARIABLE     = 19;
	public static final int TYPE_NUMERICVALUE    = 20;
	public static final int TYPE_BOOLEANSTRING   = 21;
	public static final int TYPE_POSITIONING     = 22;

		// Data types with context
	public static final int TYPE_DATATYPE_IN_DOCCOMMENT   = 23;
	public static final int TYPE_DATATYPE_GENERAL         = 24;


		// Word type objects
	StaticWordType wordTypeAccessibilities;
	StaticWordType wordTypePhpMagicalConstants;

	StaticWordType wordTypeMySqlDataTypesNumeric;
	StaticWordType wordTypeMySqlDataTypesString;

	StaticWordType wordTypeMonths;
	StaticWordType wordTypeMonthsAbbr;
	StaticWordType wordTypeWeekdays;
	StaticWordType wordTypeWeekdaysAbbr;

	StaticWordType wordTypeColors;

	StaticWordType wordTypeOrientations;
	StaticWordType wordTypePositioning;
	StaticWordType wordTypeAdverb;

	StaticWordType wordTypeJsEventsMouse;
	StaticWordType wordTypeJsEventsKeyboard;
	StaticWordType wordTypeJsEventsFrameObj;
	StaticWordType wordTypeJsEventsForm;


		// Generic types (calculated when shifted)
	BooleanString typeBooleanString;
	QuotedString typeQuotedString;
	RbgColor typeRgbColor;
	PixelValue typePixelValue;
	NumericValue typeNumericValue;
	PhpVariable typePhpVariable;
	DocCommentTag typeTagInDocComment;
	DocCommentType typeDataTypeInDocComment;
	DataType typeDataTypeGeneral;



	/**
	 * Constructor
	 */
	public Dictionary() {
		// Init word type: object visibility
		String[] keywordsAccessibilities = {"public", "private", "protected"};
		this.wordTypeAccessibilities = new StaticWordType(keywordsAccessibilities);

		// Init word type: PHP magical constants (shifted only inside PHP files)
		String[] keywordsPhpMagicalConstants = {"__dir__", "__file__", "__namespace__", "__class__", "__line__", "__function__", "__method__", "__trait__"};
		this.wordTypePhpMagicalConstants = new StaticWordType(keywordsPhpMagicalConstants);



		// Init word type: numeric MySql data types
		String[] keywordsMySqlDataTypesNumeric = {"bit", "tinyint", "bool", "boolean", "smallint", "mediumint", "int", "integer", "bigint", "float", "double", "decimal", "dec"};
		this.wordTypeMySqlDataTypesNumeric = new StaticWordType(keywordsMySqlDataTypesNumeric);

		// Init word type: Mysql string data types
		String[] keywordsMySqlDataTypesString = {"char", "varchar", "binary", "varbinary", "tinyblob", "tinytext", "blob", "text", "mediumblob", "mediumtext", "longblob", "longtext", "enum", "set"};
		this.wordTypeMySqlDataTypesString = new StaticWordType(keywordsMySqlDataTypesString);



		// Init word types: JavaScript event types
		String[] keywordsJsEventsMouse   = { "click", "dblclick", "mousedown", "mousemove", "mouseover", "mouseout", "mouseup" };
		this.wordTypeJsEventsMouse = new StaticWordType( keywordsJsEventsMouse );

		String[] keywordsJsEventsKeyboard   = { "keydown", "keypress", "keyup" };
		this.wordTypeJsEventsKeyboard = new StaticWordType( keywordsJsEventsKeyboard );

		String[] keywordsJsEventsFrameObj   = { "abort", "error", "load", "resize", "scroll", "unload" };
		this.wordTypeJsEventsFrameObj = new StaticWordType( keywordsJsEventsFrameObj );

		String[] keywordsJsEventsForm   = { "blur", "change", "focus", "reset", "select", "submit" };
		this.wordTypeJsEventsForm = new StaticWordType( keywordsJsEventsForm );



		// Init word type: Months
		String[] keywordsMonth = {"january", "february", "march", "april", "mai", "june", "july", "august", "september", "october", "november", "december"};
		this.wordTypeMonths = new StaticWordType(keywordsMonth);

		String[] keywordsMonthAbbr = {"jan", "feb", "mar", "apr", "mai", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
		this.wordTypeMonthsAbbr = new StaticWordType(keywordsMonthAbbr);



		// Init word type: Weekday names
		String[] keywordsWeekday = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
		this.wordTypeWeekdays = new StaticWordType(keywordsWeekday);

		String[] keywordsWeekdayAbbr = {"mon", "tue", "wed", "thu", "fri", "sat", "sun"};
		this.wordTypeWeekdaysAbbr = new StaticWordType(keywordsWeekdayAbbr);




		// Init word type: Color names from the sRGB color space
		String[] keywordsColors = {"white", "lightgray", "gray", "darkgray", "black", "red", "pink", "orange", "yellow", "green", "magenta", "cyan", "blue"};
		this.wordTypeColors = new StaticWordType(keywordsColors);

		// Init word type: CSS orientations
		String[] keywordsOrientation = {"top", "right", "bottom", "left"};
		this.wordTypeOrientations = new StaticWordType(keywordsOrientation);

		// Init word type: CSS positionings
		String[] keywordsPositioning = {"absolute", "relative"};
		this.wordTypePositioning = new StaticWordType(keywordsPositioning);

		// Init word type: adverbs
		String[] keywordsAdverb = {"before", "after"};
		this.wordTypeAdverb = new StaticWordType(keywordsAdverb);

		/**
		 * Generic/calculated word types
		 */
			// Must have be prefixed with "#"
		this.typeRgbColor = new RbgColor();

		// Must be prefixed with "@"
		this.typeTagInDocComment = new DocCommentTag();

		// Various words that can be booleans
		this.typeBooleanString = new BooleanString();

		// Must consist of numeric value followed by "px"
		this.typePixelValue = new PixelValue();

		this.typeQuotedString = new QuotedString();

		this.typeNumericValue = new NumericValue();

		this.typePhpVariable = new PhpVariable();

		this.typeDataTypeInDocComment = new DocCommentType();

		this.typeDataTypeGeneral = new DataType();
	}



	/**
	 * @param	word			Word whose type shall be identified
	 * @param	prefixChar		Prefix character
	 * @param	postfixChar		Postfix character
	 * @param	line			Whole line the caret is in
	 * @param	filename		Name of edited file
	 * @return	int
	 */
	public int getWordType(String word, String prefixChar, String postfixChar, String line, String filename) {
		if (this.typePhpVariable.isPhpVariable(word)) {
			return TYPE_PHPVARIABLE;
		}

		boolean isDocCommentLineContext   = this.typeDataTypeInDocComment.isDocCommentTypeLineContext(line);

		if( isDocCommentLineContext ) {
			if ( prefixChar.matches("@") && this.typeTagInDocComment.isDocCommentTag(word, prefixChar, line) ) {
				return TYPE_DOCCOMMENTTAG;
			}

			if (this.typeDataTypeInDocComment.isDocCommentType(word, prefixChar, line)) {
				return TYPE_DATATYPE_IN_DOCCOMMENT;
			}
		}



		if ( !prefixChar.equals("@") && this.wordTypeAccessibilities.hasWord(word)) {
			return TYPE_ACCESSIBILITY;
		}

			// PHP file?
		if ( filename.endsWith(".php") || filename.endsWith(".php3") || filename.endsWith(".php4") || filename.endsWith(".php5") || filename.endsWith(".phps") || filename.endsWith(".phtml") ) {
			if( this.wordTypePhpMagicalConstants.hasWord(word) ) {
				return TYPE_PHPMAGICALCONSTANT;
			}
		}

			// SQL file
		if ( filename.endsWith(".sql") ) {
			if ( this.wordTypeMySqlDataTypesNumeric.hasWord(word)) {
				return TYPE_MYSQLDATATYPENUMERIC;
			}
			if ( this.wordTypeMySqlDataTypesString.hasWord(word)) {
				return TYPE_MYSQLDATATYPESTRING;
			}
		}


		if (this.wordTypeJsEventsMouse.hasWord(word)) {
			return TYPE_JSEVENT_MOUSE;
		}
		if (this.wordTypeJsEventsKeyboard.hasWord(word)) {
			return TYPE_JSEVENT_KEYBOARD;
		}
		if (this.wordTypeJsEventsFrameObj.hasWord(word)) {
			return TYPE_JSEVENT_FRAMEOBJ;
		}
		if (this.wordTypeJsEventsForm.hasWord(word)) {
			return TYPE_JSEVENT_FORM;
		}



		if (this.wordTypeMonths.hasWord(word)) {
			return TYPE_MONTH;
		}
		if (this.wordTypeMonthsAbbr.hasWord(word)) {
			return TYPE_MONTH_ABBR;
		}

		if (this.wordTypeWeekdays.hasWord(word)) {
			return TYPE_WEEKDAY;
		}
		if (this.wordTypeWeekdaysAbbr.hasWord(word)) {
			return TYPE_WEEKDAY_ABRR;
		}

		if (this.wordTypeColors.hasWord(word)) {
			return TYPE_COLOR;
		}

		if (this.wordTypeOrientations.hasWord(word)) {
			return TYPE_ORIENTATION;
		}

		if (this.wordTypePositioning.hasWord(word)) {
			return TYPE_POSITIONING;
		}

		if (this.wordTypeAdverb.hasWord(word)) {
			return TYPE_ADVERB;
		}

		// Generic/calculated word types
		if (this.typeBooleanString.isBooleanString(word)) {
			return TYPE_BOOLEANSTRING;
		}

		if (this.typeQuotedString.isQuotedString(word, prefixChar, postfixChar)) {

			return TYPE_QUOTEDSTRING;
		}

		if (this.typeRgbColor.isRgbColorString(word, prefixChar)) {
			return TYPE_RGBCOLOR;
		}

		if (this.typePixelValue.isPixelValue(word)) {
			return TYPE_PIXELVALUE;
		}

		if (this.typeNumericValue.isNumericValue(word)) {
			return TYPE_NUMERICVALUE;
		}

		if ( this.typeDataTypeGeneral.isDataType(word) ) {
			return TYPE_DATATYPE_GENERAL;
		}

		// "Second round" for mySql data types- after everything else failed they have another chance
		if ( this.wordTypeMySqlDataTypesNumeric.hasWord(word)) {
			return TYPE_MYSQLDATATYPENUMERIC;
		}
		if ( this.wordTypeMySqlDataTypesString.hasWord(word)) {
			return TYPE_MYSQLDATATYPESTRING;
		}

		return TYPE_UNKNOWN;
	}



	/**
	 * Shift given word: get next/previous keyword of given word group in dictionary
	 *
	 * @param	word			Word to be shifted
	 * @param	wordType		Word type ID
	 * @param	isUp			Shift up or down?
	 * @param	editorText		Full text of currently edited document
	 * @param	caretOffset		Caret offset in document
	 * @param	filename		Filename of currently edited file
	 * @param	editor			Editor instance
	 * @return					The shifted word
	 *
	 */
	public String getShiftedWord(String word, int wordType, Boolean isUp, CharSequence editorText, int caretOffset, String filename, Editor editor) {
		switch (wordType) {
				// ================== String based word types
			case TYPE_BOOLEANSTRING:
				return this.typeBooleanString.getShifted(word);

			case TYPE_ACCESSIBILITY:
				return this.wordTypeAccessibilities.getShifted(word, isUp);

			case TYPE_PHPMAGICALCONSTANT:
				return this.wordTypePhpMagicalConstants.getShifted(word, isUp);

			case TYPE_MYSQLDATATYPENUMERIC:
				return this.wordTypeMySqlDataTypesNumeric.getShifted(word, isUp);

			case TYPE_MYSQLDATATYPESTRING:
				return this.wordTypeMySqlDataTypesString.getShifted(word, isUp);

			case TYPE_MONTH:
				return this.wordTypeMonths.getShifted(word, isUp);

			case TYPE_MONTH_ABBR:
				return this.wordTypeMonthsAbbr.getShifted(word, isUp);

			case TYPE_WEEKDAY:
				return this.wordTypeWeekdays.getShifted(word, isUp);

			case TYPE_WEEKDAY_ABRR:
				return this.wordTypeWeekdaysAbbr.getShifted(word, isUp);

			case TYPE_COLOR:
				return this.wordTypeColors.getShifted(word, isUp);

			case TYPE_ORIENTATION:
				return this.wordTypeOrientations.getShifted(word, isUp);

			case TYPE_POSITIONING:
				return this.wordTypePositioning.getShifted(word, isUp);

			case TYPE_ADVERB:
				return this.wordTypeAdverb.getShifted(word, isUp);

				// JS event types
			case TYPE_JSEVENT_MOUSE:
				return this.wordTypeJsEventsMouse.getShifted(word, isUp);

			case TYPE_JSEVENT_KEYBOARD:
				return this.wordTypeJsEventsKeyboard.getShifted(word, isUp);

			case TYPE_JSEVENT_FRAMEOBJ:
				return this.wordTypeJsEventsFrameObj.getShifted(word, isUp);

			case TYPE_JSEVENT_FORM:
				return this.wordTypeJsEventsForm.getShifted(word, isUp);



				// ================== Generic types (shifting is calculated)
			case TYPE_RGBCOLOR:
				return this.typeRgbColor.getShifted(word, isUp);

			case TYPE_NUMERICVALUE:
				return this.typeNumericValue.getShifted(word, isUp, editor);

			case TYPE_PIXELVALUE:
				return this.typePixelValue.getShifted(word, isUp);

			case TYPE_PHPVARIABLE:
				return this.typePhpVariable.getShifted(word, editorText, isUp);

			case TYPE_QUOTEDSTRING:
				return this.typeQuotedString.getShifted(word, editor, editorText, isUp);

			case TYPE_DOCCOMMENTTAG:
				String textAfterCaret   = editorText.toString().substring(caretOffset);
				return this.typeTagInDocComment.getShifted(word, isUp, filename, textAfterCaret);

			case TYPE_DATATYPE_IN_DOCCOMMENT:
				return this.typeDataTypeInDocComment.getShifted(word, editorText, isUp, filename);

			case TYPE_DATATYPE_GENERAL:
				return this.typeDataTypeGeneral.getShifted(word, filename, isUp);
		}

		return word;
	}

}