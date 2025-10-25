package modhero.data.major;

import modhero.data.modules.Module;
import modhero.data.modules.ModuleList;
import modhero.data.modules.Prerequisites;
import java.util.*;

/**
 * Predefined data source for supported majors (Computer Science, Computer Engineering).
 * Stores their core modules and recommended year/semester mapping.
 */
public class MajorData {

    private final Map<String, ModuleList> majorToCoreModules = new HashMap<>();
    private final Map<String, Map<String, int[]>> majorToSchedule = new HashMap<>();

    public MajorData() {
        initComputerScience();
        initComputerEngineering();
    }

    /** ---------------- COMPUTER SCIENCE ---------------- */
    private void initComputerScience() {
        ModuleList csModules = new ModuleList();

        // Core modules (basic prerequisites simplified)
        Module cs1010s = new Module("CS1010S", "Programming Methodology", 4, "core", new Prerequisites());
        Module cs1231s = new Module("CS1231S", "Discrete Structures", 4, "core", new Prerequisites());
        Module cs2030s = new Module("CS2030S", "Programming Methodology II", 4, "core",
                new Prerequisites(List.of(List.of("CS1010S"))));
        Module cs2040s = new Module("CS2040S", "Data Structures and Algorithms", 4, "core",
                new Prerequisites(List.of(List.of("CS1010S", "CS2030S"))));
        Module cs2100 = new Module("CS2100", "Computer Organisation", 4, "core",
                new Prerequisites(List.of(List.of("CS1010S"))));
        Module cs2101 = new Module("CS2101", "Effective Communication for Computing Professionals", 4, "core",
                new Prerequisites());
        Module cs2103t = new Module("CS2103T", "Software Engineering", 4, "core",
                new Prerequisites(List.of(List.of("CS2040S"))));
        Module cs2106 = new Module("CS2106", "Introduction to Operating Systems", 4, "core",
                new Prerequisites(List.of(List.of("CS2100"))));
        Module cs2109s = new Module("CS2109S", "Introduction to AI and Machine Learning", 4, "core",
                new Prerequisites(List.of(List.of("CS2040S"))));
        Module cs3230 = new Module("CS3230", "Design and Analysis of Algorithms", 4, "core",
                new Prerequisites(List.of(List.of("CS2040S"))));

        csModules.add(cs1010s);
        csModules.add(cs1231s);
        csModules.add(cs2030s);
        csModules.add(cs2040s);
        csModules.add(cs2100);
        csModules.add(cs2101);
        csModules.add(cs2103t);
        csModules.add(cs2106);
        csModules.add(cs2109s);
        csModules.add(cs3230);

        majorToCoreModules.put("Computer Science", csModules);
        majorToCoreModules.put("CS", csModules);

        // Recommended schedule (moduleCode → [year, sem])
        Map<String, int[]> csSchedule = new HashMap<>();
        csSchedule.put("CS1010S", new int[]{1, 1});
        csSchedule.put("CS1231S", new int[]{1, 1});
        csSchedule.put("CS2030S", new int[]{1, 2});
        csSchedule.put("CS2040S", new int[]{1, 2});
        csSchedule.put("CS2100", new int[]{2, 1});
        csSchedule.put("CS2101", new int[]{1, 2});
        csSchedule.put("CS2103T", new int[]{2, 1});
        csSchedule.put("CS2106", new int[]{2, 1});
        csSchedule.put("CS2109S", new int[]{2, 2});
        csSchedule.put("CS3230", new int[]{3, 1});

        majorToSchedule.put("Computer Science", csSchedule);
        majorToSchedule.put("CS", csSchedule);
    }

    /** ---------------- COMPUTER ENGINEERING ---------------- */
    private void initComputerEngineering() {
        ModuleList cegModules = new ModuleList();

        Module cg1111a = new Module("CG1111A", "Engineering Principles and Practice I", 4, "core", new Prerequisites());
        Module cg2111a = new Module("CG2111A", "Engineering Principles and Practice II", 4, "core",
                new Prerequisites(List.of(List.of("CG1111A"))));
        Module cg2023 = new Module("CG2023", "Signals and Systems", 4, "core", new Prerequisites());
        Module cg2027 = new Module("CG2027", "Transistor-Level Digital Circuits", 2, "core", new Prerequisites());
        Module cg2028 = new Module("CG2028", "Computer Organisation", 2, "core", new Prerequisites());
        Module cg2271 = new Module("CG2271", "Real-Time Operating Systems", 4, "core",
                new Prerequisites(List.of(List.of("CG2028"))));
        Module cs1231 = new Module("CS1231", "Discrete Structures", 4, "core", new Prerequisites());
        Module cs2040c = new Module("CS2040C", "Data Structures and Algorithms", 4, "core",
                new Prerequisites(List.of(List.of("CS1010"))));
        Module cs2113 = new Module("CS2113", "Software Engineering & OOP", 4, "core",
                new Prerequisites(List.of(List.of("CS2040C"))));
        Module ee2026 = new Module("EE2026", "Digital Design", 4, "core", new Prerequisites());
        Module ee4204 = new Module("EE4204", "Computer Networks", 4, "core",
                new Prerequisites(List.of(List.of("EE2026"))));
        Module cs1010 = new Module("CS1010", "Programming Methodology", 4, "core", new Prerequisites());

        cegModules.add(cg1111a);
        cegModules.add(cg2111a);
        cegModules.add(cg2023);
        cegModules.add(cg2027);
        cegModules.add(cg2028);
        cegModules.add(cg2271);
        cegModules.add(cs1231);
        cegModules.add(cs2040c);
        cegModules.add(cs2113);
        cegModules.add(ee2026);
        cegModules.add(ee4204);
        cegModules.add(cs1010);

        majorToCoreModules.put("Computer Engineering", cegModules);
        majorToCoreModules.put("CEG", cegModules);

        Map<String, int[]> cegSchedule = new HashMap<>();
        cegSchedule.put("CG1111A", new int[]{1, 1});
        cegSchedule.put("CS1010", new int[]{1, 1});
        cegSchedule.put("CS1231", new int[]{1, 1});
        cegSchedule.put("CG2111A", new int[]{1, 2});
        cegSchedule.put("CG2023", new int[]{1, 2});
        cegSchedule.put("CS2040C", new int[]{1, 2});
        cegSchedule.put("EE2026", new int[]{2, 1});
        cegSchedule.put("CS2113", new int[]{2, 1});
        cegSchedule.put("CG2027", new int[]{2, 2});
        cegSchedule.put("CG2028", new int[]{2, 2});
        cegSchedule.put("CG2271", new int[]{2, 2});
        cegSchedule.put("EE4204", new int[]{3, 1});

        majorToSchedule.put("Computer Engineering", cegSchedule);
        majorToSchedule.put("CEG", cegSchedule);
    }

    public ModuleList getCoreModules(String majorName) {
        return majorToCoreModules.get(majorName);
    }

    public Map<String, int[]> getSchedule(String majorName) {
        return majorToSchedule.get(majorName);
    }
}
