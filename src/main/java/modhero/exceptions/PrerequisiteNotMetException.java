package modhero.exceptions;

/**
 * Exception thrown when attempting to take or delete a module that would result
 * in unsatisfied prerequisite requirements.
 * <p>
 * This exception stores both the affected module code and a description of the
 * required prerequisites that were not met. It is typically used to signal that
 * an operation (such as adding or deleting a module) would violate prerequisite
 * constraints within the timetable.
 * </p>
 */
public class PrerequisiteNotMetException extends ModHeroException {
    String moduleCode;
    String required;

    /**
     * Constructs a {@code PrerequisiteNotMetException} with the specified module code
     * and the description of its unmet prerequisite requirements.
     *
     * @param moduleCode the code of the module whose prerequisites are not met
     * @param required a textual description of the required prerequisites
     */
    public PrerequisiteNotMetException(String moduleCode, String required) {
        super("Prerequisites not met for " + moduleCode + ". Requires: " + required);
        this.moduleCode = moduleCode;
        this.required = required;
    }

    /**
     * Returns a formatted string describing the module and its unmet prerequisites.
     *
     * @return a string in the format "{@code <moduleCode> Requires: <required>}"
     */
    public String getRequisites(){
        return moduleCode + " Requires: " + required;
    }
}