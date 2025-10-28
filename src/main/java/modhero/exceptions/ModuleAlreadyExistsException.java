package modhero.exceptions;

public class ModuleAlreadyExistsException extends ModHeroException {
    public ModuleAlreadyExistsException(String moduleCode) {
        super("Module " + moduleCode + " is already in your timetable!");
    }
}