# Serialization Format Documentation

## Overview

The system uses a **delimiter-based serialization** format where data is wrapped with length prefixes. The format is: `LENGTH#VALUE`, where `LENGTH` is the number of characters in `VALUE`.

### Basic Serialization Rules

- **Single value**: `5#hello` means a string of length 5 containing "hello"
- **Nested values**: Values can contain other serialized values
- **Delimiter**: The `#` character separates length from value
- **Concatenation**: Multiple serialized values are concatenated directly

---

## Module File Format (`modules.txt`)

Each line represents one module with **5 fields** (all serialized):

```
CODE|NAME|CREDITS|DESCRIPTION|PREREQUISITES
```

### Field Structure

1. **Module Code** - `serialiseMessage(code)`
2. **Module Name** - `serialiseMessage(name)`
3. **Credits** - `serialiseMessage(credits)`
4. **Description** - `serialiseMessage(description)`
5. **Prerequisites** - `serialiseMessage(toFormatedString())`

### Example 1: Simple Module (No Prerequisites)

```
7#CS1010S|23#Programming Methodology|1#4|4#core|3#0#||
```

**Breaking it down:**

|Field|Serialized|Decoded|
|---|---|---|
|Code|`7#CS1010S|`|
|Name|`23#Programming Methodology|`|
|Credits|`1#4|`|
|Description|`4#core|`|
|Prerequisites|`3#0#||

**Note:** Each field ends with `|` because that's how `serialiseMessage()` works. Fields are just concatenated together.

**Prerequisites breakdown:**

- Layer 3: `3#0#||` → unwraps to "0#|" (3 characters: '0', '#', '|')
- Layer 2: `0#|` → unwraps to "" (0 characters, empty string)
- Layer 1: Empty string → `[]` (no prerequisites)

**Why two bars at the end?**

- First `|` is from the inner serialization (Layer 2: "0#|")
- Second `|` is from the outer serialization (Layer 3: wraps "0#|")
- They accumulate!

---

### Example 2: Module with Prerequisites

```
7#CS1231S|19#Discrete Structures|1#4|4#core|30#26#10#7#MA1301X||9#6#MA1301||||
```

**Breaking it down:**

|Field|Serialized|Decoded|
|---|---|---|
|Code|`7#CS1231S|`|
|Name|`19#Discrete Structures|`|
|Credits|`1#4|`|
|Description|`4#core|`|
|Prerequisites|`30#26#10#7#MA1301X||

**Prerequisites breakdown (Triple Deserialization):**

**Layer 3 (Outer wrap):**

```
30#26#10#7#MA1301X||9#6#MA1301|||||
└┬┘└──────────value─────────────┘│
 │                                └ terminator bar
 └ 30 characters in the value
```

- Unwraps to: `26#10#7#MA1301X||9#6#MA1301||||` (30 chars)

**Layer 2 (Middle wrap):**

```
26#10#7#MA1301X||9#6#MA1301||||
└┬┘└────────value──────────┘│
 │                          └ terminator bar
 └ 26 characters in the value
```

- Unwraps to: `10#7#MA1301X||9#6#MA1301|||` (26 chars)

**Layer 1 (List of combinations):**

```
10#7#MA1301X||9#6#MA1301|||
└─combo1────┘└─combo2────┘

Combo 1: 10#7#MA1301X||
         └┬┘└──value─┘│
          │           └ bar from inner serialization
          └ 7#MA1301X|| is 10 chars
         
         Unwraps to: 7#MA1301X| (7 chars)
         Unwraps to: "MA1301X" → ["MA1301X"]

Combo 2: 9#6#MA1301||
         └┬└─value─┘│
          │         └ bar from inner serialization
          └ 6#MA1301| is 9 chars
         
         Unwraps to: 6#MA1301| (6 chars)
         Unwraps to: "MA1301" → ["MA1301"]
```

**Final Result:** `[["MA1301X"], ["MA1301"]]`

**Meaning:** CS1231S requires EITHER MA1301X OR MA1301

