package modhero.commands;

import modhero.data.timetable.Timetable;
import modhero.data.major.Major;
import modhero.data.modules.Module;
import modhero.data.modules.ModuleList;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract base class for all commands in the ModHero application.
 * Provides access to the timetable and module lists, and defines
 * the contract for command execution.
 */
public abstract class Command {
    public static final Logger logger = Logger.getLogger(Command.class.getName());

    protected Timetable timetable;
    protected Map<String, Module> allModulesData;
    protected Map<String, Major> allMajorsData;
    protected List<String> exemptedModules;

    /**
     * Sets the data context for the command, including the timetable
     * and lists of elective and core modules.
     *
     * @param timetable the timetable to operate on
     * @param allModulesData the hashmap for loading and saving data
     * @param allMajorsData the hashmap for major information
     */
    public void setData(Timetable timetable, Map<String, Module> allModulesData, Map<String, Major> allMajorsData, List<String> exemptedModules) {
        assert timetable != null : "Timetable must not be null";
        assert allModulesData != null : "All modules map must not be null";
        assert allMajorsData != null : "All majors map must not be null";

        this.timetable = timetable;
        this.allModulesData = allModulesData;
        this.allMajorsData = allMajorsData;
        this.exemptedModules = exemptedModules;

        logger.info("Command setData initialised");
        logger.log(Level.FINEST, () -> String.format("SetData sizes - modules: %d, majors: %d",
                allModulesData.size(),
                allMajorsData.size()));
        logger.log(Level.FINEST, () -> String.format(
                "Modules: %s Majors: %s",
                allModulesData.keySet(),
                allMajorsData.keySet()
        ));
    }

    /**
     * Executes the command.
     *
     * @return a {@link CommandResult} containing feedback for the user
     */
    public abstract CommandResult execute();

}
