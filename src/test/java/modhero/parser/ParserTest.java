package modhero.parser;

import modhero.commands.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static modhero.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Unit tests for {@link Parser}.
 */
public class ParserTest {

    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    // Basic parsing tests

    @Test
    void parse_emptyInput_returnsIncorrectCommand() {
        Command result = parser.parseCommand("");
        assertInstanceOf(IncorrectCommand.class, result);
        assertTrue(((IncorrectCommand) result).feedbackToUser.contains("help"),
                "Empty input should prompt help message");
    }

    @Test
    void parse_unknownCommand_returnsIncorrectCommand() {
        Command result = parser.parseCommand("nonsenseCommand");
        assertInstanceOf(IncorrectCommand.class, result);
    }

    @Test
    void parse_helpCommand_returnsHelpCommand() {
        Command result = parser.parseCommand("help");
        assertInstanceOf(HelpCommand.class, result);
    }

    @Test
    void parse_exitCommand_returnsExitCommand() {
        Command result = parser.parseCommand("exit");
        assertInstanceOf(ExitCommand.class, result);
    }

    @Test
    void parse_clearCommand_returnsClearCommand() {
        Command result = parser.parseCommand("clear");
        assertInstanceOf(ClearCommand.class, result);
    }

    @Test
    void parse_scheduleCommand_returnsScheduleCommand() {
        Command result = parser.parseCommand("schedule");
        assertInstanceOf(ScheduleCommand.class, result);
    }

    // Major command

    @Test
    void parse_majorCommandValid_returnsMajorCommand() {
        Command result = parser.parseCommand("major Computer Science specialisation AI minor Statistics");
        assertInstanceOf(MajorCommand.class, result);
        MajorCommand cmd = (MajorCommand) result;
        assertEquals("Computer Science", cmd.getMajor());
        assertEquals("AI", cmd.getSpecialisation());
        assertEquals("Statistics", cmd.getMinor());
    }

    @Test
    void parse_majorCommandMissingArgs_returnsIncorrectCommand() {
        Command result = parser.parseCommand("major");
        assertInstanceOf(IncorrectCommand.class, result);
    }

    // Elective command

    @Test
    void parse_electiveCommandSingle_returnsElectiveCommand() {
        Command result = parser.parseCommand("elective CS2100");
        assertInstanceOf(ElectiveCommand.class, result);
        ElectiveCommand cmd = (ElectiveCommand) result;
        assertEquals(List.of("CS2100"), cmd.getElectives());
    }

    @Test
    void parse_electiveCommandMultiple_returnsElectiveCommand() {
        Command result = parser.parseCommand("elective CS2100 CS2113 CS3243");
        assertInstanceOf(ElectiveCommand.class, result);
        ElectiveCommand cmd = (ElectiveCommand) result;
        assertEquals(List.of("CS2100", "CS2113", "CS3243"), cmd.getElectives());
    }

    @Test
    void parse_electiveCommandEmpty_returnsIncorrectCommand() {
        Command result = parser.parseCommand("elective");
        assertInstanceOf(IncorrectCommand.class, result);
    }

    // Delete command

    @Test
    void parse_deleteCommandValid_returnsDeleteCommand() {
        Command result = parser.parseCommand("delete CS2100");
        assertInstanceOf(DeleteCommand.class, result);
        DeleteCommand cmd = (DeleteCommand) result;
        assertEquals(List.of("CS2100"), cmd.getToDelete());
    }

    @Test
    void parse_deleteCommandMultiple_returnsDeleteCommand() {
        Command result = parser.parseCommand("delete CS2100 CS2113 CS3243");
        assertInstanceOf(DeleteCommand.class, result);
        DeleteCommand cmd = (DeleteCommand) result;
        assertEquals(List.of("CS2100", "CS2113", "CS3243"), cmd.getToDelete());
    }

    @Test
    void parse_deleteCommandEmpty_returnsIncorrectCommand() {
        Command result = parser.parseCommand("delete");
        assertInstanceOf(IncorrectCommand.class, result);
    }
}
