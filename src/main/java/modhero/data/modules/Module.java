package modhero.data.modules;

import java.util.list;

public class Module {
    private String code;    // e.g. CS2113
    private String name;    // e.g. Software Engineering
    private int mc;         // e.g. modular credits
    private String type;    // e.g. core, elective, etc.
    private List<String> prerequisites // e.g. ["CS1010", "CS1231"]

    public Module(String code, String name, int mc, String type, List<String> prerequisites) {
        this.code = code;
        this.name = name;
        this.mc = mc;
        this.type = type;
        this.prerequisites = prerequisites;
    }

    /** Get functions */
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getMc() {
        return mc;
    }

    public String getType() {
        return type;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }

}
