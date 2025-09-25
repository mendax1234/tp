package modhero.commands;

import modhero.data.modules.Module;

import java.util.List;

/**
 * Adds an elective (or multiple) to your plan.
 */
public class ElectiveCommand extends Command {

    public static final String COMMAND_WORD = "elective";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adding an elective.\n"
            + "  Parameters: YEAR, TERM, MODULE_CODES...\n"
            + "  Example: " + COMMAND_WORD + " 1 1 CS2109S"
            + "  Example: " + COMMAND_WORD + " 2 1 CS2109S CS3230 CS3219";

    private final List<String> electives;

    public ElectiveCommand(List<String> electives) {
        this.electives = electives;
    }

    @Override
    public CommandResult execute() {
        return new CommandResult(electives.toString());
    }
}
