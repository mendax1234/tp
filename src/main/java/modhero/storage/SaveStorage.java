package modhero.storage;

import modhero.commands.AddCommand;
import modhero.common.Constants;
import modhero.data.modules.Module;
import modhero.data.nusmods.ModuleRetriever;
import modhero.data.timetable.Timetable;
import modhero.exceptions.ModHeroException;
import modhero.exceptions.ParseIntegerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static modhero.common.Constants.AcademicConstants.NUM_TERMS;
import static modhero.common.Constants.AcademicConstants.NUM_YEARS;

/**
 * Handles the storage and retrieval of Timetable data to and from text files.
 */
public class SaveStorage extends Storage {
    private static final Logger logger = Logger.getLogger(SaveStorage.class.getName());

    public static final String TIMETABLE_STARTLINE = "Timetable data";
    public static final String EXEMPTED_MODULES_STARTLINE = "Exempted Modules data";

    public static final char DELIMITER = '|';
    private static final int EXEMPTED_MODULES_DELIMITER_COUNT = 0;
    private static final int TIMETABLE_DELIMITER_COUNT = 2;


    private Map<String, Module> allModulesData;
    private List<String> exemptedModules;

    private boolean isTimetableSection = false;
    private boolean isExemptedModulesSection = false;


    /**
     * Constructs a SaveStorage with the specified file path.
     * Calls the superclass constructor to initialize the file path used for loading or saving timetable data.
     *
     * @param filePath the path to the module data file to be loaded
     */
    public SaveStorage(String filePath) {
        super(filePath);
        this.allModulesData = new HashMap<>();
        this.exemptedModules = new ArrayList<>();
    }

    /**
     * Sets the timetable data sources to be used for loading operations.
     *
     * @param allModulesData  mapping of module codes to Module objects
     * @param exemptedModules list of module codes representing exempted modules
     */
    public void setLoadData(Map<String, Module> allModulesData, List<String> exemptedModules) {
        assert allModulesData != null && exemptedModules != null : "SaveStorage setData arguments contain null and cannot be initialised";
        this.allModulesData = allModulesData;
        this.exemptedModules = exemptedModules;
    }

    /**
     * Loads timetable data from the text file and populates the provided Timetable object.
     *
     * @param timetable the timeTable instance to load the data to
     */
    public void load(Timetable timetable) {
        List<String> timetableLines = new ArrayList<>();
        List<String> exemptedModulesLines = new ArrayList<>();
        separateIntoTimetableAndExemptedModulesSection(timetableLines, exemptedModulesLines);
        loadExemptedModules(exemptedModulesLines);
        loadTimetable(timetable, timetableLines);
        logger.info("Timetable and exempted modules loaded successfully.");
    }

    /**
     * Saves the current state of the given Timetable to the text file.
     *
     * @param timetable       the timeTable instance containing the data to be saved
     * @param exemptedModules the list of string containing the module code to be saved
     */
    public void save(Timetable timetable, List<String> exemptedModules) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(TIMETABLE_STARTLINE).append("\n");
        for (int year = 0; year < NUM_YEARS; year++) {
            for (int term = 0; term < NUM_TERMS; term++) {
                for (Module module : timetable.getModules(year, term)) {
                    stringBuilder.append(module.getCode())
                            .append("|").append(year + 1)
                            .append("|").append(term + 1)
                            .append("\n");
                }
            }
        }

        stringBuilder.append(EXEMPTED_MODULES_STARTLINE).append(System.lineSeparator());
        for (String exempted : exemptedModules) {
            stringBuilder.append(exempted).append(System.lineSeparator());
        }

