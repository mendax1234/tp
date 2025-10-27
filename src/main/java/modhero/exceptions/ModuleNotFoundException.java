package modhero.exceptions;

public class ModuleNotFoundException extends Exception {
    private String moduleCode;

    public ModuleNotFoundException(String moduleCode) {
        super("Module " + moduleCode + " not found in timetable");
        this.moduleCode = moduleCode;
    }

    public ModuleNotFoundException(String moduleCode, String message) {
        super(message);
        this.moduleCode = moduleCode;
    }

    public String getModuleCode(){
        return moduleCode;
    }
}
