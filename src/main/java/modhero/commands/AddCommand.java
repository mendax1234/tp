package modhero.commands;

import static modhero.common.Constants.AcademicConstants.ACAD_YEAR;

import modhero.data.modules.Module;
import modhero.data.nusmods.ModuleRetriever;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AddCommand extends Command {
    public static final Logger logger = Logger.getLogger(AddCommand.class.getName());
    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a module to a specific year and semester.\n"
            + "  Format: add MODULE_CODE to YxSy\n"
            + "  Example: add CS3240 to Y3S2";

    private final String moduleCode;
    private final int year;
    private final int semester;
    private final ModuleRetriever moduleRetriever;

    public AddCommand(String moduleCode, int year, int semester) {
        this.moduleCode = moduleCode.toUpperCase();
        this.year = year;
        this.semester = semester;
        this.moduleRetriever = new ModuleRetriever();
    }

    @Override
    public CommandResult execute() {
        logger.log(Level.INFO, () -> String.format("Adding module %s to Y%dS%d", moduleCode, year, semester));

        // Check if module exists in our database
        Module module = allModulesData.get(moduleCode);

        if (module == null) {
            // If not, try fetching it from the API
            logger.log(Level.INFO, "Module " + moduleCode + " not in local data, trying API fetch...");
            module = moduleRetriever.getModule(ACAD_YEAR, moduleCode);

            if (module == null) {
                // API fetch also failed
                return new CommandResult("Module " + moduleCode + " not found in local database or NUSMods API.");
            } else {
                // API fetch succeeded, add it to our main list
                allModulesData.put(module.getCode(), module);
                logger.log(Level.INFO, "Module " + moduleCode + " found via API and added to database.");
            }
        }

        // Add the module to the timetable
        String resultMessage = timetable.addModule(year, semester, module);

        return new CommandResult(resultMessage);
    }
}
