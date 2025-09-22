package modhero.commands;

/**
 * Defines your primary degree major, which ModHero uses to load graduation requirements.
 */
public class MajorCommand extends Command {

    public static final String COMMAND_WORD = "major";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Specifying your major.\n"
            + "  Parameters: MAJOR_NAME [specialisation SPECIALISATION] [minor MINOR_NAME]\n"
            + "  Example: " + COMMAND_WORD + " Computer Science specialisation Artificial Intelligence";

    @Override
    public CommandResult execute() {
        return new CommandResult(MESSAGE_USAGE);
    }
}
