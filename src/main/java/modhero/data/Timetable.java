package modhero.data;

import modhero.data.modules.Module;
import modhero.data.modules.ModuleList;

import java.util.ArrayList;
import java.util.List;

public class Timetable {
    private final int COL_WIDTH = 15; // standard column width for all cells
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
        timetable.get(year-1).get(term-1).add(module);
    }

    /** Remove a module by code */
    public boolean removeModule(int year, int term, String moduleCode) {
        return timetable.get(year-1).get(term-1)
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
            System.out.println("=".repeat(COL_WIDTH * timetable.get(year).size() + timetable.get(year).size() + 1));
            String yearTitle = "YEAR " + (year + 1);
            int tableWidth = COL_WIDTH * timetable.get(year).size() + timetable.get(year).size() + 1;
            int padding = (tableWidth - yearTitle.length()) / 2;
            System.out.println(" ".repeat(Math.max(0, padding)) + yearTitle);
            System.out.println("=".repeat(COL_WIDTH * timetable.get(year).size() + timetable.get(year).size() + 1));

            List<List<Module>> yearTerms = timetable.get(year);

            // Header row
            System.out.print("|");
            for (int term = 0; term < yearTerms.size(); term++) {
                String header = "Term " + (term + 1);
                System.out.print(padCell(header, COL_WIDTH) + "|");
            }
            System.out.println();

            // Find max rows needed
            int maxModules = 0;
            for (List<Module> termModules : yearTerms) {
                maxModules = Math.max(maxModules, termModules.size());
            }

            // Print modules row by row
            for (int row = 0; row < maxModules; row++) {
                System.out.print("|");
                for (List<Module> termModules : yearTerms) {
                    String content = (row < termModules.size()) ? termModules.get(row).getCode() : "";
                    System.out.print(padCell(content, COL_WIDTH) + "|");
                }
                System.out.println();
            }

            System.out.println("=".repeat(COL_WIDTH * timetable.get(year).size() + timetable.get(year).size() + 1));
            System.out.println();
        }
    }

    // Helper to print time table that pads text to ensure columns align
    private String padCell(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width - 1) + " "; // truncate if too long
        }
        int spaces = width - text.length();
        return text + " ".repeat(spaces);
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
