package modhero.exceptions;

/**
 * Exception thrown when attempting to delete a module that is required
 * as a prerequisite for other modules in the timetable.
 */
public class ModuleDeletionBlockedException extends ModHeroException {
    private final String moduleCode;
    private final String dependentModule;

    /**
     * Constructs a {@code ModuleDeletionBlockedException} with the specified module code
     * and the dependent module that requires it.
     *
     * @param moduleCode the code of the module being deleted
     * @param dependentModule the module that depends on this prerequisite
     */
    public ModuleDeletionBlockedException(String moduleCode, String dependentModule) {
        super(String.format("Cannot delete %s as it is a prerequisite for %s",
                moduleCode, dependentModule));
        this.moduleCode = moduleCode;
        this.dependentModule = dependentModule;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public String getDependentModule() {
        return dependentModule;
    }
}
