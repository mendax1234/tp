package modhero.commands;

/**
 * Shows help instructions.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Exits the program.\n"
            + "  Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute() {
        return new CommandResult(
                MajorCommand.MESSAGE_USAGE
                        + "\n" + ElectiveCommand.MESSAGE_USAGE
                        + "\n" + DeleteCommand.MESSAGE_USAGE
                        + "\n" + ScheduleCommand.MESSAGE_USAGE
                        + "\n" + HelpCommand.MESSAGE_USAGE
                        + "\n" + ExitCommand.MESSAGE_USAGE
        );
    }
}
