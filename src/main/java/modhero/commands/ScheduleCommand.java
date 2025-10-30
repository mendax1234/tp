package modhero.commands;

import modhero.storage.TimetableStorage;

import java.util.logging.Level;
import java.util.logging.Logger;

import static modhero.common.Constants.FilePathConstants.TIMETABLE_FILE_PATH;

/**
 * Displays the current 4-year study plan (already populated when a major is set).
 */
public class ScheduleCommand extends Command {

    public static final String COMMAND_WORD = "schedule";
    private static final Logger logger = Logger.getLogger(ScheduleCommand.class.getName());

    @Override
    public CommandResult execute() {
        logger.log(Level.INFO, "Executing Schedule Command");

        // Just display the timetable; don’t rebuild or print module codes
        timetable.printTimetable();

        TimetableStorage ts = new TimetableStorage(TIMETABLE_FILE_PATH);
        ts.save(timetable, exemptedModules);

        return new CommandResult("Here's your recommended 4-year schedule above!");
    }
}
