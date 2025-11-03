package modhero.common.util;

import modhero.data.modules.Module;
import modhero.exceptions.ModulePreclusionConflictException;

import java.util.List;

/**
 * Utility class for handling module preclusion logic.
 * This class cannot be instantiated.
 */
public final class PreclusionUtil {

    private PreclusionUtil() { }

    /**
     * Validates that a module to be added does not have any preclusion conflicts
     * with modules already in the timetable.
     *
     * @param moduleToAdd        The new module to be added.
     * @param allExistingModules A list of all modules currently in the timetable.
     * @throws ModulePreclusionConflictException if a preclusion conflict is found.
     */
    public static void validatePreclusions(Module moduleToAdd, List<Module> allExistingModules)
            throws ModulePreclusionConflictException {
        String newModuleCode = moduleToAdd.getCode();
        String newModulePreclusions = moduleToAdd.getPreclude();

        if (newModulePreclusions == null || newModulePreclusions.isBlank()) {
            return;
        }

        for (Module existingModule : allExistingModules) {
            String existingCode = existingModule.getCode();

            // Just check if the preclusion string of the new module mentions the existing one
            if (newModulePreclusions.toUpperCase().contains(existingCode.toUpperCase())) {
                throw new ModulePreclusionConflictException(newModuleCode, existingCode);
            }
        }
    }
}
