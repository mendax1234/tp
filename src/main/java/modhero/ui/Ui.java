package modhero.ui;

import java.util.Scanner;

import modhero.commands.CommandResult;

public class Ui {
    private Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public void showWelcome() {
        System.out.println("Welcome to ModHero");
    }

    public void showBye() {
        System.out.println("Bye");
    }

    public String readCommand() {
        return scanner.nextLine().trim();
    }

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


    public void close() {
        scanner.close();
    }
}
