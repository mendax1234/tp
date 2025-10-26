package modhero.common.predata;

import modhero.common.Constants;
import modhero.common.util.Serialiser;
import modhero.data.nusmods.ModuleRetriever;
import modhero.data.nusmods.NusmodsAPIClient;
import modhero.parser.ModuleParser;
import modhero.storage.Storage;
import modhero.data.modules.Module;
import modhero.data.modules.Prerequisites;
import modhero.common.predata.MajorSchedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Generates the text file content for modules.txt and majors.txt.
 *
 * This version contains the correct TRIPLE-SERIALIZATION logic
 * in the 'serializePrereqList' helper method, which matches
 * the triple-deserialization logic in the fixed ModuleLoader.
 *
 * REQUIRES: An active internet connection.
 */
public class DataGenerator {

    private static final String ACAD_YEAR = "2025-2026";

    public static void main(String[] args) {
        // 1. Generate Module Data
        System.out.println("Fetching live module data from NUSMods API...");
        String moduleFileContent = generateModulesTxt();
        if (moduleFileContent != null) {
            Storage moduleStorage = new Storage(Constants.MODULES_FILE_PATH);
            moduleStorage.save(moduleFileContent);
            System.out.println("Successfully saved to " + Constants.MODULES_FILE_PATH);
        } else {
            System.err.println("Failed to generate module data. File not saved.");
        }

        // 2. Generate Major Data
        System.out.println("\nGenerating major data...");
        String majorFileContent = generateMajorsTxt();
        Storage majorStorage = new Storage(Constants.MAJOR_FILE_PATH);
        majorStorage.save(majorFileContent);
        System.out.println("Successfully saved to " + Constants.MAJOR_FILE_PATH);
    }

    private static String generateModulesTxt() {
        ModuleRetriever retriever = new ModuleRetriever();
        StringBuilder fileContent = new StringBuilder();

        List<String> allModules = Arrays.asList(
                // Computer Science
                "CS1101S", "CS1231S", "CS2030S", "CS2040S", "CS2101", "CS2100",
                "CS2103T", "CS2106", "CS2109S", "CS3230",
                // Computer Engineering
                "CG1111A", "EG1311", "MA1511", "MA1512", "CG2111A", "DTK1234", "MA1508E",
                "EE2026", "CS2040C", "CS2107", "CG2023", "CS2113", "CS1231", "EE2211",
                "CG2027", "CG2028", "CG2271", "CG3201", "EE4204", "CG3207"
        );

        for (String code : allModules) {
            try {
                Module module = retriever.getModule(ACAD_YEAR, code);

                String moduleCode = code;
                String moduleName;
                String moduleMc;
                String serialisedPrereqsBlob;

                if (module == null) {
                    System.err.println("Failed to fetch or parse data for " + code + ". Using dummy data.");
                    moduleName = code;
                    moduleMc = "4";
                    serialisedPrereqsBlob = serializePrereqList(new ArrayList<>());
                } else {
                    moduleName = module.getName();
                    moduleMc = String.valueOf(module.getMc());
                    Prerequisites prereqs = module.getPrerequisites();
                    serialisedPrereqsBlob = (prereqs != null)
                            ? prereqs.toFormatedString()
                            : serializePrereqList(new ArrayList<>());
                }

                String desc = "core";

                // Triple-serialized: each field wrapped once
                String line = Serialiser.serialiseMessage(moduleCode)
                        + Serialiser.serialiseMessage(moduleName)
                        + Serialiser.serialiseMessage(moduleMc)
                        + Serialiser.serialiseMessage(desc)
                        + Serialiser.serialiseMessage(serialisedPrereqsBlob);

                fileContent.append(line).append(System.lineSeparator());

            } catch (Exception e) {
                System.err.println("Error processing module " + code + ": " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        return fileContent.toString();
    }
    /**
     * Serializes the complex List<List<String>> prerequisite structure.
     *
     * Serialization structure (to match ModuleLoader's triple deserialization):
     * 1. Each module code is serialized (WRAP 1)
     * 2. Each combination (list of codes) is serialized (WRAP 2)
     * 3. The entire blob is serialized one more time in generateModulesTxt (WRAP 3)
     *
     * Example: [[CS1010, CS1101S], [CS1010E]]
     * - CS1010 -> serialized
     * - CS1101S -> serialized
     * - Combination [CS1010, CS1101S] -> serialized (contains the two above)
     * - CS1010E -> serialized
     * - Combination [CS1010E] -> serialized
     * - All combinations together -> final blob (then wrapped once more outside)
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
                comboBuilder.append(Serialiser.serialiseMessage(moduleCode));
            }

            // WRAP 2: Serialize the entire combination
            prereqBlobBuilder.append(Serialiser.serialiseMessage(comboBuilder.toString()));
        }

        // Return the doubly-serialized blob
        // (will be wrapped one more time in generateModulesTxt with Serialiser.serialiseMessage)
        return prereqBlobBuilder.toString();
    }

    private static String generateMajorsTxt() {
        StringBuilder fileContent = new StringBuilder();
        MajorSchedule majorSchedule = new MajorSchedule();

        // --- Computer Science ---
        List<String> csCodes = Arrays.asList(
                "CS1101S", "CS1231S", "CS2030S", "CS2040S", "CS2100", "CS2101",
                "CS2103T", "CS2106", "CS2109S", "CS3230"
        );
        Map<String, int[]> csSchedule = majorSchedule.getSchedule("computer science");
        fileContent.append(buildMajorLine("Computer Science", "CS", csCodes, csSchedule))
                .append(System.lineSeparator());

        // --- Computer Engineering ---
        List<String> cegCodes = Arrays.asList(
                "CG1111A", "EG1311", "MA1511", "MA1512", "CG2111A", "DTK1234", "MA1508E",
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

            String tripletContent = Serialiser.serialiseMessage(code)
                    + Serialiser.serialiseMessage(String.valueOf(yearSem[0]))
                    + Serialiser.serialiseMessage(String.valueOf(yearSem[1]));

            modulesBlobBuilder.append(Serialiser.serialiseMessage(tripletContent));
        }
        String modulesBlob = modulesBlobBuilder.toString();
        String line = Serialiser.serialiseMessage(name)
                + Serialiser.serialiseMessage(abbr)
                + Serialiser.serialiseMessage(modulesBlob);
        return line;
    }
}
