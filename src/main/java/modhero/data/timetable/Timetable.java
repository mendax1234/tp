package modhero.data.timetable;

import static modhero.common.Constants.AcademicConstants;
import static modhero.common.Constants.MessageConstants;

import modhero.exceptions.InvalidYearOrSemException;
import modhero.exceptions.ModHeroException;
import modhero.exceptions.ModuleAlreadyExistsException;
import modhero.exceptions.ModuleNotFoundException;
import modhero.data.modules.Module;
import modhero.data.modules.Prerequisites;
import modhero.exceptions.PrerequisiteNotMetException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Represents a multi-year academic timetable, storing modules organized
 * by year and term.
 */
public class Timetable {
    public static final Logger logger = Logger.getLogger(Timetable.class.getName());

    private List<List<List<Module>>> timetable;

    /**
     * Creates a timetable structure for the specified number of years and terms.
     * Each year contains a list of terms, which in turn contain modules.
     *
     */
    public Timetable() {
        timetable = new ArrayList<>();

        // Initialize the timetable
        for (int year = 0; year < AcademicConstants.NUM_YEARS; year++) {
            List<List<Module>> yearSemesters = new ArrayList<>();
            for (int sem = 0; sem < AcademicConstants.NUM_TERMS; sem++) {
                yearSemesters.add(new ArrayList<>()); // each semester starts empty
            }
            timetable.add(yearSemesters);
        }

        logger.log(Level.FINE, () -> String.format("Timetable initialised for %d years and %d terms", AcademicConstants.NUM_YEARS, AcademicConstants.NUM_TERMS));
    }

    /**
     * Adds a module to the timetable, performing all business logic checks.
     *
     * @param module   The Module object to add.
     * @param year     The academic year (1-based).
     * @param semester The semester (1-based).
     * @return A CommandResult string indicating success, overload, or failure.
     */
    public void addModule(int year, int semester, Module module) throws ModHeroException {
        // Bounds check
        if (year < 1 || year > AcademicConstants.NUM_YEARS ||
                semester < 1 || semester > AcademicConstants.NUM_TERMS) {
            throw new InvalidYearOrSemException(year, semester);
        }

        // Check addability
        checkModuleAddable(year, semester, module);

        // Add to timetable
        addModuleInternal(year - 1, semester - 1, module);
    }



    private boolean checkIsLevel1000Module(String moduleCode){
        if (moduleCode == null || moduleCode.isEmpty()) {
            return false;
        }
        for (char c : moduleCode.toCharArray()) {
            if (Character.isDigit(c)) {
                return c == '1';
            }
        }
        return false;
    }

    private void checkModuleAddable(int year, int semester, Module moduleToAdd) throws ModHeroException {
        if (getAllModules().stream().anyMatch(m -> m.getCode().equalsIgnoreCase(moduleToAdd.getCode()))) {
            throw new ModuleAlreadyExistsException(moduleToAdd.getCode());
        }

        //check if module is 1k, if yes then it has no prerequisites
        boolean isLevel1000Module = checkIsLevel1000Module(moduleToAdd.getCode());
        if (isLevel1000Module){
            return;
        }

        List<Module> completedModules = getModulesTakenUpTo(year, semester);
        List<String> completedCodes = completedModules.stream().map(Module::getCode).toList();

        Prerequisites prereqs = moduleToAdd.getPrerequisites();
        List<List<String>> prereqSets = prereqs.getPrereq();

        if (prereqSets == null || prereqSets.isEmpty()) return;

        boolean satisfied = prereqSets.stream().anyMatch(option ->
                option.stream().allMatch(completedCodes::contains)
        );

        if (!satisfied) {
            throw new PrerequisiteNotMetException(moduleToAdd.getCode(), prereqs.toString());
        }
    }

    /**
     * Internal method to add a module to a specific year and term.
     * No checks are performed here.
     *
     * @param year   the year index (0-based)
     * @param term   the term index (0-based)
     * @param module the module to add
     */
    public void addModuleInternal(int year, int term, Module module) {
        timetable.get(year).get(term).add(module);
        logger.log(Level.FINEST, () -> String.format("Module %s added to year %d term %d", module.getCode(), year, term));
    }

    /**
     * Removes a module from a specific year and term by module code.
     *
     * @param year       the year index (0-based)
     * @param term       the term index (0-based)
     * @param moduleCode the code of the module to remove
     * @return {@code true} if a module was removed, {@code false} otherwise
     */
    public boolean removeModule(int year, int term, String moduleCode) throws ArrayIndexOutOfBoundsException {
        if (year < 0 || year > AcademicConstants.NUM_YEARS || term < 0 || term > AcademicConstants.NUM_TERMS) {
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

        throw new ModuleNotFoundException(moduleCode, "timetable");
    }

    public void deleteModule(String moduleCode) throws ModuleNotFoundException {
        int[] moduleLocation; // {year, term}
        try {
            moduleLocation = findModuleLocation(moduleCode);
        } catch (ModuleNotFoundException e) {
            throw e;
        }
        try {
            removeModule(moduleLocation[0], moduleLocation[1], moduleCode);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ModuleNotFoundException(moduleCode, "timetable. " + MessageConstants.ARRAY_INDEX_OUT_BOUND);
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
        assert year >= 0 && year < AcademicConstants.NUM_YEARS : "getModules year out of bounds";
        assert term >= 0 && term < AcademicConstants.NUM_TERMS : "getModules term out of bounds";

        return timetable.get(year).get(term);
    }

    /**
     * Retrieves all modules taken up to and including a specific year and term.
     *
     * @param targetYear the target year (1-based)
     * @param targetSem  the target semester (1-based)
     * @return a flat list of all modules taken in or before the specified term
     */
    private List<Module> getModulesTakenUpTo(int targetYear, int targetSem) {
        List<Module> completedModules = new ArrayList<>();
        int targetYearIdx = targetYear - 1;
        int targetSemIdx = targetSem - 1;

        for (int y = 0; y < AcademicConstants.NUM_YEARS; y++) {
            for (int s = 0; s < AcademicConstants.NUM_TERMS; s++) {
                if (y < targetYearIdx || (y == targetYearIdx && s <= targetSemIdx)) {
                    try {
                        completedModules.addAll(timetable.get(y).get(s));
                    } catch (IndexOutOfBoundsException e) {
                        // Should not happen, but good to log
                        logger.log(Level.WARNING, "Error accessing timetable slot: Y" + y + " S" + s, e);
                    }
                }
            }
        }
        return completedModules;
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
