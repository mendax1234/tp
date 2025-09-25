package modhero.commands;

import java.util.ArrayList;
import java.util.List;
import modhero.data.modules.Module;

/**
 * Removes a specified elective from your plan.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes the person identified by the index number used in the last person listing.\n"
            + "  Parameters: MODULE_CODE ...\n"
            + "  Example: " + COMMAND_WORD + " CS2109S"
            + "  Example: " + COMMAND_WORD + " CS2109S CS3230 CS3219";

    private final List<String> electives;

    public DeleteCommand(List<String> electives) {
        this.electives = electives;
    }


    @Override
    public CommandResult execute() {
        StringBuilder feedback = new StringBuilder("Electives removed: ");

        for (String elective : electives) {
            boolean isFound = false;
            for (int i = 0; i < electives.size(); i++) {
                Module module = electiveList.getModuleList().get(i);
                if (module.getCode().equals(elective)) {
                    electiveList.remove(i);
                    feedback.append(elective).append(" ");
                    isFound = true;
                    break; // stop after deleting the first match
                }
            }

            if (!isFound) {
                feedback.append("Elective Not Found").append("\n");
                return new CommandResult(feedback.toString());
            }
            feedback.append("\n");
        }

        return new CommandResult(feedback.toString());
    }
}
