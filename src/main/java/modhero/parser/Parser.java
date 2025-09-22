package modhero.parser;

import modhero.commands.ClearCommand;
import modhero.commands.Command;
import modhero.commands.DeleteCommand;
import modhero.commands.ElectiveCommand;
import modhero.commands.ExitCommand;
import modhero.commands.HelpCommand;
import modhero.commands.IncorrectCommand;
import modhero.commands.MajorCommand;
import modhero.commands.ScheduleCommand;

import static modhero.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Parses user input.
 */
public class Parser {

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     */
    public Command parseCommand(String userInput) {
        String[] words = userInput.trim().split(" ", 2);  // split the input into command and arguments
        if (words.length == 0) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = words[0];
        final String arguments = userInput.replaceFirst(commandWord, "").trim();

        switch (commandWord) {

        case MajorCommand.COMMAND_WORD:
            return new MajorCommand();

        case ElectiveCommand.COMMAND_WORD:
            return new ElectiveCommand();

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommand();

        case ScheduleCommand.COMMAND_WORD:
            return new ScheduleCommand();

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();
        default:
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }
    }
}