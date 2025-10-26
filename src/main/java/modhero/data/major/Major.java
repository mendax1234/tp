package modhero.data.major;

import modhero.data.modules.ModuleList;
import java.util.Map;

/**
 * Represents a Major, holding its name, abbreviation,
 * list of core modules, and the recommended schedule.
 */
public class Major {
    private final String abbrName;
    private final String name;
    private final ModuleList coreModules;
    private final Map<String, int[]> moduleSchedule;

    /**
     * Constructs a new Major.
     *
     * @param abbrName The abbreviation (e.g., "CS").
     * @param name The full name (e.g., "Computer Science").
     * @param coreModules The list of core Module objects.
     * @param moduleSchedule A map of [Module Code -> int[year, semester]].
     */
    public Major(String abbrName, String name, ModuleList coreModules, Map<String, int[]> moduleSchedule) {
        this.abbrName = abbrName;
        this.name = name;
        this.coreModules = coreModules;
        this.moduleSchedule = moduleSchedule;
    }

    public String getAbbrName() {
        return abbrName;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the list of core modules for this major.
     */
    public ModuleList getCoreModules() {
        return coreModules;
    }

    /**
     * Gets the recommended schedule for the core modules.
     *
     * @return A Map where Key = Module Code, Value = int[year, semester].
     */
    public Map<String, int[]> getModuleSchedule() {
        return moduleSchedule;
    }
}
