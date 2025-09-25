package modhero.commands;

import modhero.ModHero;
import modhero.data.Timetable;
import modhero.data.modules.ModuleList;
import modhero.storage.Storage;

public abstract class Command {
    protected Timetable data;
    protected ModuleList electiveList;
    protected ModuleList coreList;
    protected Storage storage;

    public void setData(Timetable data, ModuleList electiveList, ModuleList coreList, Storage storage) {
        this.data = data;
        this.electiveList = electiveList;
        this.coreList = coreList;
        this.storage = storage;
    }

    public abstract CommandResult execute();

    public boolean isExit() {
        return false;
    }
}
