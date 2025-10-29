package modhero.storage;

import modhero.common.util.SerialisationUtil;
import modhero.data.major.Major;
import modhero.data.modules.Module;
import modhero.exceptions.CorruptedDataFileException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Storage}.
 */
public class ModuleStorageTest {
    //@@ mendax1234

    // @TempDir creates a temporary directory for tests and cleans it up afterward.
    // This is great for testing directory creation.
    @TempDir
    Path tempDir;

    private File tempFile;
    private ModuleStorage moduleStorage;
    private SerialisationUtil serialisationUtil;

    @BeforeEach
    void setUp() throws IOException {
        // We still use a specific temp file for most save/load tests
        tempFile = File.createTempFile("modhero_test", ".txt");
        moduleStorage = new ModuleStorage(tempFile.getAbsolutePath());
        serialisationUtil = new SerialisationUtil();
    }

    @AfterEach
    void tearDown() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    // --- Tests for loadAllModulesData() ---

    @Test
    void loadFromTextFileAllModulesData_success() throws IOException {
        // A. Create the mock file content
        // Line 1: CS2040, prereq CS1010
        String prereq1 = serialisationUtil.serialiseMessage("CS1010");
        List<String> module1Args = List.of("CS2040", "Data Structures", "4", "core", prereq1);
        String line1 = serialisationUtil.serialiseList(module1Args);

        // Line 2: CS1010, no prereq
        String noPrereq = serialisationUtil.serialiseMessage("");
        List<String> module2Args = List.of("CS1010", "Programming", "4", "core", noPrereq);
        String line2 = serialisationUtil.serialiseList(module2Args);

        // B. Save the content to the file
        moduleStorage.saveToTextFile(line1 + "\n" + line2);

        // C. Run the method and assert
        Map<String, Module> allModulesData = new HashMap<>();
        assertDoesNotThrow(() -> moduleStorage.load(allModulesData));

        // Should have 4 entries: 2 modules added by code and by name
        assertEquals(4, allModulesData.size());

        Module cs2040 = allModulesData.get("CS2040");
        assertNotNull(cs2040);
        assertEquals("Data Structures", cs2040.getName());
        assertEquals(4, cs2040.getMc());
        assertEquals(List.of("CS1010"), cs2040.getPrerequisites());

        // Check that it was also added by name
        assertEquals(cs2040, allModulesData.get("Data Structures"));
    }

    @Test
    void loadFromTextFileAllModulesData_throwsCorruptedDataFileException() {
        moduleStorage.saveToTextFile("This is not valid serialised data");

        Map<String, Module> map = new HashMap<>();
        // This test correctly asserts that the *expected* exception is thrown
        assertThrows(CorruptedDataFileException.class, () -> {
            moduleStorage.load(map);
        });
    }

    @Test
    void loadFromTextFileAllModulesData_skipsOnWrongArgumentCount() {
        // Create a line with only 3 arguments instead of 5
        List<String> moduleArgs = List.of("CS1010", "Programming", "4");
        String line1 = serialisationUtil.serialiseList(moduleArgs);
        moduleStorage.saveToTextFile(line1);

        Map<String, Module> map = new HashMap<>();
        // The method should log a warning and 'break', not throw an exception
        assertDoesNotThrow(() -> moduleStorage.load(map));

        assertTrue(map.isEmpty(), "Module with wrong arg count should be skipped");
    }

    @Test
    void loadFromTextFileAllModulesData_skipsOnInvalidModuleCredit() {
        // Create a line where MC is "four", not "4"
        String noPrereq = serialisationUtil.serialiseMessage("");
        List<String> moduleArgs = List.of("CS1010", "Programming", "four", "core", noPrereq);
        String line1 = serialisationUtil.serialiseList(moduleArgs);
        moduleStorage.saveToTextFile(line1);

        Map<String, Module> map = new HashMap<>();
        // The method catches NumberFormatException and logs, not throws
        assertDoesNotThrow(() -> moduleStorage.load(map));

        assertTrue(map.isEmpty(), "Module with invalid MC should be skipped");
    }
    //@@ mendax1234
}
