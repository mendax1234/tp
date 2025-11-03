package modhero.commands;

import modhero.common.Constants;
import modhero.data.major.Major;
import modhero.data.timetable.TimetableData;
import modhero.data.modules.Module;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Defines the user's primary degree major.
 * Loads the relevant core modules and recommended study plan from the
 * pre-loaded Major object into the timetable.
 */
public class MajorCommand extends Command {
    private static final Logger logger = Logger.getLogger(MajorCommand.class.getName());

    public static final String COMMAND_WORD = "major";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Specify your major.\n"
            + "  Parameters: MAJOR_NAME \n"
            + "  Example: " + COMMAND_WORD + " Computer Science";

    private final String major;

    /**
     * Creates a MajorCommand to set the user's major.
     *
     * @param major          The user's major (e.g., "Computer Science" or "Computer Engineering")
     */
    public MajorCommand(String major) {
        assert major != null && !major.isEmpty() : "Major name must not be empty";
        this.major = major.trim().toLowerCase();

        logger.log(Level.FINEST, () -> String.format(
                "Created MajorCommand: major=%s",
                this.major));
    }

    @Override
    public CommandResult execute() {
        logger.log(Level.INFO, "Executing Major Command");

        //get the Major object corresponding to the selected major
        Major majorObject = allMajorsData.get(major);

        //if the major is not found, return an error message
        if (majorObject == null) {
            return new CommandResult("Sorry, " + major
                    + " is not supported. Try 'CS' or 'CEG'.");
        }

        //clear the timetable to prevent clashes when user redeclares major
        timetable.clearTimetable();
        exemptedModules.clear();

        if (majorObject.getAbbrName().equals("CS")) {
            exemptedModules.addAll(Constants.ExemptedModulesConstants.CS_EXEMPTED_MODULES);
        } else if (majorObject.getAbbrName().equals("CEG")) {
            exemptedModules.addAll(Constants.ExemptedModulesConstants.CEG_EXEMPTED_MODULES);
        }

        //add all modules from the selected major into the timetable
            for (TimetableData mm : majorObject.getMajorModules()) {
                Module m = allModulesData.get(mm.getCode());
                timetable.addModuleDirect(mm.getYear() - 1, mm.getTerm() - 1, m);
            }
        logger.log(Level.INFO, () -> "Major successfully set to " + major);

        return new CommandResult("Reset to default Timetable for Major in " + major + ". Type 'schedule' to view your 4-year plan!");
    }
}
