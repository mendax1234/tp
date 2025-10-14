package modhero.data.major;

import modhero.data.modules.ModuleList;

import java.util.List;

public class Major {
    private String name;    // e.g. Computer Engineering
    private String abbrName;         // e.g. CEG
    private ModuleList moduleList;

    /**
     * Creates a new major object.
     *
     * @param name the major name
     * @param abbrName the major abbreviation name
     * @param moduleList the list of core module object
     */
    public Major(String name, String abbrName, ModuleList moduleList) {
        this.name = name;
        this.abbrName = abbrName;
        this.moduleList = moduleList;
    }

    /** @return the module code */
    public String getabbrName() {
        return abbrName;
    }

    /** @return the modules */
    public ModuleList getModules() {
        return moduleList;
    }
}
