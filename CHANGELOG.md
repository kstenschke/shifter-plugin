Shifter Plugin - Changelog
==========================

* 2.0.0
  * Reworked architecture: All shiftable types extend a common abstract class
* 1.9.7
  * Improved edge case handling: Shifting selected word tupel containing space and another delimiter
  * Improved: Stabilized against various edge cases
  * Added unit tests
* 1.9.6
  * Improved: Shift multiple successive JavaScript variable declarations now also shifts const and let scopes into comma-separated declarations
* 1.9.5
  * Added: Intention popup when shifted selection is comma-separated items and string concatenation (= sort or interpolate)
  * Improved: Minor performance improvements
* 1.9.4
  * Added: Shifting selected string concatenation in TypeScript file converts it to string interpolation
  * Improved: Minor performance improvements
* 1.9.3
  * Added: Shift (selected) deprecated jQuery ".click" method: selected "click(" becomes "on('click', "
  * Added: Shift (selected) deprecated jQuery observer methods: blur, change, click, dblclick, error, focus, keypress, keydown, keyup, load, mouseenter, mouseleave, resize, submit, scroll, unload</li>
  * Changed: Shifting Sizzle-Selector to variable declaration now uses "let" instead of "var" in TypeScript files
* 1.9.2
  * Improved: Added more distinct undo action texts for various detected+shifted types
  * Improved: Sorting lines w/ different surrounding whitespace: are reformatted now after sorting
  * Improved: Adapted UI more to recent IDE UI
  * Improved: Extended default dictionary
* 1.9.1
  * Added: Toggle order of two operands in selected AND or OR logical conjunction
  * Corrected: Clarified intention popup descriptions
  * Improved: More DOC type detections
  * Improved: Added more distinct undo action texts for various detected+shifted types
  * Improved: Updated use of deprecated plugin API methods, Tested and fixed edge-cases
* 1.9.0
  * Added: Selections containing whitespace around the shiftable term can be shifted
  * Improved: Adapted UI more to recent IDE UI
  * Improved: Extended default dictionary
* 1.8.3
  * Added: Alphabetical sorting of single-lined selection of XML attribute-value pairs
* 1.8.2
  * Improved: Made all JavaScript (.js) shifting also available to TypeScript (.ts)
* 1.8.1
  * Improved: Dictionary can now also handle multi-word terms (separated by space)
  * Improved: Shifting of parenthesis now also shifts curly brackets
  * Improved: Extended default dictionary
* 1.8.0
  * Updated to JDK 1.8 / IntelliJ Idea IU-182.4323.46
  * Improved: Made action entries in undo-history more speaking
  * Improved: "More shifting" on block selection now uses value of configured "Shift more size" instead of shifting repeatedly
  * Improved: Extended guessing "AI" of DOC data types by argument name
* 1.7.7
  * Added: Option to shift camel-case selection into minus- or underscore-separated path (and vice versa)
* 1.7.6
  * Added: Option to un-escape escaped quotes in selection
  * Improved: DOC shifting: data type detection, formatting after shifting
* 1.7.5
  * Improved: Ternary shifting: maintain multi-line formatting, reformat selection after shifting
  * Improved: Quotes conversion: extended intention popup options
  * Improved: DOC data type detection
* 1.7.4
  * Improved: Shiftable type detection
  * Improved: DOC part detections and shifting
  * Improved: JsDoc shifting - added @type completion + correction, improved detect / complete / correct annotations
* 1.7.3
  * Improved: Plugin settings UI
* 1.7.2
  * Added: Selection that is wrapped in parenthesis: toggle surrounding round vs. square brackets
  * Added: Plugin setting to enable quote conversions (1. single quotes to double, 2. double quotes to single)
  * Added: Plugin setting to enable PHP array syntax conversions (1. long to short, 2. short to long)
  * Added: Added: Intention popup when selection is both: shiftable PHP array (short syntax) and surrounded by parenthesis
  * Improved: DOC param shifting and data type detection
