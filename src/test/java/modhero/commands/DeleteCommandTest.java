package modhero.commands;

import modhero.data.modules.Module;
import modhero.data.modules.Prerequisites;
import modhero.data.timetable.Timetable;
import modhero.exceptions.ModHeroException;
import modhero.exceptions.ModuleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DeleteCommand}.
 */
public class DeleteCommandTest {
    private Timetable timetable;

    private Prerequisites cs1010Prerequisites = new Prerequisites(new ArrayList<>());
    private Module cs1010 = new Module("CS1010", "Programming Methodology", 4, "core", cs1010Prerequisites);

    private List<List<String>> cs2040PrereqList = List.of(List.of("CS1010"));
    private Prerequisites cs2040Prerequisites = new Prerequisites(cs2040PrereqList);
    private Module cs2040 = new Module("CS2040", "Data Structures", 4, "core", cs2040Prerequisites);

    private Prerequisites cs2100Prerequisites = cs2040Prerequisites;
    private Module cs2100 = new Module("CS2100", "Computer Organisation", 4, "core", cs2100Prerequisites);

    @BeforeEach
    void setUp() {
        timetable = new Timetable();
        timetable.addModuleDirect(1, 1, cs1010);
        timetable.addModuleDirect(1, 2, cs2040);
        timetable.addModuleDirect(2, 1, cs2100);
    }

    @Test
    void execute_deleteExistingModule_success() throws Exception {
        // given
        DeleteCommand deleteCommand = new DeleteCommand("CS2100");
        deleteCommand.setTimetable(timetable);

        // when
        CommandResult result = deleteCommand.execute();

        // then
        assertEquals("CS2100 deleted successfully!", result.getFeedbackToUser());

        // verify module is removed
        assertThrows(ModuleNotFoundException.class, () -> timetable.findModuleLocation("CS2100"));
    }

    @Test
    void execute_deleteNonExistentModule_returnsNotFoundMessage() {
        // given
        DeleteCommand deleteCommand = new DeleteCommand("CS9999");
        deleteCommand.setTimetable(timetable);

        // when
        CommandResult result = deleteCommand.execute();

        // then
        assertEquals("This CS9999 cannot be found in the timetable", result.getFeedbackToUser());
    }

    @Test
    void execute_deleteModuleThatIsPrerequisite_returnsBlockedMessage() throws Exception {
        // given
        DeleteCommand deleteCommand = new DeleteCommand("CS1010");
        deleteCommand.setTimetable(timetable);

        // when
        CommandResult result = deleteCommand.execute();

        // then
        assertTrue(result.getFeedbackToUser().contains("Cannot delete CS1010 as it is a prerequisite for CS2040"));

        // verify module still exists (should not throw)
        assertDoesNotThrow(() -> timetable.findModuleLocation("CS1010"));
    }

    @Test
    void execute_emptyModuleCode_returnsNoModuleSpecifiedMessage() {
        // given
        DeleteCommand deleteCommand = new DeleteCommand("");
        deleteCommand.setTimetable(timetable);

        // when
        CommandResult result = deleteCommand.execute();

        // then
        assertEquals("No module specified to delete", result.getFeedbackToUser());
    }

    @Test
    void execute_deleteModule_caseInsensitiveInput_success() throws Exception {
        // given: user inputs lowercase module code
        DeleteCommand deleteCommand = new DeleteCommand("cs2040");
        deleteCommand.setTimetable(timetable);

        // when
        CommandResult result = deleteCommand.execute();

        // then
        assertEquals("CS2040 deleted successfully!", result.getFeedbackToUser());

        // verify module is removed
        assertThrows(ModuleNotFoundException.class, () -> timetable.findModuleLocation("CS2040"));
    }

    @Test
    void execute_unexpectedException_returnsErrorMessage() {
        // given: create a DeleteCommand but make timetable null to simulate internal error
        DeleteCommand deleteCommand = new DeleteCommand("CS1010");
        // Deliberately NOT setting timetable to cause a NullPointerException in execute()

        // when
        CommandResult result = deleteCommand.execute();

        // then
        assertTrue(result.getFeedbackToUser().startsWith("An unexpected error occurred:"));
    }
}