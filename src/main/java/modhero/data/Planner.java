package modhero.data;

import modhero.data.modules.Module;
import modhero.data.modules.ModuleList;

import java.util.ArrayList;
import java.util.List;

/**
 * Algorithm to plan the timetable
 */
public class Planner {
    private final Timetable timetable;
    private final ModuleList moduleList;

    final int years = 4;
    final int terms = 4;

    public Planner(Timetable timetable, ModuleList coreList, ModuleList electiveList) {
        this.timetable = timetable;
        this.moduleList = coreList.merge(electiveList);
    }

    public void planTimeTable() {
        moduleList.sort();
        addToTimetable();
    }

    /**
     * Generates the recommended timetable based on the ModuleLists
     */
    public void addToTimetable() {
        for (int i = 0; i < moduleList.size(); i++) {
            int year = (i / terms) % years;
            int term = i % terms;
            Module module = moduleList.getModule(i);
            timetable.addModule(year, term, module);
        }
    }
}
