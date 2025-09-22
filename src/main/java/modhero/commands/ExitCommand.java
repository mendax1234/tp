package modhero.commands;

public class ExitCommand extends Command {
    @Override
    public CommandResult execute() {
        return new CommandResult("Goodbye!");
    }

    @Override
    public boolean isExit() {
        return true;
    }

    public static boolean isExit(Command command) {
        return command instanceof ExitCommand;
    }
}