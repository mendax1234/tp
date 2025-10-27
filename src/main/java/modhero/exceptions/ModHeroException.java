package modhero.exceptions;
/**
 * A base exception class for custom exceptions in the ModHero application.
 * Serves as the parent class for specific exceptions related to task and command processing.
 */
public class ModHeroException extends Exception {
    /**
     * Constructs a ModHeroException with the specified error message.
     *
     * @param message The detailed message explaining the error.
     */
    public ModHeroException(String message) {
        super(message);
    }
}
