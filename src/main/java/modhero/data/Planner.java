package modhero.data;

import static modhero.common.Constants.NUM_TERMS;
import static modhero.common.Constants.NUM_YEARS;

import modhero.data.modules.Module;
import modhero.data.modules.ModuleList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Algorithm to plan the timetable
 */
public class Planner {
    public static final Logger logger = Logger.getLogger(Planner.class.getName());

    private final Timetable timetable;
    private final List<Module> moduleList;
    private List<Module> sortedModuleList;
    private PrereqGraph prereqGraph;

    public Planner(Timetable timetable, ModuleList coreList, ModuleList electiveList) {
        assert timetable != null : "Timetable must not be null";
        assert coreList != null : "Core list must not be null";
        assert electiveList != null : "Elective list must not be null";

        this.timetable = timetable;
        this.moduleList = new ArrayList<>(coreList.getList());
        this.moduleList.addAll(electiveList.getList());

        logger.log(Level.FINE, () -> String.format("Planner initialised with %d modules", moduleList.size()));
    }

    /**
     * Plan the Timetable given the moduleList
     */
    public void planTimeTable() {
        topologicallySortModuleList();
        addToTimetable();
    }


    private void generatePrereqGraph(){
        prereqGraph = new PrereqGraph(moduleList);
    }

    private void trimPrereqGraph(HashMap<String, List<String>> graphToSort, String entry){
        for ( List<String> value : graphToSort.values()){
            value.remove(entry);
        }
    }

    private Module findModuleByCode(String code){
        for (Module module : moduleList){
            if (module.getCode().equals(code)){
                return module;
            }
        }
        return null; //add proper exception handling here later
    }

    private void topologicallySortModuleList (){
        prereqGraph = new PrereqGraph(moduleList);
        sortedModuleList = new ArrayList<>();
        HashMap<String, List<String>> graphToSort = prereqGraph.getGraph();
        while (!graphToSort.isEmpty()){
            for (String key : graphToSort.keySet()) {
                if ( graphToSort.get(key).isEmpty()){
                    Module currentModule = findModuleByCode(key);
                    sortedModuleList.add(currentModule);
                    graphToSort.remove(key);
                    trimPrereqGraph(graphToSort, key);
                }
            }
            for (String key : graphToSort.keySet()) {
                if ( graphToSort.get(key).isEmpty()){
                    trimPrereqGraph(graphToSort, key);
                }
            }
        }
    }

    public void addToTimetable() {
        for (int i = 0; i < sortedModuleList.size(); i++) {
            int year = (i / NUM_TERMS) % NUM_YEARS;
            int term = i % NUM_TERMS;
            Module module = moduleList.get(i);
            timetable.addModule(year, term, module);
        }
    }
}
