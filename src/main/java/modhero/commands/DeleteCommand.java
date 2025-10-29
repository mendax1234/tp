package modhero.commands;

import modhero.exceptions.ModuleNotFoundException;
import modhero.exceptions.PrerequisiteNotMetException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Represents a command that removes one or more specified modules (electives) from the user's timetable plan.
 * <p>
 * The {@code DeleteCommand} converts all given module codes to uppercase to ensure consistency and attempts
 * to delete each from the current timetable. It reports which deletions succeeded, which modules were not found,
 * and which deletions were blocked due to prerequisite violations.
 * </p>
 */
public class DeleteCommand extends Command {
    public static final Logger logger = Logger.getLogger(DeleteCommand.class.getName());

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes the person identified by the index number used in the last person listing.\n"
            + "  Parameters: MODULE_CODE ...\n"
            + "  Example: " + COMMAND_WORD + " CS2109S"
            + "  Example: " + COMMAND_WORD + " CS2109S CS3230 CS3219";

    private final List<String> toDelete;

    /**
     * Constructs a {@code DeleteCommand} with the list of module codes to delete.
     * <p>
     * All module codes are converted to uppercase for consistency before execution.
     * </p>
     *
     * @param toDelete the list of module codes to delete
     */
    public DeleteCommand(List<String> toDelete) {
        ArrayList<String> toDeleteUpperCase = new ArrayList<String>();
        for (String module: toDelete){
            module = module.toUpperCase();
            toDeleteUpperCase.add(module);
        }
        this.toDelete = toDeleteUpperCase;
        logger.log(Level.FINEST, "Create delete electives: " + toDelete.toString());
    }


    /**
     * Executes the delete command.
     * <p>
     * For each module code specified, the method attempts to delete it from the timetable.
     * It tracks and reports:
     * <ul>
     *   <li>Modules successfully deleted</li>
     *   <li>Modules not found in the timetable</li>
     *   <li>Modules that cannot be deleted because doing so would violate prerequisites</li>
     * </ul>
     * If no modules are specified, an appropriate error message is returned.
     * </p>
     *
     * @return a {@code CommandResult} summarizing the outcome of the delete operation
     */
    @Override
    public CommandResult execute() {
        if (toDelete == null || toDelete.isEmpty()){
            //handle exception
            return new CommandResult("No module specified to delete");
        }
        ArrayList<String> modulesSucessfullyDeleted = new ArrayList<>();
        ArrayList<String> modulesNotInTimetable = new ArrayList<>();
        ArrayList<String> prerequisiteViolations = new ArrayList<>();
        for (String module: toDelete ){
            try{
                timetable.deleteModule(module);
                modulesSucessfullyDeleted.add(module);
            } catch (ModuleNotFoundException e) {
                String moduleCodeNotFound = e.getModuleCode();
                modulesNotInTimetable.add(moduleCodeNotFound);
            } catch (PrerequisiteNotMetException e){
               prerequisiteViolations.add(" { " + e.getRequisites() + " }");
            }
        }

        String message = "";
        if ( ! modulesSucessfullyDeleted.isEmpty()){
            message = " Successfully deleted : ";
            for ( String module: modulesSucessfullyDeleted){
                message = message + module + " ,";
            }
            message = message.substring(0, message.length() - 1) + "\n";
        }

        if (! modulesNotInTimetable.isEmpty()){
               message = message + "The following modules were not found in timetable: ";
            for ( String module: modulesNotInTimetable){
                message = message + module + " ,";
            }
            message = message.substring(0, message.length() - 1) + "\n";
        }

        if (!prerequisiteViolations.isEmpty()){
            message = message + "The following modules could not be deleted as that would violate the " +
                    "preRequisites for the following modules:";
            for ( String violation: prerequisiteViolations){
                message = message + violation ;
            }
        }

        return new CommandResult(message);
    }
}
