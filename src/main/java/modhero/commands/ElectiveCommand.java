package modhero.commands;

import modhero.data.Timetable;
import modhero.data.modules.Module;

import java.util.ArrayList;
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

    private final int year;
    private final int term;
    private final List<String> moduleCodes;

    public ElectiveCommand(int year, int term, List<String> moduleCodes) {
        this.year = year;
        this.term = term;
        this.moduleCodes = moduleCodes;
    }

    @Override
    public CommandResult execute() {
        StringBuilder feedback = new StringBuilder();
        feedback.append("Elective Added to Year ").append(year).append(" Sem ").append(term).append(": ");
        for (String code : moduleCodes) {
            List<String> placeholder = new ArrayList<>();
            Module module = new Module(code, "", 0, "", placeholder);
            data.addModule(year, term, module);
            feedback.append(" ").append(code);
        }
        feedback.append("\n");
        return new CommandResult(feedback.toString());
    }
}
