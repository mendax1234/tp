package modhero.data.modules;


import java.util.ArrayList;
import java.util.List;

/**
 * A simple list wrapper for {@link Module} objects.
 * Provides basic operations to add or remove modules.
 */
public class ModuleList {
    private final List<Module> moduleList = new ArrayList<>();

    /**
     * Adds a module to the list.
     *
     * @param module the module to add
     */
    public void add(Module module) {
        moduleList.add(module);
    }

    /**
     * Removes a module at the specified index.
     *
     * @param taskIndex the index of the module to remove
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void remove(int taskIndex) {
        moduleList.remove(taskIndex);
    }

    /**
     * Get module list
     */
    public List<Module> getList() {
        return moduleList;
    }

}
