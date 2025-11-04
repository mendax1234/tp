# ModHero User Guide

_ModHero_ is a **CLI-first desktop app** for planning your university degree, designed to give you the speed and precision of command-line input while retaining the clarity of a graphical overview.  
It helps you build and adapt your 4-year course roadmap more efficiently than traditional spreadsheet or browser-based tools.

## Contents
- [Quick Start](#quick-start)
- [Features](#features)
    - [Viewing Help (`help`)](#viewing-help-help)
    - [Specifying Your Major (`major`)](#specifying-your-major-major)
    - [Adding a Module (`add`)](#adding-a-module-add)
    - [Deleting an Elective (`delete`)](#deleting-an-elective-delete)
    - [Generating a Recommended Schedule (`schedule`)](#generating-a-recommended-schedule-schedule)
    - [Clearing All Data (`clear`)](#clearing-all-data-clear)
    - [Exiting the Program (`exit`)](#exiting-the-program-exit)
- [FAQ](#faq)
- [Known Issues](#known-issues)
- [Command Summary](#command-summary)

---

## Quick Start
1. Ensure you have **Java 17 or above** installed.
    - **Mac users:** Use the exact JDK version [prescribed here](https://se-education.org/guides/tutorials/javaInstallationMac.html).
2. Download the latest `.jar` file from [GitHub Releases](https://github.com/se-edu/addressbook-level3/releases).
3. Copy the file to your desired home folder for ModHero.
4. Open a command terminal and navigate to that folder:
   ```bash
   cd path/to/folder
   java -jar modhero.jar
   ```
5. A CLI interface should appear after a few seconds, with some sample data preloaded.
6. Type a command in the **command box** and press **Enter** to execute.

   **Examples:**
   ```bash
   help
   major CEG
   schedule
   add CS2113 to Y1S1
   delete CS2113
   clear
   exit
   ```
7. Refer to the [Features](#features) section for full command details.

## Features
### Notes about command format
- Words in **UPPER_CASE** are parameters you must supply.  
  Example:
  ```
  add MODULE_CODE to YXSY
  -> add CS2113 to Y1S1
  ```
  
- Square brackets `[ ]` denote optional fields.  
  Example:
  ```
  major MAJOR_NAME 
  -> major CS
  ```

- Parameters followed by `...` can be repeated.  
  Example:
  ```
  delete MODULE_CODE...
  -> delete CS2109S CS3230 CS3219
  ```

- Parameters can be entered in any order unless specified.
- Extra parameters for commands that take no input (e.g., `help`, `exit`) are ignored.  
  Example:
  ```
  help 123 -> interpreted as help
  ```

### Viewing Help: `help`
Shows a message explaining how to access the help page.

**Format:**
```
help
```

### Specifying Your Major: `major`
Defines your primary degree major, which ModHero uses to load graduation requirements.
Currently, the supported majors are CEG and CS.

**Format:**
```
major MAJOR_NAME
```

**Examples:**
```
major ceg
major CEG
major cs
major CS
```

### Adding a Module: `add`
Adds a specific module (core, elective, or any valid NUS module) to a chosen year and semester in your degree plan.

> [!Note]
> This command is typically used for manually adjusting your study roadmap after setting your major.

**Format:**
```
add MODULE_CODE to YxSy
```

**Examples:**
```
add CS2109S to Y2S1
add MA1511 to Y1S1
add CS3230 to Y3S2
```
Details:
- `MODULE_CODE` must be a valid NUS module code (e.g., CS2109S, MA1511).
- `YxSy` specifies the academic year and semester (e.g., Y2S1 = Year 2 Semester 1).
- Year must be between Y1–Y4 and semester between S1–S2.
- ModHero automatically checks:
  - Whether the module exists (in your local database or via NUSMods API)
  - Whether you already have it in your timetable
  - Whether prerequisites are satisfied 
  - Whether the semester exceeds your degree’s valid range

If any of these checks fail, ModHero will display an error message explaining the issue.

> [!CAUTION]
> Currently, our modhero doesn't support check if the mod you add is mutually exclusive to any of the existing module.

### Deleting an Elective: `delete`
Removes one module from your plan.

#### Format:
Specify module you want to delete, letters can be both upper and lower case
```
delete MODULE_CODE
```

**Examples:**
```
delete CS2109S 
```
#### Expected output
1.  If the module code is valid and deleting that module does not affect any prerequisites, a successful delete message 
will be output: 
    ```
    CS1010 deleted successfully!
    ```

2.  If the module code is invalid, or the given module is not in the timetable, the following will be added to the output:
    ```
    This CS9999 cannot be found in the timetable
    ```

3.  If deleting the module would violate the prerequisites for other modules, the first affected module will be output. 
    ```
    Cannot delete CS1010 as it is a prerequisite for CS2040C
    ```

### Printing a Recommended Schedule: `schedule`
Generates a personalised 4-year study plan factoring in prerequisites, NUSMods availability, exchanges, and graduation requirements.

**Format:**
```
schedule
```

**Example:**
```
schedule
-> Returns the timetable in a ui-friendly format
```

### Clearing All Data: `clear`
Deletes all modules and resets your plan.

**Format:**
```
clear
```

### Exiting the Program: `exit`
Closes the program.

**Format:**
```
exit
```

### Saving the Data
All changes - adding electives, setting majors, generating schedules - are **saved automatically**.  
No manual saving is required.

### Loading and Editing Data Files
ModHero stores your degree plan in:

```
[JAR file location]/data/modhero.json
```

- Advanced users can manually edit this JSON file if needed.
- **Caution:** Invalid edits may cause ModHero to reset your plan. Always back up before editing.

## FAQ
**Q:** Can I use ModHero offline?  
**A:** ModHero requires internet access to retrieve elective data from NUSMods.

## Known Issues
- None reported.

## Command Summary

| Action       | Format / Examples                                                                                                                              |
|--------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| **Major**    | `major MAJOR_NAME `<br>_e.g._ `major Computer Science` |
| **Add**      | `add MODULE_CODE to YxSy`<br>_e.g._ `add CS2109S to Y1S1`                                                                                      |
| **Delete**   | `delete MODULE_CODE ...`<br>_e.g._ `delete CS2109S`                                                                                            |
| **Schedule** | `schedule`                                                                                                                                     |
| **Clear**    | `clear`                                                                                                                                        |a
| **Help**     | `help`                                                                                                                                         |

2025 ModHero Team - CS2113-T10-4  
Based on SE-EDU framework.
