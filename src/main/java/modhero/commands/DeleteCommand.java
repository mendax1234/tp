package modhero.commands;

import modhero.exceptions.ModHeroException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a command that removes a specified module from the user's timetable plan.
 * @author sivanshno
 */
public class DeleteCommand extends Command {
    public static final Logger logger = Logger.getLogger(DeleteCommand.class.getName());

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes a module from your timetable.\n"
            + "  Parameters: MODULE_CODE\n"
            + "  Example: " + COMMAND_WORD + " CS2109S";

    private final String moduleCode;

    /**
     * Constructs a {@code DeleteCommand} with the module code to delete.
     *
     * @param moduleCode the module code to delete
     */
    public DeleteCommand(String moduleCode) {
        this.moduleCode = moduleCode.toUpperCase();
        logger.log(Level.FINEST, "Create delete command: " + this.moduleCode);
    }

    /**
     * Executes the delete command.
     *
     * @return a {@code CommandResult} summarizing the outcome of the delete operation
     */
    @Override
    public CommandResult execute() {
        if (moduleCode == null || moduleCode.isEmpty()) {
            return new CommandResult("No module specified to delete");
        }

        try {
            logger.log(Level.INFO, () -> String.format("Attempting to delete module: %s", moduleCode));

            timetable.deleteModule(moduleCode, exemptedModules);

            return new CommandResult(String.format("%s deleted successfully!", moduleCode));

        } catch (ModHeroException e) {
            return new CommandResult(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error while deleting module", e);
            return new CommandResult("An unexpected error occurred: " + e.getMessage());
        }
    }
}
