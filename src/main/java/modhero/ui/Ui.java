package modhero.ui;

import java.util.Scanner;

import modhero.commands.CommandResult;

/**
 * Handles all interactions with the user, including displaying messages
 * and reading user input.
 */
public class Ui {
    private Scanner scanner;

    /**
     * Creates a new {@code Ui} with a scanner for reading user input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome message when the program starts.
     */
    public void showWelcome() {
        System.out.println("""
        ┌──────────────────────────────────────────────────────────────┐
        │                     Welcome to ModHero                       │
        ├──────────────────────────────────────────────────────────────┤
        │ Hey there, future graduate!                                  │
        │                                                              │
        │ ModHero helps you plan your degree the smart way —           │
        │ quick commands, clean overview, no spreadsheets needed.      │
        │                                                              │
        │ Type 'help' to see what I can do.                            │
        │ Example: major Computer Science specialisation AI            │
        │                                                              │
        │ Your data is safely auto-saved in ./data/modhero.json        │
        │                                                              │
        │ Ready? Let’s start shaping your 4-year roadmap. 🚀           │
        └──────────────────────────────────────────────────────────────┘
        """);
    }

    /**
     * Displays the goodbye message when the program exits.
     */
    public void showBye() {
        System.out.println("Bye");
    }

    /**
     * Reads a command entered by the user from the console.
     *
     * @return The trimmed user input string.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays the result of a command execution to the user.
     *
     * @param result The {@code CommandResult} containing feedback and relevant module information.
     */
    public void showResultToUser(CommandResult result) {
        System.out.println(result.getFeedbackToUser());

        Module module = result.getrelevantModule();
        if (module != null) {
            System.out.println("  " + module);
            if (result.getTotalModules() > 0) {
                System.out.println("Now you have " + result.getTotalModules() + " modules in the list.");
            }
        }
    }

    /**
     * Closes the scanner used for reading user input.
     */
    public void close() {
        scanner.close();
    }
}
