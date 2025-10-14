package modhero.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Serialiser}.
 */
public class SerialiserTest {

    private Serialiser serialiser;

    @BeforeEach
    void setUp() {
        serialiser = new Serialiser();
    }

    // serialiseMessage()
    @Test
    void testSerialiseMessage_basic() {
        String result = serialiser.serialiseMessage("CS1010");
        assertEquals("6#CS1010|", result);
    }

    @Test
    void testSerialiseMessage_emptyString() {
        String result = serialiser.serialiseMessage("");
        assertEquals("0#|", result);
    }

    // serialiseList()
    @Test
    void testSerialiseList_basic() {
        List<String> list = List.of("CS1010", "CS2040");
        String result = serialiser.serialiseList(list);

        // Each message serialised individually and prefixed by total length
        // "6#CS1010|7#CS2040|" -> 14 characters
        assertEquals("14#6#CS1010|7#CS2040||", result);
    }

    @Test
    void testSerialiseList_emptyList() {
        String result = serialiser.serialiseList(List.of());
        // Expect 0-length prefix followed by delimiters
        assertEquals("0##|", result);
    }

    // deserialiseMessage()
    @Test
    void testDeserialiseMessage_basic() {
        String serialised = "6#CS1010|7#CS2040|";
        List<String> result = serialiser.deserialiseMessage(serialised);
        assertEquals(List.of("CS1010", "CS2040"), result);
    }

    @Test
    void testDeserialiseMessage_handlesCorruptedDelimiterGracefully() {
        String corrupted = "6CS1010|"; // missing #
        List<String> result = serialiser.deserialiseMessage(corrupted);
        assertTrue(result.isEmpty(), "Should return empty list when missing delimiter");
    }

    @Test
    void testDeserialiseMessage_handlesInvalidLengthGracefully() {
        String corrupted = "X#CS1010|"; // X is not a number
        List<String> result = serialiser.deserialiseMessage(corrupted);
        assertTrue(result.isEmpty(), "Should return empty list when length is invalid");
    }

    // deserialiseList()
    @Test
    void testDeserialiseList_multipleMessages() {
        List<String> serialisedList = List.of("6#CS1010|", "7#CS2040|");
        List<List<String>> result = serialiser.deserialiseList(serialisedList);

        assertEquals(2, result.size());
        assertEquals(List.of("CS1010"), result.get(0));
        assertEquals(List.of("CS2040"), result.get(1));
    }

    @Test
    void testDeserialiseList_emptyInput() {
        List<List<String>> result = serialiser.deserialiseList(List.of());
        assertTrue(result.isEmpty(), "Empty input should produce empty output");
    }

    // private helper (indirect)
    @Test
    void testHandlesTruncatedStringGracefully() {
        String incomplete = "6#CS10"; // incomplete message (missing characters)
        List<String> result = serialiser.deserialiseMessage(incomplete);
        assertTrue(result.isEmpty(), "Should return empty list when message truncated");
    }
}
