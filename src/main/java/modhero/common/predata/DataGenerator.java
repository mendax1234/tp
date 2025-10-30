package modhero.common.predata;

import static modhero.common.Constants.FilePathConstants.MAJOR_FILE_PATH;
import static modhero.common.Constants.FilePathConstants.MODULES_FILE_PATH;

import modhero.common.util.SerialisationUtil;
import modhero.data.nusmods.NusmodsAPIClient;
import modhero.parser.ModuleParser;
import modhero.storage.MajorStorage;
import modhero.storage.ModuleStorage;
import modhero.data.modules.Module;
import modhero.data.modules.Prerequisites;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Generates the text file content for modules.txt and majors.txt.
 *
 * REQUIRES: An active internet connection.
 */
public class DataGenerator {

    private static final String ACAD_YEAR = "2025-2026";

    public static void main(String[] args) {
        // Generate Module Data
        System.out.println("Fetching live module data from NUSMods API...");
        String moduleFileContent = generateModulesTxt();
        if (moduleFileContent != null) {
            ModuleStorage moduleStorage = new ModuleStorage(MODULES_FILE_PATH);
            moduleStorage.saveToTextFile(moduleFileContent);
            System.out.println("Successfully saved to " + MODULES_FILE_PATH);
        } else {
            System.err.println("Failed to generate module data. File not saved.");
        }

        // Generate Major Data
        System.out.println("\nGenerating major data...");
        String majorFileContent = generateMajorsTxt();
        MajorStorage majorStorage = new MajorStorage(MAJOR_FILE_PATH);

        majorStorage.saveToTextFile(majorFileContent);
        System.out.println("Successfully saved to " + MAJOR_FILE_PATH);
    }

