package modhero.data;

import modhero.data.modules.Module;
import java.util.ArrayList;
import java.util.List;

public class Timetable {
    private List<List<List<Module>>> timetable;

    public Timetable(int numYears, int numSemestersPerYear) {
        timetable = new ArrayList<>();

        // Initialize structure
        for (int year = 0; year < numYears; year++) {
            List<List<Module>> yearSemesters = new ArrayList<>();
            for (int sem = 0; sem < numSemestersPerYear; sem++) {
                yearSemesters.add(new ArrayList<>()); // each semester starts empty
            }
            timetable.add(yearSemesters);
        }
    }

    /** Add a module to a specific year and semester */
    public void addModule(int year, int semester, Module module) {
        timetable.get(year).get(semester).add(module);
    }

    /** Remove a module by code */
    public boolean removeModule(int year, int semester, String moduleCode) {
        return timetable.get(year).get(semester)
                .removeIf(m -> m.getCode().equals(moduleCode));
    }

    /** Get modules for a specific year and semester */
    public List<Module> getModules(int year, int semester) {
        return timetable.get(year).get(semester);
    }

    /** Get all modules across all years/semesters */
    public List<Module> getAllModules() {
        List<Module> all = new ArrayList<>();
        for (List<List<Module>> year : timetable) {
            for (List<Module> sem : year) {
                all.addAll(sem);
            }
        }
        return all;
    }

    /** Pretty-print timetable */
    public void printTimetable() {
        for (int year = 0; year < timetable.size(); year++) {
            System.out.println("Year " + (year + 1) + ":");
            for (int sem = 0; sem < timetable.get(year).size(); sem++) {
                System.out.print("  Semester " + (sem + 1) + ": ");
                List<Module> modules = timetable.get(year).get(sem);
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
}
