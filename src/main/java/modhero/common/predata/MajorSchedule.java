package modhero.common.predata;

import java.util.*;

/**
 * Stores predefined core modules and schedule mappings
 * for supported majors (Computer Science & Computer Engineering).
 */
public class MajorSchedule {
    /**
     * Returns a schedule mapping (moduleCode → [year, sem]) for each major.
     */
    public Map<String, int[]> getSchedule(String major) {
        major = major.trim().toLowerCase();

        if (major.equals("computer science")) {
            Map<String, int[]> csSchedule = new HashMap<>();
            csSchedule.put("CS1101S", new int[]{1, 1});
            csSchedule.put("CS1231S", new int[]{1, 1});
            csSchedule.put("CS2030S", new int[]{1, 2});
            csSchedule.put("CS2040S", new int[]{1, 2});
            csSchedule.put("CS2101", new int[]{1, 2});
            csSchedule.put("CS2100", new int[]{2, 1});
            csSchedule.put("CS2103T", new int[]{2, 1});
            csSchedule.put("CS2106", new int[]{2, 1});
            csSchedule.put("CS2109S", new int[]{2, 2});
            csSchedule.put("CS3230", new int[]{3, 1});
            return csSchedule;
        }

        if (major.equals("computer engineering")) {
            Map<String, int[]> cegSchedule = new HashMap<>();
            cegSchedule.put("CG1111A", new int[]{1, 1});
            cegSchedule.put("CS1010", new int[]{1, 1});
            cegSchedule.put("CS1231", new int[]{1, 1});
            cegSchedule.put("CG2111A", new int[]{1, 2});
            cegSchedule.put("CS2040C", new int[]{1, 2});
            cegSchedule.put("CG2023", new int[]{1, 2});
            cegSchedule.put("CS2113", new int[]{2, 1});
            cegSchedule.put("EE2026", new int[]{2, 1});
            cegSchedule.put("CG2027", new int[]{2, 2});
            cegSchedule.put("CG2028", new int[]{2, 2});
            cegSchedule.put("CG2271", new int[]{2, 2});
            cegSchedule.put("EE4204", new int[]{3, 1});
            return cegSchedule;
        }

        return new HashMap<>();
    }
}
