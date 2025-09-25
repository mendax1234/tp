package modhero.data.modules;


import java.util.ArrayList;
import java.util.List;

public class ModuleList {
    private  final List<Module> moduleList;

    public ModuleList() { this.moduleList = new ArrayList<>(); }

    public void add(Module module) {
        moduleList.add(module);
    }

    public void remove(int taskIndex) {
        moduleList.remove(taskIndex);
    }

    public List<Module> getModuleList() {
        return moduleList;
    }
}
