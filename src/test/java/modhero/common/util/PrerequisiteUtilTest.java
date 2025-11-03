package modhero.common.util;

import modhero.data.modules.Module;
import modhero.data.modules.Prerequisites;
import modhero.exceptions.ModuleAdditionBlockedException;
import modhero.exceptions.ModuleDeletionBlockedException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PrerequisiteUtil.
 */
public class PrerequisiteUtilTest {

    // Helper method to create a module
    private Module createModule(String code) {
        return new Module(
                code,
                "Test Module " + code,
                4,
                "Core",
                "",
                new Prerequisites(Collections.emptyList())
        );
    }

    // Helper method to create a module with prerequisites
    private Module createModuleWithPrerequisites(String code, List<List<String>> prereqSets) {
        return new Module(
                code,
                "Test Module " + code,
                4,
                "Core",
                "Nothing",
                new Prerequisites(prereqSets)
        );
    }

    // Tests for isExemptedModule

    @Test
    public void isExemptedModule_moduleInList_returnsTrue() {
        List<String> exemptedModules = Arrays.asList("CS1010", "CS1231", "MA1521");
        assertTrue(PrerequisiteUtil.isExemptedModule("CS1010", exemptedModules));
    }

    @Test
    public void isExemptedModule_moduleNotInList_returnsFalse() {
        List<String> exemptedModules = Arrays.asList("CS1010", "CS1231", "MA1521");
        assertFalse(PrerequisiteUtil.isExemptedModule("CS2103T", exemptedModules));
    }

    @Test
    public void isExemptedModule_emptyList_returnsFalse() {
        List<String> exemptedModules = Collections.emptyList();
        assertFalse(PrerequisiteUtil.isExemptedModule("CS1010", exemptedModules));
    }

    // Tests for arePrerequisitesMet

    @Test
    public void arePrerequisitesMet_nullPrereqSets_returnsTrue() {
        List<String> completedCodes = Arrays.asList("CS1010", "CS1231");
        List<String> exemptedModules = Collections.emptyList();
        assertTrue(PrerequisiteUtil.arePrerequisitesMet(null, completedCodes, exemptedModules));
    }

    @Test
    public void arePrerequisitesMet_emptyPrereqSets_returnsTrue() {
        List<String> completedCodes = Arrays.asList("CS1010", "CS1231");
        List<String> exemptedModules = Collections.emptyList();
        assertTrue(PrerequisiteUtil.arePrerequisitesMet(Collections.emptyList(), completedCodes, exemptedModules));
    }

