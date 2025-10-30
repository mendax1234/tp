package modhero.exceptions;

/**
 * Exception thrown when a specified module cannot be found in the expected context.
 * <p>
 * This exception is typically raised when an operation attempts to access or modify
 * a module that does not exist in the timetable, module list, or database.
 * It includes the module code that could not be located and a descriptive message
 * indicating where the lookup failed.
 * </p>
 * @author sivanshno
 */
public class ModuleNotFoundException extends ModHeroException {
    /**
     * Constructs a new ModuleNotFoundException with a detailed error message
     * @param moduleCode  The module that could not be found
     * @param message A string describing the context or location where
     * the module was expected but not found (e.g., "module list", "database").
     */
    public ModuleNotFoundException(String moduleCode, String message) {
        super("OOPS!!! " + moduleCode + " is not found in " + message);
    }
}
