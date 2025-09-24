package modhero.commands;

import modhero.ModHero;
import modhero.data.Timetable;
import modhero.data.modules.ModuleList;

public abstract class Command {
    protected Timetable data;
    protected ModuleList electiveList;
    protected ModuleList coreList;

    public void setData(Timetable data, ModuleList electiveList, ModuleList coreList) {
        this.data = data;
        this.electiveList = electiveList;
        this.coreList = coreList;
    }

    public abstract CommandResult execute();

    public boolean isExit() {
        return false;
    }
}
