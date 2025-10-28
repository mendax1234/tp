package modhero.exceptions;

public class PrerequisiteNotMetException extends ModHeroException {
    String moduleCode;
    String required;
    public PrerequisiteNotMetException(String moduleCode, String required) {
        super("Prerequisites not met for " + moduleCode + ". Requires: " + required);
        this.moduleCode = moduleCode;
        this.required = required;
    }

    public String getRequisites(){
        return moduleCode + ". Requires: " + required;
    }
}