package modhero.commands;

import modhero.data.Timetable;

public abstract class Command {
    protected Timetable data;

    public void setData(Timetable data) {
        this.data = data;
    }

    public abstract CommandResult execute();

    public boolean isExit() {
        return false;
    }
}
