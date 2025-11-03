package modhero.exceptions;

public class ModulePreclusionConflictException extends ModHeroException {
    public ModulePreclusionConflictException(String moduleCode, String preclusionModuleCode) {
        super("Module " + moduleCode + " cannot be taken together with Module " + preclusionModuleCode);
    }
}
