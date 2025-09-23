package modhero.commands;

/**
 * Removes a specified elective from your plan.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes the person identified by the index number used in the last person listing.\n"
            + "  Parameters: MODULE_CODE ...\n"
            + "  Example: " + COMMAND_WORD + " CS2109S"
            + "  Example: " + COMMAND_WORD + " CS2109S CS3230 CS3219";

    @Override
    public CommandResult execute() {
        return new CommandResult(MESSAGE_USAGE);
    }
}
