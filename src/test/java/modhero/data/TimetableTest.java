package modhero.data;

import modhero.data.modules.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Timetable}.
 */
public class TimetableTest {

    private Timetable timetable;
    private Module cs1010;
    private Module cs2040;
    private Module cs2100;

    @BeforeEach
    void setUp() {
        timetable = new Timetable(4, 4);
        cs1010 = new Module("CS1010", "Programming Methodology", 4, "core", List.of());
        cs2040 = new Module("CS2040", "Data Structures", 4, "core", List.of("CS1010"));
        cs2100 = new Module("CS2100", "Computer Organisation", 4, "core", List.of("CS1010"));
    }

    @Test
    void testAddAndGetModules() {
        timetable.addModule(0, 0, cs1010);
        timetable.addModule(0, 1, cs2040);

        List<Module> term1Modules = timetable.getModules(0, 0);
        List<Module> term2Modules = timetable.getModules(0, 1);

        assertEquals(1, term1Modules.size());
        assertEquals("CS1010", term1Modules.get(0).getCode());
        assertEquals(1, term2Modules.size());
        assertEquals("CS2040", term2Modules.get(0).getCode());
    }

    @Test
    void testRemoveModuleRemovesCorrectly() {
        timetable.addModule(0, 0, cs1010);
        timetable.addModule(0, 1, cs2040);

        boolean removed = timetable.removeModule(1, 1, "CS1010"); // removeModule uses 1-based indexing internally
        assertTrue(removed, "Expected CS1010 to be removed");

        // CS1010 should be gone now
        assertTrue(timetable.getModules(0, 0).isEmpty());
    }

    @Test
    void testRemoveModuleNonExistentReturnsFalse() {
        boolean result = timetable.removeModule(1, 1, "NON_EXISTENT");
        assertFalse(result, "Should return false when module does not exist");
    }

    @Test
    void testGetAllModulesAggregatesCorrectly() {
        timetable.addModule(0, 0, cs1010);
        timetable.addModule(0, 1, cs2040);
        timetable.addModule(1, 0, cs2100);

        List<Module> all = timetable.getAllModules();
        assertEquals(3, all.size());
        assertTrue(all.contains(cs1010));
        assertTrue(all.contains(cs2040));
        assertTrue(all.contains(cs2100));
    }

    @Test
    void testClearTimetableRemovesEverything() {
        timetable.addModule(0, 0, cs1010);
        timetable.addModule(0, 1, cs2040);

        timetable.clearTimetable();

        // Every term in every year should now be empty
        for (int year = 0; year < 4; year++) {
            for (int term = 0; term < 4; term++) {
                assertTrue(timetable.getModules(year, term).isEmpty(), "Expected all modules cleared");
            }
        }
    }

    @Test
    void testEmptyTimetableInitially() {
        for (int year = 0; year < 4; year++) {
            for (int term = 0; term < 4; term++) {
                assertTrue(timetable.getModules(year, term).isEmpty(), "Timetable should start empty");
            }
        }
    }
}