**Bar count explanation:**

- Combo 1 ends with `||` (double bar) because it's serialized twice
- Combo 2 ends with `|||` (triple bar) because... wait, this seems odd!
- Actually, combo 2 should also end with `||`
- The extra bars are just accumulated terminators from the outer layers

---

### Example 3: Complex Prerequisites (Multiple modules per combination)

```
7#CS2030S|26#Programming Methodology II|1#4|4#core|100#96#9#6#CS1010||10#7#CS1010E||10#7#CS1010X||10#7#CS1101S||10#7#CS1010S||10#7#CS1010J||10#7#CS1010A||||
```

**Prerequisites breakdown:**

**Layer 3:** `100#...` → 100 characters wrapped **Layer 2:** `96#...` → 96 characters wrapped  
**Layer 1:** Contains 6 combinations (each 9-10 chars):

- `9#6#CS1010||` → ["CS1010"]
- `10#7#CS1010E||` → ["CS1010E"]
- `10#7#CS1010X||` → ["CS1010X"]
- `10#7#CS1101S||` → ["CS1101S"]
- `10#7#CS1010S||` → ["CS1010S"]
- `10#7#CS1010J||` → ["CS1010J"]
- `10#7#CS1010A||` → ["CS1010A"]

**Final Result:** `[["CS1010"], ["CS1010E"], ["CS1010X"], ["CS1101S"], ["CS1010S"], ["CS1010J"], ["CS1010A"]]`

**Meaning:** CS2030S requires ANY ONE OF the 6 listed modules (OR relationship)

---

### Example 4: AND Prerequisites

```
7#CS2040S|30#Data Structures and Algorithms|1#4|4#core|659#654#19#6#CS1010|7#CS1231S||...
```

Looking at one combination:

```
19#6#CS1010|7#CS1231S||
```

- Combination length: 19 characters
- Value: `6#CS1010|7#CS1231S|`
- Contains: ["CS1010", "CS1231S"]

**Meaning:** This represents: CS1010 AND CS1231S (both required together)

**Full CS2040S Prerequisites:** Multiple combinations where each has 2 modules means:

- (CS1010 AND CS1231S) OR
- (CS1010 AND CS1231) OR
- (CS1010 AND MA1100) OR
- ... (many other valid combinations)

---

## Major File Format (`major.txt`)

Each line represents one major with **3 fields**:

```
NAME|ABBREVIATION|MODULES_BLOB
```

### Example: Computer Science Major

```
2#CS|16#Computer Science|221#216#18#7#CS1010S|1#1|1#1||18#7#CS1231S|1#1|1#1||...
```

**Breaking it down:**

|Field|Serialized|Decoded|
|---|---|---|
|Abbreviation|`2#CS`|"CS"|
|Name|`16#Computer Science`|"Computer Science"|
|Modules|`221#216#...`|See below|

**Modules breakdown (Triple Deserialization):**

**Layer 3:** `221#...` → 221 characters **Layer 2:** `216#...` → 216 characters  
**Layer 1:** List of module entries, each entry has format:

```
18#7#CS1010S|1#1|1#1||
```

**Each module entry contains 3 parts:**

```
7#CS1010S|1#1|1#1
```

1. **Module Code**: `7#CS1010S` → "CS1010S"
2. **Year**: `1#1` → "1" (Year 1)
3. **Semester**: `1#1` → "1" (Semester 1)

**Final structure for each module:**

- CS1010S: Year 1, Semester 1
- CS1231S: Year 1, Semester 1
- CS2030S: Year 1, Semester 2
- CS2040S: Year 1, Semester 2
- ... etc

---

## Serialization Methods

### `Serialiser.serialiseMessage(String message)`

Wraps a message with its length:

```java
Input: "hello"
Output: "5#hello|"
```

### `Prerequisites.toFormatedString()`

Creates **doubly-serialized** prerequisites:

