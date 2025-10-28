package modhero;

import modhero.common.Constants.FilePathConstants;

import modhero.commands.Command;
import modhero.commands.CommandResult;
import modhero.commands.ExitCommand;
import modhero.common.config.LoggerConfig;
import modhero.data.DataManager;
import modhero.parser.Parser;
import modhero.ui.Ui;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point of the ModHero application.
 * Handles initialization, user interaction loop, and program termination.
 */
public class ModHero {
    private static final Logger logger = Logger.getLogger(ModHero.class.getName());

    private Ui ui;
    private DataManager dataManager;
    private Parser parser;

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
        LoggerConfig.configureLoggers(Level.OFF);
        logger.log(Level.INFO, "Starting ModHero");
        start();
        runCommandLoopUntilExitCommand();
        exit();
        logger.log(Level.INFO, "ModHero terminated");
    }

    /**
     * Sets up the required objects, and prints the welcome message.
     */
    private void start() {
        logger.log(Level.INFO, "Start setting up data required");
        this.ui = new Ui();
        this.parser = new Parser();
        this.dataManager = new DataManager(FilePathConstants.MODULES_FILE_PATH, FilePathConstants.MAJOR_FILE_PATH);
        logger.log(Level.INFO, "Completed setting up data required");
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
            logger.log(Level.INFO, "Parsing command");
            command = parser.parseCommand(userCommandText);
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
        assert command != null : "Command must not be null";
        try {
            command.setData(
                    dataManager.getTimetable(),
                    dataManager.getAllModulesData(),
                    dataManager.getAllMajorsData()
            );
            CommandResult result = command.execute();
            logger.log(Level.INFO, "Command execution completed");
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Command execution failed", e);
            return new CommandResult(e.getMessage());
        }
    }
}
