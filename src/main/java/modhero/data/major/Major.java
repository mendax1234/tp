package modhero.data.major;

import modhero.data.timetable.TimetableData;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Major {
    public static final Logger logger = Logger.getLogger(Major.class.getName());

    private final String name;
    private final String abbrName;
    private final List<TimetableData> timetableData;

    /**
     * Creates a new major object.
     *
     * @param name the major name
     * @param abbrName the major abbreviation name
     * @param modules the list of core module object
     */
    public Major(String name, String abbrName, List<TimetableData> modules) {
        assert name != null && !name.isEmpty() : "Major name must not be empty";
        assert abbrName != null && !abbrName.isEmpty() : "Major abbreviation must not be empty";
        assert modules != null : "Module list must not be null";

        this.name = name;
        this.abbrName = abbrName;
        this.timetableData = modules;

        logger.log(Level.FINEST, () -> "Major created: " + name + " (" + abbrName + ")");
    }

    /** @return the major name */
    public String getName() {
        return name;
    }

    /** @return the abbreviation major name */
    public String getAbbrName() {
        return abbrName;
    }

    /** @return the list of core module objects for the major*/
    public List<TimetableData> getMajorModules() {
        return timetableData;
    }
}
