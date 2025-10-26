package modhero.storage;

import modhero.common.exceptions.CorruptedDataFileException;
import modhero.common.util.Deserialiser;
import modhero.data.modules.Module;
import modhero.data.modules.Prerequisites;
import modhero.storage.exceptions.ParsePrerequisitesException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads module data from persistent storage into memory.
 */
public class ModuleLoader {
    private static final Logger logger = Logger.getLogger(ModuleLoader.class.getName());
    private static final int EXPECTED_MODULE_ARGS = 5;

    private final Storage storage;

    /**
     * Creates a ModuleLoader with specified storage.
     *
     * @param storage the storage instance to use for file operations
     */
    public ModuleLoader(Storage storage) {
        assert storage != null : "Storage must not be null";
        this.storage = storage;
    }

    /**
     * Loads all modules from storage and populates the provided map.
     *
     * @param allModulesData map to populate; indexed by both code and name
     * @throws CorruptedDataFileException if critical data corruption detected
     */
    public void loadAllModulesData(Map<String, Module> allModulesData) throws CorruptedDataFileException {
        assert allModulesData != null : "loadAllModulesData allModulesData must not be null";
        logger.log(Level.FINEST, "Loading all modules data");

        List<String> rawModulesList = storage.load();
        List<List<String>> allModulesList = Deserialiser.deserialiseList(rawModulesList);

        for (List<String> moduleArgs : allModulesList) {
            if (moduleArgs.size() != EXPECTED_MODULE_ARGS) { // Corrected check
                logger.log(Level.WARNING, "Incorrect number of arguments for module: " + moduleArgs.size());
                continue;
            }
            try {
                Module module = parseModule(moduleArgs);
                addModuleToMap(allModulesData, module);
                logger.log(Level.FINEST, "Added module into database: " + module.getCode());
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Unable to parse module credit: " + moduleArgs.get(2));
            } catch (ParsePrerequisitesException | CorruptedDataFileException e) { // Handle both
                logger.log(Level.WARNING, "Unable to parse prerequisites for " + moduleArgs.get(0) + ": " + e.getMessage());
            }
        }
    }

    /**
     * Parses and constructs a Module object from the provided list of arguments.
     *
     * @param moduleArgs list of serialised module attributes
     * @return a fully constructed Module instance
     * @throws NumberFormatException if the credits value is not a valid integer
     * @throws CorruptedDataFileException if deserialisation of prerequisites fails
     * @throws ParsePrerequisitesException if prerequisites format is invalid
     */
    private Module parseModule(List<String> moduleArgs) throws NumberFormatException, CorruptedDataFileException, ParsePrerequisitesException{
        String code = moduleArgs.get(0);
        String name = moduleArgs.get(1);
        int credits = Integer.parseInt(moduleArgs.get(2));
        String description = moduleArgs.get(3);
        Prerequisites prerequisites = parsePrerequisites(moduleArgs.get(4));
        return new Module(code, name, credits, description, prerequisites);
    }

    /**
     * Deserialises and constructs a Prerequisites object from a serialised string.
     *
     * @param serialisedPrereqs string representation of prerequisites data
     * @return a constructed Prerequisites instance
     * @throws CorruptedDataFileException if deserialisation fails due to format corruption
     * @throws ParsePrerequisitesException if the serialised data cannot be parsed correctly
     */
    private Prerequisites parsePrerequisites(String serialisedPrereqs) throws CorruptedDataFileException, ParsePrerequisitesException {
        assert serialisedPrereqs != null : "parsePrerequisites serialisedPrereqs must not be null";

        // 1. Unwrap the outer layer (which was serialised by serialiseMessage)
        // e.g., "3#0#||" -> ["0#|"]
        // e.g., "245#...|" -> ["21#...||..."]
        List<String> outerList = Deserialiser.deserialiseMessage(serialisedPrereqs);
        if (outerList == null) {
            throw new CorruptedDataFileException();
        }

        // Handle the case of an empty prerequisite list
        if (outerList.isEmpty() || (outerList.size() == 1 && outerList.get(0).isEmpty())) {
            return new Prerequisites(new ArrayList<>());
        }

        // This is the concatenated list of actual prerequisite combinations
        // e.g., "21#17#...||||21#17#...||||"
        String concatenatedPrereqs = outerList.get(0);

        // 2. Unwrap the inner layer (which was serialised by serialiseList)
        // This gives us the list of individual prerequisite combinations
        // e.g., ["17#...||", "17#...||"]
        List<String> prereqCombinations = Deserialiser.deserialiseMessage(concatenatedPrereqs);
        if (prereqCombinations == null) {
            throw new CorruptedDataFileException();
        }

        // 3. Now, deserialise each combination string into a List<String>
        List<List<String>> prereqList = new ArrayList<>();
        for (String combo : prereqCombinations) {
            List<String> deserialisedCombo = Deserialiser.deserialiseMessage(combo);
            if (deserialisedCombo == null) {
                throw new CorruptedDataFileException();
            }
            prereqList.add(deserialisedCombo);
        }

        return new Prerequisites(prereqList);
    }

    private void addModuleToMap(Map<String, Module> map, Module module) {
        map.put(module.getCode(), module);
        map.put(module.getName(), module);
    }
}
