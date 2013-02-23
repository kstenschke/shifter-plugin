package com.kstenschke.shifter.models;


import com.intellij.openapi.editor.Editor;

// @todo	change types to be lazy initialized

/**
 * Dictionary of "shiftable" keywords
 */
class Dictionary {

	public static final int TYPE_UNKNOWN = 0;

		// Dictionary (list of strings) based types
	private static final int TYPE_ACCESSIBILITY      	= 1;
	private static final int TYPE_PHPMAGICALCONSTANT 	= 2;
	private static final int TYPE_MYSQLDATATYPENUMERIC	= 10;
	private static final int TYPE_MYSQLDATATYPESTRING	= 11;
	private static final int TYPE_DATACOLLECTIONTYPE	= 12;
	private static final int TYPE_LOGICALOPERATORS		= 13;
	private static final int TYPE_ARITHMETICELEMENTS	= 14;
	private static final int TYPE_ARITHMETICOPERATIONS	= 15;
	private static final int TYPE_METASYNTAX			= 16;
	private static final int TYPE_NUMBER				= 17;
	private static final int TYPE_ORDINALNUMBER			= 18;
	private static final int TYPE_MONTH					= 20;
	private static final int TYPE_MONTH_ABBR			= 21;
	private static final int TYPE_WEEKDAY				= 22;
	private static final int TYPE_WEEKDAY_ABRR			= 23;
	private static final int TYPE_TIME_UNIT				= 24;
	private static final int TYPE_JSEVENT_MOUSE			= 30;
	private static final int TYPE_JSEVENT_KEYBOARD		= 31;
	private static final int TYPE_JSEVENT_FRAMEOBJ		= 32;
	private static final int TYPE_JSEVENT_FORM			= 33;
	private static final int TYPE_JSEVENT_TOUCH			= 34;
	private static final int TYPE_JSEVENT_CONTROL		= 35;
	private static final int TYPE_COLOR					= 40;
	private static final int TYPE_TEXTSTYLE				= 41;
	private static final int TYPE_FILEEXTENSION_IMAGE 	= 42;
	private static final int TYPE_FILEEXTENSION_AUDIO 	= 43;
	private static final int TYPE_ORIENTATION			= 44;
	private static final int TYPE_GEODIRECTION			= 45;
	private static final int TYPE_SCALE					= 46;

		// Generic types
	public static final int TYPE_QUOTEDSTRING			= 50;
	private static final int TYPE_MONOCHARACTERSTRING	= 51;
	private static final int TYPE_RGBCOLOR				= 52;
	private static final int TYPE_PIXELVALUE				= 53;
	private static final int TYPE_DOCCOMMENTTAG			= 54;
	public static final int TYPE_PHPVARIABLE			= 55;
	private static final int TYPE_NUMERICVALUE			= 56;
	public static final int TYPE_BOOLEANSTRING			= 57;

		// Data types of different contexts
	private static final int TYPE_DATATYPE_IN_DOCCOMMENT	= 60;
	private static final int TYPE_DATATYPE_GENERAL			= 61;



		// Word type objects
	private final StaticWordType wordTypeAccessibilities;
	private final StaticWordType wordTypePhpMagicalConstants;
	private final StaticWordType wordTypeMySqlDataTypesNumeric;
	private final StaticWordType wordTypeMySqlDataTypesString;
	private final StaticWordType wordTypeDataCollectionType;
	private final StaticWordType wordTypeLogicalOperators;
	private final StaticWordType wordTypeArithmeticElements;
	private final StaticWordType wordTypeArithmeticOperations;
	private final StaticWordType wordTypeMetaSyntax;
	private final StaticWordType wordTypeScales;

	private final StaticWordType wordTypeNumbers;
	private final StaticWordType wordTypeOrdinalNumbers;
	private final StaticWordType wordTypeMonths;
	private final StaticWordType wordTypeMonthsAbbr;
	private final StaticWordType wordTypeWeekdays;
	private final StaticWordType wordTypeWeekdaysAbbr;
	private final StaticWordType wordTypeTimeUnit;
	private final StaticWordType wordTypeColors;
	private final StaticWordType wordTypeTextStyles;
	private final StaticWordType wordTypeFileExtensionImage;
	private final StaticWordType wordTypeFileExtensionAudio;
	private final StaticWordType wordTypeOrientations;
	private final StaticWordType wordTypeGeoDirections;

	private final StaticWordType wordTypeJsEventsMouse;
	private final StaticWordType wordTypeJsEventsKeyboard;
	private final StaticWordType wordTypeJsEventsFrameObj;
	private final StaticWordType wordTypeJsEventsForm;
	private final StaticWordType wordTypeJsEventsTouch;
	private final StaticWordType wordTypeJsEventsControl;

