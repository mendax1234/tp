package modhero.commands;

import modhero.common.exceptions.ModuleNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Removes a specified elective from your plan.
 */
public class DeleteCommand extends Command {
    public static final Logger logger = Logger.getLogger(DeleteCommand.class.getName());

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes the person identified by the index number used in the last person listing.\n"
            + "  Parameters: MODULE_CODE ...\n"
            + "  Example: " + COMMAND_WORD + " CS2109S"
            + "  Example: " + COMMAND_WORD + " CS2109S CS3230 CS3219";

    private final List<String> toDelete;

    public DeleteCommand(List<String> toDelete) {
        this.toDelete = toDelete;
        logger.log(Level.FINEST, "Create delete electives: " + toDelete.toString());
    }


    /** Searches for the corresponding module in electiveList and deletes it if found.*/
    @Override
    public CommandResult execute() {
        if (toDelete == null || toDelete.isEmpty()){
            //handle exception
            return new CommandResult("No module specified to delete");
        }
        ArrayList<String> modulesSucessfullyDeleted = new ArrayList<>();
        ArrayList<String> modulesNotInTimetable = new ArrayList<>();
        for (String module: toDelete ){
            try{
                data.deleteModule(module);
                modulesSucessfullyDeleted.add(module);
            } catch (ModuleNotFoundException e) {
                String moduleCodeNotFound = e.getModuleCode();
                modulesNotInTimetable.add(moduleCodeNotFound);
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
            for ( String module: modulesSucessfullyDeleted){
                message = message + module + " ,";
            }
            message = message.substring(0, message.length() - 1) + "\n";
        }

        return new CommandResult(message);
    }
}
