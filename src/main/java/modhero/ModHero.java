package modhero;

import modhero.commands.Command;
import modhero.commands.CommandResult;
import modhero.commands.ExitCommand;
import modhero.data.Timetable;
import modhero.data.modules.ModuleList;
import modhero.parser.Parser;
import modhero.ui.Ui;
import modhero.storage.Storage;

public class ModHero {
    private Ui ui;
    private Timetable timetable;
    private ModuleList electiveList;
    private ModuleList coreList;
    private Storage storage;
    private ModuleList allModules;
    private ModuleList tempModuleList;

    public static void main(String[] args) {
        new ModHero().run();
    }

    public void run() {
        start();
        runCommandLoopUntilExitCommand();
        exit();
    }

    /**
     * Sets up the required objects, and prints the welcome message.
     */
    private void start() {
        this.ui = new Ui();
        this.timetable = new Timetable(4, 4);
        this.tempModuleList = new ModuleList();
        this.storage = new Storage("./src/main/java/modhero/data/data.txt", tempModuleList);
        storage.loadAllModules();
        this.electiveList = new ModuleList(storage);
        this.coreList = new ModuleList(storage);
        ui.showWelcome();
        this.allModules = new ModuleList(storage);

    }

    /** Prints the goodbye message and exits. */
    private void exit() {
        ui.showBye();
        ui.close();
    }

    /** Reads the user command and executes it, until the user issues the exit command. */
    private void runCommandLoopUntilExitCommand() {
        Command command;
        do {
            String userCommandText = ui.readCommand();
            command = new Parser().parseCommand(userCommandText);
            CommandResult result = executeCommand(command);
            ui.showResultToUser(result);
        } while (!ExitCommand.isExit(command));
    }

    private CommandResult executeCommand(Command command) {
        try {
            command.setData(timetable, electiveList, coreList, storage);
            CommandResult result = command.execute();
            return result;
        } catch (Exception e) {
            return new CommandResult(e.getMessage());
        }
    }

}
