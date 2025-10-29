package modhero.storage;

import modhero.data.modules.Module;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Utility to regenerate modules.txt file with new prerequisite format.
 */
public class ModuleDataRewriter {
    private static final Logger logger = Logger.getLogger(ModuleDataRewriter.class.getName());

    /**
     * Reads modules.txt, parses it, and rewrites with the new format.
     */
    public ModuleDataRewriter ( Map<String, Module> allData) {
        System.out.println("=".repeat(60));
        System.out.println("MODULE DATA FILE REWRITER");
        System.out.println("=".repeat(60));
        System.out.println();
        String outputPath = "src/main/resources/rewrittenmodules.txt";

        // Step 1: Load existing modules
        Map<String, Module> allModulesData = allData;

        // Step 2: Rewrite all modules
        System.out.println();
        System.out.println("Step 2: Rewriting modules to " + outputPath);

        StringBuilder fileContent = new StringBuilder();
        Set<String> writtenCodes = new HashSet<>();
        int count = 0;

        for (Map.Entry<String, Module> entry : allModulesData.entrySet()) {
            Module module = entry.getValue();
            String moduleCode = module.getCode();

            // Skip duplicates (HashMap has both code and name as keys)
            if (writtenCodes.contains(moduleCode)) {
                continue;
            }

            // Serialize module with new format
            String serializedModule = module.toFormatedString();
            fileContent.append(serializedModule).append("\n");
            writtenCodes.add(moduleCode);
            count++;

            if (count % 100 == 0) {
                System.out.println("  Processed " + count + " modules...");
            }
        }

        // Step 3: Write to file
        System.out.println();
        System.out.println("Step 3: Writing to file...");

        try {
            // Ensure directory exists
            java.io.File file = new java.io.File(outputPath);
            file.getParentFile().mkdirs();

            // Write file
            try (FileWriter writer = new FileWriter(outputPath)) {
                writer.write(fileContent.toString());
            }

            System.out.println("✓ Successfully wrote " + count + " modules");

        } catch (IOException e) {
            System.err.println("✗ Failed to write file: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Done
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("REWRITE COMPLETE");
        System.out.println("=".repeat(60));
        System.out.println();
        System.out.println("Output file: " + outputPath);
        System.out.println("Total modules: " + count);
        System.out.println();
        System.out.println("Next steps:");
        System.out.println("  1. Review the new file: " + outputPath);
        System.out.println("  2. Backup your old file:");
        System.out.println("  3. Replace old file with new:");
    }

    /**
     * Main method - run this to rewrite your modules file.
     */
}