```java
// Input: [["CS1010", "CS1231"], ["CS1010E"]]

// Layer 1: Serialize each code
"6#CS1010|" + "7#CS1231|" = "6#CS1010|7#CS1231|"

// Layer 2: Serialize each combination
"19#6#CS1010|7#CS1231||" + "10#7#CS1010E||"

// Output (doubly-serialized):
"19#6#CS1010|7#CS1231||10#7#CS1010E||"
```

### DataGenerator wraps once more

```java
String prereqBlob = prerequisites.toFormatedString();
// prereqBlob = "19#6#CS1010|7#CS1231||10#7#CS1010E||"

String final = Serialiser.serialiseMessage(prereqBlob);
// final = "31#19#6#CS1010|7#CS1231||10#7#CS1010E|||"
```

---

## Deserialization Flow

### ModuleLoader.parsePrerequisites()

**Triple deserialization** to unwrap prerequisites:

```java
// Input: "31#19#6#CS1010|7#CS1231||10#7#CS1010E|||"

// Layer 3 unwrap:
deserialiseMessage() → ["19#6#CS1010|7#CS1231||10#7#CS1010E||"]

// Layer 2 unwrap:
deserialiseMessage() → ["19#6#CS1010|7#CS1231||", "10#7#CS1010E||"]

// Layer 1 unwrap:
deserialiseList() → [["CS1010", "CS1231"], ["CS1010E"]]

// Result: Prerequisites object with two combinations
```

---

## Key Insights

### Why Triple Serialization?

1. **Layer 1** (innermost): Individual module codes need wrapping
2. **Layer 2** (middle): Combinations (AND groups) need wrapping
3. **Layer 3** (outermost): The entire prerequisite structure needs wrapping to be stored as a field

### Empty Prerequisites

Empty prerequisites are represented as:

```
3#0#||
```

- Layer 3: `3#0#||` → unwraps to "0#|"
- Layer 2: `0#|` → unwraps to ""
- Layer 1: "" → empty list `[]`

### Understanding the Vertical Bar (`|`) Pattern

The `|` character is **part of the serialization format** added by `Serialiser.serialiseMessage()`. It's a **terminator**, not a delimiter.

**Rule:** Every time you call `serialiseMessage(value)`, you get: `LENGTH#VALUE|`

The `|` is automatically added at the end of each wrapped value.

#### Why Multiple Bars?

When you serialize something that's already been serialized, the inner `|` becomes part of the value:

```
Step 1: serialiseMessage("CS1010")
Result: "6#CS1010|"

Step 2: serialiseMessage("6#CS1010|")
Result: "9#6#CS1010||"  ← Notice TWO bars now!
         └─length─┘└value┘└terminator

Step 3: serialiseMessage("9#6#CS1010||")
Result: "12#9#6#CS1010|||"  ← THREE bars!
```

#### Visual Breakdown

Let's trace `[["CS1010"]]` through serialization:

```
Original data: [["CS1010"]]

Layer 1 - Serialize the code:
"CS1010" → "6#CS1010|"
             ├─6 chars─┤│
                        └ terminator (bar #1)

Layer 2 - Serialize the combination:
"6#CS1010|" → "9#6#CS1010||"
               ├─9 chars──┤│
                           └ terminator (bar #2)

Layer 3 - Serialize the whole thing:
"9#6#CS1010||" → "12#9#6#CS1010|||"
                  ├─12 chars───┤│
                                └ terminator (bar #3)
```

#### Bar Count = Serialization Depth

- `VALUE|` = serialized once
- `VALUE||` = serialized twice (nested)
- `VALUE|||` = serialized three times (double-nested)

**The bars are NOT separators between items** - they're terminators that accumulate through nested serialization!

#### Between Fields: Actual Separators

The examples in the files might be misleading. Looking at the actual format:

```
7#CS1010S|23#Programming Methodology|1#4|4#core|3#0#||
└────────┘└──────────────────────────┘└──┘└────┘└────┘
  field1          field2            field3 field4 field5
```

**Between different fields**, the bars are just part of the serialized values ending, they're not special separators. The fields are just concatenated directly!