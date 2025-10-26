package modhero.data;

import modhero.common.Constants;
import modhero.common.util.JsonUtil;
import modhero.common.util.Serialiser;
import modhero.data.major.MajorData; // Import MajorData
import modhero.data.nusmods.NusmodsAPIClient;
import modhero.parser.ModuleParser;
import modhero.storage.Storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map; // Import Map

/**
 * Generates the text file content for modules.txt and majors.txt.
 *
 * This utility class now embeds the recommended schedule (year/sem)
 * from MajorData.java directly into the major.txt file.
 *
 * It SAVES the content directly to the files defined in Constants.java.
 * REQUIRES: An active internet connection.
 */
public class DataGenerator {

    private static final String ACAD_YEAR = "2024-2025";

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
        System.out.println("\nGenerating major data (with schedule)...");
        String majorFileContent = generateMajorsTxt();
        Storage majorStorage = new Storage(Constants.MAJOR_FILE_PATH);
        majorStorage.save(majorFileContent);
        System.out.println("Successfully saved to " + Constants.MAJOR_FILE_PATH);
    }

    private static String generateModulesTxt() {
        NusmodsAPIClient client = new NusmodsAPIClient();
        StringBuilder fileContent = new StringBuilder();

        List<String> allModules = Arrays.asList(
                // Computer Science
                "CS1010S", "CS1231S", "CS2030S", "CS2040S", "CS2101", "CS2100",
                "CS2103T", "CS2106", "CS2109S", "CS3230",
                // Computer Engineering
                "CS1010", "CS1231", "CS2040C", "CS2113", "EE2026", "EE4204",
                "CG1111A", "CG2111A", "CG2023", "CG2027", "CG2028", "CG2271"
        );

        for (String code : allModules) {
            try {
                String json = client.fetchModuleDataSafely(ACAD_YEAR, code);
                if (json == null) {
                    System.err.println("Failed to fetch data for " + code + ": null response");
                    continue;
                }

                String name = JsonUtil.getArg(json, ModuleParser.NAME);
                String mc = JsonUtil.getArg(json, ModuleParser.MC);
                String prereqJson = JsonUtil.getArg(json, ModuleParser.PREREQ);
                String desc = "core";

                List<List<String>> prereqCombinations = ModuleParser.parsePrereq(prereqJson);
                String serialisedPrereqs = serializePrereqList(prereqCombinations);

                String line = Serialiser.serialiseMessage(code)
                        + Serialiser.serialiseMessage(name)
                        + Serialiser.serialiseMessage(mc)
                        + Serialiser.serialiseMessage(desc)
                        + Serialiser.serialiseMessage(serialisedPrereqs);

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
     * Generates the static content for major.txt, including schedule data.
     */
    private static String generateMajorsTxt() {
        StringBuilder fileContent = new StringBuilder();
        MajorData majorData = new MajorData(); // To get schedule

        // --- Computer Science ---
        List<String> csCodes = Arrays.asList(
                "CS1010S", "CS1231S", "CS2030S", "CS2040S", "CS2101", "CS2100",
                "CS2103T", "CS2106", "CS2109S", "CS3230"
        );
        Map<String, int[]> csSchedule = majorData.getSchedule("computer science");
        fileContent.append(buildMajorLine("CS", "Computer Science", csCodes, csSchedule))
                .append(System.lineSeparator());

        // --- Computer Engineering ---
        List<String> cegCodes = Arrays.asList(
                "CS1010", "CS1231", "CS2040C", "CS2113", "EE2026", "EE4204",
                "CG1111A", "CG2111A", "CG2023", "CG2027", "CG2028", "CG2271"
        );
        Map<String, int[]> cegSchedule = majorData.getSchedule("computer engineering");
        fileContent.append(buildMajorLine("CEG", "Computer Engineering", cegCodes, cegSchedule))
                .append(System.lineSeparator());

        return fileContent.toString();
    }

    /**
     * Helper method to build a single line for major.txt using the project's Serialiser.
     * Now includes schedule data.
     */
    private static String buildMajorLine(String abbr, String name,
                                         List<String> moduleCodes, Map<String, int[]> schedule) {

        List<String> serialisedSpecifiers = new ArrayList<>();
        for (String code : moduleCodes) {
            int[] yearSem = schedule.get(code);
            if (yearSem == null) {
                yearSem = new int[]{0, 0}; // Default/fallback
            }

            // Create the inner specifier string: [code, year, sem]
            String specifierString = Serialiser.serialiseMessage(code)
                    + Serialiser.serialiseMessage(String.valueOf(yearSem[0]))
                    + Serialiser.serialiseMessage(String.valueOf(yearSem[1]));

            // Add this specifier string to our list
            serialisedSpecifiers.add(specifierString);
        }

        // Serialise the list of specifier strings
        String serialisedList = Serialiser.serialiseList(serialisedSpecifiers);

        return Serialiser.serialiseMessage(abbr)
                + Serialiser.serialiseMessage(name)
                + Serialiser.serialiseMessage(serialisedList); // 3rd arg is now the serialised list of specifiers
    }

    /**
     * Serialises the complex List<List<String>> prerequisite structure.
     */
    private static String serializePrereqList(List<List<String>> prereqCombinations) {
        if (prereqCombinations == null) {
            prereqCombinations = new ArrayList<>();
        }

        List<String> serialisedInnerLists = new ArrayList<>();
        for (List<String> combo : prereqCombinations) {
            StringBuilder innerBuilder = new StringBuilder();
            for (String moduleCode : combo) {
                innerBuilder.append(Serialiser.serialiseMessage(moduleCode));
            }
            serialisedInnerLists.add(innerBuilder.toString());
        }
        return Serialiser.serialiseList(serialisedInnerLists);
    }
}