        saveToTextFile(stringBuilder.toString());
    }

    /**
     * Loads timetable entries from raw text lines into the provided Timetable instance.
     *
     * @param timetable the timetable instance to populate
     * @param timetableLines list of raw lines representing timetable entries
     */
    private void loadTimetable(Timetable timetable, List<String> timetableLines) {
        timetable.clearTimetable();
        for (String line : timetableLines) {
            try {
                List<String> moduleArgs = parseModule(line);
                String moduleCode = moduleArgs.get(0);
                String moduleYear = moduleArgs.get(1);
                String moduleTerm = moduleArgs.get(2);
                List<Integer> yearAndTerm = parseYearAndTerm(moduleYear, moduleTerm);
                int parsedYear = yearAndTerm.get(0);
                int parsedTerm = yearAndTerm.get(1);
                addToTimetable(timetable, moduleCode, parsedYear, parsedTerm);
            } catch (ModHeroException e) {
                logger.log(Level.WARNING, "Unable to load this line: " + line);
            }
        }
        logger.log(Level.INFO, "Exempted modules loaded successfully.");
    }

    /**
     * Splits a timetable entry line into its components — module code, year, and term.
     *
     * @param line a single line representing a module in the timetable section
     * @return a list of strings [moduleCode, year, term]
     */
    private List<String> parseModule(String line) {
        String[] moduleCodeYearTerm = line.split("\\" + DELIMITER,3);
        return List.of(moduleCodeYearTerm);
    }

    /**
     * Loads and validates exempted module codes from the provided list of lines.
     *
     * @param exemptedModulesLines list of raw lines containing exempted module codes
     */
    private void loadExemptedModules(List<String> exemptedModulesLines) {
        exemptedModules.clear();
        ModuleRetriever moduleRetriever = new ModuleRetriever();
        for (String line : exemptedModulesLines) {
            Module module = moduleRetriever.getModule(Constants.AcademicConstants.ACAD_YEAR, line);
            if (module == null) {
                logger.log(Level.WARNING, "loadExemptedModules does not recognise module: " + line);
                continue;
            }
            exemptedModules.add(module.getCode());
        }
        logger.log(Level.INFO, "Exempted modules loaded successfully.");
    }

    /**
     * Separates raw file lines into timetable and exempted module sections.
     *
     * @param timetableLines the list to store lines belonging to the timetable section
     * @param exemptedModulesLines the list to store lines belonging to the exempted modules section
     */
    private void separateIntoTimetableAndExemptedModulesSection(List<String> timetableLines, List<String> exemptedModulesLines) {
        List<String> lines = loadFromTextFile();
        for (String line : lines) {
            if (isTimetableHeaderLine(line) || isExemptedModulesHeaderLine(line) || !isLineValid(line)) {
                continue;
            } else if (isTimetableSection) {
                timetableLines.add(line.trim());
            } else if (isExemptedModulesSection) {
                exemptedModulesLines.add(line.trim());
            }
        }
        logger.log(Level.INFO, "Timetable and exempted modules separated successfully.");
    }

    /**
     * Checks if the given line marks the start of the "Timetable data" section.
     * Updates internal section flags accordingly.
     *
     * @param line a line of text from the file
     * @return true if the line marks the start of the timetable section; otherwise false
     */
    private boolean isTimetableHeaderLine(String line) {
        if (line.equals(TIMETABLE_STARTLINE)) {
            isTimetableSection = true;
            isExemptedModulesSection = false;
            return true;
        }
        return false;
    }

    /**
     * Checks if the given line marks the start of the "Exempted Modules data" section.
     * Updates internal section flags accordingly.
     *
     * @param line a line of text from the file
     * @return true if the line marks the start of the exempted modules section; otherwise false
     */
    private boolean isExemptedModulesHeaderLine(String line) {
        if (line.equals(EXEMPTED_MODULES_STARTLINE)) {
            isTimetableSection = false;
            isExemptedModulesSection = true;
            return true;
        }
        return false;
    }

    /**
     * Determines whether a line is valid for processing based on delimiter count.
     *
     * @param line the line to evaluate
     * @return true if the line contains a valid delimiter count; otherwise false
     */
    private boolean isLineValid(String line) {
        int delimiterCount = countDelimiter(line, DELIMITER);
        return ((delimiterCount == EXEMPTED_MODULES_DELIMITER_COUNT) && isExemptedModulesSection)
                || ((delimiterCount == TIMETABLE_DELIMITER_COUNT) && isTimetableSection);
    }

    /**
     * Counts the occurrences of a given delimiter in a text string.
     *
     * @param text the string to search
     * @param symbol the delimiter character
     * @return the number of times the symbol appears in the text
     */
    public int countDelimiter(String text, char symbol) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == symbol) {
                count++;
            }
        }
        return count;
    }

    /**
     * Parses numeric year and term values from strings.
     *
     * @param yearString raw year value
     * @param termString raw term value
     * @return list containing [year, term] as integers
     * @throws ParseIntegerException if parsing fails
     */
    private List<Integer> parseYearAndTerm(String yearString, String termString) throws ParseIntegerException {
        int yearInteger = parseInteger(yearString);
        int termInteger = parseInteger(termString);
        if (yearInteger == -1 && termInteger == -1) {
            logger.log(Level.WARNING, "SaveStorage load parseTermAndYear failed");
            throw new ParseIntegerException(yearString + " or " + termString + " is not an integer");
        }
        return List.of(yearInteger, termInteger);
    }

    /**
     * Adds a deserialized module to the current timetable.
     *
     * @param moduleCode module code identifier
     * @param year       academic year index (1-based)
     * @param term       academic term index (1-based)
     * @throws ModHeroException if addition fails
     */
    private void addToTimetable(Timetable timetable, String moduleCode, int year, int term) throws ModHeroException {
        AddCommand.addModule(timetable, allModulesData, moduleCode, year, term, exemptedModules);
    }

    /**
     * Safely parses a string into an integer.
     *
     * @param rawText the raw string containing a numeric value
     * @return the parsed integer value, or -1 if parsing fails
     */
    private Integer parseInteger(String rawText) {
        try {
            return Integer.parseInt(rawText);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
