package modhero.common.predata;

import modhero.common.Constants;
import modhero.common.util.Serialiser;
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
 * This version generates empty prerequisites to match ModuleLoader's
 * validation logic which only accepts empty prerequisite lists.
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
        NusmodsAPIClient client = new NusmodsAPIClient();
        ModuleParser parser = new ModuleParser();
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
                String json = client.fetchModuleDataSafely(ACAD_YEAR, code);
                String moduleCode = code;
                String moduleName;
                String moduleMc;

                if (json == null) {
                    System.err.println("Failed to fetch data for " + code + ": null response. Using dummy data.");
                    moduleName = code;
                    moduleMc = "4";
                } else {
                    Module module = parser.parseModule(json);

                    if (module == null) {
                        System.err.println("ModuleParser failed to parse " + code + ". Using dummy data.");
                        moduleName = code;
                        moduleMc = "4";
                    } else {
                        moduleName = module.getName();
                        moduleMc = String.valueOf(module.getMc());
                    }
                }

                String desc = "core";

                // Generate empty prerequisites that will pass ModuleLoader validation
                // The loader expects: after deserialiseMessage -> empty list
                // then after deserialiseList -> empty list
                String emptyPrereqsBlob = generateEmptyPrerequisites();

                String line = Serialiser.serialiseMessage(moduleCode)
                        + Serialiser.serialiseMessage(moduleName)
                        + Serialiser.serialiseMessage(moduleMc)
                        + Serialiser.serialiseMessage(desc)
                        + Serialiser.serialiseMessage(emptyPrereqsBlob);

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
     * Generates an empty prerequisites blob that matches ModuleLoader's expectations.
     *
     * ModuleLoader validation logic:
     * 1. deserialiseMessage(serialisedPrereqs) must return empty list
     * 2. deserialiseList(empty list) must return empty list
     *
     * To satisfy this, we serialize an empty string which will deserialize to an empty list.
     */
    private static String generateEmptyPrerequisites() {
        // An empty string will deserialize to an empty list in deserialiseMessage
        return "";
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
