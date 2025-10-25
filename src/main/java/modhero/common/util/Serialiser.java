package modhero.common.util;

import modhero.common.exceptions.CorruptedDataFileException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static modhero.common.Constants.FORMATED_STRING_START_DELIMITER;
import static modhero.common.Constants.FORMATED_STRING_END_DELIMITER;

/**
 * Serialises a string message into a format suitable for storage.
 */
public class Serialiser {
    private static final Logger logger = Logger.getLogger(Serialiser.class.getName());

    /**
     * Serialise list of string to be suitable format for storing into text file.
     *
     * @param messageList string of data to be stored
     * @return the serialised string
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
     * Serialise string to be suitable format for storing into text file.
     *
     * @param message string of data to be stored
     * @return the serialised string
     */
    public static String serialiseMessage(String message) {
        assert message != null : "serialiseMessage message must not be null";

        String serialisedMessage = message.length() + FORMATED_STRING_START_DELIMITER + message + FORMATED_STRING_END_DELIMITER;

        logger.log(Level.FINEST, "Successful serialising message: " + serialisedMessage);
        return serialisedMessage;
    }
}
