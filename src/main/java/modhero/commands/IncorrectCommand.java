package modhero.commands;

public class IncorrectCommand extends Command {
    private String errorMessage;

    public IncorrectCommand(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public CommandResult execute() {
        return new CommandResult(errorMessage);
    }
}
