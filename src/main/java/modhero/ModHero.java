package modhero;

import modhero.commands.Command;
import modhero.commands.CommandResult;
import modhero.commands.ExitCommand;
import modhero.data.Timetable;
import modhero.data.major.Major;
import modhero.data.modules.Module;
import modhero.data.modules.ModuleList;
import modhero.exception.CorruptedDataFileException;
import modhero.parser.Parser;
import modhero.ui.Ui;
import modhero.storage.Storage;

import java.util.HashMap;
import java.util.Map;

/**
 * Entry point of the ModHero application.
 * Handles initialization, user interaction loop, and program termination.
 */
public class ModHero {
    private Ui ui;
    private Timetable timetable;
    private ModuleList electiveList;
    private ModuleList coreList;
    private Storage storage;
    private Storage majorStorage;
    private Map<String, Module> allModulesData;
    private Map<String, Major> allMajorsData;


    /**
     * Launches the ModHero application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        new ModHero().run();
    }

    /**
     * Runs the application by initializing resources,
     * starting the command loop, and performing cleanup.
     */
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
        this.timetable = new Timetable();
        this.storage = new Storage("data/data.txt");
        this.majorStorage = new Storage("data/majorData.txt");
        this.allModulesData = new HashMap<>();
        this.allMajorsData = new HashMap<>();
        try {
            storage.loadAllModulesData(allModulesData);
            majorStorage.loadAllMajorsData(allModulesData, allMajorsData);
        } catch (CorruptedDataFileException e) {
            System.out.println("Corrupted data file");
            storage.save("");
            majorStorage.save("");
        }
        this.electiveList = new ModuleList();
        this.coreList = new ModuleList();
        ui.showWelcome();
    }

    /**
     * Prints the goodbye message and exits.
     */
    private void exit() {
        ui.showBye();
        ui.close();
    }

    /**
     * Continuously reads and executes user commands until the exit command is received.
     */
    private void runCommandLoopUntilExitCommand() {
        Command command;
        do {
            String userCommandText = ui.readCommand();
            command = new Parser().parseCommand(userCommandText);
            CommandResult result = executeCommand(command);
            ui.showResultToUser(result);
        } while (!ExitCommand.isExit(command));
    }

    /**
     * Executes the given command and returns its result.
     *
     * @param command The user command to execute.
     * @return The result of executing the command.
     */
    private CommandResult executeCommand(Command command) {
        try {
            command.setData(timetable, electiveList, coreList, allModulesData, allMajorsData);
            CommandResult result = command.execute();
            return result;
        } catch (Exception e) {
            return new CommandResult(e.getMessage());
        }
    }

}