* 1.7.1
  * Improved: JsDoc @param line w/o data type can now be shifted (to insert the missing data type) without having to be selected
  * Improved: Shifting selected JsDoc block comment (add missing data type annotations)
  * Improved: Extended type detection of PHP Doc and JsDoc data type completion
  * Improved: PHPDoc @param data type insertion - can now also be invoked on selected line of DOC comment block
  * Improved: When there are only 2 sortable items, the related intention option now says "swap items order" instead of "sort items"
* 1.7.0
  * Added: Shifting a selected (or the caret touching a) camel-cased string, converts it into a minus-separated path (and vice versa)
  * Added: Shifting a camel-cased string, consisting from only 2 words, shows an intention popup to either swap the words' order or convert to a minus-separated path
  * Added: Sort all attributes-style lines inside selected selectors in a CSS stylesheet selection (alphabetically, vendor-attributes and vendor-styles at the end)
  * Added: Sort selected attribute-style lines in a CSS stylesheet (alphabetically, vendor-attributes and vendor-styles at the end)
  * Added: Sorting CSS attribute-style lines detects and adds a missing semicolon on the last line, before sorting
  * Added: Intention popup when selection is both: sortable multi-line, containing swappable quotes
  * Improved: Shifting of single-lined PHPDoc block comments
  * Improved: Shifting of c-style line comments
  * Improved: Better case maintaining during dictionaric word shifting
  * Improved: Sorting selected lines was possible only from 3+ lines
  * Improved: More PHPDoc and JsDoc data types detection from argument names
  * Improved: Natural sort (added case insensitivity)
* 1.6.1
  * Improved: Better JsDoc comment block detection
* 1.6.0
  * Added: Correct selected invalid JsDoc block comment - convert "@return" into "@returns", add curly brackets around data types in "@param" and "@returns" lines, correct invalid data types into existing primitive data types (bool => boolean, event => Object, int(eger) => number),  guess and insert missing data type into "@param" and "@returns" lines, reduce empty JsDoc lines
  * Added: Shift invalid JsDoc "@return" statement at caret into "@returns"
  * Added: Shifted DOC block comment in PHP and JavaScript: reformat DOC block after correction
  * Added: Timestamp shifting unit (seconds or milliseconds) can be specified per file type in plugin settings
  * Improved: Better case maintaining of shifted words
  * Changed: Grouped shifting types in features list for easier comprehensibility
* 1.5.5
  * Added: Add missing curly brackets around data type at caret in jsDoc @param line
  * Added: Mode selection popup when selection is both: sortable list, swappable quote signs
  * Added: Mode selection popup when selection is both: PHP concatenation (order can be toggled), swappable quote signs
  * Improved: Sorting a selection of lines w/ common delimiter no longer removes leading whitespace
* 1.5.4
  * Added: Flip order of selected camelCase (w/ lower or upper lead character) string that is a word pair
  * Improved: PHPDoc comment data type detection
  * Improved: Tupel detection (both items must be non empty)
  * Bugfix: Numeric postfix shifting ("a99" was shifted into "a910" instead of "a100")
* 1.5.3
  * Bugfix: Fixed null pointer exception on Ubuntu during plugin initialization. Thanks to Vojtěch Krása
* 1.5.2
  * Added to multiple selected single-line comment shifting: Convert to block comment
  * Added to multiple selected single-line comment shifting: Merge into one comment
  * Added to multiple selected single-line comment shifting: Sort comments ascending/descending
* 1.5.1
  * Added: Shifting a selected HTML comment inside a PHTML file, converts it into a PHP block comment (and vice versa)
  * Added: Shifting a selected PHP doc comment block that contains @param comments w/ variable name but no data type, guesses and inserts data type
  * Added: Shifting while caret is inside a PHP doc comment @param line that contains no data type, inserts a data type (guessed from the variable name)
  * Improved: Shifting block comments into line comments now allows to merge into a single- or convert to multiple line comments
  * Improved: Plugin settings UI
  * Bugfix: Toggling tupel items order was taking precedence over switching single vs. double quotes
* 1.5.0
  * Added: Toggle among selected single / multi-line comment types
  * Added: Natural sort for lines and comma- or pipe-separated values
  * Added: Sorting of selected pipe-separated values
  * Added: Detect and optionally remove duplicate items in shifted comma- or pipe-separated list
  * Improved: Shifting a selected tupel now detects and retains variable whitespace around delimiters
  * Improved: Type detection hierarchy
