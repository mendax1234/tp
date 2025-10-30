package modhero.storage;

import modhero.commands.AddCommand;
import modhero.common.util.DeserialisationUtil;
import modhero.common.util.SerialisationUtil;
import modhero.data.modules.Module;
import modhero.data.timetable.Timetable;
import modhero.exceptions.ModHeroException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static modhero.common.Constants.AcademicConstants.NUM_TERMS;
import static modhero.common.Constants.AcademicConstants.NUM_YEARS;

/**
 * Handles the storage and retrieval of Timetable data to and from text files.
 */
public class TimetableStorage extends Storage{
    private static final Logger logger = Logger.getLogger(TimetableStorage.class.getName());

    private static final int EXPECTED_TIMETABLEDATA_ARGS = 3;


    /**
     * Constructs a TimetableStorage with the specified file path.
     * Calls the superclass constructor to initialize the file path used for loadingn or saving timetable data.
     *
     * @param filePath the path to the module data file to be loaded
     */
    public TimetableStorage(String filePath) {
        super(filePath);
    }

    /**
     * Loads timetable data from the text file and populates the provided Timetable object.
     *
     * @param timetable the Timetable object to populate with loaded data
     * @param allModulesData a mapping of module codes to Module objects, used to validate and reconstruct entries
     */
    public void load(Timetable timetable, Map<String, Module> allModulesData, List<String> exemptedModules) {
        List<String> lines = loadFromTextFile();
        for (String line : lines) {
            List<String> moduleListAndExemptedList = DeserialisationUtil.deserialiseMessage(line);
            if (moduleListAndExemptedList == null) {
                logger.log(Level.WARNING, "TimetableStorage load moduleListAndExemptedList is null");
                break;
            } else if (moduleListAndExemptedList.size() != 2) {
                logger.log(Level.WARNING, "TimetableStorage load moduleListAndExemptedList does not contains the right amount of arguments");
                break;
            }

            List<String> exemptedList = DeserialisationUtil.deserialiseMessage(moduleListAndExemptedList.get(1));
            if (exemptedList == null) {
                logger.log(Level.WARNING, "TimetableStorage load exemptedList is null");
                break;
            }
            exemptedModules.addAll(exemptedList);

            List<String> moduleList = DeserialisationUtil.deserialiseMessage(moduleListAndExemptedList.get(0));
            if (moduleList == null) {
                logger.log(Level.WARNING, "TimetableStorage load moduleList is null");
                break;
            }

            for (String module : moduleList) {
                List<String> moduleArgs = DeserialisationUtil.deserialiseMessage(module);
                if (moduleArgs == null || moduleArgs.size() != EXPECTED_TIMETABLEDATA_ARGS) {
                    logger.log(Level.WARNING, "TimetableStorage load moduleArgs does not have the right number of argument");
                    break;
                }

                int parseYear = parseInteger(moduleArgs.get(1));
                int parseTerm = parseInteger(moduleArgs.get(2));
                if (parseYear == -1 && parseTerm == -1) {
                    logger.log(Level.WARNING, "TimetableStorage load moduleArgs parse failed");
                    break;
                }
                try {
                    AddCommand.addModule(timetable, allModulesData, moduleArgs.get(0), parseYear, parseTerm, exemptedList);
                } catch (ModHeroException e) {
                    logger.log(Level.WARNING, "Load Timetable Failed",e);
                }
            }
        }
    }

    /**
     * Saves the current state of the given Timetable to the text file.
     *
     * @param timetable the Timetable object containing the data to be saved
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
