package modhero.storage;

import modhero.data.modules.Module;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Storage}.
 */
public class StorageTest {

    private File tempFile;
    private Storage storage;

    @BeforeEach
    void setUp() throws IOException {
        // create a temporary file for testing
        tempFile = File.createTempFile("modhero_test", ".txt");
        storage = new Storage(tempFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    // ensureFileDirectoryExist
    @Test
    void testEnsureFileDirectoryExist_createsParentDirs() {
        File nested = new File(tempFile.getParent(), "nested/dir/testfile.txt");
        Storage nestedStorage = new Storage(nested.getAbsolutePath());

        nestedStorage.ensureFileDirectoryExist();
        assertTrue(nested.getParentFile().exists(), "Parent directory should be created");
    }

    // save() and load()
    @Test
    void testSaveAndLoad_roundTrip() throws IOException {
        String testContent = "CS1010 Data Structures AI";
        storage.save(testContent);

        List<String> lines = storage.load();
        assertNotNull(lines);
        assertEquals(List.of(testContent), lines);
    }

    @Test
    void testLoad_missingFile_returnsNull() {
        File missingFile = new File("nonexistent_file.txt");
        Storage missingStorage = new Storage(missingFile.getAbsolutePath());

        List<String> result = missingStorage.load();
        assertNull(result, "Should return null when file is missing");
    }

    // writeToFile indirect (overwrite test)
    @Test
    void testSave_overwritesExistingFile() throws IOException {
        storage.save("CS1010");
        storage.save("CS2040");

        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertEquals(List.of("CS2040"), lines, "File content should be overwritten, not appended");
    }

    // loadAllModulesData()
    @Test
    void testLoadAllModulesData_parsesModulesCorrectly() throws IOException {
        // Simulate one serialised module line
        // Format: code, name, mc, type, prerequisites (serialised)
        Serialiser serialiser = new Serialiser();

        String moduleLine =
                serialiser.serialiseMessage("CS1010") +
                        serialiser.serialiseMessage("Programming Methodology") +
                        serialiser.serialiseMessage("4") +
                        serialiser.serialiseMessage("core") +
                        serialiser.serialiseList(List.of(""));

        // Write this single line to the temp file
        Files.write(tempFile.toPath(), List.of(moduleLine));

        Map<String, Module> map = new HashMap<>();
        storage.loadAllModulesData(map);

        assertEquals(1, map.size(), "Should load exactly one module");
        Module m = map.get("CS1010");
        assertNotNull(m);
        assertEquals("CS1010", m.getCode());
        assertEquals("Programming Methodology", m.getName());
        assertEquals(4, m.getMc());
        assertEquals("core", m.getType());
    }

    @Test
    void testLoadAllModulesData_handlesInvalidDataGracefully() throws IOException {
        Files.write(tempFile.toPath(), List.of("invalid#data|no#numbers|"));

        Map<String, Module> map = new HashMap<>();
        assertDoesNotThrow(() -> storage.loadAllModulesData(map));
        assertTrue(map.isEmpty(), "Invalid data should not add modules");
    }
}