    @Test
    public void arePrerequisitesMet_singleOptionAllSatisfied_returnsTrue() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1010", "CS1231")
        );
        List<String> completedCodes = Arrays.asList("CS1010", "CS1231", "MA1521");
        List<String> exemptedModules = Collections.emptyList();
        assertTrue(PrerequisiteUtil.arePrerequisitesMet(prereqSets, completedCodes, exemptedModules));
    }

    @Test
    public void arePrerequisitesMet_singleOptionNotAllSatisfied_returnsFalse() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1010", "CS1231")
        );
        List<String> completedCodes = Arrays.asList("CS1010");
        List<String> exemptedModules = Collections.emptyList();
        assertFalse(PrerequisiteUtil.arePrerequisitesMet(prereqSets, completedCodes, exemptedModules));
    }

    @Test
    public void arePrerequisitesMet_multipleOptionsFirstSatisfied_returnsTrue() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1010", "CS1231"),
                Arrays.asList("CS1101S")
        );
        List<String> completedCodes = Arrays.asList("CS1010", "CS1231");
        List<String> exemptedModules = Collections.emptyList();
        assertTrue(PrerequisiteUtil.arePrerequisitesMet(prereqSets, completedCodes, exemptedModules));
    }

    @Test
    public void arePrerequisitesMet_multipleOptionsSecondSatisfied_returnsTrue() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1010", "CS1231"),
                Arrays.asList("CS1101S")
        );
        List<String> completedCodes = Arrays.asList("CS1101S");
        List<String> exemptedModules = Collections.emptyList();
        assertTrue(PrerequisiteUtil.arePrerequisitesMet(prereqSets, completedCodes, exemptedModules));
    }

    @Test
    public void arePrerequisitesMet_multipleOptionsNoneSatisfied_returnsFalse() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1010", "CS1231"),
                Arrays.asList("CS1101S")
        );
        List<String> completedCodes = Arrays.asList("CS1010");
        List<String> exemptedModules = Collections.emptyList();
        assertFalse(PrerequisiteUtil.arePrerequisitesMet(prereqSets, completedCodes, exemptedModules));
    }

    @Test
    public void arePrerequisitesMet_exemptedModuleSatisfiesPrereq_returnsTrue() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1010", "CS1231")
        );
        List<String> completedCodes = Arrays.asList("CS1010");
        List<String> exemptedModules = Arrays.asList("CS1231");
        assertTrue(PrerequisiteUtil.arePrerequisitesMet(prereqSets, completedCodes, exemptedModules));
    }

    @Test
    public void arePrerequisitesMet_wildcardPrereqWithMatch_returnsTrue() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1%")
        );
        List<String> completedCodes = Arrays.asList("CS1010", "CS2103T");
        List<String> exemptedModules = Collections.emptyList();
        assertTrue(PrerequisiteUtil.arePrerequisitesMet(prereqSets, completedCodes, exemptedModules));
    }

    @Test
    public void arePrerequisitesMet_wildcardPrereqWithoutMatch_returnsFalse() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS2%")
        );
        List<String> completedCodes = Arrays.asList("CS1010", "CS1231");
        List<String> exemptedModules = Collections.emptyList();
        assertFalse(PrerequisiteUtil.arePrerequisitesMet(prereqSets, completedCodes, exemptedModules));
    }

    @Test
    public void arePrerequisitesMet_wildcardAndNormalPrereqBothSatisfied_returnsTrue() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1%", "MA1521")
        );
        List<String> completedCodes = Arrays.asList("CS1010", "MA1521");
        List<String> exemptedModules = Collections.emptyList();
        assertTrue(PrerequisiteUtil.arePrerequisitesMet(prereqSets, completedCodes, exemptedModules));
    }

    @Test
    public void arePrerequisitesMet_wildcardOnlyPercent_returnsFalse() {
        // Single "%" character should not match anything
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("%")
        );
        List<String> completedCodes = Arrays.asList("CS1010");
        List<String> exemptedModules = Collections.emptyList();
        assertFalse(PrerequisiteUtil.arePrerequisitesMet(prereqSets, completedCodes, exemptedModules));
    }

    // Tests for validatePrerequisites

    @Test
    public void validatePrerequisites_nullPrerequisites_success() {
        List<String> completedCodes = Arrays.asList("CS1010");
        List<String> exemptedModules = Collections.emptyList();

        assertDoesNotThrow(() ->
                PrerequisiteUtil.validatePrerequisites("CS2103T", null, completedCodes, exemptedModules)
        );
    }

    @Test
    public void validatePrerequisites_emptyPrerequisites_success() {
        Prerequisites prereqs = new Prerequisites(Collections.emptyList());
        List<String> completedCodes = Arrays.asList("CS1010");
        List<String> exemptedModules = Collections.emptyList();

        assertDoesNotThrow(() ->
                PrerequisiteUtil.validatePrerequisites("CS2103T", prereqs, completedCodes, exemptedModules)
        );
    }

    @Test
    public void validatePrerequisites_prerequisitesMet_success() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1010", "CS1231")
        );
        Prerequisites prereqs = new Prerequisites(prereqSets);
        List<String> completedCodes = Arrays.asList("CS1010", "CS1231");
        List<String> exemptedModules = Collections.emptyList();

        assertDoesNotThrow(() ->
                PrerequisiteUtil.validatePrerequisites("CS2103T", prereqs, completedCodes, exemptedModules)
        );
    }

    @Test
    public void validatePrerequisites_prerequisitesNotMet_throwsException() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1010", "CS1231")
        );
        Prerequisites prereqs = new Prerequisites(prereqSets);
        List<String> completedCodes = Arrays.asList("CS1010");
        List<String> exemptedModules = Collections.emptyList();

        ModuleAdditionBlockedException exception = assertThrows(
                ModuleAdditionBlockedException.class,
                () -> PrerequisiteUtil.validatePrerequisites("CS2103T", prereqs, completedCodes, exemptedModules)
        );

        assertTrue(exception.getMessage().contains("CS2103T"));
    }

    @Test
    public void validatePrerequisites_multipleOptionsOneSatisfied_success() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1010", "CS1231"),
                Arrays.asList("CS1101S")
        );
        Prerequisites prereqs = new Prerequisites(prereqSets);
        List<String> completedCodes = Arrays.asList("CS1101S");
        List<String> exemptedModules = Collections.emptyList();

        assertDoesNotThrow(() ->
                PrerequisiteUtil.validatePrerequisites("CS2103T", prereqs, completedCodes, exemptedModules)
        );
    }

    @Test
    public void validatePrerequisites_withExemption_success() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1010", "CS1231")
        );
        Prerequisites prereqs = new Prerequisites(prereqSets);
        List<String> completedCodes = Arrays.asList("CS1010");
        List<String> exemptedModules = Arrays.asList("CS1231");

        assertDoesNotThrow(() ->
                PrerequisiteUtil.validatePrerequisites("CS2103T", prereqs, completedCodes, exemptedModules)
        );
    }

    @Test
    public void validatePrerequisites_wildcardSatisfied_success() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1%")
        );
        Prerequisites prereqs = new Prerequisites(prereqSets);
        List<String> completedCodes = Arrays.asList("CS1010", "CS2103T");
        List<String> exemptedModules = Collections.emptyList();

        assertDoesNotThrow(() ->
                PrerequisiteUtil.validatePrerequisites("CS3230", prereqs, completedCodes, exemptedModules)
        );
    }

    // Tests for validateFutureDependencies

    @Test
    public void validateFutureDependencies_noDependencies_success() {
        List<Module> futureModules = Collections.emptyList();
        List<String> completedCodes = Arrays.asList("CS1010", "CS2103T");
        List<String> exemptedModules = Collections.emptyList();

        assertDoesNotThrow(() ->
                PrerequisiteUtil.validateFutureDependencies("CS2103T", futureModules, completedCodes, exemptedModules)
        );
    }

    @Test
    public void validateFutureDependencies_futureModulesWithoutPrereq_success() {
        List<Module> futureModules = Arrays.asList(
                createModule("CS3230"),
                createModule("CS2101")
        );
        List<String> completedCodes = Arrays.asList("CS1010", "CS2103T");
        List<String> exemptedModules = Collections.emptyList();

        assertDoesNotThrow(() ->
                PrerequisiteUtil.validateFutureDependencies("CS2103T", futureModules, completedCodes, exemptedModules)
        );
    }

    @Test
    public void validateFutureDependencies_futureModulesStillSatisfied_success() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS1010", "CS1231")
        );
        List<Module> futureModules = Arrays.asList(
                createModuleWithPrerequisites("CS3230", prereqSets)
        );
        List<String> completedCodes = Arrays.asList("CS1010", "CS1231", "CS2103T");
        List<String> exemptedModules = Collections.emptyList();

        assertDoesNotThrow(() ->
                PrerequisiteUtil.validateFutureDependencies("CS2103T", futureModules, completedCodes, exemptedModules)
        );
    }

    @Test
    public void validateFutureDependencies_futureModuleDependsOnDeletedModule_throwsException() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS2103T")
        );
        List<Module> futureModules = Arrays.asList(
                createModuleWithPrerequisites("CS3230", prereqSets)
        );
        List<String> completedCodes = Arrays.asList("CS1010");
        List<String> exemptedModules = Collections.emptyList();

        ModuleDeletionBlockedException exception = assertThrows(
                ModuleDeletionBlockedException.class,
                () -> PrerequisiteUtil.validateFutureDependencies("CS2103T", futureModules, completedCodes, exemptedModules)
        );

        assertTrue(exception.getMessage().contains("CS2103T"));
        assertTrue(exception.getMessage().contains("CS3230"));
    }

    @Test
    public void validateFutureDependencies_multipleFutureModulesOneDependsOnDeleted_throwsException() {
        List<List<String>> prereqSets1 = Arrays.asList(
                Arrays.asList("CS1010")
        );
        List<List<String>> prereqSets2 = Arrays.asList(
                Arrays.asList("CS2103T")
        );
        List<Module> futureModules = Arrays.asList(
                createModuleWithPrerequisites("CS2101", prereqSets1),
                createModuleWithPrerequisites("CS3230", prereqSets2)
        );
        List<String> completedCodes = Arrays.asList("CS1010");
        List<String> exemptedModules = Collections.emptyList();

        assertThrows(
                ModuleDeletionBlockedException.class,
                () -> PrerequisiteUtil.validateFutureDependencies("CS2103T", futureModules, completedCodes, exemptedModules)
        );
    }

    @Test
    public void validateFutureDependencies_futureModuleHasAlternativePrereq_success() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS2103T"),
                Arrays.asList("CS2113T")
        );
        List<Module> futureModules = Arrays.asList(
                createModuleWithPrerequisites("CS3230", prereqSets)
        );
        List<String> completedCodes = Arrays.asList("CS2103T", "CS2113T");
        List<String> exemptedModules = Collections.emptyList();

        assertDoesNotThrow(() ->
                PrerequisiteUtil.validateFutureDependencies("CS2103T", futureModules, completedCodes, exemptedModules)
        );
    }

    @Test
    public void validateFutureDependencies_exemptedModuleCoversDeleted_success() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS2103T")
        );
        List<Module> futureModules = Arrays.asList(
                createModuleWithPrerequisites("CS3230", prereqSets)
        );
        List<String> completedCodes = Arrays.asList("CS2103T");
        List<String> exemptedModules = Arrays.asList("CS2103T");

        assertDoesNotThrow(() ->
                PrerequisiteUtil.validateFutureDependencies("CS2103T", futureModules, completedCodes, exemptedModules)
        );
    }

    @Test
    public void validateFutureDependencies_wildcardPrereqStillSatisfied_success() {
        List<List<String>> prereqSets = Arrays.asList(
                Arrays.asList("CS2%")
        );
        List<Module> futureModules = Arrays.asList(
                createModuleWithPrerequisites("CS3230", prereqSets)
        );
        List<String> completedCodes = Arrays.asList("CS2040", "CS2103T");
        List<String> exemptedModules = Collections.emptyList();

        assertDoesNotThrow(() ->
                PrerequisiteUtil.validateFutureDependencies("CS2103T", futureModules, completedCodes, exemptedModules)
        );
    }
}
