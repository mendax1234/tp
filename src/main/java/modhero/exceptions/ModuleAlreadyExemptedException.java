package modhero.exceptions;

public class ModuleAlreadyExemptedException extends ModHeroException {
    public ModuleAlreadyExemptedException(String moduleCode) {
        super("You have been exempted from " + moduleCode + ", you cannot take it again!");
    }
}