* 1.4.4
  * Improved: Shift selection that is a comma-separated tupel always toggles order (instead alphabetical sort, as w/ 3+ items)
* 1.4.3
  * Added: More word-tupel delimiters (" - ", " + ", " < ", " > ", " <= ", " >= ", " == ", " != ", " === ", " !== ",  " || ", " && ")
  * Improved: Shifting of selected sizzle selector into a var declaration
* 1.4.2
  * Added: Shift numeric block selection: opens dialog to chose: 1. in/decrement each or: 2. replace by enumeration
  * Added: Shift Selection > Swap order of terms separated by "|" or " : "
  * Added: Added: Toggle (selected) logical operator: "&&" versus "||"
  * Added: Updated feature list w/ several prior not mentioned shifting types
* 1.4.1
  * Bugfix: Remove duplicates when sorting lines was removing first item
* 1.4.0
  * Added: more CSS units detections: rem|vw|vh|vmin|vmax (thanks to fireCoding)
  * Added: shift selected jQuery (sizzle) selector into local variable declaration
  * Added: shift selection of multiple consecutive lines of javascript var declarations into a comma-separated declaration
  * Prevent edge case (shifting empty lines/selection, shifting full document) exceptions
  * Changed: tab size in dictionary settings (reduced to 4)
* 1.3.2
  * Added: Shift trailing //-comment from a selected code line into a new line before the code
* 1.3.1
  * Added: Toggle order of two space-separated words
* 1.3.0
  * Upgraded to support Idea version 2016.1 onwards
  * Adapted "restore factory settings" more to general IDE look-and-feel
  * Code cleanup
* 1.2.14
  * Added detection + optional reduction of duplicate lines to alphabetical line sorting
* 1.2.13
  * Improved ternary expression shifting: can now end with semicolon
* 1.2.12
  * Added shifting (selected) PHP array among long and shorthand syntax (PHP >= 5.4)
* 1.2.11
  * Added: Shifting of roman numerals
  * Bugfix: Shifting selected dictionary strings was detecting words case sensitive only
  * Bugfix: Hyphened CSS styles were treated as two separate words
  * Highlighted source URL in change notes
  * Extended default dictionary
* 1.2.10
  * Bugfix: Case maintaining failed for dictionary terms
* 1.2.9
  * Improved CSS units detection - added % (was: cm|em|in|pt|px)
* 1.2.7
  * Added swapping of if and else parts of selected ternary expression
  * Alleviated type detection by replacing premature object-instantiation by static detector methods
* 1.2.7
  * Fixed potential "index out of range" error in common delimiter detection of multi-lines shifting
* 1.2.6
  * Shift more on PHP variables shifts until first variable with a different first letter
* 1.2.5
  * Improved case maintaining during shifting of single-lined selection
* 1.2.4
  * Improved shifting of single-lined selection (recognizes all regular shifter types now)
* 1.2.3
  * Improved detection of PHP concatenations: supports also strings containing dots and escaped quotes now
  * Improved performance of lines sorting
* 1.2.2
  * Implemented toggle of order of selected PHP concatenation items (if two)
  * Improved lines sorting to detect and maintain common lines delimiter
  * Implemented toggling of operator signs, if surrounded by whitespace (+, -, *, /, <, >)
* 1.2.1
  * Improved shifting of CSS lengths: 0px (or other unit) is auto-corrected to 0
  * Improved shifting of CSS lengths: numbers different to 0 are appended with the unit most prominently used in the current file (cm / em / in / mm / pc / pt / px. Fallback: px)
  * Bugfix: "Shift-Down More" of CSS shorthand was shifting also right-hand-sided value during shift of left-hand-sided value when decreasing digits amount
* 1.2.0
  * Added actions to instantly shift values more than 1 time: "Shift-Up More" and "Shift-Down More"
  * Added configuration of repetition amount of "Shift-Up More" / "Shift-Down More" to plugin configuration
* 1.1.12
  * Added timestamp configuration: shift day-wise in seconds or milliseconds?
  * Extended timestamp shifting info balloon: shows date from seconds (UNIX timestamp) and milliseconds (JavaScript) now
  * Improved timestamp shifting: leading zeros of timestamps are now maintained
