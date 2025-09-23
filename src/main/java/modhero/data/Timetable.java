package modhero.data;

import modhero.data.modules.Module;
import java.util.ArrayList;
import java.util.List;

public class Timetable {
    private List<List<List<Module>>> timetable;

    public Timetable(int numYears, int numTermsPerYear) {
        timetable = new ArrayList<>();

        // Initialize structure
        for (int year = 0; year < numYears; year++) {
            List<List<Module>> yearSemesters = new ArrayList<>();
            for (int sem = 0; sem < numTermsPerYear; sem++) {
                yearSemesters.add(new ArrayList<>()); // each semester starts empty
            }
            timetable.add(yearSemesters);
        }
    }

    /** Add a module to a specific year and term */
    public void addModule(int year, int term, Module module) {
        timetable.get(year).get(term).add(module);
    }

    /** Remove a module by code */
    public boolean removeModule(int year, int term, String moduleCode) {
        return timetable.get(year).get(term)
                .removeIf(m -> m.getCode().equals(moduleCode));
    }

    /** Get modules for a specific year and term */
    public List<Module> getModules(int year, int term) {
        return timetable.get(year).get(term);
    }

    /** Get all modules across all years/semesters */
    public List<Module> getAllModules() {
        List<Module> all = new ArrayList<>();
        for (List<List<Module>> year : timetable) {
            for (List<Module> term : year) {
                all.addAll(term);
            }
        }
        return all;
    }

    /** Pretty-print timetable */
    public void printTimetable() {
        for (int year = 0; year < timetable.size(); year++) {
            System.out.println("Year " + (year + 1) + ":");
            for (int term = 0; term < timetable.get(year).size(); term++) {
                System.out.print("  Semester " + (term + 1) + ": ");
                List<Module> modules = timetable.get(year).get(term);
                if (modules.isEmpty()) {
                    System.out.println("No modules");
                } else {
                    for (Module m : modules) {
                        System.out.print(m.getCode() + " ");
                    }
                    System.out.println();
                }
            }
        }
    }

    /** Clear all modules in the timetable */
    public void clearTimetable() {
        for (List<List<Module>> year : timetable) {
            for (List<Module> sem : year) {
                sem.clear();
            }
        }
    }
}
