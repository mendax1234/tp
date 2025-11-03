package modhero.commands;

import java.util.logging.Logger;

import modhero.common.Constants.MessageConstants;

/**
 * Shows help instructions.
 */
public class HelpCommand extends Command {
    public static final Logger logger = Logger.getLogger(HelpCommand.class.getName());

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Display list of available commands.\n"
            + "  Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute() {

        String helpMessage = MessageConstants.HELP;

        return new CommandResult(helpMessage);
    }
}
