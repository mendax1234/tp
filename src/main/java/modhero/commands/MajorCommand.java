package modhero.commands;

import modhero.data.major.Major;

/**
 * Defines your primary degree major, which ModHero uses to load graduation requirements.
 */
public class MajorCommand extends Command {

    public static final String COMMAND_WORD = "major";
    public static final String SPECIALISATION_REGEX = "specialisation";
    public static final String MINOR_REGEX = "minor";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Specifying your major.\n"
            + "  Parameters: MAJOR_NAME [" + SPECIALISATION_REGEX + " SPECIALISATION] [" + MINOR_REGEX + " MINOR_NAME]\n"
            + "  Example: " + COMMAND_WORD + " Computer Science specialisation Artificial Intelligence";

    private final String major;
    private final String specialisation;
    private final String minor;

    public MajorCommand(String major, String specialisation, String minor) {
        this.major = major;
        this.specialisation = specialisation;
        this.minor = minor;
    }

    @Override
    public CommandResult execute() {
        Major majorObj = allMajorsData.get(major);
        if (majorObj == null) {
            return new CommandResult("Failed to retrieve major " + major);
        }
        coreList.setList(majorObj.getModules().getList());
        return new CommandResult(major + "|" + specialisation + "|" + minor);
    }
}
