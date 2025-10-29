package modhero.commands;

import modhero.data.major.Major;
import modhero.data.timetable.TimetableData;
import modhero.data.modules.Module;
import modhero.exceptions.ModHeroException;

import java.util.ArrayList;
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
    public static final String SPECIALISATION_REGEX = "specialisation";
    public static final String MINOR_REGEX = "minor";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Specify your major.\n"
            + "  Parameters: MAJOR_NAME [" + SPECIALISATION_REGEX + " SPECIALISATION] [" + MINOR_REGEX + " MINOR_NAME]\n"
            + "  Example: " + COMMAND_WORD + " Computer Science specialisation Artificial Intelligence";

    private final String major;
    private final String specialisation;
    private final String minor;

    /**
     * Creates a MajorCommand to set the user's major.
     *
     * @param major          The user's major (e.g., "Computer Science" or "Computer Engineering")
     * @param specialisation The chosen specialisation (optional)
     * @param minor          The chosen minor (optional)
     */
    public MajorCommand(String major, String specialisation, String minor) {
        assert major != null && !major.isEmpty() : "Major name must not be empty";
        this.major = major.trim().toLowerCase();
        this.specialisation = specialisation;
        this.minor = minor;

        logger.log(Level.FINEST, () -> String.format(
                "Created MajorCommand: major=%s, specialisation=%s, minor=%s",
                this.major, this.specialisation, this.minor));
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

        //add all modules from the selected major into the timetable
        try {
            for (TimetableData mm : majorObject.getMajorModules()) {
                Module m = new Module(mm.getCode(), "", 0, "", new ArrayList<>());
                timetable.addModule(mm.getYear(), mm.getTerm(), m);
            }
        } catch (ModHeroException e) {
            logger.log(Level.WARNING, "Error adding module for major: " + major, e);
            return new CommandResult("Failed to load major modules: " + e.getMessage());
        }

        logger.log(Level.INFO, () -> "Major successfully set to " + major);

        return new CommandResult("Timetable cleared! Major set to " + major + ". Type 'schedule' to view your 4-year plan!");
    }
}
