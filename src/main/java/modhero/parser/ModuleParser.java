package modhero.parser;

import modhero.data.modules.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parses a JSON response into a Module object.
 */
public class ModuleParser {
    private static final Logger logger = Logger.getLogger(ModuleParser.class.getName());
    private final JsonExtractor extractor = new JsonExtractor();

    private final int MAX_MC  = 20;

    /**
     * Parses the JSON representation of a module into a Module object.
     *
     * @param json raw JSON string from NUSMods API
     * @return a Module, or null if required fields are missing
     */
    public Module parseModule(String json) {
        if (json == null) {
            return null;
        }

        String code = extractor.getArg(json, "moduleCode");
        String name = extractor.getArg(json, "title");
        String mc = extractor.getArg(json, "moduleCredit");
        String prereq = extractor.getArg(json, "prereqTree");

        if (!isValidRawData(code, name, mc, prereq)) {
            logger.log(Level.WARNING, () -> String.format("Module retrieved contains null %s, %s, %s, %s", code, name, mc, prereq));
            return null;
        }

        int parsedMc = parseModuleCredit(mc);
        List<String> parsedPrereq = parsePrereq(prereq);

        if (!isValidParsedData(parsedMc, parsedPrereq)) {
            logParsingErrors(parsedMc, parsedPrereq, mc, prereq);
            return null;
        }

        return new Module(code, name, parsedMc, "core", parsedPrereq);
    }

    /**
     * Validates that all required raw data fields are non-null.
     *
     * @param code The module code.
     * @param name The module name/title.
     * @param mc The module credit as a string.
     * @param prereq The prerequisite tree as a JSON string.
     * @return True if all fields are non-null, false otherwise.
     */
    private boolean isValidRawData(String code, String name, String mc, String prereq) {
        return code != null && name != null && mc != null && prereq != null;
    }

    /**
     * Validates that parsed data is valid (module credit is positive and prerequisites exist).
     *
     * @param parsedMc The parsed module credit value.
     * @param parsedPrereq The list of parsed prerequisite module codes.
     * @return True if both parsed values are valid, false otherwise.
     */
    private boolean isValidParsedData(int parsedMc, List<String> parsedPrereq) {
        return parsedMc != -1 && !parsedPrereq.isEmpty();
    }

    /**
     * Logs warnings when parsing fails for module credit or prerequisites.
     *
     * @param parsedMc The parsed module credit value (-1 indicates parsing failure).
     * @param parsedPrereq The list of parsed prerequisite module codes (empty indicates failure).
     * @param mc The original module credit string that failed to parse.
     * @param prereq The original prerequisite string that failed to parse.
     */
    private void logParsingErrors(int parsedMc, List<String> parsedPrereq, String mc, String prereq) {
        if (parsedMc == -1) {
            logger.log(Level.WARNING, "Unable to parse module credit: " + mc);
        }
        if (parsedPrereq.isEmpty()) {
            logger.log(Level.WARNING, "Unable to parse prerequisites: " + prereq);
        }
    }

    /**
     * Parses a module credit string into an integer value.
     * Validates that the credit is within the acceptable range (1-20).
     *
     * @param rawText The raw module credit string.
     * @return The parsed module credit as an integer, or -1 if parsing fails
     *         or the value is out of range.
     */
    private Integer parseModuleCredit(String rawText) {
        try {
            int moduleCredit = Integer.parseInt(rawText);
            if (moduleCredit > 0 && moduleCredit <= MAX_MC) {
                return moduleCredit;
            }
            return -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Parses the prerequisite tree JSON string and extracts module codes.
     *
     * @param rawText The prerequisite tree as a JSON string.
     * @return A list of prerequisite module codes, or an empty list if parsing fails.
     */
    private List<String> parsePrereq(String rawText) {
        String arrayContent = extractArrayContent(rawText);
        if (arrayContent == null) {
            return new ArrayList<>();
        }

        String[] entries = splitArrayEntries(arrayContent);
        return parseModuleCodes(entries);
    }

    /**
     * Extracts the content within square brackets from a JSON array string.
     *
     * @param json The JSON string containing an array.
     * @return The content between the square brackets, or null if no array is found.
     */
    private String extractArrayContent(String json) {
        int arrayStart = json.indexOf('[');
        int arrayEnd = json.lastIndexOf(']');

        if (arrayStart == -1 || arrayEnd == -1) {
            return null;
        }

        return json.substring(arrayStart + 1, arrayEnd);
    }

    /**
     * Splits a JSON array content string into individual entries by comma.
     *
     * @param arrayContent The content of the JSON array (without brackets).
     * @return An array of individual entry strings.
     */
    private String[] splitArrayEntries(String arrayContent) {
        return arrayContent.split(",");
    }


    /**
     * Parses module codes from an array of JSON entry strings.
     *
     * @param entries The array of JSON entry strings.
     * @return A list of extracted module codes.
     */
    private List<String> parseModuleCodes(String[] entries) {
        List<String> moduleCodes = new ArrayList<>();

        for (String entry : entries) {
            String moduleCode = extractModuleCode(entry);
            if (moduleCode != null) {
                moduleCodes.add(moduleCode);
            }
        }

        return moduleCodes;
    }

    /**
     * Extracts a module code from a single JSON entry string.
     * Expects format like "CS2113:D" and extracts the part before the colon.
     *
     * @param entry A single JSON entry string.
     * @return The extracted module code, or null if the format is invalid.
     */
    private String extractModuleCode(String entry) {
        int quoteStart = entry.indexOf('"');
        int colon = entry.indexOf(':');

        if (quoteStart != -1 && colon != -1) {
            return entry.substring(quoteStart + 1, colon);
        }

        return null;
    }
}
