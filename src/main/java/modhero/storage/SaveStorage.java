package modhero.storage;

import modhero.commands.AddCommand;
import modhero.common.util.DeserialisationUtil;
import modhero.common.util.SerialisationUtil;
import modhero.data.modules.Module;
import modhero.data.timetable.Timetable;
import modhero.exceptions.ModHeroException;

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

    private static final int EXPECTED_TIMETABLEDATA_ARGS = 3;

    private Map<String, Module> allModulesData;
    private List<String> exemptedModules;


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
     * @param allModulesData mapping of module codes to Module objects
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
        try {
            List<String> lines = loadFromTextFile();
            for (String line : lines) {
                List<String> moduleListAndExemptedList = parseToModuleListAndExemptedList(line);
                parseRawExemptedList(moduleListAndExemptedList.get(1));
                List<String> moduleList = parseRawModuleList(moduleListAndExemptedList.get(0));
                addAllModulesToTimetable(timetable, moduleList);
            }
        } catch (ModHeroException e) {
            logger.log(Level.WARNING, "Load Timetable Failed", e);
        }
    }

    /**
     * Saves the current state of the given Timetable to the text file.
     *
     * @param timetable the timeTable instance containing the data to be saved
     * @param exemptedModules the list of string containing the module code to be saved
     */
    public void save(Timetable timetable, List<String> exemptedModules) {
        StringBuilder timetableSaveFormat = new StringBuilder();
        for (int year = 0; year < NUM_YEARS; year++) {
            for (int term = 0; term < NUM_TERMS; term++) {
                for (Module module : timetable.getModules(year, term)) {
                    List<String> saveInformation = List.of(module.getCode(), String.valueOf(year + 1), String.valueOf(term + 1));
                    timetableSaveFormat.append(SerialisationUtil.serialiseList(saveInformation));
                }
            }
        }

        saveToTextFile(SerialisationUtil.serialiseMessage(timetableSaveFormat.toString())
                + SerialisationUtil.serialiseList(exemptedModules));
    }

    /**
     * Parses a line into timetable modules and exempted modules.
     *
     * @param line raw serialized line from the save file
     * @return a list containing two serialized strings (exempted list, module list)
     * @throws ModHeroException if data format is corrupted or invalid
     */
    private List<String> parseToModuleListAndExemptedList(String line) throws ModHeroException {
        List<String> moduleListAndExemptedList = DeserialisationUtil.deserialiseMessage(line);
        if (moduleListAndExemptedList == null || moduleListAndExemptedList.size() != 2) {
            logger.log(Level.WARNING, "SaveStorage load moduleListAndExemptedList is null");
            throw new ModHeroException("Corrupted Save Data");
        }
        return moduleListAndExemptedList;
    }

    /**
     * Deserializes the exempted module list from its raw string form.
     *
     * @param rawExemptedList serialized exempted module list
     */
    private void parseRawExemptedList(String rawExemptedList) {
        List<String> exemptedList = DeserialisationUtil.deserialiseMessage(rawExemptedList);
        if (exemptedList == null) {
            logger.log(Level.WARNING, "SaveStorage load exemptedList is null");
            return;
        }
        exemptedModules.addAll(exemptedList);
    }

    /**
     * Deserializes the raw serialized list of module entries.
     *
     * @param rawModuleList serialized list of timetable module entries
     * @return deserialized list of module strings, or null if invalid
     */
    private List<String> parseRawModuleList(String rawModuleList) {
        List<String> moduleList = DeserialisationUtil.deserialiseMessage(rawModuleList);
        if (moduleList == null) {
            logger.log(Level.WARNING, "SaveStorage load moduleList is null");
        }
        return moduleList;
    }

    /**
     * Adds all deserialized modules to the timetable.
     *
     * @param moduleList list of serialized module strings
     * @throws ModHeroException if parsing or addition fails
     */
    private void addAllModulesToTimetable(Timetable timetable, List<String> moduleList) throws ModHeroException {
        for (String module : moduleList) {
            List<String> moduleArgs = parseModule(module);
            String moduleCode = moduleArgs.get(0);
            String moduleYear = moduleArgs.get(1);
            String moduleTerm = moduleArgs.get(2);
            List<Integer> yearAndTerm = parseTermAndYear(moduleYear, moduleTerm);
            int parsedYear = yearAndTerm.get(0);
            int parsedTerm = yearAndTerm.get(1);
            addToTimetable(timetable, moduleCode, parsedYear, parsedTerm);
        }
    }

    /**
     * Deserializes and validates a single module record.
     *
     * @param module serialized module string
     * @return list of arguments [code, year, term]
     * @throws ModHeroException if format is incorrect
     */
    private List<String> parseModule(String module) throws ModHeroException {
        List<String> moduleArgs = DeserialisationUtil.deserialiseMessage(module);
        if (moduleArgs == null || moduleArgs.size() != EXPECTED_TIMETABLEDATA_ARGS) {
            logger.log(Level.WARNING, "SaveStorage load moduleArgs does not have the right number of argument");
            throw new ModHeroException("Module " + module + " does not have the right number of argument");
        }
        return moduleArgs;
    }

    /**
     * Parses numeric year and term values from strings.
     *
     * @param yearString raw year value
     * @param termString raw term value
     * @return list containing [year, term] as integers
     * @throws ModHeroException if parsing fails
     */
    private List<Integer> parseTermAndYear(String yearString, String termString) throws ModHeroException {
        int yearInteger = parseInteger(yearString);
        int termInteger = parseInteger(termString);
        if (yearInteger == -1 && termInteger == -1) {
            logger.log(Level.WARNING, "SaveStorage load parseTermAndYear failed");
            throw new ModHeroException(yearString + " or " + termString + " is not an integer");
        }
        return List.of(yearInteger, termInteger);
    }

    /**
     * Adds a deserialized module to the current timetable.
     *
     * @param moduleCode module code identifier
     * @param year academic year index (1-based)
     * @param term academic term index (1-based)
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
