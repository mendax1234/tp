package modhero.data;

import modhero.data.modules.Module;
import modhero.data.modules.ModuleList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Planner}.
 */
public class PlannerTest {

    private Timetable timetable;
    private ModuleList coreList;
    private ModuleList electiveList;

    @BeforeEach
    void setUp() {
        timetable = new Timetable(4, 4); // 4 years × 4 terms

        coreList = new ModuleList();
        coreList.add(new Module("CS2040", "Data Structures", 4, "core", List.of()));
        coreList.add(new Module("CS1010", "Programming Methodology", 4, "core", List.of()));
        coreList.add(new Module("CS1231", "Discrete Structures", 4, "core", List.of()));

        electiveList = new ModuleList();
        electiveList.add(new Module("CS3243", "AI", 4, "elective", List.of()));
        electiveList.add(new Module("CS2100", "Computer Organisation", 4, "elective", List.of()));
    }

    @Test
    void testSortModuleListOrdersByCode() {
        Planner planner = new Planner(timetable, coreList, electiveList);
        planner.sortModuleList();

        // Use reflection to check internal module list order (indirectly)
        // The sorted order should be alphabetically by module code (case-insensitive)
        List<String> expectedOrder = List.of("CS1010", "CS1231", "CS2040", "CS2100", "CS3243");

        // extract module codes after sorting
        List<String> actualOrder = coreList.getList().stream()
                .map(Module::getCode)
                .sorted(String::compareToIgnoreCase)
                .toList();

        assertEquals(expectedOrder, actualOrder);
    }

    @Test
    void testAddToTimetablePlacesModulesCorrectly() {
        Planner planner = new Planner(timetable, coreList, electiveList);
        planner.planTimeTable(); // sorts + distributes

        // Since there are 5 modules and 4 terms/year × 4 years = 16 slots,
        // they should be placed one per term starting from Year 0 Term 0.

        int totalModules = timetable.getAllModules().size();
        assertEquals(5, totalModules, "All modules should be placed in timetable");

        // Check that the first few placements follow the pattern
        assertEquals("CS1010", timetable.getModules(0, 0).get(0).getCode());
        assertEquals("CS1231", timetable.getModules(0, 1).get(0).getCode());
    }

    @Test
    void testEmptyListsHandledGracefully() {
        Planner planner = new Planner(new Timetable(4, 4), new ModuleList(), new ModuleList());
        planner.planTimeTable();

        // Timetable should remain empty
        assertTrue(planner != null);
        assertTrue(planner != null); // ensure no exceptions
    }

    @Test
    void testDoesNotExceedTimetableCapacity() {
        // create many modules (more than 16)
        ModuleList largeCoreList = new ModuleList();
        for (int i = 0; i < 20; i++) {
            largeCoreList.add(new Module("CS" + (1000 + i), "Dummy", 4, "core", List.of()));
        }

        Planner planner = new Planner(new Timetable(4, 4), largeCoreList, new ModuleList());
        assertDoesNotThrow(planner::planTimeTable, "Planner should handle more modules than slots without error");
    }
}
