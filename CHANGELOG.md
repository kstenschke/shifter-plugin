Shifter Plugin - Changelog
==========================

1.9.8
-----
* Use markdown instead of textile for readme and changelog

1.9.7
-----
* Improve edge case handling: Shifting selected word tupel containing space and another delimiter
* Improve: Stabilized against various edge cases
* Add unit tests

1.9.6
-----
* Improve: Shift multiple successive JavaScript variable declarations now also shifts const and let scopes into 
  comma-separated declarations

1.9.5
-----
* Add: Intention popup when shifted selection is comma-separated items and string concatenation 
  (= sort or interpolate)
* Improve: Minor performance improvements

1.9.4
-----
* Add: Shifting selected string concatenation in TypeScript file converts it to string interpolation
* Improve: Minor performance improvements

1.9.3
-----
* Add: Shift (selected) deprecated jQuery ".click" method: selected "click(" becomes "on('click', "
* Add: Shift (selected) deprecated jQuery observer methods: blur, change, click, dblclick, error, focus, keypress, 
  keydown, keyup, load, mouseenter, mouseleave, resize, submit, scroll, unload</li>
* Changed: Shifting Sizzle-Selector to variable declaration now uses "let" instead of "var" in TypeScript files

1.9.2
-----
* Improve: Add more distinct undo action texts for various detected+shifted types
* Improve: Sorting lines w/ different surrounding whitespace: are reformatted now after sorting
* Improve: Adapted UI more to recent IDE UI
* Improve: Extend default dictionary

1.9.1
-----
* Add: Toggle order of two operands in selected AND or OR logical conjunction
* Corrected: Clarified intention popup descriptions
* Improve: More DOC type detections
* Improve: Add more distinct undo action texts for various detected+shifted types
* Improve: Updated use of deprecated plugin API methods, Tested and fixed edge-cases

1.9.0
-----
* Add: Selections containing whitespace around the shiftable term can be shifted
* Improve: Adapted UI more to recent IDE UI
* Improve: Extend default dictionary

1.8.3
-----
* Add: Alphabetical sorting of single-lined selection of XML attribute-value pairs

1.8.2
-----
* Improve: Made all JavaScript (.js) shifting also available to TypeScript (.ts)

1.8.1
-----
* Improve: Dictionary can now also handle multi-word terms (separated by space)
* Improve: Shifting of parenthesis now also shifts curly brackets
* Improve: Extend default dictionary
    
1.8.0
-----
* Updated to JDK 1.8 / IntelliJ Idea IU-182.4323.46
* Improve: Made action entries in undo-history more speaking
* Improve: "More shifting" on block selection now uses value of configured "Shift more size" instead of shifting 
  repeatedly
* Improve: Extend guessing "AI" of DOC data types by argument name

1.7.7
-----
* Add: Option to shift camel-case selection into minus- or underscore-separated path (and vice versa)

1.7.6
-----
* Add: Option to un-escape escaped quotes in selection
* Improve: DOC shifting: data type detection, formatting after shifting

1.7.5
-----
* Improve: Ternary shifting: maintain multi-line formatting, reformat selection after shifting
* Improve: Quotes conversion: Extend intention popup options
* Improve: DOC data type detection
    
1.7.4
-----
* Improve: Shiftable type detection
* Improve: DOC part detections and shifting
* Improve: JsDoc shifting - Add @type completion + correction, Improve detect / complete / correct annotations

1.7.3
-----
* Improve: Plugin settings UI

1.7.2
-----
* Add: Selection that is wrapped in parenthesis: toggle surrounding round vs. square brackets
* Add: Plugin setting to enable quote conversions (1. single quotes to double, 2. double quotes to single)
* Add: Plugin setting to enable PHP array syntax conversions (1. long to short, 2. short to long)
* Add: Add: Intention popup when selection is both: shiftable PHP array (short syntax) and surrounded by parenthesis
* Improve: DOC param shifting and data type detection

1.7.1
-----
* Improve: JsDoc @param line w/o data type can now be shifted (to insert the missing data type) without having to be 
  selected
