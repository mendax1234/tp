package modhero.exceptions;

public class PrerequisiteNotMetException extends ModHeroException {
    public PrerequisiteNotMetException(String moduleCode, String required) {
        super("Prerequisites not met for " + moduleCode + ". Requires: " + required);
    }
}