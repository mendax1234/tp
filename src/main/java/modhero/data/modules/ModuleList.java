package modhero.data.modules;


import java.util.ArrayList;
import java.util.List;
import modhero.storage.Storage;

public class ModuleList {
    private  final List<Module> moduleList;

    private Storage storage;


    public ModuleList(Storage storage) {
        this.storage = storage;
        this.moduleList = new ArrayList<>();
    }

    public ModuleList() {
        this.moduleList = new ArrayList<>();
    }

    public void add(Module module) {
        moduleList.add(module);
    }

    public void remove(int taskIndex) {
        moduleList.remove(taskIndex);
    }

    public List<Module> getModuleList() {
        return moduleList;
    }

    /**
     * Returns the required module.
     *
     * @param code the module code to search for
     * @return a Module with the given module code
     */
    public Module findModuleByCode(String code) {
        for (Module m : storage.getAllModules().getModuleList()) {
            if (m.getCode().equals(code)) {
                return m;
            }
        }
        return null;
    }
}
