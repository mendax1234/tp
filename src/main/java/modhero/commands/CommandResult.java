package modhero.commands;

import modhero.data.modules.Module;

/**
 * Represents the result of executing a command.
 * Contains feedback for the user, optionally a relevant module,
 * and optionally the total number of modules affected.
 */
public class CommandResult {
    private final String feedbackToUser;
    /**
     * Creates a command result with only feedback.
     *
     * @param feedbackToUser the feedback message to display to the user
     */
    public CommandResult(String feedbackToUser) {
        this.feedbackToUser = feedbackToUser;
    }

    /** @return the feedback message to display to the user */
    public String getFeedbackToUser() {
        return feedbackToUser;
    }
}
