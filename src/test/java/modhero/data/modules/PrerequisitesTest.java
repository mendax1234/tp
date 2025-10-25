package modhero.data.modules;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrerequisitesTest {

    @Test
    void getPrereqReturnsNewListOnEmptyInput() {
        Prerequisites prerequisites = new Prerequisites();
        assertTrue(prerequisites.getPrereq().isEmpty());
    }

    @Test
    void getPrereqReturnsOriginalList() {
        List<List<String>> input = List.of(List.of("CS2113"), List.of("CS2040C","CS2040"));
        Prerequisites prerequisites = new Prerequisites(input);
        assertSame(input, prerequisites.getPrereq());
    }

    @Test
    void toFormatedStringOnNoInput() {
        Prerequisites prerequisites = new Prerequisites();
        assertEquals("0#|",prerequisites.toFormatedString());
    }

    @Test
    void toFormatedStringProducesExpectedSerialization() {
        List<List<String>> input = List.of(List.of("CS2113"), List.of("CS2040C","CS2040"));
        Prerequisites prerequisites = new Prerequisites(input);
        System.out.println(input);
        assertEquals("35#9#6#CS2113||19#7#CS2040C|6#CS2040|||",prerequisites.toFormatedString());
    }
}