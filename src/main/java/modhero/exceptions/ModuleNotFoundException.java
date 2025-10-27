package modhero.exceptions;

import modhero.data.modules.Module;

/**
 * Signals that a specific {@link Module} could not be found.
 * This exception is thrown when an operation attempts to access or
 * modify a module that does not exist in the specified context.
 */
public class ModuleNotFoundException extends ModHeroException {
    /**
     * Constructs a new ModuleNotFoundException with a detailed error message.
     *
     * @param module  The {@link Module} that could not be found.
     * @param message A string describing the context or location where
     * the module was expected but not found (e.g., "module list", "database").
     */
    public ModuleNotFoundException(Module module, String message) {
        super("OOPS!!!" + module.getName() + "is not found in " + message);
    }
}
