package modhero.data.timetable;

import static modhero.common.Constants.AcademicConstants.MAX_MODULES_PER_SEM;
import static modhero.common.Constants.AcademicConstants.NUM_TERMS;
import static modhero.common.Constants.AcademicConstants.NUM_YEARS;

import modhero.data.major.MajorModule;
import modhero.exceptions.ModuleNotFoundException;
import modhero.data.modules.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a multi-year academic timetable, storing modules organized
 * by year and term.
 */
public class Timetable {
    public static final Logger logger = Logger.getLogger(Timetable.class.getName());

    private final int COL_WIDTH = 15; // standard column width for all cells
    private List<List<List<Module>>> timetable;

    /**
     * Creates a timetable structure for the specified number of years and terms.
     * Each year contains a list of terms, which in turn contain modules.
     *
     */
    public Timetable() {
        timetable = new ArrayList<>();

        // Initialize the timetable
        for (int year = 0; year < NUM_YEARS; year++) {
            List<List<Module>> yearSemesters = new ArrayList<>();
            for (int sem = 0; sem < NUM_TERMS; sem++) {
                yearSemesters.add(new ArrayList<>()); // each semester starts empty
            }
            timetable.add(yearSemesters);
        }

        logger.log(Level.FINE, () -> String.format("Timetable initialised for %d years and %d terms", NUM_YEARS, NUM_TERMS));
    }

    /**
     * Adds a module to the timetable, performing all business logic checks.
     *
     * @param module The Module object to add.
     * @param year The academic year (1-based).
     * @param semester The semester (1-based).
     * @return A CommandResult string indicating success, overload, or failure.
     */
    public String addModule(int year, int semester, Module module) {
        // 1. Check if already exists in timetable
        if (this.getAllModules().stream().anyMatch(m -> m.getCode().equalsIgnoreCase(module.getCode()))) {
            return module.getCode() + " is already in your timetable!";
        }

        // 2. Check prerequisites
        PrereqGraph prereqGraph = new PrereqGraph(this.getAllModules());
        if (!prereqGraph.hasMetPrerequisites(module)) {
            return "Prerequisites not met for " + module.getCode();
        }

        // 3. Add to timetable (using the 0-based private method)
        this.addModuleInternal(year - 1, semester - 1, module);

        // 4. Check for overload *after* adding
        if (this.getModules(year - 1, semester - 1).size() > MAX_MODULES_PER_SEM) {
            return "You are overloading this semester! Please seek help if you need to. ("
                    + module.getCode() + " added)";
        }

        // 5. Success
        return module.getCode() + " added successfully to Y" + year + "S" + semester + "!";
    }

    /**
     * Internal method to add a module to a specific year and term.
     * No checks are performed here.
     *
     * @param year the year index (0-based)
     * @param term the term index (0-based)
     * @param module the module to add
     */
    public void addModuleInternal(int year, int term, Module module) {
        assert year >= 0 && year < NUM_YEARS : "addModule year out of bounds";
        assert term >= 0 && term < NUM_TERMS : "addModule term out of bounds";
        assert module != null : "addModule module must not be null";

        timetable.get(year).get(term).add(module);
        logger.log(Level.FINEST, () -> String.format("Module %s added to year %d term %d", module.getCode(), year, term));
    }

