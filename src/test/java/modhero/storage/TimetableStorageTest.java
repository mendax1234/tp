package modhero.storage;

import modhero.data.modules.Module;
import modhero.data.timetable.Timetable;
import modhero.exceptions.CorruptedDataFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimetableStorageTest {

    private File tempFile;
    private TimetableStorage timetableStorage;
    private Timetable timetable;
    private Map<String, Module> allModulesData;

    @BeforeEach
    void setUp() throws IOException, CorruptedDataFileException {
        tempFile = File.createTempFile("timetable", ".txt");
        tempFile.deleteOnExit();

        timetableStorage = new TimetableStorage(tempFile.getAbsolutePath());
        timetable = new Timetable();

        allModulesData = new HashMap<>();
        ModuleStorage moduleStorage = new ModuleStorage(tempFile.getAbsolutePath());
        moduleStorage.load(allModulesData);
    }

    @Test
    void testSaveAndLoadTimetable() {
        // Add modules to timetable
        timetableStorage.addModule(allModulesData.get("CSC101"), 1, 1);
        timetableStorage.addModule(allModulesData.get("CSC102"), 1, 2);

        // Save timetable
        storage.save(timetable);

        // File should not be empty
        assertTrue(tempFile.length() > 0, "File should contain saved timetable data");

        // Create a new timetable to load data into
        Timetable loadedTimetable = new Timetable();

        // Load from file
        storage.load(loadedTimetable, allModulesData);

        // Verify both timetables contain the same modules
        List<Module> originalModulesY1T1 = timetable.getModules(0, 0);
        List<Module> loadedModulesY1T1 = loadedTimetable.getModules(0, 0);

        List<Module> originalModulesY1T2 = timetable.getModules(0, 1);
        List<Module> loadedModulesY1T2 = loadedTimetable.getModules(0, 1);

        assertEquals(originalModulesY1T1.size(), loadedModulesY1T1.size());
        assertEquals(originalModulesY1T2.size(), loadedModulesY1T2.size());

        assertEquals(originalModulesY1T1.get(0).getCode(), loadedModulesY1T1.get(0).getCode());
        assertEquals(originalModulesY1T2.get(0).getCode(), loadedModulesY1T2.get(0).getCode());
    }

    @Test
    void testLoadWithInvalidData() throws IOException {
        // Write invalid data manually
        Files.writeString(tempFile.toPath(), "invalid_data_line");

        Timetable loadedTimetable = new Timetable();
        // Should not throw exception
        assertDoesNotThrow(() -> storage.load(loadedTimetable, allModulesData));
    }
}