* 1.1.11
  * Made icons retina and darcula compatible
  * Reduced inline changelog to previous five versions, moved full changelog to separate file
* 1.1.10
  * Improved pattern detection: better distinguishing between quoted strings and comma separated lists
  * Reduced memory expense (changed Boolean objects into boolean primitives)
* 1.1.9
  * Added setting: Preservation of camel- and upper case is now configurable
  * Added case-insensitive fallback to failed word shifting
* 1.1.8 Bugfix: Line shifting was overriding word shifting with 1.1.7
* 1.1.7 Added shifting of column mode selections (with identical items per line)
* 1.1.6 Improved compatibility: Compiled with JDK target bytecode version 1.6 (was 1.7)
* 1.1.5 Added sorting mode to settings: case sensitive/in-sensitive
* 1.1.4 Added shifting of numeric postfixes of strings
* 1.1.3 Made sorting of lines and lists case insensitive
* 1.1.2
  * Added support for shifting negative numeric- and CSS pixel values
  * Added support for shifting more CSS length types: em, pt, cm, in
* 1.1.1 Added HTML special chars encoding/decoding
* 1.1.0 Made shifter dictionary customizable via plugin configuration
* 1.0.12  Added character swapping for single/double quote, slash/backslash, in single-line selection
* 1.0.11
  * Added shifting of words consisting from any amount of the same character to the next ASCII character
  * Added shifting of web image and audio format file extensions
  * Added shifting of names of english ordinal numbers and numbers
  * Added shifting of DOM touch and control events
  * Added shifting of text styles
* 1.0.10
  * Bugfix: shifting line with regex meta characters possibly caused exception
  * Changed menu items labels and order, disable items when no editor available
* 1.0.9
  * Added shifting of meta-syntax terms
  * Added shifting of logical operators, arithmetical operations and elements
  * Added shifting of geographical orientations and time units
  * Added shifting of data collection type names
  * Extended shiftable "boolean" keyword pairs
  * Bugfix: shifting with caret at EOF failed
* 1.0.8
  * Added shifting of strings wrapped in backticks
  * Added shifting of PHP core magical constants (__FILE__, __LINE__, etc.)
* 1.0.7
  * Added balloon info with human-readable date when shifting UNIX timestamps
  * Added shifting of UNIX timestamps +/- one day
  * Added shifting of named colors from the sRGB color space (white, lightGray, gray...)
  * Added shifting of abbreviated month and weekday names (jan, feb, ... and mon, tue, ...)
  * Added shifting of JavaScript event types (mouse, keyboard, frame/object, form)
* 1.0.6
  * Improved shiftable word types detection
  * Added Objective-C specific data types
  * Bugfix: Shifted quoted strings were partly maintaining wrong case
* 1.0.5
  * Added auto-completion of shifted JsDoc method tag with respective function name
  * Added shifting of data types inside Java, JavaScript and PHP doc comments
  * Added maintaining of lower case with upper first character
  * Added option to sort items of selected comma separated list
* 1.0.4
  * Added support for single/double quoted strings: shifts to alphabetical next/previous quoted string in current file
  * Added shifting of CSS positioning: absolute/relative
  * Added shifting of Objective-c booleans YES/NO
  * Bugfix: PHP variables containing underscores were not detected
* 1.0.3
  * Added support for PHP variables: shifts to alphabetically next/previous variable in current file
  * Added shifting of numeric values w/o units
  * Added ability to undo/redo shifter actions
  * Changed CSS pixel values shifting size to 1
* 1.0.2
  * Added alphabetical ascending/descending sorting of multi-line selections.
  * Added shifting of doc-params @param/@return
  * Added shifting of months, weekdays and CSS orientations: top/right/bottom/left
  * Added shifting of a whole line: a single shiftable word in the edited line is detected
  * Added maintaining of uppercase/lowercase for all types
* 1.0.1
  * Added shifting of MySql data type keywords
  * Added CSS pixel value shifting
  * Added accessibility keywords shifting
  * Added conversion of triple digit CSS RGB values to six digits
  * Added case sensitivity for booleans
* 1.0.0 Initial release
