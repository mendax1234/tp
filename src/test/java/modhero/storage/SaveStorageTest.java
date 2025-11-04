package modhero.storage;

import modhero.common.Constants;
import modhero.data.modules.Module;
import modhero.data.modules.Prerequisites;
import modhero.data.timetable.Timetable;
import modhero.exceptions.CorruptedDataFileException;
import modhero.exceptions.ModHeroException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SaveStorageTest {

    private SaveStorage saveStorage;
    private Map<String, Module> allModulesData;
    private List<String> exemptedModules;
    private Timetable timetable;

    @BeforeEach
    void setUp() throws CorruptedDataFileException {
        timetable = new Timetable();
        allModulesData = new HashMap<>();
        ModuleStorage moduleStorage = new ModuleStorage(Constants.FilePathConstants.MODULES_FILE_PATH);
        moduleStorage.load(allModulesData);
        exemptedModules = new ArrayList<>();
        saveStorage = new SaveStorage(Constants.FilePathConstants.TIMETABLE_FILE_PATH);
        saveStorage.setLoadData(allModulesData, exemptedModules);
    }

    @Test
    void saveAndLoadSimpleTimeTable() {
        timetable.addModuleDirect(0,0, new Module("CS1010","Programming",4,"core","", new Prerequisites()));
        timetable.addModuleDirect(0,1, new Module("MA1301","Math",4,"core","", new Prerequisites()));
        timetable.addModuleDirect(3,1, new Module("CS2040C","Programming",4,"core","", new Prerequisites()));
        saveStorage.save(timetable,exemptedModules);
        Timetable newTimetable = new Timetable();
        saveStorage.load(newTimetable);
        assertEquals(timetable.getAllModules().size(), newTimetable.getAllModules().size());
    }

    @Test
    void saveAndLoadExemptedModules() {
        List<String> saveExemptedModules = new ArrayList<>();
        saveExemptedModules.add("MA1301");
        saveExemptedModules.add("PC1201");
        saveStorage.save(timetable,saveExemptedModules);
        Timetable newTimetable = new Timetable();
        saveStorage.load(newTimetable);
        assertEquals(saveExemptedModules, exemptedModules);
    }

    @Test
    void saveAndLoadInvalidNumberOfDelimiters() {
        saveStorage.saveToTextFile(SaveStorage.TIMETABLE_STARTLINE + "\nCS1010|1|1|1");
        Timetable newTimetable = new Timetable();
        saveStorage.load(newTimetable);
        assertEquals(timetable.getAllModules().size(), newTimetable.getAllModules().size());
    }

    @Test
    void saveAndLoadInvalidLine() {
        saveStorage.saveToTextFile(SaveStorage.TIMETABLE_STARTLINE + "\nXXXXX||");
        Timetable newTimetable = new Timetable();
        saveStorage.load(newTimetable);
        assertEquals(timetable.getAllModules().size(), newTimetable.getAllModules().size());
    }
}
