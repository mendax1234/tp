package modhero;

import modhero.commands.Command;
import modhero.commands.CommandResult;
import modhero.commands.ExitCommand;
import modhero.data.Timetable;
import modhero.parser.Parser;
import modhero.ui.Ui;

public class ModHero {
    private Ui ui;
    private Timetable timetable;

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
        this.timetable = new Timetable();
        ui.showWelcome();
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
            command.setData(timetable);
            CommandResult result = command.execute();
            return result;
        } catch (Exception e) {
            return new CommandResult(e.getMessage());
        }
    }

}
