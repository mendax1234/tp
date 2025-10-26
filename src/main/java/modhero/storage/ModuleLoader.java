package modhero.storage;

import modhero.exceptions.CorruptedDataFileException;
import modhero.common.util.DeserialisationUtil;
import modhero.data.modules.Module;
import modhero.data.modules.Prerequisites;
import modhero.exceptions.ParsePrerequisitesException;

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
        List<List<String>> allModulesList = DeserialisationUtil.deserialiseList(rawModulesList);

        for (List<String> moduleArgs : allModulesList) {
            if (moduleArgs.size() != EXPECTED_MODULE_ARGS) {
                logger.log(Level.WARNING, "Incorrect number of arguments for module: " + moduleArgs.size());
                continue; // Changed from break to continue - skip this module and process others
            }
            try {
                Module module = parseModule(moduleArgs);
                addModuleToMap(allModulesData, module);
                logger.log(Level.FINEST, "Added module into database: " + module.getCode());
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Unable to parse module credit: " + moduleArgs.get(2));
            } catch (ParsePrerequisitesException e) {
                logger.log(Level.WARNING, "Unable to parse prerequisites for module: " + moduleArgs.get(0));
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

        // Handle empty prerequisites (no prerequisites required)
        if (serialisedPrereqs.isEmpty()) {
            return new Prerequisites(List.of()); // Empty list for no prerequisites
        }

        // First deserialization: unwrap the outer layer
        List<String> deserialisedPrereqs = DeserialisationUtil.deserialiseMessage(serialisedPrereqs);
        if (deserialisedPrereqs == null) {
            logger.log(Level.WARNING, "Unable to deserialize prerequisites (null result): " + serialisedPrereqs);
            throw new ParsePrerequisitesException();
        }

        // Second deserialization: convert to list of lists
        List<List<String>> prereqList = DeserialisationUtil.deserialiseList(deserialisedPrereqs);
        if (prereqList == null) {
            logger.log(Level.WARNING, "Unable to deserialize prerequisite list (null result): " + serialisedPrereqs);
            throw new ParsePrerequisitesException();
        }

        return new Prerequisites(prereqList);
    }

    /**
     * Adds a single Module to the provided map, using both code and name as keys.
     *
     * @param map the module map to populate
     * @param module the Module instance to insert
     */
    private void addModuleToMap(Map<String, Module> map, Module module) {
        map.put(module.getCode(), module);
        map.put(module.getName(), module);
    }
}