		// Generic types (calculated when shifted)
	private final BooleanString typeBooleanString;
	private final QuotedString typeQuotedString;
	private final MonoCharacterString typeMonoCharacterString;
	private final RbgColor typeRgbColor;
	private final PixelValue typePixelValue;
	private final NumericValue typeNumericValue;
	private final PhpVariable typePhpVariable;
	private final DocCommentTag typeTagInDocComment;
	private final DocCommentType typeDataTypeInDocComment;
	private final DataType typeDataTypeGeneral;



	/**
	 * Constructor
	 */
	public Dictionary() {
			// Object visibility
		String[] keywordsAccessibilities = {"public", "private", "protected"};
		this.wordTypeAccessibilities = new StaticWordType(TYPE_ACCESSIBILITY, keywordsAccessibilities);

			// PHP magical constants (shifted only inside PHP files)
		String[] keywordsPhpMagicalConstants = {"__dir__", "__file__", "__namespace__", "__class__", "__line__", "__function__", "__method__", "__trait__"};
		this.wordTypePhpMagicalConstants = new StaticWordType(TYPE_PHPMAGICALCONSTANT, keywordsPhpMagicalConstants);


			// Data collection types
		String[] keywordsDataCollectionTypes = {"list", "set", "bag", "multiset", "dictionary", "table", "tree", "sequence", "queue", "heap", "graph"};
		this.wordTypeDataCollectionType = new StaticWordType(TYPE_DATACOLLECTIONTYPE, keywordsDataCollectionTypes);

			// Logical operators
		String[] keywordsLogicalOperators = {"and", "or", "xor", "not"};
		this.wordTypeLogicalOperators = new StaticWordType(TYPE_LOGICALOPERATORS, keywordsLogicalOperators);

			// Arithmetic elements and operations
		String[] keywordsArithmeticElements = {"sum", "difference", "product", "quotient", "remainder", "opposite"};
		this.wordTypeArithmeticElements = new StaticWordType(TYPE_ARITHMETICELEMENTS, keywordsArithmeticElements);

		String[] keywordsArithmeticOperations = {"addition", "subtraction", "multiplication", "division", "modulus", "negation", "concatenation"};
		this.wordTypeArithmeticOperations = new StaticWordType(TYPE_ARITHMETICOPERATIONS, keywordsArithmeticOperations);


			// Numerical and string based MySql data types
		String[] keywordsMySqlDataTypesNumeric = {"bit", "tinyint", "bool", "boolean", "smallint", "mediumint", "int", "integer", "bigint", "float", "double", "decimal", "dec"};
		this.wordTypeMySqlDataTypesNumeric = new StaticWordType(TYPE_MYSQLDATATYPENUMERIC, keywordsMySqlDataTypesNumeric);

		String[] keywordsMySqlDataTypesString = {"char", "varchar", "binary", "varbinary", "tinyblob", "tinytext", "blob", "text", "mediumblob", "mediumtext", "longblob", "longtext", "enum", "set"};
		this.wordTypeMySqlDataTypesString = new StaticWordType(TYPE_MYSQLDATATYPESTRING, keywordsMySqlDataTypesString);

		String[] keywordsMetaSyntax = {"foo", "bar", "baz", "qux"};
		this.wordTypeMetaSyntax = new StaticWordType(TYPE_METASYNTAX, keywordsMetaSyntax);


			// JavaScript event types
		String[] keywordsJsEventsMouse   = { "click", "dblclick", "mousedown", "mousemove", "mouseover", "mouseout", "mouseup" };
		this.wordTypeJsEventsMouse = new StaticWordType(TYPE_JSEVENT_MOUSE, keywordsJsEventsMouse);

		String[] keywordsJsEventsKeyboard   = { "keydown", "keypress", "keyup" };
		this.wordTypeJsEventsKeyboard = new StaticWordType(TYPE_JSEVENT_KEYBOARD, keywordsJsEventsKeyboard);

		String[] keywordsJsEventsFrameObj   = { "abort", "error", "load", "resize", "scroll", "unload" };
		this.wordTypeJsEventsFrameObj = new StaticWordType(TYPE_JSEVENT_FRAMEOBJ, keywordsJsEventsFrameObj);

		String[] keywordsJsEventsForm   = { "blur", "change", "focus", "reset", "select", "submit" };
		this.wordTypeJsEventsForm = new StaticWordType(TYPE_JSEVENT_FORM, keywordsJsEventsForm);

		String[] keywordsJsEventsTouch   = { "touchstart", "touchmove", "touchend", "touchcancel" };
		this.wordTypeJsEventsTouch = new StaticWordType(TYPE_JSEVENT_TOUCH, keywordsJsEventsTouch);

		String[] keywordsJsEventsControl   = { "resize", "scroll", "zoom", "focus", "blur", "select", "change", "submit", "reset" };
		this.wordTypeJsEventsControl = new StaticWordType(TYPE_JSEVENT_CONTROL, keywordsJsEventsControl);


			// Numbers, ordinal numbers, months, weekdays, time units
		String[] keywordsNumber = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
		this.wordTypeNumbers = new StaticWordType(TYPE_NUMBER, keywordsNumber);

		String[] keywordsOrdinalNumber = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh", "ninth", "tenth"};
		this.wordTypeOrdinalNumbers = new StaticWordType(TYPE_NUMBER, keywordsOrdinalNumber);

		String[] keywordsMonth = {"january", "february", "march", "april", "mai", "june", "july", "august", "september", "october", "november", "december"};
		this.wordTypeMonths = new StaticWordType(TYPE_MONTH, keywordsMonth);

		String[] keywordsMonthAbbr = {"jan", "feb", "mar", "apr", "mai", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
		this.wordTypeMonthsAbbr = new StaticWordType(TYPE_MONTH_ABBR, keywordsMonthAbbr);

		String[] keywordsWeekday = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
		this.wordTypeWeekdays = new StaticWordType(TYPE_WEEKDAY, keywordsWeekday);

		String[] keywordsWeekdayAbbr = {"mon", "tue", "wed", "thu", "fri", "sat", "sun"};
		this.wordTypeWeekdaysAbbr = new StaticWordType(TYPE_WEEKDAY_ABRR, keywordsWeekdayAbbr);

		String[] keywordsTimeUnit = {"seconds", "minutes", "hours", "day", "month", "year"};
		this.wordTypeTimeUnit	= new StaticWordType(TYPE_TIME_UNIT, keywordsTimeUnit);


			// Color names from the sRGB color space
		String[] keywordsColors = {"white", "lightgray", "gray", "darkgray", "black", "red", "pink", "orange", "yellow", "green", "magenta", "cyan", "blue"};
		this.wordTypeColors = new StaticWordType(TYPE_COLOR, keywordsColors);

			// Text styles
		String[] keywordsTextStyles = {"bold", "italic", "strikethrough", "subscript", "superscript", "underline"};
		this.wordTypeTextStyles = new StaticWordType(TYPE_TEXTSTYLE, keywordsTextStyles);

			// Image format file extensions
		String[] keywordsFileExtensionImage = {"gif", "jpg", "png"};
		this.wordTypeFileExtensionImage = new StaticWordType(TYPE_FILEEXTENSION_IMAGE, keywordsFileExtensionImage);

		String[] keywordsFileExtensionAudio = {"aif", "au", "mpeg", "mpg", "mp3", "ra", "ram", "wav"};
		this.wordTypeFileExtensionAudio = new StaticWordType(TYPE_FILEEXTENSION_AUDIO, keywordsFileExtensionAudio);

			// CSS and geographical orientations
		String[] keywordsOrientation = {"top", "right", "bottom", "left"};
		this.wordTypeOrientations = new StaticWordType(TYPE_ORIENTATION, keywordsOrientation);

		String[] keywordsGeoDirections = {"north", "east", "south", "west"};
		this.wordTypeGeoDirections = new StaticWordType(TYPE_GEODIRECTION, keywordsGeoDirections);

			// General scale sizes
		String[] keywordsScale = {"small", "medium", "large"};
		this.wordTypeScales = new StaticWordType(TYPE_SCALE, keywordsScale);

		// @todo	add single->double->multiple



		/**
		 * Generic (=calculated) word types
		 */
			// Must be prefixed with "#"
		this.typeRgbColor = new RbgColor();

			// Must be prefixed with "@"
		this.typeTagInDocComment = new DocCommentTag();

			// Various words coming in pairs, can be booleans
		this.typeBooleanString = new BooleanString();

			// Must consist of numeric value followed by "px"
		this.typePixelValue = new PixelValue();

			// Must be wrapped in single or double quotes or backticks
		this.typeQuotedString = new QuotedString();

			// String constisting from any amount of one character
		this.typeMonoCharacterString	= new MonoCharacterString();

		this.typeNumericValue = new NumericValue();

			// Must be prefixed with "$"
		this.typePhpVariable = new PhpVariable();

		this.typeDataTypeInDocComment = new DocCommentType();

			// Various general data types, e.g. int, array, ... of JavaScript, Java, PHP, ObjectiveC
		this.typeDataTypeGeneral = new DataType();
	}



	/**
	 * Detect word type (get the one with highest priority to be shifted) of given string
	 *
	 * @param	word			Word whose type shall be identified
	 * @param	prefixChar		Prefix character
	 * @param	postfixChar		Postfix character
	 * @param	line			Whole line the caret is in
	 * @param	filename		Name of edited file
	 * @return	int
	 */
	public int getWordType(String word, String prefixChar, String postfixChar, String line, String filename) {
		if (this.typePhpVariable.isPhpVariable(word)) return TYPE_PHPVARIABLE;

		boolean isDocCommentLineContext   = this.typeDataTypeInDocComment.isDocCommentTypeLineContext(line);

		if( isDocCommentLineContext ) {
			if ( prefixChar.matches("@") && this.typeTagInDocComment.isDocCommentTag(word, prefixChar, line) ) {
				return TYPE_DOCCOMMENTTAG;
			}

			if (this.typeDataTypeInDocComment.isDocCommentType(word, prefixChar, line)) return TYPE_DATATYPE_IN_DOCCOMMENT;
		}



		if ( !prefixChar.equals("@") && this.wordTypeAccessibilities.hasWord(word)) return TYPE_ACCESSIBILITY;

			// PHP file?
		if( filename.endsWith(".php") || filename.endsWith(".php3") || filename.endsWith(".php4") || filename.endsWith(".php5") || filename.endsWith(".phps") || filename.endsWith(".phtml") ) {
			if( this.wordTypePhpMagicalConstants.hasWord(word) ) return TYPE_PHPMAGICALCONSTANT;
		}

			// SQL file
		if ( filename.endsWith(".sql") ) {
			StaticWordType []	staticWordTypeList = { this.wordTypeMySqlDataTypesNumeric, this.wordTypeMySqlDataTypesString };
			for (StaticWordType wordType: staticWordTypeList) {
				if(wordType.hasWord(word))	return wordType.getTypeID();
			}
		}

		StaticWordType []	staticWordTypeList = {
			this.wordTypeJsEventsMouse,
			this.wordTypeJsEventsKeyboard,
			this.wordTypeJsEventsFrameObj,
			this.wordTypeJsEventsForm,
			this.wordTypeJsEventsControl,
			this.wordTypeJsEventsTouch,
			this.wordTypeDataCollectionType,
			this.wordTypeLogicalOperators,
			this.wordTypeArithmeticElements,
			this.wordTypeArithmeticOperations,
			this.wordTypeMetaSyntax,
			this.wordTypeNumbers,
			this.wordTypeOrdinalNumbers,
			this.wordTypeMonths,
			this.wordTypeFileExtensionImage,
			this.wordTypeFileExtensionAudio,
			this.wordTypeMonthsAbbr,
			this.wordTypeWeekdays,
			this.wordTypeWeekdaysAbbr,
			this.wordTypeTimeUnit,
			this.wordTypeColors,
			this.wordTypeTextStyles,
			this.wordTypeOrientations,
			this.wordTypeScales,
			this.wordTypeGeoDirections,
		};
		for (StaticWordType wordType: staticWordTypeList) {
			if(wordType.hasWord(word))	return wordType.getTypeID();
		}


			// Generic/calculated word types
		if (this.typeBooleanString.isBooleanString(word)) return TYPE_BOOLEANSTRING;

		if (this.typeQuotedString.isQuotedString(word, prefixChar, postfixChar)) return TYPE_QUOTEDSTRING;

		if (this.typeMonoCharacterString.isMonoCharacterString(word)) return TYPE_MONOCHARACTERSTRING;

		if (this.typeRgbColor.isRgbColorString(word, prefixChar)) return TYPE_RGBCOLOR;

		if (this.typePixelValue.isPixelValue(word)) return TYPE_PIXELVALUE;

		if (this.typeNumericValue.isNumericValue(word)) return TYPE_NUMERICVALUE;

		if ( this.typeDataTypeGeneral.isDataType(word) ) return TYPE_DATATYPE_GENERAL;

			// "Second round" for mySql data types- after everything else failed they have a chance (w/o need of being .sql file)
		if ( this.wordTypeMySqlDataTypesNumeric.hasWord(word)) return TYPE_MYSQLDATATYPENUMERIC;

		if ( this.wordTypeMySqlDataTypesString.hasWord(word)) return TYPE_MYSQLDATATYPESTRING;

		return TYPE_UNKNOWN;
	}



	/**
	 * Shift given word
	 * Dictionary: get next/previous keyword of given word group
	 * Generic: calculate shifted value
	 *
	 * @param	word			Word to be shifted
	 * @param	idWordType		Word type ID
	 * @param	isUp			Shift up or down?
	 * @param	editorText		Full text of currently edited document
	 * @param	caretOffset		Caret offset in document
	 * @param	filename		Filename of currently edited file
	 * @param	editor			Editor instance
	 * @return					The shifted word
	 *
	 */
	public String getShiftedWord(String word, int idWordType, Boolean isUp, CharSequence editorText, int caretOffset, String filename, Editor editor) {
		switch (idWordType) {
				// ================== String based word types
			case TYPE_BOOLEANSTRING:
				return this.typeBooleanString.getShifted(word);

			case TYPE_ACCESSIBILITY:
				return this.wordTypeAccessibilities.getShifted(word, isUp);

			case TYPE_PHPMAGICALCONSTANT:
				return this.wordTypePhpMagicalConstants.getShifted(word, isUp);

			case TYPE_DATACOLLECTIONTYPE:
				return this.wordTypeDataCollectionType.getShifted(word, isUp);

			case TYPE_LOGICALOPERATORS:
				return this.wordTypeLogicalOperators.getShifted(word, isUp);

			case TYPE_ARITHMETICELEMENTS:
				return this.wordTypeArithmeticElements.getShifted(word, isUp);

			case TYPE_ARITHMETICOPERATIONS:
				return this.wordTypeArithmeticOperations.getShifted(word, isUp);

			case TYPE_MYSQLDATATYPENUMERIC:
				return this.wordTypeMySqlDataTypesNumeric.getShifted(word, isUp);

			case TYPE_METASYNTAX:
				return this.wordTypeMetaSyntax.getShifted(word, isUp);

			case TYPE_MYSQLDATATYPESTRING:
				return this.wordTypeMySqlDataTypesString.getShifted(word, isUp);

			case TYPE_NUMBER:
				return this.wordTypeNumbers.getShifted(word, isUp);

			case TYPE_ORDINALNUMBER:
				return this.wordTypeOrdinalNumbers.getShifted(word, isUp);

			case TYPE_MONTH:
				return this.wordTypeMonths.getShifted(word, isUp);

			case TYPE_MONTH_ABBR:
				return this.wordTypeMonthsAbbr.getShifted(word, isUp);

			case TYPE_WEEKDAY:
				return this.wordTypeWeekdays.getShifted(word, isUp);

			case TYPE_WEEKDAY_ABRR:
				return this.wordTypeWeekdaysAbbr.getShifted(word, isUp);

			case TYPE_TIME_UNIT:
				return this.wordTypeTimeUnit.getShifted(word, isUp);

			case TYPE_COLOR:
				return this.wordTypeColors.getShifted(word, isUp);

			case TYPE_TEXTSTYLE:
				return this.wordTypeTextStyles.getShifted(word, isUp);

			case TYPE_SCALE:
				return this.wordTypeScales.getShifted(word, isUp);

			case TYPE_FILEEXTENSION_IMAGE:
				return this.wordTypeFileExtensionImage.getShifted(word, isUp);

			case TYPE_FILEEXTENSION_AUDIO:
				return this.wordTypeFileExtensionAudio.getShifted(word, isUp);

			case TYPE_ORIENTATION:
				return this.wordTypeOrientations.getShifted(word, isUp);

			case TYPE_GEODIRECTION:
				return this.wordTypeGeoDirections.getShifted(word, isUp);

				// JS event types
			case TYPE_JSEVENT_MOUSE:
				return this.wordTypeJsEventsMouse.getShifted(word, isUp);

			case TYPE_JSEVENT_KEYBOARD:
				return this.wordTypeJsEventsKeyboard.getShifted(word, isUp);

			case TYPE_JSEVENT_FRAMEOBJ:
				return this.wordTypeJsEventsFrameObj.getShifted(word, isUp);

			case TYPE_JSEVENT_FORM:
				return this.wordTypeJsEventsForm.getShifted(word, isUp);

			case TYPE_JSEVENT_CONTROL:
				return this.wordTypeJsEventsControl.getShifted(word, isUp);

			case TYPE_JSEVENT_TOUCH:
				return this.wordTypeJsEventsTouch.getShifted(word, isUp);



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

			case TYPE_MONOCHARACTERSTRING:
				return this.typeMonoCharacterString.getShifted(word, isUp);

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