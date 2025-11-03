package modhero.commands;

import modhero.common.Constants.AcademicConstants;

import modhero.data.modules.Module;
import modhero.data.nusmods.ModuleRetriever;
import modhero.data.timetable.Timetable;
import modhero.exceptions.ModHeroException;
import modhero.exceptions.ModuleNotFoundException;

import java.util.List;
import java.util.Map;
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

    public AddCommand(String moduleCode, int year, int semester) {
        assert year >= 0 && year < AcademicConstants.NUM_YEARS : "addModule year out of bounds";
        assert semester >= 0 && semester < AcademicConstants.NUM_TERMS : "addModule term out of bounds";

        this.moduleCode = moduleCode.toUpperCase();
        this.year = year;
        this.semester = semester;
    }

    @Override
    public CommandResult execute() {
        try {
            logger.log(Level.INFO, () -> String.format("Adding module %s to Y%dS%d", moduleCode, year, semester));

            addModule(timetable, allModulesData, moduleCode, year, semester, exemptedModules);

            return new CommandResult(String.format("%s added successfully to Y%dS%d!", moduleCode, year, semester));
        } catch (ModHeroException e) {
            return new CommandResult(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error while adding module", e);
            return new CommandResult("An unexpected error occurred: " + e.getMessage());
        }
    }

    public static void addModule(Timetable timetable, Map<String, Module> allModulesData, String moduleCode, int year, int term, List<String> exemptedModules) throws ModHeroException {
        ModuleRetriever moduleRetriever = new ModuleRetriever();
        Module module = allModulesData.get(moduleCode);
        if (module == null) {
            logger.log(Level.INFO, "Module " + moduleCode + " not in local data, trying API fetch...");
            try {
                module = moduleRetriever.getModule(AcademicConstants.ACAD_YEAR, moduleCode);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to fetch module from API", e);
                throw new ModuleNotFoundException(moduleCode, "NUSMODS\nPlease ensure you are connected to the internet and provide valid module code");
            }
            // In case ModuleRetreiver return null without an Exception
            if (module == null) {
                throw new ModuleNotFoundException(moduleCode, "NUSMODS\nPlease ensure you are connected to the internet and provide valid module code");
            }
            allModulesData.put(module.getCode(), module);
        }

        timetable.addModule(year, term, module, exemptedModules);
    }
}