* Improve: Shifting selected JsDoc block comment (add missing data type annotations)
* Improve: Extend type detection of PHP Doc and JsDoc data type completion
* Improve: PHPDoc @param data type insertion - can now also be invoked on selected line of DOC comment block
* Improve: When there are only 2 sortable items, the related intention option now says "swap items order" instead of 
  "sort items"

1.7.0
-----
* Add: Shifting a selected (or the caret touching a) camel-cased string, converts it into a minus-separated path 
  (and vice versa)
* Add: Shifting a camel-cased string, consisting from only 2 words, shows an intention popup to either swap the words' 
  order or convert to a minus-separated path
* Add: Sort all attributes-style lines inside selected selectors in a CSS stylesheet selection (alphabetically, 
  vendor-attributes and vendor-styles at the end)
* Add: Sort selected attribute-style lines in a CSS stylesheet (alphabetically, vendor-attributes and vendor-styles at
  the end)
* Add: Sorting CSS attribute-style lines detects and adds a missing semicolon on the last line, before sorting
* Add: Intention popup when selection is both: sortable multi-line, containing swappable quotes
* Improve: Shifting of single-lined PHPDoc block comments
* Improve: Shifting of c-style line comments
* Improve: Better case maintaining during dictionaric word shifting
* Improve: Sorting selected lines was possible only from 3+ lines
* Improve: More PHPDoc and JsDoc data types detection from argument names
* Improve: Natural sort (Add case insensitivity)

1.6.1
-----
* Improve: Better JsDoc comment block detection

1.6.0
-----
* Add: Correct selected invalid JsDoc block comment - convert "@return" into "@returns", add curly brackets around
  data types in "@param" and "@returns" lines, correct invalid data types into existing primitive data types 
  (bool => boolean, event => Object, int(eger) => number),  guess and insert missing data type into "@param" and 
  "@returns" lines, reduce empty JsDoc lines
* Add: Shift invalid JsDoc "@return" statement at caret into "@returns"
* Add: Shifted DOC block comment in PHP and JavaScript: reformat DOC block after correction
* Add: Timestamp shifting unit (seconds or milliseconds) can be specified per file type in plugin settings
* Improve: Better case maintaining of shifted words
* Changed: Grouped shifting types in features list for easier comprehensibility

1.5.5
-----
* Add: Add missing curly brackets around data type at caret in jsDoc @param line
* Add: Mode selection popup when selection is both: sortable list, swappable quote signs
* Add: Mode selection popup when selection is both: PHP concatenation (order can be toggled), swappable quote signs
* Improve: Sorting a selection of lines w/ common delimiter no longer removes leading whitespace

1.5.4
-----
* Add: Flip order of selected camelCase (w/ lower or upper lead character) string that is a word pair
* Improve: PHPDoc comment data type detection
* Improve: Tupel detection (both items must be non empty)
* Bugfix: Numeric postfix shifting ("a99" was shifted into "a910" instead of "a100")

1.5.3
-----
* Bugfix: Fixed null pointer exception on Ubuntu during plugin initialization. Thanks to Vojtěch Krása

1.5.2
-----
* Add to multiple selected single-line comment shifting: Convert to block comment
* Add to multiple selected single-line comment shifting: Merge into one comment
* Add to multiple selected single-line comment shifting: Sort comments ascending/descending

1.5.1
-----
* Add: Shifting a selected HTML comment inside a PHTML file, converts it into a PHP block comment (and vice versa)
* Add: Shifting a selected PHP doc comment block that contains @param comments w/ variable name but no data type, 
  guesses and inserts data type
* Add: Shifting while caret is inside a PHP doc comment @param line that contains no data type, inserts a data type 
  (guessed from the variable name)
* Improve: Shifting block comments into line comments now allows to merge into a single- or convert to multiple line 
  comments
* Improve: Plugin settings UI
* Bugfix: Toggling tupel items order was taking precedence over switching single vs. double quotes

1.5.0
-----
* Add: Toggle among selected single / multi-line comment types
* Add: Natural sort for lines and comma- or pipe-separated values
* Add: Sorting of selected pipe-separated values
* Add: Detect and optionally remove duplicate items in shifted comma- or pipe-separated list
* Improve: Shifting a selected tupel now detects and retains variable whitespace around delimiters
* Improve: Type detection hierarchy

