package modhero.storage;

import modhero.common.Constants;
import modhero.data.modules.Module;
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

    private static final String CS = "321#18#7#CS1101S|1#1|1#1||17#6#MA1522|1#1|1#1||18#7#CS1231S|1#1|1#1||17#6#ES2660|1#1|1#1||18#7#CS2030S|1#1|1#2||18#7#CS2040S|1#1|1#2||17#6#MA1521|1#1|1#2||17#6#CS2100|1#2|1#1||17#6#CS2101|1#2|1#1||18#7#CS2103T|1#2|1#1||18#7#CS2109S|1#2|1#1||17#6#IS1108|1#2|1#1||17#6#CS2106|1#2|1#2||17#6#CS3230|1#2|1#2||17#6#ST2334|1#2|1#2|||18#6#MA1301|6#PC1201||";
    private static final String CEG = "467#18#7#CG1111A|1#1|1#1||17#6#EG1311|1#1|1#1||17#6#MA1511|1#1|1#1||17#6#MA1512|1#1|1#1||17#6#CS1010|1#1|1#1||18#7#CG2111A|1#1|1#2||18#7#DTK1234|1#1|1#2||18#7#MA1508E|1#1|1#2||17#6#CS1231|1#1|1#2||17#6#EE2026|1#2|1#1||18#7#CS2040C|1#2|1#1||17#6#CS2107|1#2|1#1||17#6#CG2023|1#2|1#2||17#6#CS2113|1#2|1#2||17#6#EE2211|1#2|1#2||17#6#CG2271|1#2|1#2||17#6#ST2334|1#3|1#2||17#6#CG2027|1#3|1#2||17#6#CG2028|1#3|1#2||17#6#CG3201|1#3|1#2||17#6#EE4204|1#4|1#1||17#6#CG3207|1#4|1#1|||36#6#MA1301|6#PC1201|6#MA1301|6#PC1201||";

    private SaveStorage saveStorage;
    private Map<String, Module> allModulesData;
    private List<String> exemptedModules;
    private Timetable timetable;

    // You might need to specify the correct file/resource path used in ModuleStorage
    private final String MODULE_DATA_PATH = "src/main/resources/modules.txt";

    @BeforeEach
    void setUp() throws CorruptedDataFileException {
        // Create a fresh timetable for each test
        timetable = new Timetable();
        // Load all modules via ModuleStorage
        allModulesData = new HashMap<>();
        ModuleStorage moduleStorage = new ModuleStorage(MODULE_DATA_PATH);
        moduleStorage.load(allModulesData);
        // Create a new exempted list for each test
        exemptedModules = new ArrayList<>();
        // Setup SaveStorage (override to return test string for loadFromTextFile)
        saveStorage = new SaveStorage("dummy.txt") {
            @Override
            public List<String> loadFromTextFile() {
                // For each test, you might select CS or CEG
                // Default to CS here; can override in each test
                return List.of(CS);
            }
        };
        saveStorage.setLoadData(allModulesData, exemptedModules);
    }

    @Test
    void testLoadCSDataLoadsModulesAndExempted() {
        // Load using CS string from file simulation
        saveStorage.load(timetable);

        // Check exemptedModules list is populated
        assertFalse(exemptedModules.isEmpty(), "Exempted modules should be parsed from CS string");

        // Check that modules in timetable match those in allModulesData (at least one!)
        boolean foundModule = false;
        for (int year = 0; year < 4; year++) {
            for (int term = 0; term < 3; term++) {
                List<Module> mods = timetable.getModules(year, term);
                if (mods != null && !mods.isEmpty()) {
                    foundModule = true;
                    break;
                }
            }
            if (foundModule) break;
        }
        assertTrue(foundModule, "Modules should be present in timetable after loading CS string");
    }

    @Test
    void testLoadCEGDataLoadsModulesAndExempted() {
        saveStorage = new SaveStorage("dummy.txt") {
            @Override
            public List<String> loadFromTextFile() {
                return List.of(CEG);
            }
        };
        saveStorage.setLoadData(allModulesData, exemptedModules);
        saveStorage.load(timetable);

        assertFalse(exemptedModules.isEmpty(), "Exempted modules should be loaded from CEG string");
        boolean foundModule = false;
        for (int year = 0; year < Constants.AcademicConstants.NUM_YEARS; year++) {
            for (int term = 0; term < Constants.AcademicConstants.NUM_TERMS; term++) {
                if (!timetable.getModules(year, term).isEmpty()) {
                    foundModule = true;
                }
            }
        }
        assertTrue(foundModule, "Modules should be present in timetable after loading CEG string");
    }

    @Test
    void testSaveProducesNonEmptyResult() throws ModHeroException {
        // Add modules and exempted modules
        timetable.addModule(1, 1, allModulesData.get("CS1101S"), exemptedModules);
        exemptedModules.add("CS1231S");

        // We will capture output using override as before
        final StringBuilder output = new StringBuilder();
        SaveStorage testStorage = new SaveStorage("dummy.txt") {
            @Override
            public void saveToTextFile(String content) {
                output.append(content);
            }
        };
        testStorage.setLoadData(allModulesData, exemptedModules);
        testStorage.save(timetable, exemptedModules);

        assertTrue(output.length() > 10, "Saved content should have significant length");
        assertTrue(output.toString().contains("CS1101S"), "Module code should appear in saved output");
    }

    @Test
    void testLoadCorruptedDataDoesNotCrash() {
        saveStorage = new SaveStorage("dummy.txt") {
            @Override
            public List<String> loadFromTextFile() {
                return List.of("corrupted##string");
            }
        };
        saveStorage.setLoadData(allModulesData, exemptedModules);
        assertDoesNotThrow(() -> saveStorage.load(timetable));
        // Should not populate modules or exempted list
        boolean modulesFound = false;
        for (int year = 0; year < Constants.AcademicConstants.NUM_YEARS; year++) {
            for (int term = 0; term < Constants.AcademicConstants.NUM_TERMS; term++) {
                if (!timetable.getModules(year, term).isEmpty()) {
                    modulesFound = true;
                }
            }
        }
        assertFalse(modulesFound, "No modules should be present after loading corrupted data");
        assertTrue(exemptedModules.isEmpty(), "Exempted list should be empty if corrupted data");
    }
}
