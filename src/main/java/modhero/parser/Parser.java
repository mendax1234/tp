package modhero.parser;

import modhero.commands.AddCommand;
import modhero.commands.Command;
import modhero.commands.ExitCommand;
import modhero.commands.IncorrectCommand;


public class Parser {
    public Command parseCommand(String input) {
        try {
            if (input.isEmpty()) {
                return new IncorrectCommand("Please enter a command!");
            }

            String lowerInput = input.toLowerCase();

            if (lowerInput.equals("add")) {
                return new AddCommand(input);
            } else if (lowerInput.equals("bye")) {
                return new ExitCommand();
            } else {
                String firstWord = input.split("\\s+")[0];
                return new IncorrectCommand("Unknown command: " + firstWord);
            }
        } catch (Exception e) {
            return new IncorrectCommand(e.getMessage());
        }
    }
}