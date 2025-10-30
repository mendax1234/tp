package modhero.common.util;

import modhero.data.modules.Prerequisites;
import modhero.exceptions.PrerequisiteNotMetException;
import java.util.List;

public final class PrerequisiteUtil {

    private PrerequisiteUtil() {} // prevent instantiation

    public static boolean isLevel1000Module(String moduleCode) {
        if (moduleCode == null || moduleCode.isEmpty()) return false;
        for (char c : moduleCode.toCharArray()) {
            if (Character.isDigit(c)) return c == '1';
        }
        return false;
    }

    private static boolean isPrerequisiteSatisfied(String prereqCode, List<String> completedCodes, List<String> exemptedModules) {
        if (prereqCode.endsWith("%") && prereqCode.length() > 1) {
            String prefix = prereqCode.substring(0, prereqCode.length() - 1);
            return completedCodes.stream().anyMatch(c -> c.startsWith(prefix));
        }
        return (completedCodes.contains(prereqCode) || exemptedModules.contains(prereqCode));
    }

    public static boolean arePrerequisitesMet(List<List<String>> prereqSets, List<String> completedCodes, List <String> exemptedModules) {
        if (prereqSets == null || prereqSets.isEmpty()) return true;
        return prereqSets.stream().anyMatch(
                option -> option.stream().allMatch(code -> isPrerequisiteSatisfied(code, completedCodes, exemptedModules))
        );
    }

    public static void validatePrerequisites(String moduleCode, Prerequisites prereqs, List<String> completedCodes, List<String> exemptedModules)
            throws PrerequisiteNotMetException {
        if (isExemptedModule(moduleCode, exemptedModules)) {
            return;
        }

        if (prereqs == null || prereqs.getPrereq() == null || prereqs.getPrereq().isEmpty()) return;

        boolean satisfied = arePrerequisitesMet(prereqs.getPrereq(), completedCodes, exemptedModules);

        if (!satisfied) {
            throw new PrerequisiteNotMetException(moduleCode, prereqs.toString());
        }
    }

    public static boolean isExemptedModule(String moduleCode, List<String> exemptedModules) {
        return exemptedModules.contains(moduleCode);
    }
}
