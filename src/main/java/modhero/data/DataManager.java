package modhero.data;

import modhero.exceptions.CorruptedDataFileException;
import modhero.data.major.Major;
import modhero.data.modules.Module;
import modhero.storage.MajorStorage;
import modhero.storage.ModuleStorage;
import modhero.data.timetable.Timetable;
import modhero.storage.SaveStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private final List<String> exemptedModules;

    /**
     * Creates a new DataManager with specified storage paths.
     *
     * @param modulesPath Path to module data storage.
     * @param majorPath Path to major data storage.
     */
    public DataManager(String modulesPath, String majorPath, String timetablePath) {
        this.timetable = new Timetable();
        this.allModulesData = new HashMap<>();
        this.allMajorsData = new HashMap<>();
        this.exemptedModules = new ArrayList<>();
        initializeData(modulesPath, majorPath, timetablePath);
    }

    /**
     * Loads all data from storage files.
     */
    private void initializeData(String modulesPath, String majorPath, String timetablePath) {
        try {
            ModuleStorage moduleStorage = new ModuleStorage(modulesPath);
            MajorStorage majorStorage = new MajorStorage(majorPath);
            moduleStorage.load(allModulesData);
            majorStorage.load(allModulesData, allMajorsData);
            logger.log(Level.INFO, "Data loaded successfully");
            SaveStorage saveStorage = new SaveStorage(timetablePath);
            saveStorage.setLoadData(allModulesData, exemptedModules);
            saveStorage.load(timetable);
        } catch (CorruptedDataFileException e) {
            timetable.clearTimetable();
            exemptedModules.clear();
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

    public List<String> getExemptedModules() {
        return exemptedModules;
    }
}
