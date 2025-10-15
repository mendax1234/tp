package modhero.commands;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shows help instructions.
 */
public class HelpCommand extends Command {
    public static final Logger logger = Logger.getLogger(HelpCommand.class.getName());

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Exits the program.\n"
            + "  Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute() {
        logger.log(Level.INFO, "Executing Help Command");

        String helpMessage = MajorCommand.MESSAGE_USAGE
                + '\n' + ElectiveCommand.MESSAGE_USAGE
                + '\n' + DeleteCommand.MESSAGE_USAGE
                + '\n' + ScheduleCommand.MESSAGE_USAGE
                + '\n' + ClearCommand.MESSAGE_USAGE
                + '\n' + HelpCommand.MESSAGE_USAGE
                + '\n' + ExitCommand.MESSAGE_USAGE;

        return new CommandResult(helpMessage);
    }
}
