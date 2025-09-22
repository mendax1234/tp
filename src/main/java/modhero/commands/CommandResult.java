package modhero.commands;

public class CommandResult {
    private final String feedbackToUser;
    private final Module relevantModule;
    private final int totalModules;

    public CommandResult(String feedbackToUser) {
        this.feedbackToUser = feedbackToUser;
        this.relevantModule = null;
        this.totalModules = 0;
    }

    public CommandResult(String feedbackToUser, Module relevantModule) {
        this.feedbackToUser = feedbackToUser;
        this.relevantModule = relevantModule;
        this.totalModules = 0;
    }

    public CommandResult(String feedbackToUser, Module relevantModule, int totalModules) {
        this.feedbackToUser = feedbackToUser;
        this.relevantModule = relevantModule;
        this.totalModules = totalModules;
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public Module getrelevantModule() {
        return relevantModule;
    }

    public int getTotalModules() {
        return totalModules;
    }
}