package modhero.commands;

/**
 * Deletes all modules and resets your plan.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes all modules.\n"
            + "  Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute() {
        data.clearTimetable();
        return new CommandResult("Reset the timetable.");
    }
}
