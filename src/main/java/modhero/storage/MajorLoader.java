package modhero.storage;

import modhero.common.exceptions.CorruptedDataFileException;
import modhero.common.util.Deserialiser;
import modhero.data.major.Major;
import modhero.data.modules.Module;
import modhero.data.modules.ModuleList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads major data from persistent storage into memory.
 * Now loads core modules AND their recommended schedules.
 */
public class MajorLoader {
    private static final Logger logger = Logger.getLogger(MajorLoader.class.getName());
    private static final int EXPECTED_MAJOR_ARGS = 3;
    private static final int EXPECTED_SPEC_ARGS = 3; // code, year, sem

    private final Storage storage;

    /**
     * Creates a MajorLoader with specified storage.
     *
     * @param storage the storage instance to use for file operations
     */
    public MajorLoader(Storage storage) {
        assert storage != null : "Storage must not be null";
        this.storage = storage;
    }

    /**
     * Loads all majors from storage and populates the provided map.
     *
     * @param allModulesData existing modules map for module lookup
     * @param allMajorsData map to populate; indexed by both abbreviation and name
     * @throws CorruptedDataFileException if critical data corruption detected
     */
    public void loadAllMajorsData(Map<String, Module> allModulesData, Map<String, Major> allMajorsData)
            throws CorruptedDataFileException {
        assert allModulesData != null : "loadAllMajorsData allModulesData must not be null";
        assert allMajorsData != null : "loadAllMajorsData allMajorsData must not be null";
        logger.log(Level.FINEST, "Loading all major data");

        List<String> rawMajorsList = storage.load();
        List<List<String>> allMajorsList = Deserialiser.deserialiseList(rawMajorsList);

        for (List<String> majorArgs : allMajorsList) {
            if (majorArgs.size() != EXPECTED_MAJOR_ARGS) {
                logger.log(Level.WARNING, "Incorrect number of arguments for major: " + majorArgs.size());
                break;
            }
            try {
                String abbr = majorArgs.get(0);
                String name = majorArgs.get(1);
                String serialisedSpecs = majorArgs.get(2);

                // 1. Deserialise the outer-most layer
                // e.g., "245#...|" -> ["21#...||..."]
                List<String> outerList = Deserialiser.deserialiseMessage(serialisedSpecs);
                if (outerList == null || outerList.isEmpty()) {
                    throw new CorruptedDataFileException();
                }

                // [FIXED] 2. Deserialise the second layer to get the list
                // This was the missing step.
                // e.g., "21#...||..." -> ["17#...||", "17#...||"]
                String concatenatedSpecs = outerList.get(0);
                List<String> serialisedSpecifiers = Deserialiser.deserialiseMessage(concatenatedSpecs);
                if (serialisedSpecifiers == null) {
                    throw new CorruptedDataFileException();
                }

                Map<String, int[]> moduleSchedule = new HashMap<>();
                ModuleList coreModules = createModuleListAndSchedule(allModulesData, serialisedSpecifiers, moduleSchedule);

                Major major = new Major(abbr, name, coreModules, moduleSchedule);

                allMajorsData.put(major.getAbbrName(), major);
                allMajorsData.put(major.getName(), major);
                logger.log(Level.FINEST, "Added major into database: " + major.getAbbrName());

            } catch (CorruptedDataFileException e) {
                logger.log(Level.WARNING, "Corrupted data encountered for major: " + majorArgs.get(0), e);
                throw e; // Propagate critical error
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to parse major: " + majorArgs.get(0), e);
            }
        }
    }

    /**
     * Return ModuleList for major object.
     *
     * @param allModulesData hashmap to get modules object from
     * @param moduleCodes modules code in a list of string
     * @return ModuleList
     */
    private ModuleList createModuleListAndSchedule(Map<String, Module> allModulesData,
                                                   List<String> serialisedSpecifiers,
                                                   Map<String, int[]> outSchedule)
            throws CorruptedDataFileException {
        ModuleList moduleList = new ModuleList();

        // 3. Now, 'spec' is "17#7#CS...|1#1|1#1||"
        for (String spec : serialisedSpecifiers) {
            // 4. Deserialise the inner specifier string
            // e.g., ["7#CS...", "1", "1"]
            List<String> parts = Deserialiser.deserialiseMessage(spec);

            if (parts == null || parts.size() != EXPECTED_SPEC_ARGS) {
                logger.log(Level.WARNING, "Corrupted module specifier in major file: " + spec);
                continue;
            }

            try {
                String code = parts.get(0);
                int year = Integer.parseInt(parts.get(1));
                int sem = Integer.parseInt(parts.get(2));

                Module module = allModulesData.get(code);
                if (module != null) {
                    moduleList.add(module);
                } else {
                    logger.log(Level.WARNING, "Missing module for major: " + code);
                }
                outSchedule.put(code, new int[]{year, sem});
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Corrupted year/sem in major file: " + spec, e);
            }
        }
        return moduleList;
    }
}
