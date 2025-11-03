package modhero.common.util;

import modhero.exceptions.CorruptedDataFileException; // Must import the exception
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SerialisationUtil}.
 */
public class SerialisationUtilTest {


    // --- serialiseMessage() ---
    @Test
    void testSerialiseMessage_basic() {
        String result = SerialisationUtil.serialiseMessage("CS1010");
        assertEquals("6#CS1010|", result);
    }

    @Test
    void testSerialiseMessage_emptyString() {
        String result = SerialisationUtil.serialiseMessage("");
        assertEquals("0#|", result);
    }

    // --- serialiseList() ---
    @Test
    void testSerialiseList_basic() {
        List<String> list = List.of("CS1010", "CS2040");
        String result = SerialisationUtil.serialiseList(list);

        // This test was correct:
        // "6#CS1010|" + "7#CS2040|" = "6#CS1010|7#CS2040|" (Length 14)
        // serialiseMessage("6#CS1010|7#CS2040|") = "14#6#CS1010|7#CS2040||"
        assertEquals("18#6#CS1010|6#CS2040||", result);
    }

    @Test
    void testSerialiseList_emptyList() {
        String result = SerialisationUtil.serialiseList(List.of());
        // serialiseList(empty) calls serialiseMessage("") which returns "0#|"
        assertEquals("0#|", result, "Empty list should serialise to what an empty string serialises to");
    }
}
