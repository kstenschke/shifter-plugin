Shifter Plugin
==============

Extension plugin for the various Jetbrains IDEs, working in IntelliJ IDEA, PhpStorm, WebStorm, PyCharm, RubyMine, 
AppCode, CLion,  Gogland, DataGrip, Rider and Android Studio


Description
-----------

Shifter performs string manipulations on a single keyboard shortcut, after detection of intended manipulation.
The type of manipulation has to be selected manually only when several are possible.

When evoked, Shifter detects the type of selection (or lets the user choose when ambiguous) in the current line or
keyword at the caret and performs the possible string manipulation.
To manipulations with multiple steps, there is an "up"- and "down"-shifting mode available.

If there's only one shiftable word in a line, it can be manipulated without the caret touching it.
Lowercase/uppercase or lower case with upper first character of shifted words is maintained.
To shift values instantly multiple times, there are additional "Shift More" actions, the amount of repetitions can be
configured in the plugin configurations (default: 10).


### Default Keyboard Shortcuts

Shifter adds the following editing tools (you can change the keymap in the IDE preferences):

* Ctrl+Shift+Alt+Comma  - Shift-Down
* Ctrl+Shift+Alt+Period - Shift-Up
* Ctrl+Shift+Alt+J  - Shift-Down More
* Ctrl+Shift+Alt+K  - Shift-Up More

**Mac Users:** On Macs, the keyboard shortcuts are Comma or Period key together with Shift+Alt+Cmd

### Shifting Types

#### Sorting
* Shifting a multi-line selection sorts the lines alphabetically ascending/descending.
* Shifting a single-line selection, that is a comma- or pipe-separated list, sorts the items ascending/descending.
* Shifting a single-line selection, that is XML attribute-value pairs, sorts them alphabetically.
* Shifting a single-line selection, that is a tupel, flips the items' order (delimiters: ":", "|", ", ", " - ", " + ", " < ", " > ", " <= ", " >= ", " == ", " != ", " === ", " !== ", " || ")
* Shifting a selection that is a camelCased (w/ lower or upper lead character) word pair, flips the order of the words.
* Shifting a selected AND && or OR || logical conjunction with two operands, swaps the operands' order
* Shifting a selected (from questionmark on) ternary expression, swaps "then" and "else" statements
* Shifting a selected PHP concatenation from two strings / variables, toggles the concatenated items' order
* Shifting a selection from a CSS file, sorts all attributes inside their selectors (alphabetically, vendor-attributes and vendor-styles at the end)</li>
* Shifting selected attribute-style lines inside a CSS file, sorts them (alphabetically, vendor-attributes and vendor-styles at the end)</li>

#### Numeric Shifting
* Numeric values - Incrementing/decrementing numbers
* Strings ending with numbers - increments/decrements the postfix
* Numeric block selection: opens dialog to choose: 1. in/decrement each or: 2. replace by enumeration
* UNIX (and millisecond based) timestamps - Increments/decrements by one day, shows a balloon info with the shifted date in human-readable format
* CSS hex RGB colors - Shifts color value lighter/darker
* CSS length values - Shifts numeric length values up/down by 1 (units: em, in, px, pt, cm, rem, vw, vh, vmin, vmax)
* Increment/decrement roman numerals

#### String Manipulations
* Shifting a selection within a single line: detects and swaps characters: single quote vs. double quote OR slash vs. backslash
* Shifting a selected (or the caret touching a) camel-cased string, converts it into a minus- or underscore-separated path (and vice versa)
* Strings wrapped in single/double quotes and backticks - Shifts to alphabetically next/previous quoted string found in current document
* Parenthesis: Strings surrounded by round, square or curly brackets: toggles surrounding "(" and ")" to "[" and "]" to "{" and "}"
* Logical operators: toggles between (selected) "&&" and "||"
* HTML en/decoding of selected special characters
* Single characters and string consisting from any amount of the same character can be shifted to the previous/next ASCII value
* Escaped single or double quotes can be unescaped when shifting a selection

