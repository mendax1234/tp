package modhero.data.modules;


import java.util.ArrayList;
import java.util.List;
import modhero.storage.Storage;

/**
 * A wrapper class around a {@link List} of {@link Module} objects.
 * <p>
 * Provides basic operations to add, remove, and search for modules.
 * A Storage reference may optionally be provided to lookup the
 * list of all modules.
 * <p>
 */
public class ModuleList {
    private  final List<Module> moduleList;

    /** Optional reference to Storage for performing global lookups. */
    private Storage storage;

    /**
     * Creates a {@code ModuleList} backed by a new empty list,
     * with access to the provided {@link Storage} for global module lookup.
     *
     * @param storage the storage instance used to look up modules
     */
    public ModuleList(Storage storage) {
        this.storage = storage;
        this.moduleList = new ArrayList<>();
    }

    /**
     * Creates a {@code ModuleList} backed by a new empty list,
     * without any storage reference.
     * Use this constructor when no global module lookup is required.
     */
    public ModuleList() {
        this.moduleList = new ArrayList<>();
    }

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
     * Returns the required module.
     *
     * @param code the module code to search for
     * @return a Module with the given module code
     */
    public Module findModuleByCode(String code) {
        for (Module m : storage.getAllModules().getList()) {
            if (m.getCode().equals(code)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Get module list
     */
    public List<Module> getList() {
        return moduleList;
    }

}
