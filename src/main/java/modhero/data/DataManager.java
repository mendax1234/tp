package modhero.data;

import modhero.exceptions.CorruptedDataFileException;
import modhero.data.major.Major;
import modhero.data.modules.Module;
import modhero.storage.MajorLoader;
import modhero.storage.ModuleLoader;
import modhero.data.timetable.Timetable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages all application data including modules, majors, and timetable.
 */
public class DataManager {
    private static final Logger logger = Logger.getLogger(DataManager.class.getName());

    private final Timetable timetable;
    private final Map<String, Module> allModulesData;
    private final Map<String, Major> allMajorsData;

    /**
     * Creates a new DataManager with specified storage paths.
     *
     * @param modulesPath Path to module data storage.
     * @param majorPath Path to major data storage.
     */
    public DataManager(String modulesPath, String majorPath) {
        this.timetable = new Timetable();
        this.allModulesData = new HashMap<>();
        this.allMajorsData = new HashMap<>();
        initializeData(modulesPath, majorPath);
    }

    /**
     * Loads all data from storage files.
     */
    private void initializeData(String modulesPath, String majorPath) {
        try {
            ModuleLoader moduleLoader = new ModuleLoader(modulesPath);
            MajorLoader majorLoader = new MajorLoader(majorPath);
            moduleLoader.load(allModulesData);
            majorLoader.load(allModulesData, allMajorsData);
            logger.log(Level.INFO, "Data loaded successfully");
        } catch (CorruptedDataFileException e) {
            logger.log(Level.SEVERE, "Data file is corrupted", e);
        }
    }

    // Getters
    public Timetable getTimetable() {
        return timetable;
    }

    public Map<String, Module> getAllModulesData() {
        return allModulesData;
    }

    public Map<String, Major> getAllMajorsData() {
        return allMajorsData;
    }
}
