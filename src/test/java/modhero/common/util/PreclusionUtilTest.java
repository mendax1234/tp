package modhero.common.util;

import modhero.data.modules.Module;
import modhero.data.modules.Prerequisites;
import modhero.exceptions.ModulePreclusionConflictException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PreclusionUtil.
 */
public class PreclusionUtilTest {

    // Helper method to quickly create a dummy module
    private Module createModule(String code) {
        return new Module(
                code,
                "Test Module " + code,
                4,
                "Core",
                "", // empty preclude
                new Prerequisites(Collections.emptyList()) // empty prereq
        );
    }

    // Helper method to create a module with preclusions
    private Module createModuleWithPreclusion(String code, String preclusions) {
        return new Module(
                code,
                "Test Module " + code,
                4,
                "Core",
                preclusions,
                new Prerequisites(Collections.emptyList())
        );
    }

    @Test
    public void validatePreclusions_noPreclusions_success() {
        Module moduleToAdd = createModule("CS2103T");
        List<Module> existingModules = new ArrayList<>();
        existingModules.add(createModule("CS2101"));
        existingModules.add(createModule("CS2040"));

        assertDoesNotThrow(() ->
                PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );
    }

    @Test
    public void validatePreclusions_emptyExistingModules_success() {
        Module moduleToAdd = createModuleWithPreclusion("CS2103T", "CS2103");
        List<Module> existingModules = new ArrayList<>();

        assertDoesNotThrow(() ->
                PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );
    }

    @Test
    public void validatePreclusions_blankPreclusion_success() {
        Module moduleToAdd = createModuleWithPreclusion("CS2103T", "   ");
        List<Module> existingModules = new ArrayList<>();
        existingModules.add(createModule("CS2103"));

        assertDoesNotThrow(() ->
                PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );
    }

    @Test
    public void validatePreclusions_emptyPreclusion_success() {
        Module moduleToAdd = createModuleWithPreclusion("CS2103T", "");
        List<Module> existingModules = new ArrayList<>();
        existingModules.add(createModule("CS2103"));

        assertDoesNotThrow(() ->
                PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );
    }

    @Test
    public void validatePreclusions_noPreclusionConflict_success() {
        Module moduleToAdd = createModuleWithPreclusion("CS2103T", "CS2103");
        List<Module> existingModules = new ArrayList<>();
        existingModules.add(createModule("CS2101"));
        existingModules.add(createModule("CS2040"));

        assertDoesNotThrow(() ->
                PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );
    }

    @Test
    public void validatePreclusions_exactPreclusionConflict_throwsException() {
        Module moduleToAdd = createModuleWithPreclusion("CS2103T", "CS2103");
        List<Module> existingModules = new ArrayList<>();
        existingModules.add(createModule("CS2103"));

        ModulePreclusionConflictException exception = assertThrows(
                ModulePreclusionConflictException.class,
                () -> PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );

        assertTrue(exception.getMessage().contains("CS2103T"));
        assertTrue(exception.getMessage().contains("CS2103"));
    }

    @Test
    public void validatePreclusions_caseInsensitiveConflict_throwsException() {
        Module moduleToAdd = createModuleWithPreclusion("CS2103T", "cs2103");
        List<Module> existingModules = new ArrayList<>();
        existingModules.add(createModule("CS2103"));

        assertThrows(
                ModulePreclusionConflictException.class,
                () -> PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );
    }

    @Test
    public void validatePreclusions_multiplePreclusionsOneConflict_throwsException() {
        Module moduleToAdd = createModuleWithPreclusion("CS2103T", "CS2103, CS2113T");
        List<Module> existingModules = new ArrayList<>();
        existingModules.add(createModule("CS2101"));
        existingModules.add(createModule("CS2103"));

        assertThrows(
                ModulePreclusionConflictException.class,
                () -> PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );
    }

    @Test
    public void validatePreclusions_multiplePreclusionsNoConflict_success() {
        Module moduleToAdd = createModuleWithPreclusion("CS2103T", "CS2103, CS2113T");
        List<Module> existingModules = new ArrayList<>();
        existingModules.add(createModule("CS2101"));
        existingModules.add(createModule("CS2040"));

        assertDoesNotThrow(() ->
                PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );
    }

    @Test
    public void validatePreclusions_partialMatchInString_throwsException() {
        // Tests that "CS2103" is found within a longer preclusion string
        Module moduleToAdd = createModuleWithPreclusion("CS2103T",
                "Students who have taken CS2103 or CS2113T cannot take this module");
        List<Module> existingModules = new ArrayList<>();
        existingModules.add(createModule("CS2103"));

        assertThrows(
                ModulePreclusionConflictException.class,
                () -> PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );
    }

    @Test
    public void validatePreclusions_multipleExistingModulesFirstConflicts_throwsException() {
        Module moduleToAdd = createModuleWithPreclusion("CS2103T", "CS2101");
        List<Module> existingModules = new ArrayList<>();
        existingModules.add(createModule("CS2101"));
        existingModules.add(createModule("CS2040"));
        existingModules.add(createModule("CS2030"));

        assertThrows(
                ModulePreclusionConflictException.class,
                () -> PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );
    }

    @Test
    public void validatePreclusions_multipleExistingModulesLastConflicts_throwsException() {
        Module moduleToAdd = createModuleWithPreclusion("CS2103T", "CS2030");
        List<Module> existingModules = new ArrayList<>();
        existingModules.add(createModule("CS2101"));
        existingModules.add(createModule("CS2040"));
        existingModules.add(createModule("CS2030"));

        assertThrows(
                ModulePreclusionConflictException.class,
                () -> PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );
    }

    @Test
    public void validatePreclusions_similarButDifferentCodes_success() {
        // CS210 should not match CS2101
        Module moduleToAdd = createModuleWithPreclusion("CS2103T", "CS210");
        List<Module> existingModules = new ArrayList<>();
        existingModules.add(createModule("CS2101"));

        assertDoesNotThrow(() ->
                PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );
    }

    @Test
    public void validatePreclusions_preclusionWithExtraSpaces_throwsException() {
        Module moduleToAdd = createModuleWithPreclusion("CS2103T", "  CS2103  ");
        List<Module> existingModules = new ArrayList<>();
        existingModules.add(createModule("CS2103"));

        assertThrows(
                ModulePreclusionConflictException.class,
                () -> PreclusionUtil.validatePreclusions(moduleToAdd, existingModules)
        );
    }
}