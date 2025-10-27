package modhero.exceptions;

/**
 * Signals that an error occurred while parsing module prerequisites.
 * This exception is typically thrown when the prerequisite string format
 * is invalid, malformed, or contains unrecognized tokens.
 */
public class ParsePrerequisitesException extends ModHeroException {
    /**
     * Constructs a new ParsePrerequisitesException with the specified
     * detail message.
     *
     * @param message The detailed message explaining why the parsing failed.
     */
    public ParsePrerequisitesException(String message) {
        super(message);
    }
}
