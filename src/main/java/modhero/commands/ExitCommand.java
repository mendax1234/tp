package modhero.commands;

/**
 * Terminates the program.
 */
public class ExitCommand extends Command {

    public static final String COMMAND_WORD = "exit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Exits the program.\n"
            + "  Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute() {
        return new CommandResult("Goodbye!");
    }

    @Override
    public boolean isExit() {
        return true;
    }

    public static boolean isExit(Command command) {
        return command instanceof ExitCommand;
    }
}