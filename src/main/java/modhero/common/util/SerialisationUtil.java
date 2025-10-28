package modhero.common.util;

import modhero.common.Constants.FormatConstants;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides utility methods for serialising strings and string lists into a
 * length-prefixed format suitable for persistent storage.
 * Serialisation Format: [length of content][START_DELIMITER][content][END_DELIMITER]
 */
public class SerialisationUtil {
    private static final Logger logger = Logger.getLogger(SerialisationUtil.class.getName());

    /**
     * Serialises a list of strings into a format suitable for file storage.
     *
     * @param messageList the list of strings to serialise
     * @return the serialised representation of the entire list
     */
    public static String serialiseList(List<String> messageList) {
        assert messageList != null : "serialiseList message list must not be null";
        logger.log(Level.FINEST, "Serialising list");

        StringBuilder stringBuilder = new StringBuilder();
        for (String message : messageList) {
            stringBuilder.append(serialiseMessage(message));
        }
        String serialisedList = serialiseMessage(stringBuilder.toString());

        logger.log(Level.FINEST, "Successful serialising list");
        return serialisedList;
    }

    /**
     * Serialises a single string into a format suitable for file storage.
     *
     * @param message the string to serialise
     * @return the serialised string
     */
    public static String serialiseMessage(String message) {
        assert message != null : "serialiseMessage message must not be null";

        String serialisedMessage = message.length() + FormatConstants.START_DELIMITER + message + FormatConstants.END_DELIMITER;

        logger.log(Level.FINEST, "Successful serialising message: " + serialisedMessage);
        return serialisedMessage;
    }
}