    /**
     * Removes a module from a specific year and term by module code.
     *
     * @param year the year index (0-based)
     * @param term the term index (0-based)
     * @param moduleCode the code of the module to remove
     * @return {@code true} if a module was removed, {@code false} otherwise
     */
    public boolean removeModule(int year, int term, String moduleCode) throws ArrayIndexOutOfBoundsException{
        if ( year < 0 || year > NUM_YEARS || term < 0 || term > NUM_TERMS){
            throw new ArrayIndexOutOfBoundsException();
        }

        boolean hasRemoved = timetable.get(year).get(term)
                .removeIf(m -> m.getCode().equals(moduleCode));

        if (hasRemoved) {
            logger.log(Level.FINEST, () -> String.format("Module %s removed from year %d term %d", moduleCode, year, term));
        }
        return hasRemoved;
    }
    /**
     * Finds the year and term indices where a module is scheduled.
     *
     * @param moduleCode the code of the module to find
     * @return an int array [year, term] where the module is found, or null if not found
     */
    public int[] findModuleLocation(String moduleCode) throws ModuleNotFoundException {
        for (int year = 0; year < timetable.size(); year++) {
            List<List<Module>> yearSemesters = timetable.get(year);
            for (int term = 0; term < yearSemesters.size(); term++) {
                List<Module> modules = yearSemesters.get(term);
                for (Module module : modules) {
                    if (module.getCode().equals(moduleCode)) {
                        return new int[]{year, term};
                    }
                }
            }
        }

        throw new ModuleNotFoundException(moduleCode);
    }

    public void deleteModule(String code) throws ModuleNotFoundException {
        int[] moduleLocation = new int[2]; // {year, term}
        try {
            moduleLocation = findModuleLocation(code);
        }catch (ModuleNotFoundException e){
            throw e;
        }
        try{
            removeModule(moduleLocation[0], moduleLocation[1], code);
        }catch (ArrayIndexOutOfBoundsException e){
            throw new ModuleNotFoundException(code);
        }
    }

    /**
     * Retrieves the list of modules for a given year and term.
     *
     * @param year the year index (0-based)
     * @param term the term index (0-based)
     * @return list of modules in the specified term
     */
    public List<Module> getModules(int year, int term) {
        assert year >= 0 && year < NUM_YEARS : "getModules year out of bounds";
        assert term >= 0 && term < NUM_TERMS : "getModules term out of bounds";

        return timetable.get(year).get(term);
    }

    /**
     * Retrieves all modules across all years and terms in the timetable.
     *
     * @return a flat list of all modules
     */
    public List<Module> getAllModules() {
        List<Module> all = new ArrayList<>();
        for (List<List<Module>> year : timetable) {
            for (List<Module> term : year) {
                all.addAll(term);
            }
        }
        return all;
    }

    /**
     * Prints a formatted view of the timetable to the console,
     * organized by year and term in table format.
     */
    public void printTimetable() {
        int COL_WIDTH = 20;

        for (int year = 0; year < timetable.size(); year++) {
            System.out.println("+--------------------+--------------------+");
            String yearTitle = "YEAR " + (year + 1);
            System.out.printf("%22s\n", yearTitle);
            System.out.println("+--------------------+--------------------+");
            System.out.printf("|%-20s|%-20s|\n", "Semester 1", "Semester 2");
            System.out.println("+--------------------+--------------------+");

            List<List<Module>> semesters = timetable.get(year);
            int maxRows = Math.max(semesters.get(0).size(), semesters.get(1).size());

            for (int row = 0; row < maxRows; row++) {
                String s1 = row < semesters.get(0).size() ? semesters.get(0).get(row).getCode() : "";
                String s2 = row < semesters.get(1).size() ? semesters.get(1).get(row).getCode() : "";
                System.out.printf("|%-20s|%-20s|\n", s1, s2);
            }

            System.out.println("+--------------------+--------------------+\n");
        }
    }


    /**
     * Pads the given text with spaces or truncates it so that
     * it fits within a fixed-width table cell.
     *
     * @param text the text to pad
     * @param width the fixed width of the cell
     * @return the padded (or truncated) string
     */
    private String padCell(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width - 1) + " "; // truncate if too long
        }
        int spaces = width - text.length();
        return text + " ".repeat(spaces);
    }

    /**
     * Removes all modules from the entire timetable.
     */
    public void clearTimetable() {
        for (List<List<Module>> year : timetable) {
            for (List<Module> sem : year) {
                sem.clear();
            }
        }
    }
}