1.4.4
-----
* Improve: Shift selection that is a comma-separated tupel always toggles order (instead alphabetical sort, as w/ 3+ 
  items)

1.4.3
-----
* Add: More word-tupel delimiters (" - ", " + ", " < ", " > ", " <= ", " >= ", " == ", " != ", " === ", " !== ",  
  " || ", " && ")
* Improve: Shifting of selected sizzle selector into a var declaration

1.4.2
-----
* Add: Shift numeric block selection: opens dialog to chose: 1. in/decrement each or: 2. replace by enumeration
* Add: Shift Selection > Swap order of terms separated by "|" or " : "
* Add: Add: Toggle (selected) logical operator: "&&" versus "||"
* Add: Updated feature list w/ several prior not mentioned shifting types

1.4.1
-----
* Bugfix: Remove duplicates when sorting lines was removing first item

1.4.0
-----
* Add: more CSS units detections: rem|vw|vh|vmin|vmax (thanks to fireCoding)
* Add: shift selected jQuery (sizzle) selector into local variable declaration
* Add: shift selection of multiple consecutive lines of javascript var declarations into a comma-separated declaration
* Prevent edge case (shifting empty lines/selection, shifting full document) exceptions
* Changed: tab size in dictionary settings (reduced to 4)

1.3.2
-----
* Add: Shift trailing //-comment from a selected code line into a new line before the code

1.3.1
-----
* Add: Toggle order of two space-separated words

1.3.0
-----
* Upgraded to support Idea version 2016.1 onwards
* Adapted "restore factory settings" more to general IDE look-and-feel
* Code cleanup

1.2.14
------
* Add detection + optional reduction of duplicate lines to alphabetical line sorting

1.2.13
------
* Improve ternary expression shifting: can now end with semicolon

1.2.12
------
* Add shifting (selected) PHP array among long and shorthand syntax (PHP >= 5.4)

1.2.11
------
* Add: Shifting of roman numerals
* Bugfix: Shifting selected dictionary strings was detecting words case sensitive only
* Bugfix: Hyphened CSS styles were treated as two separate words
* Highlight source URL in change notes
* Extend default dictionary

1.2.10
------
* Bugfix: Case maintaining failed for dictionary terms

1.2.9
-----
* Improve CSS units detection - Add % (was: cm|em|in|pt|px)

1.2.7
-----
* Add swapping of if and else parts of selected ternary expression
* Alleviated type detection by replacing premature object-instantiation by static detector methods

1.2.7
-----
* Fixed potential "index out of range" error in common delimiter detection of multi-lines shifting

1.2.6
-----
* Shift more on PHP variables shifts until first variable with a different first letter

1.2.5
-----
* Improve case maintaining during shifting of single-lined selection

1.2.4
-----
* Improve shifting of single-lined selection (recognizes all regular shifter types now)

1.2.3
-----
* Improve detection of PHP concatenations: supports also strings containing dots and escaped quotes now
* Improve performance of lines sorting

1.2.2
-----
* Implemented toggle of order of selected PHP concatenation items (if two)
* Improve lines sorting to detect and maintain common lines delimiter
* Implemented toggling of operator signs, if surrounded by whitespace (+, -, *, /, <, >)

1.2.1
-----
* Improve shifting of CSS lengths: 0px (or other unit) is auto-corrected to 0
* Improve shifting of CSS lengths: numbers different to 0 are appended with the unit most prominently used in the 
  current file (cm / em / in / mm / pc / pt / px. Fallback: px)
* Bugfix: "Shift-Down More" of CSS shorthand was shifting also right-hand-sided value during shift of left-hand-sided
  value when decreasing digits amount

1.2.0
-----
* Add actions to instantly shift values more than 1 time: "Shift-Up More" and "Shift-Down More"
* Add configuration of repetition amount of "Shift-Up More" / "Shift-Down More" to plugin configuration

1.1.12
------
* Add timestamp configuration: shift day-wise in seconds or milliseconds?
* Extend timestamp shifting info balloon: shows date from seconds (UNIX timestamp) and milliseconds (JavaScript) now
* Improve timestamp shifting: leading zeros of timestamps are now maintained

