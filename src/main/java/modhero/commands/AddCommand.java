package modhero.commands;

public class AddCommand extends Command{
    private String fullCommand;

    public AddCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    @Override
    public CommandResult execute() {
        return new CommandResult("Add Command Test");
    }
}