    private static String generateModulesTxt() {
        NusmodsAPIClient client = new NusmodsAPIClient();
        ModuleParser parser = new ModuleParser();
        StringBuilder fileContent = new StringBuilder();

        List<String> allModules = Arrays.asList(
                // Computer Science
                "CS1101S", "MA1522", "CS1231S", "ES2660",
                "CS2030S", "CS2040S", "MA1521",
                "CS2100", "CS2101", "CS2103T", "CS2109S", "IS1108",
                "CS2106", "CS3230", "ST2334",
                // Computer Engineering
                "CG1111A", "EG1311", "MA1511", "MA1512", "CS1010",
                "CG2111A", "DTK1234", "MA1508E", "EE2026", "CS2040C",
                "CS2107", "CG2023", "CS2113", "CS1231", "EE2211",
                "CG2027", "CG2028", "CG2271", "CG3201", "EE4204", "CG3207"
        );

        for (String code : allModules) {
            try {
                String json = client.fetchModuleDataSafely(ACAD_YEAR, code);
                String moduleCode = code;
                String moduleName;
                String moduleMc;
                String serialisedPrereqsBlob;

                if (json == null) {
                    System.err.println("Failed to fetch data for " + code + ": null response. Using dummy data.");
                    moduleName = code;
                    moduleMc = "4";
                    serialisedPrereqsBlob = serializePrereqList(new ArrayList<>()); // Empty prerequisites
                } else {
                    Module module = parser.parseModule(json);

                    if (module == null) {
                        System.err.println("ModuleParser failed to parse " + code + ". Using dummy data.");
                        moduleName = code;
                        moduleMc = "4";
                        serialisedPrereqsBlob = serializePrereqList(new ArrayList<>());
                    } else {
                        moduleName = module.getName();
                        moduleMc = String.valueOf(module.getMc());
                        Prerequisites prereqs = module.getPrerequisites();

                        // Get the prerequisite combinations from the Prerequisites object
                        // List<List<String>> prereqCombos = prereqs.getPrerequisiteCombinations();
                        serialisedPrereqsBlob = prereqs.toFormatedString();
                    }
                }

                String desc = "core";

                // Build the module line with triple-serialized prerequisites
                String line = SerialisationUtil.serialiseMessage(moduleCode)
                        + SerialisationUtil.serialiseMessage(moduleName)
                        + SerialisationUtil.serialiseMessage(moduleMc)
                        + SerialisationUtil.serialiseMessage(desc)
                        + SerialisationUtil.serialiseMessage(serialisedPrereqsBlob);

                fileContent.append(line).append(System.lineSeparator());

            } catch (Exception e) {
                System.err.println("Failed to generate data for " + code + ": " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        return fileContent.toString();
    }

    /**
     * Serializes the complex List<List<String>> prerequisite structure.
     *
     * @param prereqCombinations the prerequisite combinations structure
     * @return doubly-serialized prerequisites blob (to be wrapped once more)
     */
    private static String serializePrereqList(List<List<String>> prereqCombinations) {
        if (prereqCombinations == null || prereqCombinations.isEmpty()) {
            return ""; // Empty string for no prerequisites
        }

        StringBuilder prereqBlobBuilder = new StringBuilder();

        // For each combination (OR group)
        for (List<String> combo : prereqCombinations) {
            // WRAP 1: Serialize each module code
            StringBuilder comboBuilder = new StringBuilder();
            for (String moduleCode : combo) {
                comboBuilder.append(SerialisationUtil.serialiseMessage(moduleCode));
            }

            // WRAP 2: Serialize the entire combination
            prereqBlobBuilder.append(SerialisationUtil.serialiseMessage(comboBuilder.toString()));
        }

        // Return the doubly-serialized blob
        return prereqBlobBuilder.toString();
    }

    private static String generateMajorsTxt() {
        StringBuilder fileContent = new StringBuilder();
        MajorSchedule majorSchedule = new MajorSchedule();

        // CS
        List<String> csCodes = Arrays.asList(
                "CS1101S", "MA1522", "CS1231S", "ES2660",
                "CS2030S", "CS2040S", "MA1521",
                "CS2100", "CS2101", "CS2103T", "CS2109S", "IS1108",
                "CS2106", "CS3230", "ST2334"
        );
        Map<String, int[]> csSchedule = majorSchedule.getSchedule("computer science");
        fileContent.append(buildMajorLine("Computer Science", "CS", csCodes, csSchedule))
                .append(System.lineSeparator());

        // CEG
        List<String> cegCodes = Arrays.asList(
                "CG1111A", "EG1311", "MA1511", "MA1512", "CS1010", "CG2111A", "DTK1234", "MA1508E",
                "EE2026", "CS2040C", "CS2107", "CG2023", "CS2113", "CS1231", "EE2211",
                "CG2027", "CG2028", "CG2271", "CG3201", "EE4204", "CG3207"
        );
        Map<String, int[]> cegSchedule = majorSchedule.getSchedule("computer engineering");
        fileContent.append(buildMajorLine("Computer Engineering", "CEG", cegCodes, cegSchedule))
                .append(System.lineSeparator());

        return fileContent.toString();
    }

    private static String buildMajorLine(String name, String abbr,
                                         List<String> moduleCodes, Map<String, int[]> schedule) {
        StringBuilder modulesBlobBuilder = new StringBuilder();
        for (String code : moduleCodes) {
            int[] yearSem = schedule.get(code);
            if (yearSem == null) {
                yearSem = new int[]{0, 0};
            }

            String tripletContent = SerialisationUtil.serialiseMessage(code)
                    + SerialisationUtil.serialiseMessage(String.valueOf(yearSem[0]))
                    + SerialisationUtil.serialiseMessage(String.valueOf(yearSem[1]));

            modulesBlobBuilder.append(SerialisationUtil.serialiseMessage(tripletContent));
        }
        String modulesBlob = modulesBlobBuilder.toString();
        String line = SerialisationUtil.serialiseMessage(name)
                + SerialisationUtil.serialiseMessage(abbr)
                + SerialisationUtil.serialiseMessage(modulesBlob);
        return line;
    }
}