1.1.11
------
* Made icons retina and darcula compatible
* Reduced inline changelog to previous five versions, moved full changelog to separate file

1.1.10
------
* Improve pattern detection: better distinguishing between quoted strings and comma separated lists
* Reduced memory expense (changed Boolean objects into boolean primitives)

1.1.9
-----
* Add setting: Preservation of camel- and upper case is now configurable
* Add case-insensitive fallback to failed word shifting

1.1.8
----- 
* Bugfix: Line shifting was overriding word shifting with 1.1.7

1.1.7
-----
* Add shifting of column mode selections (with identical items per line)

1.1.6
-----
* Improve compatibility: Compiled with JDK target bytecode version 1.6 (was 1.7)

1.1.5
-----
* Add sorting mode to settings: case sensitive/in-sensitive

1.1.4
-----
* Add shifting of numeric postfixes of strings

1.1.3
-----
* Made sorting of lines and lists case insensitive

1.1.2
-----
* Add support for shifting negative numeric- and CSS pixel values
* Add support for shifting more CSS length types: em, pt, cm, in

1.1.1
----- 
* Add HTML special chars encoding/decoding

1.1.0
-----
* Make shifter dictionary customizable via plugin configuration

1.0.12
-----
* Add character swapping for single/double quote, slash/backslash, in single-line selection

1.0.11
-----
* Add shifting of words consisting from any amount of the same character to the next ASCII character
* Add shifting of web image and audio format file extensions
* Add shifting of names of english ordinal numbers and numbers
* Add shifting of DOM touch and control events
* Add shifting of text styles

1.0.10
------
* Bugfix: shifting line with regex meta characters possibly caused exception
* Change menu items labels and order, disable items when no editor available

1.0.9
-----
* Add shifting of meta-syntax terms
* Add shifting of logical operators, arithmetical operations and elements
* Add shifting of geographical orientations and time units
* Add shifting of data collection type names
* Extend shiftable "boolean" keyword pairs
* Bugfix: shifting with caret at EOF failed

1.0.8
-----
* Add shifting of strings wrapped in backticks
* Add shifting of PHP core magical constants (__FILE__, __LINE__, etc.)

1.0.7
-----
* Add balloon info with human-readable date when shifting UNIX timestamps
* Add shifting of UNIX timestamps +/- one day
* Add shifting of named colors from the sRGB color space (white, lightGray, gray...)
* Add shifting of abbreviated month and weekday names (jan, feb, ... and mon, tue, ...)
* Add shifting of JavaScript event types (mouse, keyboard, frame/object, form)

1.0.6
-----
* Improve shiftable word types detection
* Add Objective-C specific data types
* Bugfix: Shifted quoted strings were partly maintaining wrong case

1.0.5
-----
* Add auto-completion of shifted JsDoc method tag with respective function name
* Add shifting of data types inside Java, JavaScript and PHP doc comments
* Add maintaining of lower case with upper first character
* Add option to sort items of selected comma separated list

1.0.4
-----
* Add support for single/double quoted strings: shifts to alphabetical next/previous quoted string in current file
* Add shifting of CSS positioning: absolute/relative
* Add shifting of Objective-c booleans YES/NO
* Bugfix: PHP variables containing underscores were not detected

1.0.3
-----
* Add support for PHP variables: shifts to alphabetically next/previous variable in current file
* Add shifting of numeric values w/o units
* Add ability to undo/redo shifter actions
* Changed CSS pixel values shifting size to 1

1.0.2
-----
* Add alphabetical ascending/descending sorting of multi-line selections.
* Add shifting of doc-params @param/@return
* Add shifting of months, weekdays and CSS orientations: top/right/bottom/left
* Add shifting of a whole line: a single shiftable word in the edited line is detected
* Add maintaining of uppercase/lowercase for all types

1.0.1
-----
* Add shifting of MySql data type keywords
* Add CSS pixel value shifting
* Add accessibility keywords shifting
* Add conversion of triple digit CSS RGB values to six digits
* Add case sensitivity for booleans

1.0.0
----- 
Initial release
