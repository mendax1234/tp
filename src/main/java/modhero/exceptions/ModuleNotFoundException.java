package modhero.exceptions;

/**
 * Signals that a specific module could not be found.
 * This exception is thrown when an operation attempts to access or
 * modify a module that does not exist in the specified context.
 * @author sivanshno
 */
public class ModuleNotFoundException extends ModHeroException {
    private String moduleCode;
    /**
     * Constructs a new ModuleNotFoundException with a detailed error message
     * @param moduleCode  The module that could not be found
     * @param message A string describing the context or location where
     * the module was expected but not found (e.g., "module list", "database").
     */
    public ModuleNotFoundException(String moduleCode, String message) {
        super("OOPS!!!" + moduleCode + "is not found in " + message);
        this.moduleCode = moduleCode;
    }

    public String getModuleCode() {
        return moduleCode;
    }
}
