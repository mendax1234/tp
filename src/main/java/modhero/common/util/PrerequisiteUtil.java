package modhero.common.util;

import modhero.data.modules.Prerequisites;
import modhero.exceptions.ModuleAdditionBlockedException;
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

    private static boolean isPrerequisiteSatisfied(String prereqCode, List<String> completedCodes) {
        if (prereqCode.endsWith("%") && prereqCode.length() > 1) {
            String prefix = prereqCode.substring(0, prereqCode.length() - 1);
            return completedCodes.stream().anyMatch(c -> c.startsWith(prefix));
        }
        return completedCodes.contains(prereqCode);
    }

    public static boolean arePrerequisitesMet(List<List<String>> prereqSets, List<String> completedCodes) {
        if (prereqSets == null || prereqSets.isEmpty()) return true;
        return prereqSets.stream().anyMatch(
                option -> option.stream().allMatch(code -> isPrerequisiteSatisfied(code, completedCodes))
        );
    }

    public static void validatePrerequisites(String moduleCode, Prerequisites prereqs, List<String> completedCodes)
            throws ModuleAdditionBlockedException {
        if (isLevel1000Module(moduleCode)) return;
        if (prereqs == null || prereqs.getPrereq() == null || prereqs.getPrereq().isEmpty()) return;

        boolean satisfied = arePrerequisitesMet(prereqs.getPrereq(), completedCodes);
        if (!satisfied) {
            throw new ModuleAdditionBlockedException(moduleCode, prereqs.toString());
        }
    }
}
