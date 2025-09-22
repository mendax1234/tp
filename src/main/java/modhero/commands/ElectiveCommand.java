package modhero.commands;

/**
 * Adds an elective (or multiple) to your plan.
 */
public class ElectiveCommand extends Command {

    public static final String COMMAND_WORD = "elective";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adding an elective.\n"
            + "  Parameters: MODULE_CODE ...\n"
            + "  Example: " + COMMAND_WORD + " CS2109S"
            + "  Example: " + COMMAND_WORD + " CS2109S CS3230 CS3219";

    @Override
    public CommandResult execute() {
        return new CommandResult(MESSAGE_USAGE);
    }
}
