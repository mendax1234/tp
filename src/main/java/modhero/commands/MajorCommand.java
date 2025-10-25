package modhero.commands;

import modhero.data.major.MajorData;
import modhero.data.modules.Module;
import modhero.data.modules.ModuleList;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Defines your primary degree major, which ModHero uses to load graduation requirements.
 */
public class MajorCommand extends Command {
    private static final Logger logger = Logger.getLogger(MajorCommand.class.getName());

    public static final String COMMAND_WORD = "major";
    public static final String SPECIALISATION_REGEX = "specialisation";
    public static final String MINOR_REGEX = "minor";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Specify your major.\n"
            + "  Parameters: MAJOR_NAME [" + SPECIALISATION_REGEX + " SPECIALISATION] [" + MINOR_REGEX + " MINOR_NAME]\n"
            + "  Example: " + COMMAND_WORD + " Computer Science specialisation Artificial Intelligence";

    private final String major;
    private final String specialisation;
    private final String minor;

    public MajorCommand(String major, String specialisation, String minor) {
        assert major != null && !major.isEmpty() : "Major name must not be empty";

        this.major = major;
        this.specialisation = specialisation;
        this.minor = minor;

        // Fixed logger usage
        logger.log(Level.FINEST, () -> String.format(
                "Created MajorCommand with major=%s, specialisation=%s, minor=%s",
                major, specialisation, minor));
    }

    // Removed duplicate @Override
    @Override
    public CommandResult execute() {
        logger.log(Level.INFO, "Executing Major Command");

        MajorData majorData = new MajorData();
        ModuleList coreModules = majorData.getCoreModules(major);

        if (coreModules == null) {
            return new CommandResult(
                    "Sorry, " + major + " is not supported. Try 'Computer Science' or 'Computer Engineering'.");
        }

        // Load core modules into data structures
        coreList.setList(coreModules.getList());
        data.clearTimetable();

        // Populate timetable based on schedule map
        Map<String, int[]> scheduleMap = majorData.getSchedule(major);
        for (Module m : coreModules.getList()) {
            int[] yAndS = scheduleMap.getOrDefault(m.getCode(), new int[]{1, 1});
            data.addModule(yAndS[0] - 1, yAndS[1] - 1, m);
        }

        return new CommandResult(
                "Major set to " + major + ". Type 'schedule' to view your 4-year plan!");
    }
}
