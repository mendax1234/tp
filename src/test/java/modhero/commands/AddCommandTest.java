package modhero.commands;

import modhero.data.major.Major;
import modhero.data.modules.Module;
import modhero.data.modules.Prerequisites;
import modhero.data.timetable.Timetable;
import modhero.exceptions.ModHeroException;
import modhero.exceptions.ModuleAlreadyExistsException;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AddCommand}.
 */
public class AddCommandTest {

    private Timetable timetable;
    private Map<String, Module> allModulesData;
    private Map<String, Major> dummyMajorsData; // placeholder since not used in AddCommand
    private List<String> exemptedModules;

    // Helper method to quickly create a dummy module
    private Module createModule(String code) {
        return new Module(
                code,
                "Test Module " + code,
                4,
                "Core",
                "", // empty preclude
                new Prerequisites(Collections.emptyList()) // empty prereq
        );
    }

    @BeforeEach
    void setup() {
        timetable = new Timetable();
        allModulesData = new HashMap<>();
        dummyMajorsData = new HashMap<>();
        exemptedModules = new ArrayList<>();
    }

    @Test
    void execute_successfullyAddsModuleFromLocalData() {
        Module module = createModule("CS1010");
        allModulesData.put("CS1010", module);

        AddCommand command = new AddCommand("CS1010", 1, 1);
        command.setData(timetable, allModulesData, dummyMajorsData, exemptedModules);

        CommandResult result = command.execute();

        assertTrue(result.getFeedbackToUser().contains("added successfully"));
        assertEquals(1, timetable.getModules(0, 0).size());
        assertEquals("CS1010", timetable.getModules(0, 0).get(0).getCode());
    }

    @Test
    void execute_moduleNotFound_returnsErrorMessage() {
        AddCommand command = new AddCommand("FAKE9999", 1, 1);
        command.setData(timetable, allModulesData, dummyMajorsData, exemptedModules);

        // Execute the command and get the result
        CommandResult result = command.execute();

        // Check that the feedback to the user contains the error message
        assertNotNull(result);
        assertTrue(result.getFeedbackToUser().contains("FAKE9999"));
        assertTrue(result.getFeedbackToUser().toLowerCase().contains("not found"));
    }

    @Test
    void addModule_duplicateModule_throwsModuleAlreadyExistsException() throws ModHeroException {
        Module mod = createModule("CS2030S");
        allModulesData.put("CS2030S", mod);

        // First add works fine
        timetable.addModule(1, 1, mod, exemptedModules);

        // Try to add again
        Module duplicate = createModule("CS2030S");
        allModulesData.put("CS2030S", duplicate);

        assertThrows(ModuleAlreadyExistsException.class, () ->
                AddCommand.addModule(timetable, allModulesData, "CS2030S", 1, 1, exemptedModules)
        );
    }

    @Test
    void addModule_exemptedModule_throwsModHeroException() {
        Module mod = createModule("CS1231S");
        allModulesData.put("CS1231S", mod);
        exemptedModules.add("CS1231S");

        assertThrows(ModHeroException.class, () ->
                AddCommand.addModule(timetable, allModulesData, "CS1231S", 1, 1, exemptedModules)
        );
    }
}
