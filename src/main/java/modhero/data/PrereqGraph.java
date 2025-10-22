package modhero.data;

import modhero.data.modules.Module;
import java.util.*;

public class PrereqGraph {
    private List<Module> modules;
    private final HashMap<String, List<String>> graph = new HashMap<>();

    /**
     * Builds a prerequisite graph from the given list of modules.
     * Each module is a node. An edge A → B means A must be taken before B.
     */
    public PrereqGraph(List<Module> modules) {
        this.modules = modules;
        buildGraph();
    }

    private void buildGraph() {
        // Initialize all modules as keys
        for (Module m : modules) {
            graph.putIfAbsent(m.getCode(), new ArrayList<>());
        }

        // For each module, add edges from each prerequisite → this module
        for (Module m : modules) {
            graph.putIfAbsent(m.getCode(), new ArrayList<>(m.getPrerequisites()));
        }
           /* for (String prereq : m.getPrerequisites()) {
                graph.computeIfAbsent(prereq, k -> new ArrayList<>()).add(m.getCode());
            }
        }*/
    }

    /** Returns the adjacency list (module → list of dependents). */
    public HashMap<String, List<String>> getGraph() {
        return graph;
    }

    /** Prints the adjacency list for debugging. */
    public void printGraph() {
        System.out.println("=== Prerequisite Graph ===");
        for (Map.Entry<String, List<String>> e : graph.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}