#### Code- and DOC Comments
* Selection which is a block comment or multiple line-comments (e.g. PHP, JavaScript, C syntax) - toggle among comment type (//... vs. /*...*/). Merges multi-line comments into 1 line.
* Selected line of code ending with a trailing //-comment - Moves the comment into a new line before the code
* Selected HTML comment inside a PHP / PHTML file: converts it into a PHP block comment (and vice versa)
* PHPDoc: Shifting a selected PHP doc comment block that contains @param comments w/ variable name but no data type, guesses and inserts data types
* PHPDoc: Shifting while the caret is inside an @param annotation that contains no data type, inserts a data type (guessed from the variable name)
* JsDOC: Shifting w/ the caret touching a data type that is not wrapped in curly brackets, inside an "@param", "@returns" or "@type" annotation: adds the missing curly brackets
* Selected JsDoc block: Convert "@return" into "@returns", add missing curly brackets around data types in "@param", "@returns" and "@type" lines, correct invalid data types into existing data types (bool => boolean, int(eger) => number), reduce empty JsDoc lines

#### PHP, JavaScript and TypeScript shifting
* PHP variables - Shifts to alphabetically next/previous variable found in current document ("shift more" shifts until first variable with a different first letter)
* Selected PHP array - Shifts among long and shorthand syntax (PHP >= 5.4)
* Selection which is a Sizzle-Selector - Converts the selector into a corresponding JavaScript variable declaration
* Selection of multiple JavaScript (const, var or let) declarations in succession - are joined into multiple comma-separated declarations
* Selected deprecated jQuery observer methods are modernized, e.g. ".click(" becomes ".on('click', ": Event types: blur, change, click, dblclick, error, focus, keypress, keydown, keyup, load, mouseenter, mouseleave, resize, submit, scroll, unload
* Selected JavaScript string concatenation in TypeScript file - Converts to TypeScript string interpolation

#### Dictionaric Keyword Shifting

Shifter comes with a customizable, file extension specific and globally usable, dictionary of shiftable keywords.
Some keyword types from the default dictionary:

* PHP core magical constants - Shifts PHP's magic constants (__FILE__/__LINE__ etc.)
* CSS orientations and positioning: top/right/bottom/left and absolute/relative, and geographical directions (north/east/south/west)
* Accessibility types: public/private/protected
* MySql data types: Shifts to next bigger/smaller numeric/string type
* Primitive data types of Java, JavaScript, PHP, Objective-C
* Doc comment tags (Java, JavaScript, PHP)
* JavaScript event types (mouse, keyboard, frame/object, forml, control, touch)
* Named colors from the sRGB color space (white/lightgray/gray...)
* Text styles (bold, italic...)
* Web image (gif/jpg/png) and audio (au/aif/wav etc.) format extensions
* Full and abbreviated names of months and weekdays and units of time
* Logical operators (and/or/xor/not), arithmetic operations (addition/subtraction...) and arithmetic elements (sum/difference/product..)
* Metasyntax terms: foo/bar/baz...
* Names of common data collection types (list/set/bag/dictionary/tree...)
* Names of numbers (one/two/three etc.) and ordinal numbers (first/second/third  etc.)
* Boolean (paired) keyword values - Toggles true/false, on/off, yes/no (e.g. Objective-C), shown/hidden, show/hide, positive/negative, from/until, enable(d)/disable(d), pass/fail, min/max, expand/collapse, asc/desc, first/last


#### Tip: Using Mouse Wheel

To setup the mouse wheel to invoke shifting, open the IDE preferences and go to: "Keymap". Search for "Shift",
then right-click the shifter action items and use the option "Add mouse shortcut".
Mouse shortcuts can include hotkeys, this way for instance "Shift + Wheel Up" can be assigned to "Shift-Up",
"Shift + Wheel Down" to "Shift-Down".


#### Changelog

See https://github.com/kstenschke/shifter-plugin/blob/master/CHANGELOG.md


#### Contributors

Thanks for their input, ideas, help or other contribution

* Andriy Bazanov
* Eike Thies
* John Fearnside from Moz
* John Lindquist from Jetbrains
* Joshua David
* Nicolaj Schumacher
* Vojtěch Krása
* Srdjan Marković
* Steve Smith from NOLS
* Yann Cebron from Jetbrains


## License

Copyright 2011-2019 Kay Stenschke

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

"http://www.apache.org/licenses/LICENSE-2.0":http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
